package com.caribu.cliente;

import java.math.BigDecimal;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CompanyRequests {
    String nomeAzienda;
    String request1; //TODO: this will be an array of requests   
    String request2;
    BigDecimal numRequests;

    public JsonObject toJsonObject(){
        return JsonObject.mapFrom(this);
    }
}
