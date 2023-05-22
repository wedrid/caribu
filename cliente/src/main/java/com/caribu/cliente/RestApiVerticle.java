package com.caribu.cliente;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caribu.cliente.clientapi.CompaniesRestApi;
import com.caribu.cliente.config.ClientConfig;
import com.caribu.cliente.config.ConfigLoader;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class RestApiVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(RestApiVerticle.class);

    @Override
    public void start(final Promise<Void> startPromise) throws Exception{
        ConfigLoader.load(vertx)
            .onFailure(startPromise::fail)
            .onSuccess(configuration -> {
                LOG.info("Retrieved configuration {}", configuration);
                startHttpServerAndAttachRoutes(startPromise, configuration);
        });
    }
    


  private void startHttpServerAndAttachRoutes(Promise<Void> startPromise, final ClientConfig configuration) {
    final Router restApi = Router.router(vertx); //restApi IS the router
    restApi.route().failureHandler(handleFailure());
    
    CompaniesRestApi.attach(restApi);

    //creates HTTP server
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
