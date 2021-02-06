package com.keziko.dvdtek.dtos.json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Nom de classe : XlsObject
 * @version 10/01/2021
 * @author Luc CLÉMENT - lucclement38@gmail.com
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
    private String[] cms;
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
                '}';
    }
}
