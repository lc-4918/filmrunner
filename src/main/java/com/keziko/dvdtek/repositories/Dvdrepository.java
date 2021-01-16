package com.keziko.dvdtek.repositories;

import com.keziko.dvdtek.entities.Director;
import com.keziko.dvdtek.entities.Dvd;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.SortedSet;

/*
 * Nom de classe : Dvdrepository
 * Description   :
 * Version       : 1.0
 * Date          : 16/03/2020
 * Copyright     : Luc CLÉMENT - lucclement38@gmail.com
 */
@Repository
public interface Dvdrepository extends PagingAndSortingRepository<Dvd, Long> {
    @Query("SELECT d.dvds from Director d where d.id = ?1")
    List<Dvd> findAllByDirectorId(Long id);

    @Query("SELECT t.dvds from Theme t where t.id = ?1")
    List<Dvd> findAllByThemeId(Long id);

    @Query("SELECT t.dvds from Theme t where t.name = ?1")
    List<Dvd> findAllByThemeName(String name);

    List<Dvd> findAllByImageUrl(String imageUrl);
}
