package com.caribu.cliente.clientapi;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.http.HttpHeaders;
import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.rxjava3.sqlclient.Pool;
import io.vertx.rxjava3.sqlclient.templates.SqlTemplate;

public class RxCreateNewRequestHandler implements Handler<RoutingContext>{
    private static final Logger LOG = LoggerFactory.getLogger(RxCreateNewRequestHandler.class);
    private final Pool db;

    public RxCreateNewRequestHandler(Pool db) {
        this.db = db;
    }


    /*
        request_id SERIAL PRIMARY KEY,
        client_id SERIAL,
        request_date NUMERIC,
        filiale_id NUMERIC,
        depth NUMERIC,
        width NUMERIC,
        height NUMERIC,
        weight NUMERIC,
        tratta_id NUMERIC,
        due_date TIMESTAMP,
        created_at TIMESTAMP,
        FOREIGN KEY (client_id) REFERENCES Clients(client_id)
     */

    @Override
    public void handle(RoutingContext context) {
        JsonObject requestBody = context.body().asJsonObject();
        
        var reqDate = requestBody.getString("request_date");
        String dateTimeString = reqDate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);

        Map<String, Object> parameters = new HashMap<>();
        // TODO: add operational id and 
        /* Probabilmente overkill, ma permette di fare il check del tipo dei dati */
        parameters.put("client_id", requestBody.getInteger("client_id"));
        parameters.put("request_date", dateTime);
        parameters.put("filiale_id", requestBody.getInteger("filiale_id"));
        parameters.put("origin_lat", requestBody.getDouble("origin_lat"));
        parameters.put("origin_long", requestBody.getDouble("origin_long"));
        parameters.put("destination_lat", requestBody.getDouble("destination_lat"));
        parameters.put("destination_long", requestBody.getDouble("destination_long"));
        parameters.put("depth", requestBody.getInteger("depth"));
        parameters.put("width", requestBody.getInteger("width"));
        parameters.put("height", requestBody.getInteger("height"));
        parameters.put("weight", requestBody.getInteger("weight"));
        parameters.put("tratta_id", requestBody.getInteger("tratta_id")); //TODO change to origin destination
        parameters.put("due_date", LocalDateTime.parse(requestBody.getString("due_date"), formatter));
        parameters.put("status", requestBody.getString("status"));
        
        SqlTemplate.forUpdate(db,
            "INSERT INTO client.requests (client_id, request_date, origin, destination, filiale_id, depth, height, width, weight, due_date, created_at, status) VALUES (#{client_id}, #{request_date}, point(#{origin_lat},#{origin_long}), point(#{destination_lat},#{destination_long}), #{filiale_id}, #{depth}, #{height}, #{width}, #{weight}, #{due_date}, current_date, #{status})")
        .rxExecute(parameters)
        .doOnError(err -> {
            LOG.debug("Failure: ", err , err.getMessage());
            context.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                .setStatusCode(500)
                .end(new JsonObject().put("error", err.getMessage()).encode());
        })
        .doOnSuccess(result -> {
            LOG.info("Success, added request for client of id: {}", parameters.get("client_id"));
            //TODO: as a request is created, forward request to the preventivi microservice.
            context.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                .end(new JsonObject().put("message", "ok").encode());
            }).subscribe();
    }

}
