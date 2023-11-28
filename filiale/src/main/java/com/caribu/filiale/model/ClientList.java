package com.caribu.filiale_hib.model;
    
import java.util.List;

public class ClientList {
    private List<ClientDTO> clients;

    public List<ClientDTO> getClients() {
        return clients;
    }

    public void setClients(List<ClientDTO> clients) {
        this.clients = clients;
    }

    public ClientList(List<ClientDTO> clients) {
        this.clients = clients;
    }

    
}