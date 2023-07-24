package com.caribu.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;

public class DispatchRequestHandler implements Handler<RoutingContext>{
    private static final Logger LOG = LoggerFactory.getLogger(DispatchRequestHandler.class);
    private ServiceDiscovery discovery;

    public DispatchRequestHandler(ServiceDiscovery discovery) {
        this.discovery = discovery;
    }

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
            
    }

}
