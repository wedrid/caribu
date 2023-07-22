package com.caribu.filiale.operatorInf;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caribu.filiale.db.DbResponse;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;

public class DeleteOpDatabaseHandler implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(DeleteOpDatabaseHandler.class);
  private final Pool db;

  public DeleteOpDatabaseHandler(final Pool db) {
    this.db = db;
  }

  @Override
  public void handle(final RoutingContext context) {
    Integer id = OpRestApi.getAccountId(context);

    SqlTemplate.forUpdate(db,
      "DELETE FROM broker.operator where id=#{id}")
      .execute(Collections.singletonMap("id", id))
      .onFailure(DbResponse.errorHandler(context, "Failed to delete watchlist for accountId: " + id))
      .onSuccess(result -> {
        LOG.debug("Deleted {} rows for accountId {}", result.rowCount(), id);
        context.response()
          .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
          .end();
      });
  }
}
