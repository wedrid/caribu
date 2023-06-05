package com.caribu.apigateway;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.openapi.RouterBuilder;

public class APIGatewayVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(APIGatewayVerticle.class);

  //protected ServiceDiscovery discovery;
  private HttpServer server;
  final List<JsonObject> pets = new ArrayList<>(Arrays.asList(
    new JsonObject().put("id", 1).put("name", "Fufi").put("tag", "ABC"),
    new JsonObject().put("id", 2).put("name", "Garfield").put("tag", "XYZ"),
    new JsonObject().put("id", 3).put("name", "Puffa")
  ));

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    RouterBuilder.create(this.vertx, "/Users/edrid/Desktop/SWAM/caribu/apigateway/src/main/resources/APIGateway.yaml") //TODO: change to relative path
      .onSuccess(routerBuilder -> {
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
          routerBuilder.operation();

          // the other operations here
          //...

          // generate the router
          Router router = routerBuilder.createRouter();
          // we can setup here the error handler like so: 
          // router.errorHandler(400, rc -> { ... });
          // clearly, we can create a new class and also add multiple error handlers
          // see https://github.dev/vertx-howtos/web-and-openapi-howto @ line 93

          // create the HTTP server
          server = vertx.createHttpServer(new HttpServerOptions().setPort(8888).setHost("localhost"));
          server.requestHandler(router).listen();
          startPromise.complete();
      })
      .onFailure(startPromise::fail);
      
  }


  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new APIGatewayVerticle());
  }
}
