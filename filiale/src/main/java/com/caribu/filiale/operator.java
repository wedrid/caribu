package com.caribu.filiale;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class operator {
    
    Integer id;
    String nameop;
    String surnameop;
    Integer numReq; 
    Boolean isavailable;

    public boolean isAvailable(Integer numReq){
        // Quando è disponibile ? 

        return true;
    }

    public JsonObject toJsonObject() {
        return JsonObject.mapFrom(this);
      }

  /*   public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("id", idOp);
        json.put("name", nameOp);
        json.put("cognome", cognomeOp);
        json.put("numRequest", numReqAss);
        return json;
    } */

    // OPERAZIONI SUL DATABASE
  
}