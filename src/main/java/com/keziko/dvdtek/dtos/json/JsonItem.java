package com.keziko.dvdtek.dtos.json;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Nom de classe : JsonItem
 * Objet contenu dans les listes de l'objet ligne excel parsé dans la classe {@link ListeObject}
 * @version 18/01/2021
 * @author Luc CLÉMENT - lucclement38@gmail.com
 */
@Data
@NoArgsConstructor
public class JsonItem {
    private String label;
    private int id;
    private String abbr;

    public JsonItem(String label, int id) {
        this.label = label;
        this.id = id;
    }

    public JsonItem(String label, int id, String abbr) {
        this.label = label;
        this.id = id;
        this.abbr = abbr;
    }
}
