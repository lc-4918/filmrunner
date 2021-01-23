package com.keziko.dvdtek.services;

import com.keziko.dvdtek.controllers.ThemeController;
import com.keziko.dvdtek.dtos.ThemeDTO;
import com.keziko.dvdtek.entities.Theme;
import com.keziko.dvdtek.repositories.ThemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Nom de classe : ThemeService
 * @version 28/12/2020
 * @author Luc CLÉMENT - lucclement38@gmail.com
 */
@Service
public class ThemeService {

    private final ThemeRepository themeRepository;

    @Autowired
    public ThemeService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    /**
     * Récupère la liste des noms de thèmes
     * @return liste de <@code>String</@code> des noms de thèmes
     * @see ThemeController#findAllThemeNames()
     */
    public List<ThemeDTO> findAllThemes() {
        List<Theme> themes = themeRepository.findAll();
        if (Objects.nonNull(themes) && !themes.isEmpty()) {
            return themes.stream().map(th->new ThemeDTO(th.getName(),th.getColor())).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Persiste un nouveau thème
     * @param name nom du thème à créer
     * @return <@code>true</@code> si la persistence est réussie sinon false
     */
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_UNCOMMITTED)
    public Theme createTheme(String name, String color){
        Optional<Theme> _optional = themeRepository.findThemeByName(name);
        if (!_optional.isPresent()){
                Theme newTheme = new Theme(name, color);
                return themeRepository.save(newTheme);
        }
        return null;
    }

    /**
     * Met à jour un thème
     * @param name ancien nom du thème à modifier
     * @param newName nouveau nom du thème
     * @return {@code true} si la mise à jour est réussie
     * @throws NoResultException si le thème à modifier n'existe pas en base de données
     */
    @Transactional
    public Theme updateTheme(String name, String newName){
        Optional<Theme> _optional = themeRepository.findThemeByName(name);
        if(!_optional.isPresent()){
            throw new NoResultException("aucun thème n'existe avec ce nom");
        }else {
            Theme theme = _optional.get();
            theme.setName(newName);
            return themeRepository.save(theme);
        }
    }

    /**
     * Supprime un thème
     * @param name nom du thème à supprimer
     */
    @Transactional
    public void deleteTheme(String name){
        Optional<Theme> optTheme = themeRepository.findThemeByName(name);
        optTheme.ifPresent(themeRepository::delete);
    }

    /**
     * Suppression de tous les thèmes
     */
    @Transactional
    public void deleteAllThemes(){
        themeRepository.deleteAll();
    }

}
