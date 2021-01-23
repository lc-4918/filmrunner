package com.keziko.dvdtek.controllers;

import com.keziko.dvdtek.dtos.DvdListItem;
import com.keziko.dvdtek.dtos.ThemeDTO;
import com.keziko.dvdtek.entities.Theme;
import com.keziko.dvdtek.services.DvdService;
import com.keziko.dvdtek.services.ThemeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.persistence.NoResultException;
import java.util.List;

/**
 * Nom de classe : ThemeController
 * @version 28/12/2020
 * @author  Luc CLÉMENT - lucclement38@gmail.com
 */
@Slf4j
@RestController
public class ThemeController {
    private final DvdService dvdService;
    private final ThemeService themeService;

    @Autowired
    public ThemeController(DvdService dvdService,ThemeService themeService) {
        this.dvdService = dvdService;
        this.themeService = themeService;
    }

    /**
     * Récupère la liste des noms de thèmes<br>
     * Alimente les options de la balise select du formulaire d'ajout et de modification d'un film ("/add", "/edit")<br>
     * Alimente la popup du gestionnaire de thèmes
     * @return {@link ResponseEntity} contenant la liste des noms de thèmes de la base de données
     */
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/theme")
    public ResponseEntity<List<ThemeDTO>> findAllThemeNames(){
        try{
            log.info("GET /theme");
            List<ThemeDTO> result = themeService.findAllThemes();
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch(Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Liste des films par thème
     * @param id attribut identifiant unique d'une entité persistente thème de type {@link Theme}
     * @return responseEntity liste de DTO films {@link DvdListItem}
     */
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("theme/{id}")
    public ResponseEntity<List<DvdListItem>> getDtosByThemeId(@PathVariable("id") Long id) {
        try {
            log.info("GET /theme/id | id = {}",id);
            List<DvdListItem> dtos = dvdService.findDtosByThemeId(id);
            return ResponseEntity.status(HttpStatus.OK).body(dtos);
        } catch (NoResultException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Modifie le nom d'un thème
     * @param name ancien nom du thème
     * @param newName nouveau nom du thème
     * @return responseEntity contenant l'entité persistente {@link Theme} si la modification a réussi ou bien une erreur serveur
     */
    @RequestMapping(value = "/theme/{name}",
            produces = "application/json",
            method=RequestMethod.PUT)
    public ResponseEntity<ThemeDTO> updateThemeName(@PathVariable("name") String name, @RequestBody String  newName){
        try{
            log.info("PUT /theme/id | name = {}, newName = {}",name,newName);
            Theme theme = themeService.updateTheme(name,newName);
            ThemeDTO themeDTO = new ThemeDTO(theme.getName(),theme.getColor());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(themeDTO);
        }catch(Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Création d'un nouveau thème
     * Méthode utilisée par la popup du gestionnaire de thèmes
     * méthode PUT {@code RestService#createTheme}
     * @param theme est le {@link ThemeDTO} contenant le nom et la couleur du {@link Theme} à créer
     * @return responseEntity contenant <@code>true</@code> si la création a été effectuée ou bien une erreur serveur
     */
    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping("/theme")
    public ResponseEntity<Theme> createTheme(@RequestBody ThemeDTO theme){
        try{
            log.info("PUT /theme | name = {}",theme);
            Theme result = themeService.createTheme(theme.getName(),theme.getColor());
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch(Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Supprime un thème
     * Méthode utilisée sur le bouton de suppression d'un thème dans la popup du gestionnaire de thèmes
     * @param name nom du thème à supprimer
     * @return responseEntity contenant <@code>true</@code> si la création a été effectuée ou bien <@code>false</@code>
     */
    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping("theme/{name}")
    public ResponseEntity<Boolean> deleteThemeByName(@PathVariable("name") String name) {
        try {
            log.info("DELETE /theme | name = {}",name);
            themeService.deleteTheme(name);
            return ResponseEntity.status(HttpStatus.OK).body(true);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.OK).body(false);
        }
    }

}
