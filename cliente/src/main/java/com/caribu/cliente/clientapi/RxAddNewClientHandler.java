package com.caribu.cliente.clientapi;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.http.HttpHeaders;
import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.rxjava3.sqlclient.Pool;
import io.vertx.rxjava3.sqlclient.templates.SqlTemplate;

public class RxAddNewClientHandler implements Handler<RoutingContext>{
    private static final Logger LOG = LoggerFactory.getLogger(RxAddNewClientHandler.class);
    private final Pool db;

    public RxAddNewClientHandler(Pool db) {
        this.db = db;
    }

    /*
     * -- Create the Clients table
        CREATE TABLE clients (
        client_id SERIAL PRIMARY KEY,
        ragione_sociale VARCHAR(255),
        date_added TIMESTAMP
        );
     * 
     */


    @Override
    public void handle(RoutingContext context) {
        JsonObject requestBody = context.body().asJsonObject();
        //int prova = requestBody.getInteger("ragione_sociale");
        var companyName = requestBody.getString("company_name");
        LOG.info("Company name is {}", companyName); 
                Map<String, Object> parameters = new HashMap<>();
                    parameters.put("company_name", companyName);
            SqlTemplate.forUpdate(db,
                "INSERT INTO client.clients (ragione_sociale, created_at) VALUES (#{company_name}, current_date)")
            .rxExecute(parameters)
            .doOnError(err -> {
                LOG.debug("Failure: ", err , err.getMessage());
                context.response()
                    .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                    .setStatusCode(500)
                    .end(new JsonObject().put("error", err.getMessage()).encode());
            })
            .doOnSuccess(result -> {
                //TODO errore nome sbagliato
                LOG.info("Added client {} ", companyName);
                context.response()
                    .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                    .end(new JsonObject().put("message", "ok").encode());
                }).subscribe();
                
    }

}
