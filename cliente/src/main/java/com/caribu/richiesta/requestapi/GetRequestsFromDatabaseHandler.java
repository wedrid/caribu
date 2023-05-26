package com.caribu.richiesta.requestapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caribu.cliente.db.DbResponse;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;

public class GetRequestsFromDatabaseHandler implements Handler<RoutingContext>{
    private static final Logger LOG = LoggerFactory.getLogger(GetRequestsFromDatabaseHandler.class);
    private final Pool db;
    public GetRequestsFromDatabaseHandler(final Pool db){
        this.db = db;
    }

    @Override
    public void handle(RoutingContext context) {
        db.query("SELECT * FROM client.requests r")
            .execute()
            .onFailure(DbResponse.errorHandler(context, "failed to get assets from db"))
            .onSuccess(result -> {
                var response = new JsonArray();
                result.forEach(row -> {
                    JsonObject request = new JsonObject();
                    request.put("request_id", row.getValue("request_id"));
                    request.put("client_id", row.getValue("client_id"));
                    request.put("request_date", row.getValue("request_date"));
                    request.put("filiale_id", row.getValue("filiale_id"));
                    request.put("depth", row.getValue("depth"));
                    response.add(request);
                });
                LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
                context.response()
                    .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                    .end(response.toBuffer());
            });
    }

}
