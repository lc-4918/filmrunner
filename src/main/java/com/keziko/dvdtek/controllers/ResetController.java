package com.keziko.dvdtek.controllers;

import com.keziko.dvdtek.dtos.ResponseMessage;
import com.keziko.dvdtek.services.DirectorService;
import com.keziko.dvdtek.services.DvdService;
import com.keziko.dvdtek.services.ThemeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Nom de classe : ResetController
 * @version 17/01/2021
 * @author Luc CLÉMENT - lucclement38@gmail.com
 */
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class ResetController {

    private final DvdService dvdService;
    private final DirectorService directorService;
    private final ThemeService themeService;


    @Autowired
    public ResetController(DvdService dvdService, DirectorService directorService, ThemeService themeService) {
        this.dvdService = dvdService;
        this.directorService = directorService;
        this.themeService = themeService;
    }

    /**
     * Supprime les entités existantes en base de données (films, court-métrages, réalisateurs et thèmes)
     * Ne supprime pas les tables ni les séquences
     * méthode GET {RestService#cleanDb()}
     * @return un objet de type {@link ResponseMessage}. Renvoie true et un message de succès si la réinitialisation est réussie ou bien le message d'erreur et false
     * @see DvdService#deleteAllDvdsDirectorsThemesAndShortFilm() (sous-méthode)
     */
    @GetMapping("/reset/cleandb")
    public ResponseEntity<ResponseMessage> cleandb(){
        ResponseMessage message;
        try{
            log.info("GET /reset/cleandb");
            dvdService.deleteAllDvdsDirectorsThemesAndShortFilm();

            message = new ResponseMessage("Nettoyage de la base de données réussie",true);
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }catch (Exception e){
            message = new ResponseMessage(e.getMessage(),false);
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }
    }
}
