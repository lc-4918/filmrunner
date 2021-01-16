package com.keziko.dvdtek.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SortNatural;

import javax.persistence.*;
import java.util.*;

/*
 * Nom de classe : Dvd
 * Description   :
 * Version       : 1.0
 * Date          : 15/03/2020
 * Copyright     : Luc CLÉMENT - lucclement38@gmail.com
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
    @SortNatural
    private List<Director> directors;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "dvd_themes",
            joinColumns = {@JoinColumn(name = "dvd_id")},
            inverseJoinColumns = {@JoinColumn(name = "theme_id")})
    @SortNatural
    private List<Theme> themes;

    @OneToMany(mappedBy="dvd")
    @SortNatural
    private List<Shortfilm> shortfilms;

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

    public Dvd(List<Director> directors, List<Theme> themes, List<Shortfilm> shortfilms, String imageUrl, String sublangs, String pays, String titre, String titreVf, String description, String details, Integer annee, Integer duree, Integer format, Integer type, Integer norme, Integer support, Integer source) {
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
    public String toString() {
        return "Dvd{" +
                "id=" + id +
                ", directors=" + directors +
                ", themes=" + themes +
                ", shortfilms=" + shortfilms +
                ", imageUrl='" + imageUrl + '\'' +
                ", sublangs='" + sublangs + '\'' +
                ", pays='" + pays + '\'' +
                ", titre='" + titre + '\'' +
                ", titreVf='" + titreVf + '\'' +
                ", description='" + description + '\'' +
                ", details='" + details + '\'' +
                ", annee=" + annee +
                ", duree=" + duree +
                ", format=" + format +
                ", type=" + type +
                ", norme=" + norme +
                ", support=" + support +
                ", source=" + source +
                '}';
    }
}
