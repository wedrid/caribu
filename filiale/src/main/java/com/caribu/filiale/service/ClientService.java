package com.caribu.filiale_hib.service;

import io.vertx.core.Future;
import java.util.Optional;
import com.caribu.filiale_hib.model.ClientDTO;

public interface ClientService {

  Future<ClientDTO> createClient (ClientDTO projectDTO);

  //Future<ClientDTO> updateClient(ClientDTO projectDTO);

  Future<Optional<ClientDTO>> findClientById (Integer id);

  //Future<Void> removeClient (Integer id);

  //Future<ClientsList> findClientsByUser (Integer userId);
}
