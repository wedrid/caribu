package com.caribu.filiale.operatorInf;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caribu.filiale.operator;
import com.caribu.filiale.db.DbResponse;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;

public class GetOpFromDatabaseHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(GetOpFromDatabaseHandler.class);
  private final Pool db;

  public GetOpFromDatabaseHandler(final Pool db) {
    this.db = db;
  }

  @Override
  public void handle(final RoutingContext context) {
    //final String nameParm = context.pathParam("nameop");
    final Integer id = OpRestApi.getAccountId(context);
    LOG.debug("Id parameter: {}", id);

    SqlTemplate.forQuery(db,
      "SELECT o.id, o.nameop, o.surnameop from broker.operator o where id=#{id}")
      .mapTo(operator.class)
      .execute(Collections.singletonMap("id", id))
      .onFailure(DbResponse.errorHandler(context, "Failed to get operator for " + 
      id + " from db!"))
      .onSuccess(op -> {
        if (!op.iterator().hasNext()) {
          DbResponse.notFound(context, "operator " + id + " not available!");
          return;
        }
        var response = op.iterator().next().toJsonObject(); // operator class
        LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer());
      });
  }
}
