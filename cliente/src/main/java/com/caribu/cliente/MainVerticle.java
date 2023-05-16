package com.caribu.cliente;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
  
  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.exceptionHandler(error -> 
      LOG.error("Unhandled: {}", error)
    );
    vertx.deployVerticle(new MainVerticle(), ar -> {
      if(ar.failed()){
        LOG.error("Failed to deploy:", ar.cause());
        return;
      }
      LOG.info("Deployed {}}!", MainVerticle.class.getName());
    });
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    final Router restApi = Router.router(vertx); //restApi IS the router
    restApi.route().failureHandler(handleFailure());
    
    RequestsRestApi.attach(restApi);

    //creates HTTP server
    vertx.createHttpServer()
    .requestHandler(restApi)
    .exceptionHandler(error -> LOG.error("HTTP Server error: ", error))
    .listen(8888, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        LOG.info("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }

  private Handler<RoutingContext> handleFailure() {
    return errorContext -> {
      if (errorContext.response().ended()){
        // Ignore, e.g. if client stops request
        return;
      }
      LOG.error("Route Error: {}", errorContext.failure());
      errorContext.response()
      .setStatusCode(500)
        .end(new JsonObject().put("message", "Something went wrong").toBuffer());
    };
  }
}
