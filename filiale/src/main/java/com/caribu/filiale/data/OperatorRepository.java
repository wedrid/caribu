package com.caribu.filiale_hib.data;


import io.vertx.core.Future;

import com.caribu.filiale_hib.model.OperatorDTO;
import com.caribu.filiale_hib.model.OperatorList;

import java.util.Optional;

// espongo solo le interfaccie delle funzioni HTTP
public interface OperatorRepository {

  Future<OperatorDTO> createOperator (OperatorDTO operator);

  // Future<TaskDTO> updateTask (TaskDTO task);

  // Future<Void> removeTask (Integer id);

  Future<Optional<OperatorDTO>> findOperatorById (Integer id);

  // Future<TasksList> findTasksByUser (Integer userId);
}
