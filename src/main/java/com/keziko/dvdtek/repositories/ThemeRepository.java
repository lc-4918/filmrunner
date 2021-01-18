package com.keziko.dvdtek.repositories;

import com.keziko.dvdtek.entities.Theme;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThemeRepository extends PagingAndSortingRepository<Theme, Long> {

    Optional<Theme> findThemeByName(String name);
    @Override
    List<Theme> findAll();
}