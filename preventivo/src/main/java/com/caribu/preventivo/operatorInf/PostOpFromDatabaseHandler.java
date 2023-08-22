package com.caribu.preventivo.operatorInf;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.validation.RequestParameters;
import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.rxjava3.ext.web.validation.ValidationHandler;
import io.vertx.rxjava3.sqlclient.Pool;
import io.vertx.rxjava3.sqlclient.templates.SqlTemplate;

public class PostOpFromDatabaseHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(PostOpFromDatabaseHandler.class);
  private final Pool db;

  public PostOpFromDatabaseHandler(final Pool db) {
    this.db = db;
  }

  @Override
  public void handle(final RoutingContext context) {
  
    RequestParameters params = context.get(ValidationHandler.REQUEST_CONTEXT_KEY); // (1)
    JsonObject json = params.body().getJsonObject(); // (2)
    System.out.println("json:  " + params.body());
    
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("id_quotes", json.getValue("id_quotes"));
    parameters.put("oLat", json.getValue("olat"));
    parameters.put("oLon", json.getValue("olon"));
    parameters.put("dLat", json.getValue("dlat"));
    parameters.put("dLon", json.getValue("dlon"));
    parameters.put("lunghezza", json.getValue("lunghezza"));
    parameters.put("profondita", json.getValue("profondita"));
    parameters.put("larghezza", json.getValue("larghezza"));
    parameters.put("id_fornitore", json.getValue("id_fornitore"));
    parameters.put("costo", json.getValue("costo"));
    parameters.put("operativo", json.getValue("operativo"));
    System.out.println("PARAMETERS" + parameters);
  

    SqlTemplate.forUpdate(db,
        "INSERT INTO schema.quotes VALUES (#{id_quotes},  #{operativo}, #{lunghezza}, #{larghezza}, #{profondit\u00E0}, #{id_fornitore}, #{costo},"
        + " ST_SetSRID(ST_Point(#{oLat}, #{oLon}), 4326),ST_SetSRID(ST_Point(#{dLat}, #{dLon}), 4326))"
        + " ON CONFLICT (id_quotes) DO NOTHING")
        .rxExecute(parameters)
        .doOnError(err -> {
          LOG.debug("Failure: ", err, err.getMessage());
        })
        .doOnSuccess(result -> {

          LOG.info("Add quotes: ??????????,", result);
          // if(context.response().ended()){
          context.response()
              .setStatusCode(200)
              .end("Element added successfully");
          // }
        }).subscribe();
  }
}
