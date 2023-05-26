package com.caribu.richiesta.requestapi;

import java.math.BigDecimal;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    @JsonProperty("request_id")
    BigDecimal requestId;
    @JsonProperty("client_id")
    BigDecimal clientId;
    @JsonProperty("request_date")
    BigDecimal requestDate;
    @JsonProperty("filiale_id")
    BigDecimal filialeId;
    int depth; 
    int width;
    int height;
    int weight;
    @JsonProperty("tratta_id")
    BigDecimal trattaId;

    JsonObject toJsonObject() {
        return JsonObject.mapFrom(this);
    }

    public HashMap<String, Object> getHashMap(){
        HashMap<String, Object> params = new HashMap<>();
        params.put("client_id", clientId);
        params.put("request_date", requestDate);
        params.put("filiale_id", filialeId);
        params.put("depth", depth);
        params.put("width", width);
        params.put("height", height);
        params.put("weight", weight);
        params.put("tratta_id", trattaId);
        return params;
    }
}
