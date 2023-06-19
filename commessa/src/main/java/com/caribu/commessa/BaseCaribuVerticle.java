package com.caribu.commessa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.web.RoutingContext;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public abstract class BaseCaribuVerticle extends AbstractVerticle{
    private static final Logger LOG = LoggerFactory.getLogger(BaseCaribuVerticle.class);
    protected ServiceDiscovery discovery; 
    protected HttpServer server;
    

    public void setupClusteredVerticle(AbstractVerticle verticle){
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
        discovery = ServiceDiscovery.create(vertx);
    }

    
}
