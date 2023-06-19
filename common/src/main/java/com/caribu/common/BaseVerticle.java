package com.caribu.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class BaseVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(BaseVerticle.class);


  public void deployClusteredVerticle(AbstractVerticle verticle){
    ClusterManager mgr = new HazelcastClusterManager();
    VertxOptions options = new VertxOptions().setClusterManager(mgr);
    Vertx
      .clusteredVertx(options, cluster -> {
       if (cluster.succeeded()) {
           cluster.result().deployVerticle(verticle, res -> {
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

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    
  }


}
