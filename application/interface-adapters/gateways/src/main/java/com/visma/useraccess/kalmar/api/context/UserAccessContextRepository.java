package com.visma.useraccess.kalmar.api.context;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccessContextRepository extends JpaRepository<Context, UUID> {
  Context save(Context context);

  Optional<Context> findById(UUID contextId);
}
