package com.caribu.cliente;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caribu.cliente.clientapi.CompaniesRestApi;
import com.caribu.cliente.config.ClientConfig;
import com.caribu.cliente.config.ConfigLoader;
import com.caribu.richiesta.requestapi.RequestsRestApi;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.HttpEndpoint;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;

public class RestApiVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(RestApiVerticle.class);
    private ServiceDiscovery discovery;

    @Override
    public void start(final Promise<Void> startPromise) throws Exception{
        /*ConfigLoader.load(vertx)
            .onFailure(startPromise::fail)
            .onSuccess(configuration -> {
                LOG.info("Retrieved configuration {}", configuration);
                startHttpServerAndAttachRoutes(startPromise, configuration);
        }
      );*/
    }
    
    

    private void startHttpServerAndAttachRoutes(Promise<Void> startPromise, final ClientConfig configuration) {
      // Create DB Pool (for scaling)
      Pool db = createDbPool(configuration);

      final Router restApi = Router.router(vertx); //restApi IS the router
      //restApi.route().failureHandler(handleFailure()); //sbagliato 
  
      CompaniesRestApi.attach(restApi, db); //pass database connection to CompaniesRestApi
      RequestsRestApi.attach(restApi, db); 

      // last handler, è la rete di salvataggio. Se non viene trovata una route, viene restituito un errore 404
      restApi.route().handler(handleFailure());

      // Create Service Discovery instance
      discovery = ServiceDiscovery.create(vertx);

      // Create HTTP server
      vertx.createHttpServer()
      .requestHandler(restApi)
      .exceptionHandler(error -> LOG.error("HTTP Server error: ", error))
      .listen(configuration.getServerPort(), http -> {
        if (http.succeeded()) {
          // Publish the HTTP endpoint to Service Discovery
          
          discovery.publish(
            HttpEndpoint.createRecord("requests-api", "127.0.0.1", 8303, "/"),
            ar -> {
              if (ar.succeeded()) {
                startPromise.complete();
                LOG.info("HTTP server started on port {}", configuration.getServerPort());
                LOG.info("Service published on port 8303");
              } else {
                startPromise.fail(ar.cause());
              }
            }
          );
        } else {
          startPromise.fail(http.cause());
        }
      });
    }


  private Pool createDbPool(final ClientConfig configuration) {
    final var connectOptions = new PgConnectOptions()
      .setHost(configuration.getDbConfig().getHost()) //get host from configurations
      .setPort(configuration.getDbConfig().getPort()) //get port from configurations
      .setDatabase(configuration.getDbConfig().getDatabase()) //get database from configurations
      .setUser(configuration.getDbConfig().getUser()) //get user from configurations
      .setPassword(configuration.getDbConfig().getPassword()); //get password from configurations

    var poolOptions = new PoolOptions().setMaxSize(4); //set max pool size to 5 //possible error here?
     
    return Pool.pool(vertx, connectOptions, poolOptions);
  }

  private Handler<RoutingContext> handleFailure() {
    return errorContext -> {
      /* 
      if (errorContext.response().ended()){
        // Ignore, e.g. if client stops request
        return;
      } 
      */ 
      LOG.info("Route Error: {}", errorContext.failure());
      errorContext.response()
      .setStatusCode(500)
        .end(new JsonObject()
          .put("message", "Something went wrong")
          .put("path", errorContext.normalizedPath())
        .toBuffer());
    };
  }


}
