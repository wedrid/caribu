package com.caribu.preventivo.quotesApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.validation.RequestParameters;
import io.vertx.rxjava3.core.eventbus.Message;
import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.rxjava3.ext.web.validation.ValidationHandler;
import io.vertx.rxjava3.sqlclient.templates.SqlTemplate;
import io.vertx.rxjava3.sqlclient.Pool;
import io.vertx.rxjava3.sqlclient.Row;
import io.vertx.rxjava3.sqlclient.RowSet;
import io.vertx.rxjava3.sqlclient.Tuple;

public class AddQuotesCache_mess implements Handler<Message<Object>> {

  private static final Logger LOG = LoggerFactory.getLogger(AddQuotesCache_mess.class);
  private final Pool db;

  public AddQuotesCache_mess(final Pool db) {
    this.db = db;
  }

  @Override
  public void handle(Message<Object> message) {
    // Ho una certa tratta e seleziono tutti i preventivi che
    // potrebbero andare bene
    System.out.println("Cache Event");
    // in input ho un json
    JsonObject json = (JsonObject) message.body();
    System.out.println("json:  " + message.body());

    Map<String, Object> parameters = new HashMap<>();
    parameters.put("id_tratta", json.getValue("id_tratta"));
    parameters.put("oLat", json.getValue("origLat"));
    parameters.put("oLon", json.getValue("origLon"));
    parameters.put("dLat", json.getValue("destLat"));
    parameters.put("dLon", json.getValue("destLon"));

    System.out.println("PARAMETERS" + parameters);

    String input_Ogeo = "ST_SetSRID(ST_MakePoint(#{oLon} , #{oLat}), 4326)";
    String input_Dgeo = "ST_SetSRID(ST_MakePoint(#{dLon} , #{dLat}), 4326)";
    String distance = "ST_DistanceSphere(" + input_Ogeo + "," + input_Dgeo + ")";

    String query = "SELECT *, ST_DistanceSphere(o.origin_geom, o.destination_geom) as dist from schema.quotes o where (ST_DistanceSphere(o.origin_geom, o.destination_geom) BETWEEN "
        + distance + "-10000 AND " + distance + "+10000)";

    // Trovo valori con una distanza simile a distanza dell atratta
    SqlTemplate.forQuery(db, query)
        .rxExecute(parameters)
        .doOnError(err -> {
          LOG.debug("Failure: ", err, err.getMessage());
        })
        .doOnSuccess(result -> {
          LOG.info("Got " + result.size() + " rows ");

          result.forEach(row -> {

            Quotes quotes = row.toJson().mapTo(Quotes.class);
            System.out.println("Quotes:  " + quotes.toJsonObject());
            Map<String, Object> parameters_ins = quotes.toJsonObject().getMap();

            parameters_ins.put("id_tratta", parameters.get("id_tratta"));
            // inserisco valori nella cache
            insertValue(parameters_ins);
          });

        }).subscribe();

  }

  //subquery
  private void insertValue(Map<String, Object> parameters_ins) {
    SqlTemplate.forUpdate(db,
        "INSERT INTO schema.cache VALUES (#{id_tratta},#{id_quotes}, #{id_operativo}, #{lunghezza}, #{larghezza}, #{profondit\u00E0}, #{id_fornitore}, #{costo},"
            +" #{destination_geom}, #{origin_geom})" +" ON CONFLICT (id_tratta, id_quotes) DO NOTHING")
        .rxExecute(parameters_ins)
        .doOnError(err -> {
          LOG.debug("Failure: ", err, err.getMessage());
        })
        .doOnSuccess(result -> {

          LOG.info("Add quotes in chache: {}", result);
        }).subscribe();
  }
}

/*
 * 
 * Map<String, Object> parameters_ins = new HashMap<>();
 * parameters_ins.put("id_quotes", row.getValue("id_quotes"));
 * parameters_ins.put("destination_geom", row.getValue("destination_geom"));
 * parameters_ins.put("origin_geom", row.getValue("origin_geom"));
 * parameters_ins.put("lunghezza", row.getValue("lunghezza"));
 * parameters_ins.put("profondità", row.getValue("profondità"));
 * parameters_ins.put("larghezza", row.getValue("larghezza"));
 * parameters_ins.put("id_fornitore", row.getValue("id_fornitore"));
 * parameters_ins.put("costo", row.getValue("costo"));
 * parameters_ins.put("operativo", row.getValue("operativo"));
 */