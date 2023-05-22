package com.caribu.cliente.clientapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caribu.cliente.db.DbResponse;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;

public class GetClientsFromDatabaseHandler implements Handler<RoutingContext>{

    private static final Logger LOG = LoggerFactory.getLogger(GetClientsFromDatabaseHandler.class);
    private final Pool db;

    public GetClientsFromDatabaseHandler(final Pool db){
        this.db = db;
    }

    @Override
    public void handle(RoutingContext context) {
        db.query("SELECT c.ragione_sociale FROM client.clients c")
            .execute()
            .onFailure(DbResponse.errorHandler(context, "failed to get assets from db"))
            .onSuccess(result -> {
                
                var response = new JsonArray();
                result.forEach( row -> {
                    response.add(row.getValue("ragione_sociale"));
                });
                LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
                context.response()
                    .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                    .end(response.toBuffer());
            });
        
    }

    
}
