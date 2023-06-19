package com.caribu.commessa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.HttpEndpoint;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class CommesseVerticle extends AbstractVerticle {
  public static final int PORT = 10000;
  private static final Logger LOG = LoggerFactory.getLogger(CommesseVerticle.class);
  private ServiceDiscovery discovery; // this should be moved to base class
  private HttpServer server; // this should be moved to base class

  final List<JsonObject> sampleData = new ArrayList<>(Arrays.asList(
    new JsonObject().put("id", 1).put("name", "Fufi").put("tag", "ABC"),
    new JsonObject().put("id", 2).put("name", "Garfield").put("tag", "XYZ"),
    new JsonObject().put("id", 3).put("name", "Puffa")
  ));

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    //super.start(startPromise);
    discovery = ServiceDiscovery.create(vertx);
    RouterBuilder.create(vertx, "/Users/edrid/Desktop/SWAM/caribu/commessa/src/main/resources/commesse.yaml")
      .onSuccess(handlergenerateRoutesAndStartHttpServer(startPromise));
    //in route builder I need to call the service discovery
  
  }

  private Handler<RouterBuilder> handlergenerateRoutesAndStartHttpServer(Promise<Void> startPromise){
    return routerBuilder -> {
      routerBuilder
          .operation("getAllRequests")
          .handler(context -> {
            LOG.info("Get all requests called");
            context
              .response() // <1>
              .setStatusCode(200)
              .putHeader(HttpHeaders.CONTENT_TYPE, "application/json") // <2>
              .end(new JsonArray(sampleData).encode()); // <3>
          });

      routerBuilder
        .operation("provaProxy")
        .handler(context -> {
          LOG.info("Provaproxy fatta");
          context
            .response() // <1>
            .setStatusCode(200)
            .putHeader(HttpHeaders.CONTENT_TYPE, "application/json") // <2>
            .end(new JsonArray(sampleData).encode()); // <3>
        });
      // route ..
      // route ..

      // TODO see if this works then,
      // create method that takes in name, host, port, root
      Router router = routerBuilder.createRouter();
      router.route().handler(handleFailure());
      server = vertx.createHttpServer()
        .requestHandler(router)
        .listen(PORT, http -> {
        if (http.succeeded()) {
          // Publish the HTTP endpoint to Service Discovery
          discovery.publish(
            HttpEndpoint.createRecord("commesse-api", "127.0.0.1", PORT, "/"),
            ar -> {
              if (ar.succeeded()) {
                startPromise.complete();
                LOG.info("HTTP server started on port {}", PORT);
                LOG.info("Service published on port {}", PORT);
              } else {
                startPromise.fail(ar.cause());
              }
            }
          );
        } else {
          startPromise.fail(http.cause());
        }
      });
      //startPromise.complete();
    };
  }

  /* 
  public static void main(String[] args) {
    MainVerticle mainVerticle = new MainVerticle();
    mainVerticle.setupClusteredVerticle(mainVerticle);
  }
  */

  public static void main(String[] args) {
    ClusterManager mgr = new HazelcastClusterManager();
    VertxOptions options = new VertxOptions().setClusterManager(mgr);
    Vertx
      .clusteredVertx(options, cluster -> {
       if (cluster.succeeded()) {
           cluster.result().deployVerticle(new CommesseVerticle(), res -> {
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