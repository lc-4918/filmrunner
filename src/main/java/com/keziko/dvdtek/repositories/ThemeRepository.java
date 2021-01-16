package com.keziko.dvdtek.repositories;

import com.keziko.dvdtek.entities.Dvd;
import com.keziko.dvdtek.entities.Theme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.util.List;
import java.util.Optional;

@Repository
public interface ThemeRepository extends PagingAndSortingRepository<Theme, Long> {

    Optional<Theme> findThemeByName(String name);
    @Override
    List<Theme> findAll();
}