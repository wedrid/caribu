package com.caribu.cliente.clientapi;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class GetAllClientsHandler implements Handler<RoutingContext>{
    private static final Logger LOG = LoggerFactory.getLogger(GetAllClientsHandler.class);

    public GetAllClientsHandler() {
        // here there will be the database reference
    }
    @Override
    public void handle(final RoutingContext context) {
        var allClients = CompaniesRestApi.CLIENTS;
        //var watchList = Optional.ofNullable(watchListPerAccount.get(UUID.fromString(accountId)));
        if (allClients == null) { //to change 
        context.response()
            .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
            .end(new JsonObject()
            .put("message", "All accounts not available!")
            .put("path", context.normalizedPath())
            .toBuffer()
            );
        return;
        }
        JsonArray clientsJsonArray = new JsonArray();
        for (Client client : allClients) {
            JsonObject jsonObject = client.toJsonObject();
            clientsJsonArray.add(jsonObject);
        }
        
        LOG.info("Path {} responds with json array", context.normalizedPath());
        context.response().end(clientsJsonArray.toBuffer());
    }

}
