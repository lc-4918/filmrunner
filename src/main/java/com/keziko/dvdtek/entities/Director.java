package com.keziko.dvdtek.entities;

import com.keziko.dvdtek.enums.CodePaysEnum;
import lombok.Data;
import javax.persistence.*;
import java.util.List;
import java.util.Set;

/*
 * Nom de classe : Director
 * Description   :
 * Version       : 1.0
 * Date          : 15/03/2020
 * Copyright     : Luc CLÉMENT - lucclement38@gmail.com
 */
@Data
@Entity
@Table(name = "director")
public class Director {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private CodePaysEnum nationalite;

    public Director(String name) {
        this.name = name;
    }

    @ManyToMany(fetch = FetchType.LAZY,
    mappedBy = "directors")
    private List<Dvd> dvds;

    public Director() {

    }

    @Override
    public String toString() {
        return "Director{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nationalite=" + nationalite +
                ", dvdsCount=" + dvds.size() +
                '}';
    }
}
