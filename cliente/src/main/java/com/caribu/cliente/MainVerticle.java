package com.caribu.cliente;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caribu.cliente.clientapi.CompaniesRestApi;
import com.caribu.cliente.config.ConfigLoader;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
  public static final int PORT = 8888;

  public static void main(String[] args) {
    System.setProperty(ConfigLoader.SERVER_PORT, "8888");

    var vertx = Vertx.vertx();
    vertx.exceptionHandler(error -> 
      LOG.error("Unhandled: {}", error)
    );
    vertx.deployVerticle(new MainVerticle())
      .onFailure(err -> LOG.error("Failed to deploy: ", err))
      .onSuccess(id -> LOG.info("Deployed {} with id {}", MainVerticle.class.getSimpleName(), id));
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.deployVerticle(VersionInfoVerticle.class.getName())
      .onFailure(startPromise::fail)
      .onSuccess(id -> LOG.info("Deployed {} with id {}", RestApiVerticle.class.getSimpleName(), id))
      .compose(next -> migrateDatabase())
      .onFailure(startPromise::fail)
      .onSuccess(id -> LOG.info("Migrated database to latest version "))
      .compose(next -> deployRestApiVerticle(startPromise));
      }
      

  private Future<Void> migrateDatabase() {
    return ConfigLoader.load(vertx)
      .compose(config -> FlywayMigration.migrate(vertx, config.getDbConfig()))
    ;
    
  }

  private Future<String> deployRestApiVerticle(Promise<Void> startPromise) {
    return vertx.deployVerticle(RestApiVerticle.class.getName(),
    new DeploymentOptions().setInstances(processors()))
    .onFailure(startPromise::fail)
    .onSuccess(id -> {
      LOG.info("Deployed {} with id {}", RestApiVerticle.class.getSimpleName(), id);
      startPromise.complete();
    });
  }

  private int processors() {
    //return Math.max(1, Runtime.getRuntime().availableProcessors());
    return 1; //TODO can be changed to do load balancing with line
  }
}


