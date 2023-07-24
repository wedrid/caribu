package com.caribu.preventivo.eliminare;

import java.util.List;

import com.caribu.preventivo.quotes;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WatchList {

  List<quotes> op;

  JsonObject toJsonObject() {
    return JsonObject.mapFrom(this);
  }
}
