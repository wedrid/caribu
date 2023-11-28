package com.caribu.filiale.data;


import io.vertx.core.Future;

import com.caribu.filiale.model.ClientDTO;
import com.caribu.filiale.model.ClientList;

import java.util.Optional;

// espongo solo le interfaccie delle funzioni HTTP
public interface ClientRepository {

  Future<ClientDTO> createClient (ClientDTO client);

  // Future<TaskDTO> updateTask (TaskDTO task);

  // Future<Void> removeTask (Integer id);

  Future<Optional<ClientDTO>> findClientById (Integer id);

  // Future<TasksList> findTasksByUser (Integer userId);
}
