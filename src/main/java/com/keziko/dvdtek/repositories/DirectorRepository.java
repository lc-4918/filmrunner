package com.keziko.dvdtek.repositories;

import com.keziko.dvdtek.entities.Director;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DirectorRepository extends PagingAndSortingRepository<Director, Long>  {

    Optional<Director> findByName(String name);

    @Query("SELECT u.id FROM Director u WHERE u.name = ?1")
    List<Long> getDirectorIdByName(String name);

    @Query("SELECT u.name FROM Director u WHERE u.id = ?1")
    Optional<String> getDirectorNameById(Long id);

    @Query("SELECT u.name FROM Director u")
    List<String> getDirectorNames();

}
