package com.caribu.preventivo.quotesApi;

import org.locationtech.jts.geom.Geometry;

import com.caribu.preventivo.strategy.StrategyQuery;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// This is the Model

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Quotes {

  Integer id_quotes;
  // Integer id_commission;
  Integer id_operativo;
  Integer id_fornitore;

  @JsonIgnore
  String origin_geom;
  @JsonIgnore
  String destination_geom;

  // Double olat;
  // Double olon;
  // Double dlat;
  // Double dlon;
    

  Integer costo;
  Integer lunghezza;
  Integer larghezza;
  Integer profondità;

  public JsonObject toJsonObject() {
    return JsonObject.mapFrom(this);
  }

}