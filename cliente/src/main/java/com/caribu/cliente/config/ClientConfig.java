package com.caribu.cliente.config;

import java.util.Objects;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

@Builder
@Value
@ToString
public class ClientConfig {
    int serverPort; 
    String version;
    DbConfig dbConfig;
    
    public static ClientConfig from(final JsonObject config){
        final Integer serverPort = config.getInteger(ConfigLoader.SERVER_PORT);
        if(Objects.isNull(serverPort)){
            throw new RuntimeException("Missing configuration for " + ConfigLoader.SERVER_PORT);
        }
        final String version = config.getString("version");
        if(Objects.isNull(version)){
            throw new RuntimeException("Missing configuration for version");
        }

        return ClientConfig.builder()
            .serverPort(config.getInteger(ConfigLoader.SERVER_PORT))
            .version(config.getString("version"))
            .dbConfig(parseDbConfig(config))
            .build();
    }

    private static DbConfig parseDbConfig(JsonObject config) {
        return DbConfig.builder()
            .host(config.getString(ConfigLoader.DB_HOST))
            .port(config.getInteger(ConfigLoader.DB_PORT))
            .database(config.getString(ConfigLoader.DB_DATABASE))
            .user(config.getString(ConfigLoader.DB_USER))
            .password(config.getString(ConfigLoader.DB_PASSWORD))
            .build();
    }
}
