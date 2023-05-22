package com.caribu.cliente.config;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DbConfig {
    String host; // = "localhost";
    int port; // = 5432;
    String database; // = "vertx_richieste";
    String user; // = "postgres";
    String password; // = "secret";


    @Override
    public String toString(){
        return "DbConfig{" +
            "host='" + host + '\'' +
            ", port=" + port +
            ", database='" + database + '\'' +
            ", user='" + user + '\'' +
            ", password='****'";
    }
}
