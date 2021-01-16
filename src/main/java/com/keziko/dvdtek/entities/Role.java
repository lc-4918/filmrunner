package com.keziko.dvdtek.entities;

import javax.persistence.*;

/*
 * Nom de classe : Role
 * Description   :
 * Version       : 1.0
 * Date          : 07/01/2021
 * Copyright     : Luc CLÉMENT - lucclement38@gmail.com
 */
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;

    public Role() {

    }

    public Role(ERole name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ERole getName() {
        return name;
    }

    public void setName(ERole name) {
        this.name = name;
    }
}
