package com.caribu.filiale_hib.model;

import javax.persistence.*;

@Entity
@Table(name = "operator")
public class Operator {
//Relazione con filiale!!!
    @Id
    @GeneratedValue
    private Integer id;
    private Integer userId;
    
    @ManyToOne
    @JoinColumn(name = "userId", nullable = true)

    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    private String name;
    private String surname;
    private String date;
    
    
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
 

}