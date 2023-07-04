package com.caribu.prova_auth;

import java.util.Set;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.vertx.ext.auth.authorization.Authorizations;
import io.vertx.ext.auth.authorization.PermissionBasedAuthorization;
import io.vertx.ext.auth.authorization.RoleBasedAuthorization;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2Options;
import io.vertx.ext.auth.oauth2.authorization.KeycloakAuthorization;
import io.vertx.ext.auth.oauth2.providers.KeycloakAuth;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.AuthorizationHandler;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.OAuth2AuthHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

public class MainVerticle extends AbstractVerticle {

  private static final String HOST = "http://localhost:8888";
  private static final String CALLBACK_URI = "/callback";

  private OAuth2Auth oAuth2Auth;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    System.out.println("Hello");
    Router router = Router.router(vertx);
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
            
          AuthorizationProvider authorizationProvider = KeycloakAuthorization.create();
          AuthorizationHandler operationalRoleHandler = AuthorizationHandler.create(RoleBasedAuthorization.create("operational"))
              .addAuthorizationProvider(authorizationProvider);
          router.route("/hello/*").handler(oauth2handler);
          router.route("/hello/*").handler(operationalRoleHandler);
          router.route("/hello/world").handler(context -> {
            var user = context.user();
            context.response().end(user.principal().encodePrettily());
          });
              
          router.route("/logout").handler(this::handleLogout);
          router.route("/private/*").handler(oauth2handler);
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


        vertx.createHttpServer().requestHandler(router).listen(8888, http -> {
          if (http.succeeded()) {
            startPromise.complete();
            System.out.println("HTTP server started on port 8888");
          } else {
            startPromise.fail(http.cause());
          }
        });
      })
      .onFailure(startPromise::fail);
  }
    
  public static void main(String[] args) {
    System.out.println("Starting...");
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
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
