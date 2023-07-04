package com.caribu.prova_auth;

import io.vertx.core.Handler;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.web.RoutingContext;

public class LogoutHandler implements Handler<RoutingContext> {
    private OAuth2Auth oAuth2Auth;

    public LogoutHandler(OAuth2Auth oAuth2Auth) {
        this.oAuth2Auth = oAuth2Auth;
    }

    @Override
    public void handle(RoutingContext ctx) {
        User user = ctx.user();

        ctx.session().destroy();
        ctx.response()
        .setStatusCode(302)
        .putHeader("location", oAuth2Auth.endSessionURL(user))
        .end();
  }
}
