package com.caribu.cliente;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(VertxExtension.class)
public class TestRequestRestApi {

  private static final Logger LOG = LoggerFactory.getLogger(TestRequestRestApi.class);

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void returns_all_assets(Vertx vertx, VertxTestContext context) throws Throwable {
    var client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));
    client.get("/requests")
        .send()
        .onComplete( context.succeeding(response -> {
            var json = response.bodyAsJsonArray();
            LOG.info("Response: {}", json);
            assertEquals("[{\"nome_richiedente\":\"pippo\"},{\"nome_richiedente\":\"pluto\"},{\"nome_richiedente\":\"mario\"},{\"nome_richiedente\":\"rossi\"}]", json.encode());
            //assertEquals("", json.encode());
            assertEquals(200, response.statusCode());
            context.completeNow();
        })
        );
  }
}
