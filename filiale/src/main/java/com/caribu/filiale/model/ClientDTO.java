package com.caribu.filiale_hib.model;

import java.time.LocalDateTime;
import java.util.Optional;

public class ClientDTO {
    private Integer id;
    private Integer userId;
    
    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    private String name;
    private String surname;
    private String date;

    public ClientDTO(Integer id, Integer userId, String name, String surname, String date) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.date = date;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

  

    // getters and setters
}