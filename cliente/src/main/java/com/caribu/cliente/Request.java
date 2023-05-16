package com.caribu.cliente;

public class Request {
    private final String nome_richiedente;
    public Request(final String nome_richiedente) {
        this.nome_richiedente = nome_richiedente;
    }

    public String getNome_richiedente() {
        return nome_richiedente;
    }
}
