package com.caribu.preventivo.quotesApi;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caribu.preventivo.db.DbResponse;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.rxjava3.sqlclient.Pool;
import io.vertx.rxjava3.sqlclient.templates.SqlTemplate;

public class GetQuotesHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(GetQuotesHandler.class);
  private final Pool db;

  public GetQuotesHandler(final Pool db) {
    this.db = db;
  }

  @Override
  public void handle(final RoutingContext context) {

    final Integer id_quotes = Integer.parseInt(context.pathParam("id_quotes"));
    LOG.debug("Id parameter: {}", id_quotes);

    SqlTemplate.forQuery(db,
        "SELECT * from schema.quotes o where id_quotes=#{id_quotes}")
        .mapTo(Quotes.class)
        .rxExecute(Collections.singletonMap("id_quotes", id_quotes)) //TODO
        .doOnError(err -> {
          LOG.debug("Failure: ", err, err.getMessage());
          context.response()
              .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
              .setStatusCode(500)
              .end(new JsonObject().put("error", err.getMessage()).encode());
        })
        .doOnSuccess(q -> {
          if (!q.iterator().hasNext()) {
            DbResponse.notFound(context, "quotes " + id_quotes + " not available!");
            return;
          }
          var response = q.iterator().next().toJsonObject();
          LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
          context.response()
              .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
              .end(response.encode());
        }).subscribe();
  }
}
