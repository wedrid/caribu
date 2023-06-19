package com.caribu.commessa;

import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.Promise;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.rxjava3.RxHelper;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.core.http.HttpServer;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;


public class RxCommesseVerticle extends AbstractVerticle{
    private static final Logger LOG = LoggerFactory.getLogger(RxCommesseVerticle.class);
    
    @Override
    public Completable rxStart() {

        LOG.info("Trying to deploy verticle");
        Single<HttpServer> single = vertx.createHttpServer()
            .requestHandler(req -> req.response().end("Hello World!"))
            .rxListen(9000);
        return Completable.complete();
    }




    public static void main(String[] args) {
        var vertx = Vertx.vertx();
        LOG.info("Hello");
        vertx.deployVerticle(new RxCommesseVerticle());
    }

}
