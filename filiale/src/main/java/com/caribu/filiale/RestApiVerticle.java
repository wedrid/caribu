package com.caribu.filiale;

import com.caribu.filiale.db.DBPools;
import com.caribu.filiale.db.DbResponse;
import com.caribu.filiale.operatorInf.DeleteOpDatabaseHandler;
import com.caribu.filiale.operatorInf.GetAllOpFromDatabaseHandler;
import com.caribu.filiale.operatorInf.GetOpFromDatabaseHandler;
import com.caribu.filiale.operatorInf.OpRestApi;
import com.caribu.filiale.operatorInf.PostOpFromDatabaseHandler;
import com.caribu.filiale.operatorInf.PutOpDatabaseHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caribu.filiale.config.BrokerConfig;
import com.caribu.filiale.config.ConfigLoader;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;

public class RestApiVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(RestApiVerticle.class);

  @Override
  public void start(final Promise<Void> startPromise) throws Exception {
    ConfigLoader.load(vertx)
      .onFailure(startPromise::fail)
      .onSuccess(configuration -> {
        LOG.info("Retrieved Configuration: {}", configuration);
        startHttpServerAndAttachRoutes(startPromise, configuration);
      });
  }

  private void startHttpServerAndAttachRoutes(final Promise<Void> startPromise,
    final BrokerConfig configuration) {
    // One pool for each Rest Api Verticle
    //final Pool db = DBPools.createPgPool(configuration, vertx);
    // Alternatively use MySQL
    // final Pool db = DBPools.createMySQLPool(configuration, vertx);
    final var connectOptions = new PgConnectOptions()
      .setHost(configuration.getDbConfig().getHost())
      .setPort(configuration.getDbConfig().getPort())
      .setDatabase(configuration.getDbConfig().getDatabase())
      .setUser(configuration.getDbConfig().getUser())
      .setPassword(configuration.getDbConfig().getPassword());

    final var poolOptions = new PoolOptions()
      .setMaxSize(4);

    final Pool db = PgPool.pool(vertx, connectOptions, poolOptions);


    // final Router restApi = Router.router(vertx);
    // restApi.route().handler(BodyHandler.create());
    RouterBuilder.create(vertx, "openapi.yml")
      .onSuccess(routerBuilder -> { // (1)
          //associate the operationId "listOp" with its handler 
        routerBuilder.operation("listOp").handler(new GetAllOpFromDatabaseHandler(db)); // (3)
        routerBuilder.operation("getOp").handler(new GetOpFromDatabaseHandler(db)); // (3)
        routerBuilder.operation("updateOpAvailability").handler(new PutOpDatabaseHandler(db)); // (3)
        routerBuilder.operation("deleteOp").handler(new DeleteOpDatabaseHandler(db)); // (3)
        routerBuilder.operation("AddOp").handler(new PostOpFromDatabaseHandler(db)); // (3)


        Router restApi = routerBuilder.createRouter();
        restApi.route().handler(BodyHandler.create());
      
        //AssetsRestApi.attach(restApi, db);
        //OpRestApi.attach(restApi, db);
        //filialeVertical.sendOp(restApi);
        restApi.route().handler(handleFailure()); // handlerFailure non funziona??

        vertx.createHttpServer()
          .requestHandler(restApi)
          .exceptionHandler(error -> LOG.error("HTTP Server error: ", error))
          .listen(configuration.getServerPort(), http -> {
            if (http.succeeded()) {
              startPromise.complete();
              LOG.info("HTTP server started on port {}", configuration.getServerPort());
            } else {
              startPromise.fail(http.cause());
            }
        });
    
        // You can start building the router using routerBuilder
      }).onFailure(cause -> { // (2)
      // Something went wrong during router factory initialization
      startPromise.fail(cause);

    });
  
  }

  private Handler<RoutingContext> handleFailure() {
    return errorContext -> {
      
      if (errorContext.response().ended()) {
         // Ignore completed response
         LOG.info("------");
        return;
      }
      LOG.info("Route Error:", errorContext.failure());
      errorContext.response()
        .setStatusCode(500)
        .end(new JsonObject().put("message: Something went wrong, path: ", errorContext.normalizedPath()).toBuffer());
    };
  }

}
