package com.caribu.preventivo.operatorInf;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caribu.preventivo.eliminare.WatchList;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.validation.RequestParameters;
import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.rxjava3.ext.web.validation.ValidationHandler;
import io.vertx.rxjava3.sqlclient.templates.SqlTemplate;
import io.vertx.rxjava3.sqlclient.Pool;


public class PostOpFromDatabaseHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(PostOpFromDatabaseHandler.class);
  private final Pool db;

  public PostOpFromDatabaseHandler(final Pool db) {
    this.db = db;
  }

  @Override
  public void handle(final RoutingContext context) {
   // var nameop = OpRestApi.getAccountId(context);

    //var json = context.body().asJsonObject();
    //var watchList = json.mapTo(WatchList.class);
                  //AGGIUNGERE
    //             operativo VARCHAR(10),
    //             lunghezza INTEGER,
    //             larghezza INTEGER, 
    //             profondità INTEGER,
    //             id_fornitore VARCHAR,
    //             costo INTEGER,

    System.out.println("QUIIIIII");

    RequestParameters params = context.get(ValidationHandler.REQUEST_CONTEXT_KEY); // (1)
    JsonObject json = params.body().getJsonObject(); // (2)
    System.out.println("json:  " + params.body());

    var watchList = json.mapTo(WatchList.class);
    var parameterBatch = watchList.getOp().stream()
      .map(op -> {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("id_quotes", op.getId_quotes());
        parameters.put("oLat", op.getOLat());
        parameters.put("oLon", op.getOLon());
        parameters.put("dLat", op.getDLat());
        parameters.put("dLon", op.getDLon());
        return parameters;
      }).collect(Collectors.toList());

      SqlTemplate.forUpdate(db,
      "INSERT INTO schema.quotes VALUES (#{id_quotes},ST_SetSRID(ST_Point(#{oLat}, #{oLon}), 4326),ST_SetSRID(ST_Point(#{dLat}, #{dLon}), 4326))"
      + "ON CONFLICT (id_quotes) DO NOTHING")
      .rxExecuteBatch(parameterBatch)
      .doOnError(err-> {LOG.debug("Failure: ", err , err.getMessage());})
      .doOnSuccess(result -> {
        
        LOG.info("Add quotes: ??????????,",result);
        //if(context.response().ended()){
        context.response()
        .setStatusCode(200)
        .end("Element added successfully");
       // }
      }).subscribe();
      }  
}
