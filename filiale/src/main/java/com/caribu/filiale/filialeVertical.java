package com.caribu.filiale;
import org.slf4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caribu.filiale.db.DbResponse;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import io.vertx.reactivex.redis.client.Response;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

//SENDER 
// FILIALE = Main
public class filialeVertical extends AbstractVerticle{
    
    static final String ADDRESS = "my.request.address";
    private String nameFiliale;
    private static final Logger LOG = LoggerFactory.getLogger(filialeVertical.class);


    private final Pool db;
    public filialeVertical(final Pool db) {
        this.db = db;
    }
    
    @Override
    public void start(final Promise<Void> startPromise) throws Exception{

        final Router restApi = Router.router(vertx);
        sendOp(restApi);
        //final JsonObject message = new JsonObject().put("message", "Hello").put("version", 1);
        //final String message = "Hello World!";
        //System.out.println("Sending: "+ message);
        //LOG.debug("Sending: {}", message);
        
         
    }

    // Notificare operatori/ Assegna richiesta
    public void assignReq(){
        // Arriva notifica a tutti i op -> HTTP (verificare se disponibili)
         // -> risposta sarà solo da 1 op (tornerà indietro id di op che ha accettato)
         // Notifica va via --- 
    }

    public void assignSpecificOpReq(){
        // Arriva notifica a op specificato nella richiesta -> HTTP 
        // ->Attende un tot di tempo:
        // Se accetta: tornerà indietro il suo id
        // Altrimenti chiama assignReq()
    }

    // Ricevere la richiesta da Client 
    public void getReq(){
        // metodo HTTP -> GET (/request)
        //-> mi darà id e opSpecifico .. 
        // retrun json o String 
    }
    
    // Invia al Client la lista degli operativi
    public static void sendOp(final Router router){
        //Endpoint
        router.get("/api/names").handler(ctx -> {
            // Retrieve the list of names and surnames from your data source
            //final JsonObject namesAndSurnames = getAllOperative(router);
            getAllOperative(router);
            System.out.println("Path:" + ctx.normalizedPath() + "responds with " + namesAndSurnames.encode());

            ctx.response()
              .putHeader("content-type", "application/json")
              .end(namesAndSurnames.encode());
          });
        // metodo HTTP -> risposta a richiesta
        // invia json o altro con lista 'Nome Cognome' degli operatvi al Client
        // chiama getOperative()
    }

     // OPERAZIONI con QUERY o le faccio in operator?
    //Operazioni sul DATABASE 
    public void getAllOperative(RoutingContext context){
        //mi devo connettere al DATABASE
        // restituisce Nome, cognome di op disponibili.. 
        db.query("SELECT a.nameop, a.id FROM broker.operator a")
        .execute()
        .onFailure(DbResponse.errorHandler(context, "Failed to get operator from db!"))
        .onSuccess(result -> {
          var response = new JsonArray();
          result.forEach(row -> {
            response.add(row.getValue("nameop")).add(row.getValue("id"));
          });
          LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
          context.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
            .end(response.toBuffer());
        })
        .onComplete(ar -> {
            if (ar.succeeded()) {
              RowSet<Row> result = ar.result();
              System.out.println("Got " + result.size() + " rows ");
            } else {
              System.out.println("Failure: " + ar.cause().getMessage());
            }
        });
    }

    public void deleteOp(){
        //  
    }
    public void updateOp(){
        // 
    }
    public void addOp(RoutingContext context){
        db.query("SELECT a.nameop, a.id FROM broker.operator a")
        .execute()
        .onFailure(DbResponse.errorHandler(context, "Failed to get operator from db!"))
        .onSuccess(result -> {
          var response = new JsonArray();
          result.forEach(row -> {
            response.add(row.getValue("nameop")).add(row.getValue("id"));
          });
          LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
          context.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
            .end(response.toBuffer());
        })
        .onComplete(ar -> {
            if (ar.succeeded()) {
              RowSet<Row> result = ar.result();
              System.out.println("Got " + result.size() + " rows ");
            } else {
              System.out.println("Failure: " + ar.cause().getMessage());
            }
        });
        // 
    }

}
