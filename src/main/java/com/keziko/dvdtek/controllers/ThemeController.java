package com.keziko.dvdtek.controllers;
import com.keziko.dvdtek.dtos.DvdListItem;
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
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/theme")
    public ResponseEntity<List<String>> findAllThemeNames(){
        try{
            List<String> result = themeService.findAllThemeNames();
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch(Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("theme/{id}")
    public ResponseEntity<List<DvdListItem>> getDtosByThemeId(@PathVariable("id") Long id) {
        try {
            List<DvdListItem> dtos = dvdService.findDtosByThemeId(id);
            return ResponseEntity.status(HttpStatus.OK).body(dtos);
        } catch (NoResultException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(value = "/theme/{name}",
            produces = "application/json",
            method=RequestMethod.PUT)
    public ResponseEntity<Theme> updateThemeName(@PathVariable("name") String name, @RequestBody String  newName){
        try{
            Theme theme = themeService.updateTheme(name,newName);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(theme);
        }catch(Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping("/theme")
    public ResponseEntity<Boolean> createTheme(@RequestBody String name){
        try{
            Boolean result = themeService.createTheme(name);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch(Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping("theme/{name}")
    public ResponseEntity<Boolean> deleteThemeByName(@PathVariable("name") String name) {
        try {
            themeService.deleteTheme(name);
            return ResponseEntity.status(HttpStatus.OK).body(true);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.OK).body(false);
        }
    }

}
