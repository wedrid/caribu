package com.caribu.cliente.clientapi;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class GetClientsHandler implements Handler<RoutingContext>{
    private static final Logger LOG = LoggerFactory.getLogger(GetClientsHandler.class);

    public GetClientsHandler() {
        // here there will be the database reference
    }
    @Override
    public void handle(final RoutingContext context) {
        var clientId = CompaniesRestApi.getClientId(context);
        LOG.debug("{} for account {}", context.normalizedPath(), clientId);
        //var watchList = Optional.ofNullable(watchListPerAccount.get(UUID.fromString(accountId)));
        if (clientId == null) { //to change 
        context.response()
            .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
            .end(new JsonObject()
            .put("message", "watchlist for account " + clientId + " not available!")
            .put("path", context.normalizedPath())
            .toBuffer()
            );
        return;
        }
        JsonObject randomJsonObject = new JsonObject()
            .put("name", "John")
            .put("age", 30)
            .put("city", "New York");
        LOG.info("Path {} responds with {}", context.normalizedPath(), randomJsonObject.encode());
        context.response().end(randomJsonObject.toBuffer());
    }

}
