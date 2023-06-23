package com.caribu.cliente.clientapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.http.HttpHeaders;
import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.rxjava3.sqlclient.Pool;

public class RxGetAllOpenRequestsHandler implements Handler<RoutingContext>{
    private static final Logger LOG = LoggerFactory.getLogger(RxGetAllOpenRequestsHandler.class);
    private final Pool db;

    public RxGetAllOpenRequestsHandler(final Pool db) {
        this.db = db;
    }

    @Override
    public void handle(final RoutingContext context) {
        int user_id = Integer.parseInt(context.request().getHeader("operator_id"));
        LOG.info("User id is {}", user_id);
        db.query("SELECT r.request_id FROM client.requests r WHERE r.status = 'active'")
            .rxExecute()
            .doOnError(err -> {
                LOG.debug("Failure: ", err , err.getMessage());
                context.response()
                    .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                    .setStatusCode(500)
                    .end(new JsonObject().put("error", err.getMessage()).encode());
            })
            .doOnSuccess(result -> {
                LOG.info("Got " + result.size() + " rows ");
                JsonArray response = new JsonArray();
                result.forEach(row -> {
                    //response.add(row.getValue("ragione_sociale"));
                });
                    LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
                    context.response()
                        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                        .end(response.encode());
                }).subscribe();
        
        }
    
}
