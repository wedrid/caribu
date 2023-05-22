package com.caribu.cliente.clientapi;

import java.math.BigDecimal;
import java.util.Random;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class Client {
    BigDecimal client_id; // to be replaced with UUID
    String ragioneSociale;
    BigDecimal dateAdded;


    public Client(String ragioneSociale) {
        this(getRandomBigDecimal(), ragioneSociale, getRandomBigDecimal());
    }

    private static BigDecimal getRandomBigDecimal() {
        Random random = new Random();
        return BigDecimal.valueOf(random.nextDouble());
    }
    public JsonObject toJsonObject() {
        return JsonObject.mapFrom(this);
    }
}
