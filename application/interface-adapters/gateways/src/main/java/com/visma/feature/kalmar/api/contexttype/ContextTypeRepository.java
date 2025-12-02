package com.visma.feature.kalmar.api.contexttype;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContextTypeRepository extends JpaRepository<ContextType, UUID> {

    Optional<ContextType> findByName(String name);
}
