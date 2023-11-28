package com.caribu.filiale.data;

import com.caribu.filiale.model.Client;
import com.caribu.filiale.model.ClientDTO;
import com.caribu.filiale.model.ClientList;

import java.util.Optional;
import java.util.function.Function;

class ClientDTOMapper implements Function<Client, ClientDTO> {
  @Override
  public ClientDTO apply(Client client) {
    return new ClientDTO(client.getId(), client.getUserId(), client.getName(), client.getSurname(), client.getDate());
  }
}
