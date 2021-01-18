package com.keziko.dvdtek.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Nom de classe : DvdListItem
 * @version 16/03/2020
 * @author Luc CLÉMENT - lucclement38@gmail.com
 */
@Data
@NoArgsConstructor
public class DvdListItem {
    private Long id;
    private String titre;
    private String titreVf;
    private List<String> realisateurs;
    private Integer annee;
    private String pays;
    private String subLangues;
    private List<String> themes;

    public DvdListItem(Long id, String titre, String titreVf, List<String> realisateurs, Integer annee, String pays, String subLangues, List<String> themes) {
        this.id = id;
        this.titre = titre;
        this.titreVf = titreVf;
        this.realisateurs = realisateurs;
        this.annee = annee;
        this.pays = pays;
        this.subLangues = subLangues;
        this.themes = themes;
    }
}
