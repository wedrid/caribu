package com.caribu.cliente.clientapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PutClientDetailsFromDatabaseHandler implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(PutClientDetailsFromDatabaseHandler.class);
    private final Pool db;
    public PutClientDetailsFromDatabaseHandler(final Pool db) {
        this.db = db;
    }

    @Override
    public void handle(final RoutingContext context){
        var clientId = CompaniesRestApi.getClientId(context);
        var body = context.body();
    }
}

