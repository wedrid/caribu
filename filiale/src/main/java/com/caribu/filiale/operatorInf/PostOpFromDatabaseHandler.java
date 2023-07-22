package com.caribu.filiale.operatorInf;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caribu.filiale.operator;
import com.caribu.filiale.db.DbResponse;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.validation.RequestParameters;
import io.vertx.ext.web.validation.ValidationHandler;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.templates.SqlTemplate;

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

    RequestParameters params = context.get(ValidationHandler.REQUEST_CONTEXT_KEY); // (1)
    JsonObject json = params.body().getJsonObject(); // (2)
    
    System.out.println("json:  " + params.body());
    
    var watchList = json.mapTo(WatchList.class);
    var parameterBatch = watchList.getOp().stream()
      .map(op -> {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", op.getId());
        parameters.put("nameop", op.getNameop());
        parameters.put("surnameop", op.getSurnameop());
        parameters.put("numReq", op.getNumReq());
        parameters.put("isavailable", op.getIsavailable());
        return parameters;
      }).collect(Collectors.toList());

      SqlTemplate.forUpdate(db,
      "INSERT INTO broker.operator VALUES (#{id},#{nameop},#{surnameop},#{numReq},#{isavailable})"
      + "ON CONFLICT (id) DO NOTHING")
      .executeBatch(parameterBatch)
      .onFailure(DbResponse.errorHandler(context, "Failed to insert into watchlist"))
      .onSuccess(result -> {
        
        LOG.info("Add operator: ??????????,",result);
        //if(context.response().ended()){
        context.response()
        .setStatusCode(200)
        .end("Element added successfully");
       // }
      });
    }  
}
