package com.caribu.filiale.operatorInf;

import java.util.HashMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caribu.filiale.operator;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.handler.BodyHandler;

import io.vertx.sqlclient.Pool;
import io.vertx.pgclient.PgPool;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import io.vertx.ext.web.validation.ValidationHandler;
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder;

import io.vertx.json.schema.common.dsl.Schemas;
import io.vertx.json.schema.common.dsl.Keywords;
import io.vertx.json.schema.JsonSchema;
import io.vertx.json.schema.common.dsl.SchemaBuilder;
import io.vertx.ext.web.validation.builder.Parameters;

import io.vertx.json.schema.common.dsl.ObjectSchemaBuilder;
import io.vertx.ext.web.validation.builder.Bodies;

public class OpRestApi {

  private static final Logger LOG = LoggerFactory.getLogger(OpRestApi.class);

  public static void attach(final Router parent, final Pool db) {
    
    final HashMap<UUID, operator> watchListPerAccount = new HashMap<>();
    parent.get("/op").handler(new GetAllOpFromDatabaseHandler(db));
    parent.get("/op/:id").handler(new GetOpFromDatabaseHandler(db)); 
    parent.post("/op/add").handler(new PostOpFromDatabaseHandler(db));
    parent.delete("/op/delete/:id").handler(new DeleteOpDatabaseHandler(db));

    parent.put("/op/update/:id").handler(new PutOpDatabaseHandler(db));
  }
  



  static Integer getAccountId(final RoutingContext context) {
    int id =  Integer.parseInt(context.pathParam("id"));
    LOG.debug("{} for account {}", context.normalizedPath(), id);
    return id;
  }

}

/* 

static ValidationHandler validationHandler(){
      
  // ValidationHandler validationHandler = ValidationHandlerBuilder.create()
  // .queryParameter(requiredParam("id", stringSchema().minLength(6).maxLength(6)))
  // .queryParameter(requiredParam("name", stringSchema()))
  // .build();
  // SchemaBuilder      // Create new schemas
  SchemaBuilder schemaBuilder = Schemas.objectSchema()
  .property("firstName", Schemas.stringSchema())
  .property("Id", Schemas.stringSchema()  // intero ?? 
            .with(Keywords.maxLength(5))
            .with(Keywords.minLength(5)))
  .property("lastName", Schemas.stringSchema()); 

  JsonObject schemaJson = schemaBuilder.toJson(); // Convert the schema to JSON
  System.out.println(schemaJson);
  //.requiredProperty("firstName", "lastName");
  JsonSchema schema = JsonSchema.of(schemaJson);  // parse the schema ??

  //Handler that performs parsing and validation of the request:
  ValidationHandler validation = ValidationHandlerBuilder.
      return validation;
} */