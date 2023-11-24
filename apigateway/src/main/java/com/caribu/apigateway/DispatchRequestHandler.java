package com.caribu.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;

public class DispatchRequestHandler implements Handler<RoutingContext>{
    private static final Logger LOG = LoggerFactory.getLogger(DispatchRequestHandler.class);
    private ServiceDiscovery discovery;
    //Add circuit breaker
    private CircuitBreaker breaker;



    public DispatchRequestHandler(ServiceDiscovery discovery, Vertx vertx) {

      this.discovery = discovery;
      breaker = CircuitBreaker.create("circuit-breaker", vertx,
        new CircuitBreakerOptions()
            .setMaxFailures(3) // number of failure before opening the circuit
            .setTimeout(2000) // consider a failure if the operation does not succeed in time
            .setFallbackOnFailure(false) // do we call the fallback on failure
            .setResetTimeout(20000) // time spent in open state before attempting to re-try
    );

    }

    //Following handler is with circuit breaker
    @Override
    public void handle(RoutingContext context) {
              String[] parts = context.normalizedPath().split("/", 3);
              String ms_name = parts[1];
              String endpoint_path = "/" + parts[2];
              JsonObject recordInformation = new JsonObject();
              recordInformation.put("name", ms_name);

              breaker.executeWithFallback(promise -> {
                discovery.getRecord(recordInformation)
                  .onComplete(ar -> {
                    if (ar.succeeded() && ar.result() != null) {
                      // Retrieve the service reference
                      System.out.println("Found record " + recordInformation.getString("name"));
                      ServiceReference reference = discovery.getReference(ar.result());
                      LOG.info("Found and retrieved service");
                      String address = ar.result().getLocation().getString("host");
                      int port = ar.result().getLocation().getInteger("port");
                      LOG.info("Service found at " + address + ":" + port);
                      
                      WebClient client = reference.getAs(WebClient.class);

                      client.request(context.request().method(), endpoint_path)
                        .putHeaders(context.request().headers())
                        .send()
                        .onComplete(response -> {
                          if (response.succeeded()) {
                            LOG.info("The other verticle responds with: " + response.result().bodyAsString());
                            response.result().headers().forEach(header -> {
                              context.response().putHeader(header.getKey(), header.getValue());
                            });
                            context.response()
                              //.putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON) //TODO: put headers that get with the response
                              .end(response.result().bodyAsBuffer());
                            promise.complete(endpoint_path);
                          } else {
                            LOG.error("Request failed", response.cause());
                            promise.fail(response.cause());
                          }
                      }); 
                      reference.release();
                    } else {
                      System.out.println("Record not found" + recordInformation.getString("name"));
                      promise.fail("Not finding the service");
                    }
              });
            }, throwable -> {
              // Fallback
              LOG.error("Circuit breaker open", throwable);
              context.response()
                .setStatusCode(503)
                .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                .end(new JsonObject().put("message", "Circuit breaker open").toBuffer());
              return "Here's a fallback";
            })
            .onComplete(ar -> {
              if (ar.succeeded()) {
                  // Handle the successful result of the command or fallback
                  System.out.println("Result: " + ar.result());
              } else {
                  // Handle the failure of the command or fallback
                  System.out.println("Error: " + ar.cause().getMessage());
              }
          });

              
    }

    /* Same handle but without circuit breaker (the original)
    @Override
    public void handle(RoutingContext context) {
              String[] parts = context.normalizedPath().split("/", 3);
              String ms_name = parts[1];
              String endpoint_path = "/" + parts[2];
              discovery.getRecord(new JsonObject().put("name", ms_name)).onComplete(ar -> {
                if (ar.succeeded() && ar.result() != null) {
                  // Retrieve the service reference
                  ServiceReference reference = discovery.getReference(ar.result());
                  LOG.info("Found and retrieved service");
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
                        response.result().headers().forEach(header -> {
                          context.response().putHeader(header.getKey(), header.getValue());
                        });
                        context.response()
                          //.putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON) //TODO: put headers that get with the response
                          .end(response.result().bodyAsBuffer());
                      } else {
                        LOG.error("Request failed", response.cause());
                      }
                  }); 
                  
                  reference.release();
                } else {
                  LOG.info("Not finding the service");
                }
              });
    } */
}
