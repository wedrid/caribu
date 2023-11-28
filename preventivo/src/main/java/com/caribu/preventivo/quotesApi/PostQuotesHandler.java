package com.caribu.preventivo.quotesApi;

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

public class PostQuotesHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(PostQuotesHandler.class);
  private final Pool db;

  public PostQuotesHandler(final Pool db) {
    this.db = db;
  }

  @Override
  public void handle(final RoutingContext context) {
    // Quotes quotes = new Quotes();

    RequestParameters params = context.get(ValidationHandler.REQUEST_CONTEXT_KEY); 
    JsonObject json = params.body().getJsonObject(); 
    Quotes quotes = json.mapTo(Quotes.class);
    System.out.println("Quotes:  " + quotes.toJsonObject());
    Map<String, Object> parameters = quotes.toJsonObject().getMap();

    parameters.put("olat", json.getValue("olat"));
    parameters.put("olon", json.getValue("olon"));
    parameters.put("dlat", json.getValue("dlat"));
    parameters.put("dlon", json.getValue("dlon"));
    System.out.println("parameters:  " + parameters);

    SqlTemplate.forUpdate(db,
        "INSERT INTO schema.quotes VALUES (#{id_quotes},  #{id_operativo}, #{lunghezza}, #{larghezza}, #{profondit\u00E0}, #{id_fornitore}, #{costo},"
            + " ST_SetSRID(ST_Point(#{olat}, #{olon}), 4326),ST_SetSRID(ST_Point(#{dlat}, #{dlon}), 4326))"
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

/* 
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
*/
