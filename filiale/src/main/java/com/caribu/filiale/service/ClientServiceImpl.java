package com.caribu.filiale_hib.service;

import io.vertx.core.Future;

import com.caribu.filiale.data.ClientRepository;
import com.caribu.filiale.model.ClientDTO;
import com.caribu.filiale.model.ClientList;

import java.util.Optional;

public class ClientServiceImpl implements ClientService {

  private ClientRepository repository;

  public ClientServiceImpl(ClientRepository repository) {
    this.repository = repository;
  }

  @Override
  public Future<ClientDTO> createClient(ClientDTO clientDTO) {
    return repository.createClient(clientDTO);
  }

  // @Override
  // public Future<ClientDTO> updateClient(ClientDTO clientDTO) {
  // return repository.updateClient(clientDTO);
  // }

  @Override
  public Future<Optional<ClientDTO>> findClientById(Integer id) {
    return repository.findClientById(id);
  }

  // @Override
  // public Future<Void> removeClient(Integer id) {
  // return repository.removeClient(id);
  // }

  // @Override
  // public Future<ClientsList> findClientsByUser(Integer userId) {
  // return repository.findClientsByUser(userId);
  // }
}
