package com.keziko.dvdtek.entities;

import com.keziko.dvdtek.enums.CodePaysEnum;
import lombok.Data;
import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

/**
 * Nom de classe : Director
 * @version 15/03/2020
 * @version Luc CLÉMENT - lucclement38@gmail.com
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
    private Set<Dvd> dvds;

    public Director() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Director director = (Director) o;
        return Objects.equals(id, director.id) && Objects.equals(name, director.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
