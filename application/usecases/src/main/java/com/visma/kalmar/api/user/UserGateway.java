package com.visma.kalmar.api.user;

import com.visma.kalmar.api.entities.user.User;

import java.util.UUID;

public interface UserGateway {

    User save(User user);

    User findById(UUID userId);

    User findByEmail(String email);

    User update(User user);

    boolean existsByEmail(String email);

    boolean existsById(UUID userId);

    void deleteById(UUID userId);
}
