package com.caribu.filiale.service;

import com.caribu.filiale.model.Operator;
import com.caribu.filiale.model.OperatorDTO;
import com.caribu.filiale.model.OperatorList;

import io.vertx.core.Future;


import java.util.Optional;

public interface OperatorService {

  Future<OperatorDTO> createOperator (OperatorDTO operatorDTO);

  //Future<OperatorDTO> updateOperator(Principal principal, OperatorDTO operatorDTO);

  Future<Optional<OperatorDTO>> findOperatorById (Integer id);

  //Future<Void> removeOperator (Principal principal, Integer id);

  //Future<OperatorsList> findProjectsByUser (Integer userId);
}
