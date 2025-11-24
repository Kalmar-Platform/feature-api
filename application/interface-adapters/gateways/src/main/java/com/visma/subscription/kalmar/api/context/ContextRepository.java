package com.visma.subscription.kalmar.api.context;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ContextRepository extends JpaRepository<Context, UUID> {
    Context save(Context context);

    Optional<Context> findById(UUID contextId);
}
