package com.keziko.dvdtek.controllers;

import com.keziko.dvdtek.dtos.DvdListItem;
import com.keziko.dvdtek.dtos.ResponseMessage;
import com.keziko.dvdtek.entities.Director;
import com.keziko.dvdtek.services.DirectorService;
import com.keziko.dvdtek.services.DvdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.NoResultException;
import java.util.List;

/**
 * Nom de classe : DirectorController
 * @version 18/03/2020
 * @author Luc CLÉMENT - lucclement38@gmail.com
 */

@Slf4j
@RestController
public class DirectorController {

    private final DirectorService directorService;
    private final DvdService dvdService;


    @Autowired
    public DirectorController(DirectorService directorService, DvdService dvdService) {
        this.directorService = directorService;
        this.dvdService = dvdService;
    }

    /**
     * Alimente la liste de films d'un réalisateur<br>
     * méthode GET {@code RestService#getDirectorNamebyId(id: number)}
     * @param id attribut identifiant du réalisateur de type {@link Long} contenu dans l'URL
     * @return responseEntity contenant une liste de DTO {@link DvdListItem} ou une erreur serveur si une exception est levée
     */
    @GetMapping("director/{id}")
    public ResponseEntity<List<DvdListItem>> getDtosByDirectorId(@PathVariable("id") Long id) {
        try {
            List<DvdListItem> dtos = dvdService.findDtosByDirectorId(id);
            return ResponseEntity.status(HttpStatus.OK).body(dtos);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Enregistre la mise à jour du nom du réalisateur<br>
     * méthode PATCH {@code RestService#getDirectorIdByName(id: number, name: string)}
     * @param id attribut identifiant unique du réalisateur à modifier contenu dans l'URL
     * @param name nouvelle valeur pour le nom du réalisateur contenue dans le corps de la requête
     * @return true si la misa à jour est réussie et une erreur serveur si la mise à jour a échoué
     */
    @PatchMapping("/director/{id}")
    public ResponseEntity<Boolean> updateDirectorName(@PathVariable("id") Long id, @RequestBody String  name){
        try{
            Director director = directorService.updateDirector(name,id);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(true);
        }catch(Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Alimente le composant autocomplete du formulaire d'ajout et d'édition de film<br>
     * {@code RestService#getAllDirectorsName()}
     * @return la liste des noms de réalisateurs existants
     */
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/director")
    public ResponseEntity<List<String>>getAllDirectors(){
        try{
            List<String> directors = directorService.findAllDirectorNames();
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(directors);
        }catch(Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Permet de naviguer vers le formulaire d'édition du réalisateur
     * Méthode GET {@code RestService#getDirectorIdByName(name: string)}
     * @param name nom du réalisateur dans la liste de films
     * @return {@link ResponseEntity} contenant l'attribut id identifiant unique du réalisateur
     */
    @GetMapping("/director/")
    public ResponseEntity<String>findDirectorIdByName(@RequestParam("name") String name){
        try{
            Long directorId = directorService.findDirectorIdByName(name);
            return ResponseEntity.status(HttpStatus.OK).body(directorId.toString());
        }catch (NoResultException e){
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère le nom du réalisateur pour alimenter le modal de modification du nom du réalisateur
     * Méthode GET {@code RestService#getDirectorNamebyId(id: number)}
     * @param id est l'attribut identifiant unique du réalisateur contenu dans l'URL
     * @return une ResponseEntity ayant pour corps un {@link ResponseMessage} contenant le nom du réalisateur ou bien une erreur serveur
     */
    @GetMapping("/director/id/{id}")
    public ResponseEntity<ResponseMessage>findDirectorNameById(@PathVariable("id") Long id){
        try{
            String directorName = directorService.findDirectorNameById(id);
            ResponseMessage message = new ResponseMessage(directorName);
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }catch (NoResultException e){
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/director/{id}")
    public ResponseEntity<Boolean>deleteDirector(@PathVariable("id") Long id){
        try {
            directorService.deleteDirectorById(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
