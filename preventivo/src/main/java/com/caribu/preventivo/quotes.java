package com.caribu.preventivo;
import com.caribu.preventivo.strategy.StrategyQuery;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class quotes {//metto maiuscolo
    
 String id_quotes;
 String id_commission;

Double oLat;
Double oLon;
Double dLat;
Double dLon;
Integer maxCost;
Integer minCost;
private StrategyQuery format;

//                 operativo VARCHAR(10),
//                 lunghezza INTEGER,
//                 larghezza INTEGER, 
//                 profondità INTEGER,
//                 id_fornitore VARCHAR,
//                 costo INTEGER,

//etc...
   
    public JsonObject toJsonObject() {
        return JsonObject.mapFrom(this);
      }

   
    
  
}