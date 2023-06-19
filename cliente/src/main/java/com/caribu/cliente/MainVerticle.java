package com.caribu.cliente;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caribu.cliente.clientapi.RoutesManagement;
import com.caribu.cliente.config.ConfigLoader;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
  public static final int PORT = 10001;

  @Override
  public Completable rxStart() {
    return vertx.rxDeployVerticle(VersionInfoVerticle.class.getName())
        .doOnSuccess(id -> LOG.info("Deployed {} with id {}", VersionInfoVerticle.class.getSimpleName(), id))
        .flatMapCompletable(id -> migrateDatabase()
          .doOnComplete(() -> LOG.info("Migrated db schema to latest version! "))
        )
        .andThen(deployVertxRxWeb());
  }

  private Completable migrateDatabase() {
    return ConfigLoader.load(vertx)
      .flatMapCompletable(config -> {
        return FlywayMigration.migrate(vertx, config.getDbConfig())
        .doOnError(rs->LOG.error("Error FlywayMigration",rs));
      });
  }

  private Completable deployVertxRxWeb() {
    return vertx.rxDeployVerticle(RoutesManagement.class.getName(),
        new DeploymentOptions().setInstances(halfProcessors())
    )
    .doOnSuccess(id -> {
        LOG.info("Deployed {} with id {}", RoutesManagement.class.getSimpleName(), id);
    })
    .ignoreElement();
  }

  private int halfProcessors() {
    return 1; //I want to use one processor for now.
    //return Math.max(1, Runtime.getRuntime().availableProcessors() / 2);
  }

  /* 
  public static void main(String[] args) {
    var vertx = Vertx.vertx();

    vertx.rxDeployVerticle(new MainVerticle())
        .ignoreElement()
        .subscribe(
            () -> LOG.info("Deployed {} successfully", MainVerticle.class.getSimpleName()),
            err -> LOG.error("Failed to deploy_qui:", err)
        );
  }*/
  public static void main(String[] args) {
      System.out.println("starting main");
      var vertx = Vertx.vertx();
      ClusterManager mgr = new HazelcastClusterManager();
      VertxOptions options = new VertxOptions().setClusterManager(mgr);
      Vertx.rxClusteredVertx(options)
        .flatMap(vertx2 -> vertx2.rxDeployVerticle(new MainVerticle()))
        .ignoreElement()
        .subscribe(() -> LOG.info("Deployed successfully"), err -> LOG.error("Error: {}", err));
  }
}


    /**** NON RxJava ****/
    /* 
    System.setProperty(ConfigLoader.SERVER_PORT, "8303");
    ClusterManager mgr = new HazelcastClusterManager();
    VertxOptions options = new VertxOptions().setClusterManager(mgr);

    Vertx
      .clusteredVertx(options, cluster -> {
       if (cluster.succeeded()) {
           cluster.result().deployVerticle(new MainVerticle(), res -> {
               if(res.succeeded()){
                   LOG.info("Deployment id is: " + res.result());
               } else {
                   LOG.error("Deployment failed!");
               }
           });
       } else {
           LOG.error("Cluster up failed: " + cluster.cause());
       }
    });

    //var vertx = Vertx.vertx();
    //vertx.exceptionHandler(error -> 
    //  LOG.error("Unhandled: {}", error)
    //);
    //vertx.deployVerticle(new MainVerticle())
    //  .onFailure(err -> LOG.error("Failed to deploy: ", err))
    //  .onSuccess(id -> LOG.info("Deployed {} with id {}", MainVerticle.class.getSimpleName(), id));
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
}*/


