package com.caribu.cliente.clientapi;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;

public class CompaniesRestApi {

    private static final Logger LOG = LoggerFactory.getLogger(CompaniesRestApi.class);
    public static final List<Client> CLIENTS = Arrays.asList( //TODO: remove when refactoring 
        new Client("pippo"),
        new Client("rossi"),
        new Client("pluto"),
        new Client("florex"),
        new Client("unifi"),
        new Client("rub")
    );


    public static void attach(Router parent, final Pool db){
        final String path = "/client/:clientId";
        parent.get("/client").handler(new GetAllClientsHandler());
        parent.get(path).handler(new GetClientsHandler());

        parent.get("/pg/clients").handler(new GetClientsFromDatabaseHandler(db));
        parent.get("/pg/client/:clientId").handler(new GetClientDetailsFromDatabaseHandler(db));
        //parent.put("/pg/client/:clientId").handler(new PutClientDetailsFromDatabaseHandler(db));

        //parent.put(path).handler(new PutClientsHandler());
        //parent.delete(path).handler(new DeleteClientsHandler());
    }

    static String getClientId(final RoutingContext context) {
        var clientId = context.pathParam("clientId");
        LOG.debug("{} for account {}", context.normalizedPath(), clientId);
        return clientId;
    }
    
}
