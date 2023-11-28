package com.caribu.filiale;
import com.caribu.filiale.model.Operator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
  public static final int PORT = 10001;
  public static final int PROCESSORS = 1; //Runtime.getRuntime().availableProcessors();
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    //TODO: check correctness
    LOG.info("Starting {} with {} processors", MainVerticle.class.getSimpleName(), PROCESSORS);
     deployRestApiVerticle(startPromise);
  }

  private Future<String> deployRestApiVerticle(Promise<Void> startPromise) {
    return vertx.deployVerticle(RestApiVerticle.class.getName(),
    new DeploymentOptions().setInstances(PROCESSORS))
      .onFailure(startPromise::fail)
      .onSuccess(id -> {
        LOG.info("Deployed {} with id {}", RestApiVerticle.class.getSimpleName(), id);
        startPromise.complete();
      });
  }

  public static void main(String[] args) {
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
  }
}