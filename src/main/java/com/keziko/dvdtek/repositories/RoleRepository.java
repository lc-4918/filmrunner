package com.keziko.dvdtek.repositories;

import com.keziko.dvdtek.enums.ERole;
import com.keziko.dvdtek.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
