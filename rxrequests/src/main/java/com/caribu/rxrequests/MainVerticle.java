package com.caribu.rxrequests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class MainVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
    @Override
    public Completable rxStart() {
      System.out.println("Starting server");
      return vertx.createHttpServer()
        .requestHandler(req -> req.response().end("Hello World!"))
        .rxListen(10001)
        .ignoreElement();
    }

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
