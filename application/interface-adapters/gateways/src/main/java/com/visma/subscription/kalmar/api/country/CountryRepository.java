package com.visma.subscription.kalmar.api.country;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, UUID> {
  Optional<Country> findByCode(String code);
}
