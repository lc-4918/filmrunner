package com.keziko.dvdtek.controllers;

import com.keziko.dvdtek.dtos.DvdForm;
import com.keziko.dvdtek.dtos.DvdListItem;
import com.keziko.dvdtek.dtos.ResponseMessage;
import com.keziko.dvdtek.entities.Dvd;
import com.keziko.dvdtek.services.DvdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.persistence.NoResultException;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Nom de classe : DvdController
 *
 * @author Luc CLÉMENT - lucclement38@gmail.com
 * @version 18/03/2020
 */
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class DvdController {
    private final DvdService dvdService;

    @Autowired
    public DvdController(DvdService dvdService) {
        this.dvdService = dvdService;
    }

    /**
     * Alimente la liste des films de la page d'accueil<br>
     * méthode GET {@code RestService#findAllDvd()}
     *
     * @return responseEntity contenant une liste de DTO {@link DvdListItem} ou une erreur serveur si une exception est levée
     */
    @GetMapping("/dvd")
    public ResponseEntity<List<DvdListItem>> getDtos() {
        try {
            List<DvdListItem> dtos = dvdService.findAllDto();
            if (Objects.isNull(dtos))
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            return ResponseEntity.status(HttpStatus.OK).body(dtos);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Enregistrement d'un nouveau film {@link Dvd}
     * méthode PUT {@code RestService#createDvd}
     * @param formDto objet {@link DvdForm} renvoyé par le front contenant les informations nécessaires à la création du film par le service
     * @return responseEntity contenant la liste de DTO mise à jour {@link List<DvdListItem>}
     */
    @PutMapping("/dvd")
    public ResponseEntity<DvdListItem> addDvd(@RequestBody DvdForm formDto) {
        try {
            Dvd dvd = dvdService.addDvd(formDto);
            DvdListItem dvdItem = dvdService.convertDvdToListDto(dvd);
            if (Objects.isNull(dvdItem))
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            return ResponseEntity.status(HttpStatus.OK).body(dvdItem);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Alimente la page d'édition d'un film
     * méthode GET {@code RestService#findDvdById(id: number)}
     * @param id id attribut identifiant du film de type {@link Long} contenu dans l'URL
     * @return responseEntity contenant le film {@link DvdForm} servant à alimenter les balises de la page ou une erreur serveur si une exception est levée
     */
    @GetMapping("/dvd/{id}")
    public ResponseEntity<DvdForm> getDvd(@PathVariable("id") Long id) {
        try {
            Optional<Dvd> optionalDvd = dvdService.findDvdById(id);
            if (optionalDvd.isPresent()) {
                Dvd dvd = optionalDvd.get();
                DvdForm editDvd = dvdService.convertDvdToFormDto(dvd);
                return ResponseEntity.status(HttpStatus.OK).body(editDvd);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Met à jour les attributs d'un film.<br>
     * Méthode exécutée à la soumission du formulaire de la page d'édition d'un film
     * méthode PUT {@code RestService#updateDvd(dvdForm: DvdForm)}
     * @param dvdForm film mis à jour de type {@link DvdForm} renvoyé par le front contenant les informations nécessaires à la création du film par le service
     * @param id attribut identifiant du réalisateur de type {@link Long} contenu dans l'URL
     * @return responseEntity contenant le film de type {@link DvdListItem}
     */
    @PutMapping("/dvd/{id}")
    public ResponseEntity<DvdListItem> updateDvd(@RequestBody DvdForm dvdForm, @PathVariable("id") Long id) {
        try {
            Dvd dvd = dvdService.updateDvd(dvdForm, id);
            DvdListItem item = dvdService.convertDvdToListDto(dvd);
            return ResponseEntity.status(HttpStatus.OK).body(item);
        } catch (NoResultException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Suppression d'un film
     * méthode DELETE {@code RestService#deleteDvd(id: number)}
     * @param id attribut identifiant du film de type {@link Long} contenu dans l'URL
     * @return responseEntity contenant true si la suppression est réussie
     */
    @DeleteMapping("/dvd/{id}")
    public ResponseEntity<Boolean> deleteDvdById(@PathVariable("id") Long id) {
        try {
            dvdService.deleteDvdById(id);
            return ResponseEntity.status(HttpStatus.OK).body(true);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère le chemin du dossier où sont stockées les images attachées aux films
     * méthode GET {RestService#getImageDirectory()}
     * @return une responseEntity contenant le chemin du répertoire "img" sous forme de chaîne de caractères
     */
    @GetMapping("/context")
    public ResponseEntity<ResponseMessage> getApplicationContext() {
        try {
            String userDirectory = new File("").getAbsolutePath();
            String imageDirectory = userDirectory + "\\img\\";
            ResponseMessage message = new ResponseMessage(imageDirectory);
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Enregistrement de plusieurs films
     * @param formDtos objet {@link DvdForm} renvoyé par le front contenant les informations nécessaires à la création du film par le service
     * @return responseEntity contenant la liste de DTO mise à jour {@link List<DvdListItem>}
     */
    @PostMapping("/dvd")
    public ResponseEntity<List<DvdListItem>> addDvds(List<DvdForm> formDtos) {
        try {
            List<Dvd> dvds = dvdService.addDvds(formDtos);
            if (Objects.isNull(dvds)) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                List<DvdListItem> items = dvdService.convertDvdToListDtos(dvds);
                return ResponseEntity.status(HttpStatus.OK).body(items);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
