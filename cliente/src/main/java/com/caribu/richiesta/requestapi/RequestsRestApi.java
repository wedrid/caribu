package com.caribu.richiesta.requestapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caribu.cliente.clientapi.GetClientsFromDatabaseHandler;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.sqlclient.Pool;

public class RequestsRestApi {
    private static final Logger LOG = LoggerFactory.getLogger(RequestsRestApi.class);
    public static void attach(Router parent, final Pool db){
        parent.route().handler(BodyHandler.create());
        parent.get("/pg/requests").handler(new GetRequestsFromDatabaseHandler(db));
        parent.put("/pg/request/:clientId").handler(new PutRequestDbHandler(db)); 
    }
}
