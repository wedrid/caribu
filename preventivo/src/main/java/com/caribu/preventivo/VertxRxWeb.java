package com.caribu.preventivo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caribu.preventivo.config.ConfigLoader;
import com.caribu.preventivo.config.QuotesConfig;
import com.caribu.preventivo.quotesApi.AddQuotesCache;
import com.caribu.preventivo.quotesApi.AddQuotesCache_mess;
import com.caribu.preventivo.quotesApi.DeleteQuotesHandler;
import com.caribu.preventivo.quotesApi.GetQuotesHandler;
import com.caribu.preventivo.quotesApi.GetQuotesSameTratta;
import com.caribu.preventivo.quotesApi.PostQuotesHandler;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.reactivex.servicediscovery.types.HttpEndpoint;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.http.HttpServer;
import io.vertx.rxjava3.ext.web.Router;
import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.rxjava3.ext.web.handler.BodyHandler;
import io.vertx.rxjava3.ext.web.openapi.RouterBuilder;
import io.vertx.rxjava3.pgclient.PgPool;
import io.vertx.rxjava3.sqlclient.Pool;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.sqlclient.PoolOptions;

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
          test_event(); // TODO eliminare
        })
        .doOnError(configuration -> {
          LOG.info("Errore: {}", configuration);
        })
        .ignoreElement();
  }
  
   //TODO eliminare
  private void test_event() {
    //oLat=43.7696&oLon=11.2558&dLat=44.6983&dLon=10.6312
      JsonObject requestData = new JsonObject()
        .put("id_tratta", 22)
        .put("oLat", 43.7696)
        .put("oLon", 11.2558)
        .put("dLat", 44.6983)
        .put("dLon", 10.6312);

      vertx.eventBus().send("added-tratta-address", requestData);
  } 

  // Event Bus
  private void inteceptNewRequestTrattaEvent(final Pool db) {
    vertx.eventBus().consumer("added-tratta-address").handler(new AddQuotesCache_mess(db));//, message -> {
    // #LOG.info("Received message: {}", message.body());
      //JsonObject body = (JsonObject) message.body();
      //System.out.println(body.toString()); // TODO: replace sout with chiamata a calcola cache
    //});
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
    inteceptNewRequestTrattaEvent(db);
    RouterBuilder.create(vertx, "openapi.yml")
        .doOnSuccess(routerBuilder -> { // (1)
          // The concept of "controller" in vert.x doesn't inheretly exist. The following,
          // could be considered the "controllers".
          // e.g. routerBuilder.operation("-").handler();
          // the handler object would be a "mix" of the controller and the "service" or
          // "module" (depending on the terminology is preferred in different contexts)
          routerBuilder.operation("listQuotes").handler(new GetQuotesSameTratta(db)); 
          routerBuilder.operation("getQuotes").handler(new GetQuotesHandler(db)); 
          routerBuilder.operation("deleteQuotes").handler(new DeleteQuotesHandler(db));
          routerBuilder.operation("addQuotes").handler(new PostQuotesHandler(db)); 
          routerBuilder.operation("addQuotesCache").handler(new AddQuotesCache(db));

          
          
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
                    });
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