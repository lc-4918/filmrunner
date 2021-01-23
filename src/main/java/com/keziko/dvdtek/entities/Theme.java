package com.keziko.dvdtek.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

/**
 * Nom de classe : ThemeList
 * @version 27/12/2020
 * @author Luc CLÉMENT - lucclement38@gmail.com
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
    private Set<Dvd> dvds;

    private String name;

    private String color;

    public Theme(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Theme(String name, Set<Dvd> dvds) {
        this.name = name;
        this.dvds = dvds;
    }

}
