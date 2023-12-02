package com.caribu.preventivo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caribu.preventivo.config.ConfigLoader;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;


public class MainVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  public static void main(String[] args) {
    // TODO: add support for Hazelcust cluster manager with TCP/IP
    System.out.println("starting main");
    var vertx = Vertx.vertx();
    ClusterManager mgr = new HazelcastClusterManager();
    VertxOptions options = new VertxOptions().setClusterManager(mgr);
    Vertx.rxClusteredVertx(options)
      .flatMap(vertx2 -> vertx2.rxDeployVerticle(new MainVerticle()))
      .ignoreElement()
      .subscribe(() -> LOG.info("Deployed successfully"), err -> LOG.error("Error: {}", err));


    //----
    /*var vertx = Vertx.vertx();
    vertx.rxDeployVerticle(new MainVerticle())
        .ignoreElement()
        .subscribe(
            () -> LOG.info("Deployed {} successfully", MainVerticle.class.getSimpleName()),
            err -> LOG.error("Failed to deploy_qui:", err)
        );*/
  }
   
  @Override
  public Completable rxStart() {
    // Deploys the version of the verticle that we want. Version is specified 
    return vertx.rxDeployVerticle(VersionInfoVerticle.class.getName())
        .doOnSuccess(id -> LOG.info("Deployed {} with id {}", VersionInfoVerticle.class.getSimpleName(), id))
        .flatMapCompletable(id -> migrateDatabase()
          .doOnComplete(() -> LOG.info("Migrated db schema to latest version! ")) // ask flyway to do migration of the database if necessary. Migrations are in src/main/resources/db/migrations
        )
        .andThen(deployVertxRxWeb()); //deploy the web verticle.
  }

  private Completable migrateDatabase() {
    return ConfigLoader.load(vertx)
      .flatMapCompletable(config -> {
        return FlywayMigration.migrate(vertx, config.getDbConfig())
        .doOnError(rs->LOG.error("Error FlywayMigration",rs));
      });
  }

  private Completable deployVertxRxWeb() {
    return vertx.rxDeployVerticle(VertxRxWeb.class.getName(),
        new DeploymentOptions().setInstances(halfProcessors()) //set number of processors that the process will use.
    )
    .doOnSuccess(id -> {
        LOG.info("Deployed {} with id {}", VertxRxWeb.class.getSimpleName(), id);
    })
    .ignoreElement();
  }

  private int halfProcessors() {
    return Math.max(1, Runtime.getRuntime().availableProcessors() / 2);
  }
}

