package com.visma.useraccess.kalmar.api.language;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LanguageRepository extends JpaRepository<Language, UUID> {
    
    Optional<Language> findByCode(String code);
    
    boolean existsByCode(String code);
}
