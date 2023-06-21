package com.caribu.cliente.clientapi;

import java.math.BigDecimal;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;


//NOTE: these annotations are very important. If I use @Value instead of @Data, the json serialization will not work because @Value generates an immutable class
// and ALSO, we need the NoArgsConstructor in order for jackson to be able to deserialize the json into the object
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Client {
    @JsonProperty("client_id")
    BigDecimal clientId; // to be replaced with UUID
    @JsonProperty("ragione_sociale")
    String ragioneSociale;
    @JsonProperty("date_added")
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
