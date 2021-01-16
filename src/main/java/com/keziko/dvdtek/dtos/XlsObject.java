package com.keziko.dvdtek.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Nom de classe : XlsObject
 * Description   :
 * Version       : 1.0
 * Date          : 10/01/2021
 * Copyright     : Luc CLÉMENT - lucclement38@gmail.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class XlsObject {
    private String format;
    private String type;
    private String titre;
    private String annee;
    private String realisateurs;
    private String realisateursId;
    private String pays;
    private String duree;
    private String sub;
    private String support;
    private String norme;
    private String details;
    private String source;
    private String titreVf;
    private String themes;
    private String cm1;
    private String cm2;
    private String cm3;
    private String cm4;
    private String cm5;
    private Integer ligneFichier;

    @Override
    public String toString() {
        return "XlsObject{" +
                "format='" + format + '\'' +
                ", type='" + type + '\'' +
                ", titre='" + titre + '\'' +
                ", annee='" + annee + '\'' +
                ", realisateurs='" + realisateurs + '\'' +
                ", realisateursId='" + realisateursId + '\'' +
                ", pays='" + pays + '\'' +
                ", duree='" + duree + '\'' +
                ", sub='" + sub + '\'' +
                ", support='" + support + '\'' +
                ", norme='" + norme + '\'' +
                ", details='" + details + '\'' +
                ", source='" + source + '\'' +
                ", titreVf='" + titreVf + '\'' +
                ", themes='" + themes + '\'' +
                ", cm1='" + cm1 + '\'' +
                ", cm2='" + cm2 + '\'' +
                ", cm3='" + cm3 + '\'' +
                ", cm4='" + cm4 + '\'' +
                ", cm5='" + cm5 + '\'' +
                '}';
    }
}
