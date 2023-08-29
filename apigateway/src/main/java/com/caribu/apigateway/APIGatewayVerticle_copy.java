package com.caribu.apigateway;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.RoleBasedAuthorization;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2Options;
import io.vertx.ext.auth.oauth2.authorization.KeycloakAuthorization;
import io.vertx.ext.auth.oauth2.providers.KeycloakAuth;
import io.vertx.ext.auth.oauth2.providers.OpenIDConnectAuth;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.AuthorizationHandler;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.OAuth2AuthHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.httpproxy.HttpProxy;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class APIGatewayVerticle_copy extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(APIGatewayVerticle.class);

  //protected ServiceDiscovery discovery;
  private HttpServer server;
  private HttpClient httpClient; 

  // per ora, HttpProxy così, poi forse hashmap, poi anche da fare ServiceDiscovery
  private HttpProxy proxy; 
  private HttpProxy requestProxy;

  ServiceDiscovery discovery;

  private static final String HOST = "http://localhost:8888";
  private static final String CALLBACK_URI = "/callback";
  private OAuth2Auth oAuth2Auth;

  
  final List<JsonObject> pets = new ArrayList<>(Arrays.asList(
    new JsonObject().put("id", 1).put("name", "Fufi").put("tag", "ABC"),
    new JsonObject().put("id", 2).put("name", "Garfield").put("tag", "XYZ"),
    new JsonObject().put("id", 3).put("name", "Puffa")
  ));

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    

    httpClient = vertx.createHttpClient();
    proxy = HttpProxy.reverseProxy(httpClient);
    
    requestProxy = HttpProxy.reverseProxy(httpClient);

    discovery = ServiceDiscovery.create(vertx);
    Router securitySubRouter = Router.router(vertx);

    RouterBuilder.create(this.vertx, "/Users/edrid/Desktop/SWAM/caribu/apigateway/src/main/resources/APIGateway.yaml") //TODO: change to relative path
      .onSuccess(routerBuilder -> {
        //authentication handler for openAPI
        /*routerBuilder
          .securityHandler("openId")
          .bind(config ->


            OpenIDConnectAuth
              .discover(vertx, new OAuth2Options()
                .setClientId("client-id") // user provided
                .setClientSecret("client-secret") // user provided
                .setSite(config.getString("openIdConnectUrl")))
              .compose(authProvider -> {
                AuthenticationHandler handler =
                  OAuth2AuthHandler.create(vertx, authProvider);
                return Future.succeededFuture(handler);
              }))
          .onSuccess(self -> {
            // Creation completed with success
          })
          .onFailure(err -> {
            // Something went wrong
          });*/

          /*  this should be the good way to use openapi and security
          OAuth2Options clientOptions = new OAuth2Options()
            .setClientId("vertx-dev-client")
            .setClientSecret("MLW2U31o1zsh0OF13aclRjzJEkXuUoEU")
            .setSite("http://localhost:8989/realms/vertx-dev");
          Router router1 = Router.router(vertx);
          Route callbackRoute = router1.get(CALLBACK_URI);
          routerBuilder
            .securityHandler("oauth")
            .bind(config -> KeycloakAuth.discover(vertx, clientOptions)
              .compose(oidc -> Future.succeededFuture(
                OAuth2AuthHandler.create(vertx, oidc, HOST + CALLBACK_URI) // Additional scopes: openid for OpenID Connect, tells the Authorization server that we are doing OIDC and not OAuth
                  .withScope("openid")
                  .setupCallback(callbackRoute)
                )))
            .onSuccess(self -> {
              self
                .operation("ping")
                .handler(context -> {
                  LOG.info("Called ping :)");
                  context
                    .response()
                    .setStatusCode(200)
                    .putHeader(HttpHeaders.CONTENT_TYPE, "application/json") 
                    .end(new JsonArray(pets).encode()); 
                });
            });*/

        /*
        routerBuilder
          .operation("getAllClients")
          .handler(
            context -> {
              String[] parts = context.normalizedPath().split("/", 3);
              String ms_name = parts[1];
              String endpoint_path = "/" + parts[2];
              discovery.getRecord(new JsonObject().put("name", ms_name)).onComplete(ar -> {
                if (ar.succeeded() && ar.result() != null) {
                  // Retrieve the service reference
                  ServiceReference reference = discovery.getReference(ar.result());
                  String address = ar.result().getLocation().getString("host");
                  int port = ar.result().getLocation().getInteger("port");
                  LOG.info("Service found at " + address + ":" + port);
                  
                  WebClient client = reference.getAs(WebClient.class);
                  //WebClient client = WebClient.create(vertx);

                  client.request(context.request().method(), endpoint_path)
                    .putHeaders(context.request().headers())
                    .send()
                    .onComplete(response -> {
                      if (response.succeeded()) {
                        LOG.info("The other verticle responds with: " + response.result().bodyAsString());
                        context.response()
                          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                          .end(response.result().bodyAsBuffer());
                      } else {
                        LOG.error("Request failed", response.cause());
                      }
                  });
                    
                  LOG.info("Found and retrieved service");
                  reference.release();
                } else {
                  LOG.info("Not finding the service");
                }
              });
            }
          );*/
        
        
        
        routerBuilder
            .operation("getAllClients")
            .handler(new DispatchRequestHandler(discovery, vertx));
          

        routerBuilder
            .operation("provaProxy")
            .handler(context -> {
              LOG.info("Trial with service discovery");
              discovery.getRecord(new JsonObject().put("name", "commesse-api")).onComplete(ar -> {
                if (ar.succeeded() && ar.result() != null) {
                  // Retrieve the service reference
                  ServiceReference reference = discovery.getReference(ar.result());
                  // Retrieve the service object
                  WebClient client = reference.getAs(WebClient.class);
                  // implement reverse proxy logic
                  reference.release();
                } else {
                  LOG.info("Not finding the service");
                }
              });
            });
            //PING
        /*routerBuilder
          .operation("ping")
          .handler(context -> {
            LOG.info("Called ping :)");
            context
              .response() 
              .setStatusCode(200)
              .putHeader(HttpHeaders.CONTENT_TYPE, "application/json") 
              .end(new JsonArray(pets).encode()); 
          });*/
          routerBuilder
            .operation("getAllRequests")
            .handler(context -> {
              LOG.info("Called getAllRequest");
              requestProxy.handle(context.request());
            });
          /* 
          routerBuilder
            .operation("provaProxy")
            .handler(context -> {
              provaProxy.origin(10000, "127.0.0.1");
              LOG.info("Called provaProxy");
              provaProxy.handle(context.request());
            }); 
          */
          
          routerBuilder
            .operation("getRequestsFromCommesseApi")
            .handler(context -> {
              LOG.info("Called getRequestsFromCommesseApi, looking for service");
              discovery.getRecord(new JsonObject().put("name", "commesse-api")).onComplete(ar -> {
                if (ar.succeeded() && ar.result() != null) {
                  // Retrieve the service reference
                  ServiceReference reference = discovery.getReference(ar.result());
                  String address = ar.result().getLocation().getString("host");
                  int port = ar.result().getLocation().getInteger("port");
                  LOG.info("Service found at " + address + ":" + port);
                  LOG.info("Forwarding request..");
                  //proxy.origin(port, address);
                  proxy.handle(context.request());
                  HttpServerRequest request = context.request();
                  LOG.info("HTTP method: " + request.method());
                  LOG.info("Request path: " + request.path());
                  LOG.info("Request headers: " + request.headers());
                  reference.release();
                // ######## previous attempt withouth proxy
                // You need to path the complete path
                // Note: "reqapi" should be the root
                /*WebClient client = reference.getAs(WebClient.class);
                client.get("/reqapi/requests").send().onComplete(
                    response -> {
                      if (response.succeeded()) {
                        LOG.info("The other verticle responds with: " + response.result().bodyAsString());
                        context.response()
                          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                          .end(response.result().bodyAsBuffer());
                      } else {
                        LOG.error("Request failed", response.cause());
                      }
                      reference.release();
                    }); */
                } else {
                  LOG.info("Not finding the service");
                }
              });
            });

          routerBuilder
            .operation("discoveryTrial")
            .handler(context -> {
              discovery.getRecord(new JsonObject().put("name", "requests-api")).onComplete(ar -> {
              if (ar.succeeded() && ar.result() != null) {
                // Retrieve the service reference
                ServiceReference reference = discovery.getReference(ar.result());
                // Retrieve the service object
                WebClient client = reference.getAs(WebClient.class);

                // You need to path the complete path
                client.get("/pg/requests").send().onComplete(
                  response -> {
                    if (response.succeeded()) {
                      LOG.info("The other verticle responds with: " + response.result().bodyAsString());
                      context.response()
                        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                        .end(response.result().bodyAsBuffer());
                    } else {
                      LOG.error("Request failed", response.cause());
                    }
                    reference.release();
                  });
              } else {
                LOG.info("Not finding the service");
              }
            });
            });
          // the other operations here
          //...


          // generate the router
          Router router = routerBuilder.createRouter();
          /* 
          //for authentication
          LocalSessionStore localSessionStore = LocalSessionStore.create(vertx);
          SessionHandler sessionHandler = SessionHandler.create(localSessionStore);
          router.route().handler(LoggerHandler.create(LoggerFormat.SHORT));
          router.route().handler(BodyHandler.create());
          router.route().handler(sessionHandler);
          OAuth2Options clientOptions = new OAuth2Options()
            .setClientId("vertx-dev-client")
            .setClientSecret("MLW2U31o1zsh0OF13aclRjzJEkXuUoEU")
            .setSite("http://localhost:8989/realms/vertx-dev");
          KeycloakAuth.discover(
            vertx,
            clientOptions)
            .onSuccess(oAuth2Auth -> {
                  
            System.out.println("Keycloak discovery complete.");
            this.oAuth2Auth = oAuth2Auth;
            try {
              Route callbackRoute = router.get(CALLBACK_URI);
              OAuth2AuthHandler oauth2handler = OAuth2AuthHandler.create(vertx, oAuth2Auth, HOST + CALLBACK_URI)
                // Additional scopes: openid for OpenID Connect, tells the Authorization server that we are doing OIDC and not OAuth
                .withScope("openid")
                .setupCallback(callbackRoute);

              router.route("/hello/*").handler(oauth2handler);
              router.route("/hello/world").handler(
                context -> {
                  User user = context.user();
                  System.out.println(user.principal().encodePrettily());

                  
                  KeycloakAuthorization.create().getAuthorizations(user)
                    .onSuccess(v -> {
                      // At this point the user object should have the authorizations filled in.
                      // We can now use an AuthorizationHandler to protect resources:
                      AuthorizationHandler.create(RoleBasedAuthorization.create("operational"))
                        .handle(context);
                    })
                    .onFailure(context::fail); 
                /*var user = context.user();
                context.response().end(user.principal().encodePrettily()); //ex fine
              });
                  
              //router.route("/logout").handler(this::handleLogout);
              //router.route("/private/*").handler(oauth2handler);
              router.route("/*").handler(oauth2handler);
              router.route("/private/account").handler(routingContext -> {
                System.out.println(routingContext.user().principal().encodePrettily());
                routingContext.response()
                  .setStatusCode(200)
                  .putHeader("Content-Type", "application/json")
                  .end(routingContext.user().attributes().encodePrettily());
              });
              
              
              router.get().handler(StaticHandler.create("www"));
            } catch (Exception e) {
              e.printStackTrace();
            }
            */

            /*vertx.createHttpServer().requestHandler(router).listen(8888, http -> {
              if (http.succeeded()) {
                startPromise.complete();
                System.out.println("HTTP server started on port 8888");
              } else {
                startPromise.fail(http.cause());
              }
            });
                  
            } );*/

            // create the HTTP server
            server = vertx.createHttpServer(new HttpServerOptions().setPort(10000).setHost("localhost"));
            server.requestHandler(router).listen();
            startPromise.complete();
      })
      .onFailure(startPromise::fail);
      
  }

  private Handler<RoutingContext> dispatchRequest() {
    return context -> {
      discovery.getRecord(new JsonObject().put("name", "requests-api")).onComplete(ar -> {
        if (ar.succeeded() && ar.result() != null) {
          // Retrieve the service reference
          ServiceReference reference = discovery.getReference(ar.result());
          // Retrieve the service object
          WebClient client = reference.getAs(WebClient.class);

          // You need to path the complete path
          client.get("/pg/requests").send().onComplete(
            response -> {
              if (response.succeeded()) {
                LOG.info("Response: " + response.result().bodyAsString());
              } else {
                LOG.error("Request failed", response.cause());
              }
              reference.release();
            });
        } else {
          LOG.info("Not finding the service");
        }
      });
    };
  }


  public static void main(String[] args) {
    ClusterManager mgr = new HazelcastClusterManager();
    VertxOptions options = new VertxOptions().setClusterManager(mgr);
    Vertx
      .clusteredVertx(options, cluster -> {
       if (cluster.succeeded()) {
           cluster.result().deployVerticle(new APIGatewayVerticle(), res -> {
               if(res.succeeded()){
                   LOG.info("Deployment id is: " + res.result());
               } else {
                   LOG.error("Deployment failed!");
               }
           });
       } else {
           LOG.error("Cluster up failed: " + cluster.cause());
       }
   });
  }

  private void handleLogout(RoutingContext ctx) {
    User user = ctx.user();

    ctx.session().destroy();
    ctx.response()
      .setStatusCode(302)
      .putHeader("location", oAuth2Auth.endSessionURL(user))
      .end();
  }

}
