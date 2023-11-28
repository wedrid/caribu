package com.caribu.filiale.data;

import com.caribu.filiale.model.Client;
import com.caribu.filiale.model.ClientDTO;

import java.util.function.Function;

class ClientEntityMapper implements Function<ClientDTO, Client> {
  
  @Override
  public Client apply(ClientDTO clientDTO) {
    Client client = new Client();
    client.setId(clientDTO.getId());
    client.setUserId(clientDTO.getUserId());
    client.setName(clientDTO.getName());
    client.setSurname(clientDTO.getSurname());
    client.setDate(clientDTO.getDate());
    
    return client;
  }
}
