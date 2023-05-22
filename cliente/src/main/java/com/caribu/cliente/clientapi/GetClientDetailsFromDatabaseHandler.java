package com.caribu.cliente.clientapi;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caribu.cliente.db.DbResponse;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;

public class GetClientDetailsFromDatabaseHandler implements Handler<RoutingContext>{
    private static final Logger LOG = LoggerFactory.getLogger(GetClientDetailsFromDatabaseHandler.class);
    private final Pool db;
    public GetClientDetailsFromDatabaseHandler(final Pool db){
        this.db = db;
    }

    @Override
    public void handle(RoutingContext context) {
        final String clientParam = context.pathParam("clientId");
        Number clientId = Integer.parseInt(clientParam); // convert to Number
        LOG.info("Client id parameter: {}", clientParam);
        
        // forQuery should be used when we expect a return parameter
        SqlTemplate
            .forQuery(db, "SELECT c.client_id, c.ragione_sociale, c.date_added from client.clients c where c.client_id=#{clientId}")
            .mapTo(Client.class)
            .execute(Collections.singletonMap("clientId", clientId))
            .onFailure(DbResponse.errorHandler(context, "Failed to get client " + clientParam  + " from db"))
            .onSuccess(clientDetails -> {
        if(!clientDetails.iterator().hasNext()){
            //No entry
            DbResponse.notFound(context, "Client " + clientParam + " not found");
            //LOG.info(clientDetails.iterator().next().toJsonObject().toString());
            return;
        }
        var response = clientDetails.iterator().next().toJsonObject();
        LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
        context.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
            .end(response.toBuffer());
    });

        
    }
}
