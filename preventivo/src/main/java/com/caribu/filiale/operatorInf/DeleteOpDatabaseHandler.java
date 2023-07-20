package com.caribu.filiale.operatorInf;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.http.HttpHeaders;
import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.rxjava3.sqlclient.Pool;
import io.vertx.rxjava3.sqlclient.templates.SqlTemplate;

public class DeleteOpDatabaseHandler implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(DeleteOpDatabaseHandler.class);
  private final Pool db;

  public DeleteOpDatabaseHandler(final Pool db) {
    this.db = db;
  }

  @Override
  public void handle(final RoutingContext context) {
    final String id_quotes = context.pathParam("id_quotes");
    LOG.info("Executing DB query to delete:...",id_quotes);
    SqlTemplate.forUpdate(db,
      "DELETE FROM schema.quotes where id_quotes=#{id_quotes}")
      .rxExecute(Collections.singletonMap("id_quotes", id_quotes))
      .doOnSuccess(result -> {
        LOG.debug("Deleted {} rows for accountId {}", result.rowCount(), id_quotes);
        context.response()
          .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
          .end();
    })
    .doOnError(err -> {
      LOG.debug("Failure: ", err , err.getMessage());
      context.response()
        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .setStatusCode(500)
        .end(new JsonObject().put("error", err.getMessage()).encode());
    })
    .subscribe(); 
  }
}
