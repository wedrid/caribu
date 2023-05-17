package com.caribu.cliente;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class CompaniesRestApi {

    private static final Logger LOG = LoggerFactory.getLogger(CompaniesRestApi.class);
    public static final List<String> COMPANIES = Arrays.asList("pippo", "rossi", "pluto", "florex", "unifi", "rub");


    static void attach(Router restApi){
        restApi.get("/companies").handler(context -> {
            final JsonArray response = new JsonArray();
            COMPANIES.stream().map(Company::new).forEach(response::add);
            LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
            context.response().end(response.toBuffer());
          });
    }
    
}
