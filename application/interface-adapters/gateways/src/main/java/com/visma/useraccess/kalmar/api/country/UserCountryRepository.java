package com.visma.useraccess.kalmar.api.country;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserCountryRepository extends JpaRepository<Country, UUID> {
    Optional<Country> findByCode(String code);

    boolean existsByCode(String code);
}
