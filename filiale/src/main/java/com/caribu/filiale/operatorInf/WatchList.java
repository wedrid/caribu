package com.caribu.filiale.operatorInf;

import java.util.List;

import com.caribu.filiale.operator;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WatchList {

  List<operator> op;

  JsonObject toJsonObject() {
    return JsonObject.mapFrom(this);
  }
}
