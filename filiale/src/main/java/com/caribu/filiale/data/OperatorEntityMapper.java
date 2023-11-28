package com.caribu.filiale_hib.data;

import com.caribu.filiale_hib.model.Operator;
import com.caribu.filiale_hib.model.OperatorDTO;

import java.util.function.Function;

class OperatorEntityMapper implements Function<OperatorDTO, Operator> {
  
  @Override
  public Operator apply(OperatorDTO operatorDTO) {
    Operator operator = new Operator();
    operator.setId(operatorDTO.getId());
    operator.setUserId(operatorDTO.getUserId());
    operator.setName(operatorDTO.getName());
    operator.setSurname(operatorDTO.getSurname());
    operator.setDate(operatorDTO.getDate());
    
    return operator;
  }
}
