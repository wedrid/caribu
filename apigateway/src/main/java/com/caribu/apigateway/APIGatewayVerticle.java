package com.caribu.apigateway;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.httpproxy.HttpProxy;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class APIGatewayVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(APIGatewayVerticle.class);

  //protected ServiceDiscovery discovery;
  private HttpServer server;
  private HttpClient httpClient; 

  // per ora, HttpProxy così, poi forse hashmap, poi anche da fare ServiceDiscovery
  private HttpProxy proxy; 
  private HttpProxy requestProxy;

  ServiceDiscovery discovery;
  
  final List<JsonObject> pets = new ArrayList<>(Arrays.asList(
    new JsonObject().put("id", 1).put("name", "Fufi").put("tag", "ABC"),
    new JsonObject().put("id", 2).put("name", "Garfield").put("tag", "XYZ"),
    new JsonObject().put("id", 3).put("name", "Puffa")
  ));

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    // create the HTTP client for the reverse proxy
    
    httpClient = vertx.createHttpClient();
    proxy = HttpProxy.reverseProxy(httpClient);
    
    requestProxy = HttpProxy.reverseProxy(httpClient);

    discovery = ServiceDiscovery.create(vertx);

    RouterBuilder.create(this.vertx, "/Users/edrid/Desktop/SWAM/caribu/apigateway/src/main/resources/APIGateway.yaml") //TODO: change to relative path
      .onSuccess(routerBuilder -> {
        /* 
        routerBuilder
          .operation("getAllClients")
          .handler(
            context -> {
              String[] parts = context.normalizedPath().split("/", 3);
              String ms_name = parts[1];
              String endpoint_path = "/" + parts[2];
              discovery.getRecord(new JsonObject().put("name", ms_name)).onComplete(ar -> {
                if (ar.succeeded() && ar.result() != null) {
                  // Retrieve the service reference
                  ServiceReference reference = discovery.getReference(ar.result());
                  String address = ar.result().getLocation().getString("host");
                  int port = ar.result().getLocation().getInteger("port");
                  LOG.info("Service found at " + address + ":" + port);
                  
                  WebClient client = reference.getAs(WebClient.class);
                  //WebClient client = WebClient.create(vertx);

                  client.request(context.request().method(), endpoint_path)
                    .putHeaders(context.request().headers())
                    .send()
                    .onComplete(response -> {
                      if (response.succeeded()) {
                        LOG.info("The other verticle responds with: " + response.result().bodyAsString());
                        context.response()
                          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                          .end(response.result().bodyAsBuffer());
                      } else {
                        LOG.error("Request failed", response.cause());
                      }
                  });
                    
                  LOG.info("Found and retrieved service");
                  reference.release();
                } else {
                  LOG.info("Not finding the service");
                }
              });
            }
          );*/
        
        routerBuilder
            .operation("getAllClients")
            .handler(new DispatchRequestHandler(discovery));
          

        routerBuilder
            .operation("provaProxy")
            .handler(context -> {
              LOG.info("Trial with service discovery");
              discovery.getRecord(new JsonObject().put("name", "commesse-api")).onComplete(ar -> {
                if (ar.succeeded() && ar.result() != null) {
                  // Retrieve the service reference
                  ServiceReference reference = discovery.getReference(ar.result());
                  // Retrieve the service object
                  WebClient client = reference.getAs(WebClient.class);
                  // implement reverse proxy logic
                  reference.release();
                } else {
                  LOG.info("Not finding the service");
                }
              });
            });

        routerBuilder
          .operation("ping")
          .handler(context -> {
            LOG.info("Called ping :)");
            context
              .response() // <1>
              .setStatusCode(200)
              .putHeader(HttpHeaders.CONTENT_TYPE, "application/json") // <2>
              .end(new JsonArray(pets).encode()); // <3>
          });
          routerBuilder
            .operation("getAllRequests")
            .handler(context -> {
              LOG.info("Called getAllRequest");
              requestProxy.handle(context.request());
            });
          /* 
          routerBuilder
            .operation("provaProxy")
            .handler(context -> {
              provaProxy.origin(10000, "127.0.0.1");
              LOG.info("Called provaProxy");
              provaProxy.handle(context.request());
            }); 
          */
          
          routerBuilder
            .operation("getRequestsFromCommesseApi")
            .handler(context -> {
              LOG.info("Called getRequestsFromCommesseApi, looking for service");
              discovery.getRecord(new JsonObject().put("name", "commesse-api")).onComplete(ar -> {
                if (ar.succeeded() && ar.result() != null) {
                  // Retrieve the service reference
                  ServiceReference reference = discovery.getReference(ar.result());
                  String address = ar.result().getLocation().getString("host");
                  int port = ar.result().getLocation().getInteger("port");
                  LOG.info("Service found at " + address + ":" + port);
                  LOG.info("Forwarding request..");
                  //proxy.origin(port, address);
                  proxy.handle(context.request());
                  HttpServerRequest request = context.request();
                  LOG.info("HTTP method: " + request.method());
                  LOG.info("Request path: " + request.path());
                  LOG.info("Request headers: " + request.headers());
                  reference.release();
                // ######## previous attempt withouth proxy
                // You need to path the complete path
                // Note: "reqapi" should be the root
                /*WebClient client = reference.getAs(WebClient.class);
                client.get("/reqapi/requests").send().onComplete(
                    response -> {
                      if (response.succeeded()) {
                        LOG.info("The other verticle responds with: " + response.result().bodyAsString());
                        context.response()
                          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                          .end(response.result().bodyAsBuffer());
                      } else {
                        LOG.error("Request failed", response.cause());
                      }
                      reference.release();
                    }); */
                } else {
                  LOG.info("Not finding the service");
                }
              });
            });

          routerBuilder
            .operation("discoveryTrial")
            .handler(context -> {
              discovery.getRecord(new JsonObject().put("name", "requests-api")).onComplete(ar -> {
              if (ar.succeeded() && ar.result() != null) {
                // Retrieve the service reference
                ServiceReference reference = discovery.getReference(ar.result());
                // Retrieve the service object
                WebClient client = reference.getAs(WebClient.class);

                // You need to path the complete path
                client.get("/pg/requests").send().onComplete(
                  response -> {
                    if (response.succeeded()) {
                      LOG.info("The other verticle responds with: " + response.result().bodyAsString());
                      context.response()
                        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                        .end(response.result().bodyAsBuffer());
                    } else {
                      LOG.error("Request failed", response.cause());
                    }
                    reference.release();
                  });
              } else {
                LOG.info("Not finding the service");
              }
            });
            });
          // the other operations here
          //...


          // generate the router
          Router router = routerBuilder.createRouter();
          // we can setup here the error handler like so: 
          // router.errorHandler(400, rc -> { ... });
          // clearly, we can create a new class and also add multiple error handlers
          // see https://github.dev/vertx-howtos/web-and-openapi-howto @ line 93

          // create the HTTP server
          server = vertx.createHttpServer(new HttpServerOptions().setPort(10000).setHost("localhost"));
          server.requestHandler(router).listen();
          startPromise.complete();
      })
      .onFailure(startPromise::fail);
      
  }

  private Handler<RoutingContext> dispatchRequest() {
    return context -> {
      discovery.getRecord(new JsonObject().put("name", "requests-api")).onComplete(ar -> {
        if (ar.succeeded() && ar.result() != null) {
          // Retrieve the service reference
          ServiceReference reference = discovery.getReference(ar.result());
          // Retrieve the service object
          WebClient client = reference.getAs(WebClient.class);

          // You need to path the complete path
          client.get("/pg/requests").send().onComplete(
            response -> {
              if (response.succeeded()) {
                LOG.info("Response: " + response.result().bodyAsString());
              } else {
                LOG.error("Request failed", response.cause());
              }
              reference.release();
            });
        } else {
          LOG.info("Not finding the service");
        }
      });
    };
  }


  public static void main(String[] args) {
    ClusterManager mgr = new HazelcastClusterManager();
    VertxOptions options = new VertxOptions().setClusterManager(mgr);
    Vertx
      .clusteredVertx(options, cluster -> {
       if (cluster.succeeded()) {
           cluster.result().deployVerticle(new APIGatewayVerticle(), res -> {
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
    

   /*   Non clustered version
    *     Vertx vertx = Vertx.vertx();
    *     vertx.deployVerticle(new APIGatewayVerticle());
    */

  }
}
