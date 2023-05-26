package com.caribu.richiesta.requestapi;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caribu.cliente.db.DbResponse;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;

public class PutRequestDbHandler implements Handler<RoutingContext>{
    private static final Logger LOG = LoggerFactory.getLogger(PutRequestDbHandler.class);
    private final Pool db;

    public PutRequestDbHandler(final Pool db){
        this.db = db;
    }
    @Override
    public void handle(RoutingContext context) {
        var clientId = context.pathParam("clientId");
        
        var jsonBody = context.body().asJsonObject();
        var request = jsonBody.mapTo(Request.class);
        LOG.info("Received request: {}", request);
        var params = request.getHashMap();
        
        SqlTemplate.forUpdate(db, "INSERT INTO client.requests (client_id, request_date, filiale_id, depth, width, height, weight, tratta_id) VALUES (#{client_id}, #{request_date}, #{filiale_id}, #{depth}, #{width}, #{height}, #{weight}, #{tratta_id})")
            .execute(params)
            .onFailure(DbResponse.errorHandler(context, "Failed to insert request into database"))
            .onSuccess(result -> {
                if(!context.response().ended()){
                    context.response()
                        .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
                        .end();
                }
            });
        
        
    }

}