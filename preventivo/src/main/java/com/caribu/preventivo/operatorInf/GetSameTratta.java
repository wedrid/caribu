package com.caribu.preventivo.operatorInf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caribu.preventivo.strategy.DistanceQuery;
import com.caribu.preventivo.strategy.GenericQuery;
import com.caribu.preventivo.strategy.ODQuery;
import com.caribu.preventivo.strategy.Query;
import com.caribu.preventivo.VertxRxWeb;
import com.caribu.preventivo.db.DbResponse;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.rxjava3.sqlclient.Pool;
import io.vertx.rxjava3.sqlclient.Row;
import io.vertx.rxjava3.sqlclient.templates.SqlTemplate;
import java.text.DecimalFormat;


public class GetSameTratta implements Handler<RoutingContext> {
    DecimalFormat df = new DecimalFormat("#.###");
    private static final Logger LOG = LoggerFactory.getLogger(GetSameTratta.class);
    private final Pool db;
    private String string_query;
    

    public GetSameTratta(final Pool db) {
        this.db = db;
    }

    //Esempio:
    // http://localhost:8888/quotesapi/quotes?select=1&oLat=43.7696&oLon=11.2558&dLat=44.6983&dLon=10.6312
    
    @Override
    public void handle(final RoutingContext context) {
        // in input ho origine=(LAT, LONG), destinazione=(LAT, LONG)
        Map<String, Object> parameters = new HashMap<>();
        Query query = new Query(); // Strategy
        Integer select = Integer.parseInt(context.request().getParam("select"));

        String soglia = context.request().getParam("sogliaDist"); // raggio & soglia

        if (select != 1 && select != 2) {
            // Preventivi senza filtro
            LOG.info("Executing DB query to find all quotes");
            // Strategy
            query.setQuery(new GenericQuery());
            string_query = query.getQuery(query);
            LOG.info("Query: {}", string_query);
            parameters = null;

        }
        else {

            query.setOLat(getParameters(context, "oLat"));
            query.setDLon(getParameters(context, "dLon"));
            query.setDLat(getParameters(context, "dLat"));
            query.setOLon(getParameters(context, "oLon"));

            //Parameters
            parameters.put("oLat", query.getOLat());
            parameters.put("oLon", query.getOLon());
            parameters.put("dLat", query.getDLat());
            parameters.put("dLon", query.getDLon());

            if (select == 1) { // Qualsiasi O-D con simile distanza -> soglia
                // Strategy
                query.setQuery(new DistanceQuery());
                string_query = query.getQuery(query);// + query.getCost();
                LOG.info("Query: {}", string_query);

            } else if (select == 2) { // Intorno O e D -> raggio
                // Strategy
                query.setQuery(new ODQuery());
                string_query = query.getQuery(query);// + query.getCost();
                LOG.info("Query: {}", string_query);
            }

        }
        //Query
        sqlTemplate(context, string_query, parameters);
    }

    private void sqlTemplate(RoutingContext context, String stringQuery, Map<String, Object> parameters) {

        SqlTemplate.forQuery(db, stringQuery)
                .rxExecute(parameters)
                .doOnError(err -> {
                    LOG.debug("Failure: ", err, err.getMessage());
                    context.response()
                            .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                            .setStatusCode(500)
                            .end(new JsonObject().put("error", err.getMessage()).encode());
                })
                .doOnSuccess(result -> {
                    LOG.info("Got " + result.size() + " rows ");
                    StringBuilder responseStr = new StringBuilder("Numero risultati " + result.size() + ": \n");
                    result.forEach(row -> {
                        responseStr.append("\t Distanza richiesta: " + convertNum(row, "dist_in"))
                                .append(" km, Distanza: " + convertNum(row, "dist"))
                                .append(" km, idQuota: " + row.getString("id_quotes"))
                                .append(". \n");
                    });
                    LOG.info("Path {} responds with {}", context.normalizedPath(), responseStr.toString());
                    context.response()
                            .putHeader(HttpHeaders.CONTENT_TYPE, "text/plain").end(responseStr.toString());
                }).subscribe();
    }

    private Float getParameters(RoutingContext context, String name) {
        //AVVISO todo se non passa parametri
        Float parm = Float.parseFloat(context.request().getParam(name));
        return parm;
    }

    //convertire m in km 
    private String convertNum(Row row, String name) {
        return df.format(Float.parseFloat(row.getValue(name).toString()) / 1000);
    }
    
}
