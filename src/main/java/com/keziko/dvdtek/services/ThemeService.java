package com.keziko.dvdtek.services;

import com.keziko.dvdtek.entities.Theme;
import com.keziko.dvdtek.repositories.ThemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.*;
import java.util.stream.Collectors;

/*
 * Nom de classe : ThemeService
 * Description   :
 * Version       : 1.0
 * Date          : 28/12/2020
 * Copyright     : Luc CLÉMENT - lucclement38@gmail.com
 */
@Service
public class ThemeService {

    private final ThemeRepository themeRepository;

    @Autowired
    public ThemeService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    public List<String> findAllThemeNames() {
        List<Theme> themes = themeRepository.findAll();
        if (Objects.nonNull(themes) && !themes.isEmpty()) {
            return themes.stream().map(Theme::getName).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_UNCOMMITTED)
    public boolean createTheme(String name){
        boolean result = false;
        Theme _optional = findByName(name);
        if (Objects.isNull(_optional)){
                Theme newTheme = new Theme(name);
                themeRepository.save(newTheme);
                result = true;
        }
        return result;
    }

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

    @Transactional
    public void deleteTheme(String name){
        Optional<Theme> optTheme = themeRepository.findThemeByName(name);
        optTheme.ifPresent(themeRepository::delete);
    }

    private Theme findByName(String name){
        Optional<Theme> optTheme = themeRepository.findThemeByName(name);
        return optTheme.orElse(null);
    }
}
