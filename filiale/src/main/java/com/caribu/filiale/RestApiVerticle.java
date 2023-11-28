package com.caribu.filiale;

import java.util.Properties;

import org.hibernate.boot.cfgxml.internal.ConfigLoader;
import org.hibernate.reactive.provider.ReactiveServiceRegistryBuilder;
import org.hibernate.reactive.stage.Stage;

// import com.caribu.filiale.db.DBPools;
// import com.caribu.filiale.db.DbResponse;
// import com.caribu.filiale.operatorInf.DeleteOpDatabaseHandler;
// import com.caribu.filiale.operatorInf.GetAllOpFromDatabaseHandler;
// import com.caribu.filiale.operatorInf.GetOpFromDatabaseHandler;
// import com.caribu.filiale.operatorInf.OpRestApi;
// import com.caribu.filiale.operatorInf.PostOpFromDatabaseHandler;
// import com.caribu.filiale.operatorInf.PutOpDatabaseHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;

import org.hibernate.cfg.Configuration;
import org.hibernate.reactive.provider.ReactiveServiceRegistryBuilder;
import org.hibernate.reactive.stage.Stage;
import org.hibernate.service.ServiceRegistry;

import com.caribu.filiale.data.OperatorRepository;
import com.caribu.filiale.data.OperatorRepositoryImpl;
import com.caribu.filiale.model.Operator;

import java.time.LocalDateTime;
import java.util.Properties;
import java.util.concurrent.CompletionStage;

public class RestApiVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(RestApiVerticle.class);

  @Override
  public void start(final Promise<Void> startPromise) throws Exception {

    startHttpServerAndAttachRoutes(startPromise);          
  }

  private void startHttpServerAndAttachRoutes(final Promise<Void> startPromise) {
      // 1. Hibernate configuration
    Properties hibernateProps = new Properties();
    hibernateProps.put("hibernate.connection.url", "jdbc:postgresql://localhost:5432/hib");

    hibernateProps.put("hibernate.connection.username", "postgres");
    hibernateProps.put("hibernate.connection.password", "secret");
    hibernateProps.put("javax.persistence.schema-generation.database.action", "create");
    hibernateProps.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL95Dialect");
    Configuration hibernateConfiguration = new Configuration();
    hibernateConfiguration.setProperties(hibernateProps);
    hibernateConfiguration.addAnnotatedClass(Operator.class);
    //hibernateConfiguration.addAnnotatedClass(Project.class);

    // 2. Session factroy
    ServiceRegistry serviceRegistry = new ReactiveServiceRegistryBuilder()
      .applySettings(hibernateConfiguration.getProperties()).build();
    Stage.SessionFactory sessionFactory = hibernateConfiguration
        .buildSessionFactory(serviceRegistry).unwrap(Stage.SessionFactory.class);

      
    // 3. Project repository
    OperatorRepository projectRepository = new OperatorRepositoryImpl(sessionFactory);
    // 5. WebVerticle
    WebVerticle verticle = new WebVerticle();

    System.out.println("Task ID before insertion is: ");
    // final Router restApi = Router.router(vertx);
    // restApi.route().handler(BodyHandler.create());
    // RouterBuilder.create(vertx, "openapi.yml")
    //   .onSuccess(routerBuilder -> { // (1)
    //       //associate the operationId "listOp" with its handler 
    //     // routerBuilder.operation("listOp").handler(new GetAllOpFromDatabaseHandler(db)); // (3)
    //     // routerBuilder.operation("getOp").handler(new GetOpFromDatabaseHandler(db)); // (3)
    //     // routerBuilder.operation("updateOpAvailability").handler(new PutOpDatabaseHandler(db)); // (3)
    //     // routerBuilder.operation("deleteOp").handler(new DeleteOpDatabaseHandler(db)); // (3)
    //     // routerBuilder.operation("AddOp").handler(new PostOpFromDatabaseHandler(db)); // (3)


    //     Router restApi = routerBuilder.createRouter();
    //     restApi.route().handler(BodyHandler.create());
      
    //     //AssetsRestApi.attach(restApi, db);
    //     //OpRestApi.attach(restApi, db);
    //     //filialeVertical.sendOp(restApi);
    //     restApi.route().handler(handleFailure()); // handlerFailure non funziona??

    //     vertx.createHttpServer()
    //       .requestHandler(restApi)
    //       .exceptionHandler(error -> LOG.error("HTTP Server error: ", error))
    //       .listen(8888, http -> {
    //         if (http.succeeded()) {
    //           startPromise.complete();
    //           LOG.info("HTTP server started on port {}", 8888);
    //         } else {
    //           startPromise.fail(http.cause());
    //         }
    //     });
    
    //     // You can start building the router using routerBuilder
    //   }).onFailure(cause -> { // (2)
    //   // Something went wrong during router factory initialization
    //   startPromise.fail(cause);

    // });
  
  }

  private Handler<RoutingContext> handleFailure() {
    return errorContext -> {
      
      if (errorContext.response().ended()) {
         // Ignore completed response
         LOG.info("------");
        return;
      }
      LOG.info("Route Error:", errorContext.failure());
      errorContext.response()
        .setStatusCode(500)
        .end(new JsonObject().put("message: Something went wrong, path: ", errorContext.normalizedPath()).toBuffer());
    };
  }

}
