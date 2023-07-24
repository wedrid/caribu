package com.caribu.preventivo.strategy;
import com.caribu.preventivo.strategy.StrategyQuery;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Query {
    
    String id_quotes;
    String id_commission;

    private Float oLat;
    private Float oLon;
    private Float dLat;
    private Float dLon;
    private Integer maxCost = 1000000;
    private Integer minCost = 0;
    private StrategyQuery format;

    public JsonObject toJsonObject() {
        return JsonObject.mapFrom(this);
      }
      
    public void setQuery(StrategyQuery format) {
      this.format = format;
    }
    public String getQuery(Query query) {
      return format.createQuery(query);
    }

    public void setCost(int maxCost, int minCost) {
      this.maxCost = maxCost;
      this.minCost = minCost;
    }

    public String getCost() {
      String stringCost = "AND o.cost BETWEEN " + minCost +" AND " + maxCost;
      return stringCost;
    }
  
}