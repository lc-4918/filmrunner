package com.keziko.dvdtek.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
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

    public Theme(String name) {
        this.name = name;
    }

    public Theme(String name, Set<Dvd> dvds) {
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
