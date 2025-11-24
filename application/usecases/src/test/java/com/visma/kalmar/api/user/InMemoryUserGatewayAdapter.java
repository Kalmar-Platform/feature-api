package com.visma.kalmar.api.user;

import com.visma.kalmar.api.entities.user.User;
import com.visma.kalmar.api.exception.ResourceNotFoundException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserGatewayAdapter implements UserGateway {

    private final Map<UUID, User> database = new ConcurrentHashMap<>();
    private final Map<String, User> emailIndex = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        database.put(user.idUser(), user);
        emailIndex.put(user.email(), user);
        return user;
    }

    @Override
    public User findById(UUID userId) {
        User user = database.get(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User", "User not found with id: " + userId);
        }
        return user;
    }

    @Override
    public User findByEmail(String email) {
        User user = emailIndex.get(email);
        if (user == null) {
            throw new ResourceNotFoundException("User", "User not found with email: " + email);
        }
        return user;
    }

    @Override
    public User update(User user) {
        if (!database.containsKey(user.idUser())) {
            throw new ResourceNotFoundException("User", "User not found with id: " + user.idUser());
        }
        database.put(user.idUser(), user);
        emailIndex.put(user.email(), user);
        return user;
    }

    @Override
    public boolean existsByEmail(String email) {
        return emailIndex.containsKey(email);
    }

    @Override
    public boolean existsById(UUID userId) {
        return database.containsKey(userId);
    }

    @Override
    public void deleteById(UUID userId) {
        User user = database.get(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User", "User not found with id: " + userId);
        }
        database.remove(userId);
        emailIndex.remove(user.email());
    }

    public void clear() {
        database.clear();
        emailIndex.clear();
    }
}
