package com.caribu.cliente.config;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ConfigLoader {

    private static final String CONFIG_FILE = "application.yaml";

    private static final Logger LOG = LoggerFactory.getLogger(ConfigLoader.class);

    public static final String SERVER_PORT = "SERVER_PORT"; 
    static final List<String> EXPOSED_ENVIRONMENT_VARIABLES = Arrays.asList(SERVER_PORT);

    public static Future<ClientConfig> load(Vertx vertx){

        final var exposedKeys = new JsonArray();
        EXPOSED_ENVIRONMENT_VARIABLES.forEach(exposedKeys::add);
        LOG.debug("Fetch configuration for {}", exposedKeys.encode());
        var envStore = new ConfigStoreOptions()
            .setType("env")
            .setConfig(new JsonObject().put("keys", exposedKeys));
        
        var propertyStore = new ConfigStoreOptions()
            .setType("sys")
            .setConfig(new JsonObject().put("cache", false));

        var yamlStore = new ConfigStoreOptions()
            .setType("file")
            .setFormat("yaml")
            .setConfig(new JsonObject().put("path", CONFIG_FILE));

        var retriever = ConfigRetriever.create(vertx,
        new ConfigRetrieverOptions()
            .addStore(yamlStore)
            .addStore(propertyStore)
            .addStore(envStore)
            );

        return retriever.getConfig().map(ClientConfig::from);
    }
}
