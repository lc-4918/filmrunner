package com.keziko.dvdtek.repositories;

import com.keziko.dvdtek.entities.Dvd;
import com.keziko.dvdtek.entities.Shortfilm;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShortfilmRepository extends PagingAndSortingRepository<Shortfilm, Long> {

    @Query("SELECT d.shortfilms from Dvd d where d.id = ?1")
    List<Shortfilm> findByDvdId(Long id);
}
