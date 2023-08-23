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

  private Float soglia;
  private Float maxCost;
  private Float minCost;
  private StrategyQuery format;

  public JsonObject toJsonObject() {
    return JsonObject.mapFrom(this);
  }

  public void setQuery(StrategyQuery format) {
    this.format = format;
  }

  public String getQuery(Query query, String table) {
    return format.createQuery(query, table);
  }

  public String getCost() {
    String stringCost = " o.costo BETWEEN " + minCost + " AND " + maxCost;
    return stringCost;
  }

}