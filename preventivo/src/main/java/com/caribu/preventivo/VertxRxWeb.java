package com.caribu.preventivo;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.reactivex.servicediscovery.types.HttpEndpoint;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.http.HttpHeaders;
import io.vertx.rxjava3.core.http.HttpServer;
import io.vertx.rxjava3.ext.web.Router;
import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.rxjava3.ext.web.handler.BodyHandler;
import io.vertx.rxjava3.ext.web.openapi.RouterBuilder;
import io.vertx.rxjava3.pgclient.PgPool;
import io.vertx.rxjava3.sqlclient.Pool;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.sqlclient.PoolOptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caribu.preventivo.config.QuotesConfig;
import com.caribu.preventivo.config.ConfigLoader;
import com.caribu.preventivo.operatorInf.AddQuotesCache;
import com.caribu.preventivo.operatorInf.DeleteOpDatabaseHandler;
import com.caribu.preventivo.operatorInf.GetOpFromDatabaseHandler;
import com.caribu.preventivo.operatorInf.GetSameTratta;
import com.caribu.preventivo.operatorInf.PostOpFromDatabaseHandler;
import com.caribu.preventivo.operatorInf.PutOpDatabaseHandler;

public class VertxRxWeb extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(VertxRxWeb.class);
  private ServiceDiscovery discovery;
  private final int PORT = 10005; // new port for service quotes

  @Override
  public Completable rxStart() {
    return ConfigLoader.load(vertx)
        .doOnSuccess(configuration -> {
          LOG.info("Retrieved Configuration: {}", configuration);
          startHttpServerAndAttachRoutes(configuration);
          inteceptNewRequestTrattaEvent();
        })
        .doOnError(configuration -> {
          LOG.info("Errore: {}", configuration);
        })
        .ignoreElement();
  }
  //TODO: 
  private void inteceptNewRequestTrattaEvent() {
    vertx.eventBus().consumer("added-tratta-address", message -> {
      LOG.info("Received message: {}", message.body());
      JsonObject body = (JsonObject) message.body(); 
      System.out.println(body.toString()); //TODO: replace sout with chiamata a calcola cache
    });
  }

  private void startHttpServerAndAttachRoutes(final QuotesConfig configuration) {
    discovery = ServiceDiscovery.create(vertx.getDelegate());
    // database configuration. 
    final var poolOptions = new PoolOptions()
        .setMaxSize(4);

    final var connectOptions = new PgConnectOptions()
        .setHost(configuration.getDbConfig().getHost())
        .setPort(configuration.getDbConfig().getPort())
        .setDatabase(configuration.getDbConfig().getDatabase())
        .setUser(configuration.getDbConfig().getUser())
        .setPassword(configuration.getDbConfig().getPassword());
    LOG.debug("DB Config: {}", connectOptions.getHost());

    final Pool db = PgPool.pool(vertx, connectOptions, poolOptions);

    RouterBuilder.create(vertx, "openapi.yml")
        .doOnSuccess(routerBuilder -> { // (1)
          // The concept of "controller" in vert.x doesn't inheretly exist. The following, could be considered the "controllers".
          // e.g. routerBuilder.operation("-").handler();
          // the handler object would be a "mix" of the controller and the "service" or "module" (depending on the terminology is preferred in different contexts)
          routerBuilder.operation("listQuotes").handler(new GetSameTratta(db)); // (3)
          routerBuilder.operation("getQuotes").handler(new GetOpFromDatabaseHandler(db)); // (3)
          // routerBuilder.operation("updateOpAvailability").handler(new
          // PutOpDatabaseHandler(db)); // (3)
          routerBuilder.operation("deleteQuotes").handler(new DeleteOpDatabaseHandler(db)); // (3)
          routerBuilder.operation("addQuotes").handler(new PostOpFromDatabaseHandler(db)); // (3)
          routerBuilder.operation("addQuotesCache").handler(new AddQuotesCache(db)); // (3)

          Router restApi = routerBuilder.createRouter();
          restApi.route().handler(BodyHandler.create());

          restApi.route().handler(this::failureHandler);

          Single<HttpServer> single = vertx.createHttpServer()
              .requestHandler(restApi)
              .rxListen(PORT, "localhost");
          single.subscribe(
              server -> {
                LOG.info("HTTP server started, attaching to discovery");
                discovery.publish(
                  HttpEndpoint.createRecord("quoteapi", "127.0.0.1", PORT, "/"), 
                  ar -> {
                    if (ar.succeeded()) {
                      LOG.info("HTTP server started on port {}", PORT);
                      LOG.info("Service published on port {}", PORT);
                    } else {
                      LOG.error("Error starting the discovery infrastructure");
                      LOG.info("Error starting the discovery infrastructure");
                    }
                  }
                  );
              },
              failure -> {
                LOG.error("Server could not start: (1) " + failure.getMessage(), failure);
              });
        }).doOnError(cause -> {
          LOG.error("Server could not start: (2)" + cause.getMessage(), cause);
        }).subscribe();
  }

  private void failureHandler(RoutingContext errorContext) {

    if (errorContext.response().ended()) {
      // Ignore completed response
      LOG.info("------");
      return;
    }
    LOG.info("Route Error:", errorContext.failure());
    errorContext.response()
        .setStatusCode(500)
        .end(new JsonObject().put("message: Something went wrong, path: ", errorContext.normalizedPath()).toString());
  }

  public static String getAccountId(final RoutingContext context) {
    String id = context.pathParam("id");// Integer.parseInt();
    LOG.debug("{} for account {}", context.normalizedPath(), id);
    return id;
  }

}

// Router router = Router.router(vertx);
// router.route().handler(BodyHandler.create());
// router.get("/quotes").handler(new
// GetAllOpFromDatabaseHandler(db));//this::hello);//
// router.get("/quotes/delete/:id").handler(new
// GetAllOpFromDatabaseHandler(db));//this::hello);//
