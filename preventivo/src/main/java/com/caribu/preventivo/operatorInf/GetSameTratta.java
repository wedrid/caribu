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

    // http://localhost:8888/quoteapi/quotes?select=1&oLat=43.7696&oLon=11.2558&dLat=44.6983&dLon=10.6312&cmin=10&cmax=1000&soglia=200000
    @Override
    public void handle(final RoutingContext context) {
        // in input ho origine=(LAT, LONG), destinazione=(LAT, LONG)
        Map<String, Object> parameters = new HashMap<>();
        Query query = new Query(); // Strategy

        String table = "boh";

        Float select = convertParameters(context, "select", 0f); // Strategy
        getParameters(context, parameters, query);

        if (select != 1 && select != 2) {
            // Get all tratta_ query con nessun filtro
            LOG.info("Executing DB query to find all quotes");
            // Strategy
            query.setQuery(new GenericQuery());
            string_query = query.getQuery(query, table);
            LOG.info("Query: {}", string_query);

        } else if (select == 1) { // Qualsiasi O-D con simile distanza -> soglia
            // Strategy
            query.setQuery(new DistanceQuery());
            string_query = query.getQuery(query, table) + " AND" + query.getCost();
            LOG.info("Query: {}", string_query);

        } else if (select == 2) { // Intorno O e D -> raggio
            // Strategy
            query.setQuery(new ODQuery());
            string_query = query.getQuery(query, table) + " AND" + query.getCost();
            LOG.info("Query: {}", string_query);

        }
        sqlTemplate(context, string_query, parameters);
    }

    private void getParameters(final RoutingContext context, Map<String, Object> parameters, Query query) {
        query.setOLat(convertParameters(context, "oLat", null));
        query.setDLon(convertParameters(context, "dLon", null));
        query.setDLat(convertParameters(context, "dLat", null));
        query.setOLon(convertParameters(context, "oLon", null));

        query.setSoglia(convertParameters(context, "soglia", 15000f)); // raggio & soglia

        // costo
        query.setMaxCost(convertParameters(context, "cmax", 100000000f));
        query.setMinCost(convertParameters(context, "cmin", 0f));

        // Parameters
        parameters.put("cmax", query.getMaxCost());
        parameters.put("cmin", query.getMinCost());

        parameters.put("oLat", query.getOLat());
        parameters.put("oLon", query.getOLon());
        parameters.put("dLat", query.getDLat());
        parameters.put("dLon", query.getDLon());

        parameters.put("soglia", query.getSoglia());
    }

    private Float convertParameters(RoutingContext context, String name, Float defValue) {
        // AVVISO todo se non passa parametri
        String parmString = context.request().getParam(name);
        Float parm = defValue;
        if (parmString != null) {
            parm = Float.parseFloat(parmString);
        }
        return parm;
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
                    StringBuilder responseStr = new StringBuilder("Numero risultati " + result.size())
                            .append(", costo compreso tra :" + parameters.get("cmin") + " - " + parameters.get("cmax"))
                            .append("\n");

                    result.forEach(row -> {
                        responseStr.append("\t Distanza richiesta: " + convertNum(row, "dist_in"))
                                .append(" km, distanza: " + convertNum(row, "dist"))
                                .append(" km, costo: " + row.getValue("costo"))
                                .append(", idQuota: " + row.getString("id_quotes"))
                                .append(". \n");
                    });
                    LOG.info("Path {} responds with {}", context.normalizedPath(), responseStr.toString());
                    context.response()
                            .putHeader(HttpHeaders.CONTENT_TYPE, "text/plain").end(responseStr.toString());
                }).subscribe(succ -> {
                }, err -> {
                    LOG.debug("Failure2: ", err, err.getMessage());
                });
    }

    // convertire m in km
    private String convertNum(Row row, String name) {
        return df.format(Float.parseFloat(row.getValue(name).toString()) / 1000);
    }

}
