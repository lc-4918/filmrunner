package com.keziko.dvdtek.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/*
 * Nom de classe : ThemeList
 * Description   :
 * Version       : 1.0
 * Date          : 27/12/2020
 * Copyright     : Luc CLÉMENT - lucclement38@gmail.com
 */
@NoArgsConstructor
@Data
@Entity
@Table(name="themelist")
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY,
            mappedBy = "themes")
    private List<Dvd> dvds;

    private String name;

    public Theme(String name) {
        this.name = name;
    }

    public Theme(String name, List<Dvd> dvds) {
        this.name = name;
        this.dvds = dvds;
    }

    @Override
    public String toString() {
        return "Theme{" +
                "id=" + id +
                ", dvdsCount=" + dvds.size() +
                ", name='" + name + '\'' +
                '}';
    }
}
