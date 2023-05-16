package com.caribu.cliente;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class RequestsRestApi {

    private static final Logger LOG = LoggerFactory.getLogger(RequestsRestApi.class);
    static void attach(Router restApi){
        restApi.get("/requests").handler(context -> {
            final JsonArray response = new JsonArray();
            response
              .add(new Request("pippo"))
              .add(new Request("pluto"))
              .add(new Request("mario"))
              .add(new Request("rossi"));
            LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
            context.response().end(response.toBuffer());
          });
    }
    
}
