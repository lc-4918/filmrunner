package com.keziko.dvdtek.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;

/**
 * Nom de classe : Dvd
 * @version 15/03/2020
 * @author Luc CLÉMENT - lucclement38@gmail.com
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "dvd")
public class Dvd {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "dvd_directors",
            joinColumns = {@JoinColumn(name = "dvd_id")},
            inverseJoinColumns = {@JoinColumn(name = "director_id")})
    //@SortNatural
    private Set<Director> directors;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "dvd_themes",
            joinColumns = {@JoinColumn(name = "dvd_id")},
            inverseJoinColumns = {@JoinColumn(name = "theme_id")})
    //@SortNatural
    private Set<Theme> themes;

    @OneToMany(mappedBy="dvd")
    //@SortNatural
    private Set<Shortfilm> shortfilms;

    private String imageUrl;
    private String sublangs;
    private String pays;
    private String titre;
    private String titreVf;
    private String description;
    private String details;
    private Integer annee;
    private Integer duree;
    private Integer format;
    private Integer type;
    private Integer norme;
    private Integer support;
    private Integer source;

    public Dvd(Set<Director> directors, Set<Theme> themes, Set<Shortfilm> shortfilms, String imageUrl, String sublangs, String pays, String titre, String titreVf, String description, String details, Integer annee, Integer duree, Integer format, Integer type, Integer norme, Integer support, Integer source) {
        this.directors = directors;
        this.themes = themes;
        this.shortfilms = shortfilms;
        this.imageUrl = imageUrl;
        this.sublangs = sublangs;
        this.pays = pays;
        this.titre = titre;
        this.titreVf = titreVf;
        this.description = description;
        this.details = details;
        this.annee = annee;
        this.duree = duree;
        this.format = format;
        this.type = type;
        this.norme = norme;
        this.support = support;
        this.source = source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dvd dvd = (Dvd) o;
        return Objects.equals(titre, dvd.titre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titre);
    }
}
