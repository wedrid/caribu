package com.caribu.filiale.operatorInf;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caribu.filiale.db.DbResponse;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;


public class GetAllOpFromDatabaseHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(GetAllOpFromDatabaseHandler.class);
  private final Pool db;

  public GetAllOpFromDatabaseHandler(final Pool db) {
    this.db = db;
  }

  @Override
  public void handle(final RoutingContext context) {
    db.query("SELECT * FROM broker.operator a")
      .execute()
      .onFailure(DbResponse.errorHandler(context, "Failed to get assets from db!"))
      .onSuccess(result -> {
        var response = new JsonArray();
        result.forEach(row -> {
          response.add(row.getValue("id")) 
                  .add(row.getValue("nameop"))
                  .add(row.getValue("surnameop"))
                  .add(row.getValue("isavailable")) ;
        });
        LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer());
      });
  }

}