package com.caribu.filiale_hib.data;

import com.caribu.filiale_hib.model.Operator;
import com.caribu.filiale_hib.model.OperatorDTO;
import com.caribu.filiale_hib.model.OperatorList;

import java.util.Optional;
import java.util.function.Function;

class OperatorDTOMapper implements Function<Operator, OperatorDTO> {
  @Override
  public OperatorDTO apply(Operator operator) {
    return new OperatorDTO(operator.getId(), operator.getUserId(), operator.getName(), operator.getSurname(), operator.getDate());
  }
}
