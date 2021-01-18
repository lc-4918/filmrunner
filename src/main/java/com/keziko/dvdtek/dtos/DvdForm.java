package com.keziko.dvdtek.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Nom de classe : DvdForm
 * @version 18/03/2020
 * @author Luc CLÉMENT - lucclement38@gmail.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DvdForm {

    private Long id;

    private String titre;
    private String titrevf;
    private Integer annee;
    private Integer duree;
    private String description;
    private String imageUrl;
    private Integer support;
    private Integer type;
    private Integer norme;
    private String details;
    private Integer format;
    private Integer source;

    private List<String> subLangs;
    private List<String> pays;
    private List<String> realisateurs;
    private List<String> shortfilms;
    private List<String> themes;


}
