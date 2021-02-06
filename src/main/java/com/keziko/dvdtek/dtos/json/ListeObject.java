package com.keziko.dvdtek.dtos.json;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

/**
 * Nom de classe : ListeObject
 * Objet contenant les listes de valeurs format(type), type(genre), support, norme, détails et source<br>
 * Sert à générer une ligne du fichier excel exporté
 * @version 18/01/2021
 * @author Luc CLÉMENT - lucclement38@gmail.com
 */
@Data
@NoArgsConstructor
public class ListeObject {
    private List<JsonItem> genres;
    private List<JsonItem> normes;
    private List<JsonItem> supports;
    private List<JsonItem> types;
    private List<JsonItem> details;
    private List<JsonItem> sources;

    public String getGenreById(int id) {
        String result;
        switch (id){
            case 1:
                result = "DOC";
                break;
            case 2:
                result = "ANI";
                break;
            case 3:
                result = "FIC";
                break;
            case 4:
                result = "EXP";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + id);
        }
        return result;
    }

    public String getLabelById(Integer id, List<JsonItem> liste){
        String result = null;
        if (Objects.nonNull(id) && Objects.nonNull(liste)){
            for (JsonItem item : liste){
                if (id==(item.getId())){
                    result = item.getLabel();
                    break;
                }
            }
        }
        return result;
    }
}
