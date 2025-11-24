package com.visma.kalmar.api.adapters.user;

import com.visma.kalmar.api.entities.user.User;
import com.visma.kalmar.api.exception.ResourceNotFoundException;
import com.visma.kalmar.api.user.UserGateway;
import com.visma.useraccess.kalmar.api.user.UserRepository;

import java.util.UUID;

public class UserGatewayAdapter implements UserGateway {

    private final UserRepository userAccessUserRepository;

    public UserGatewayAdapter(UserRepository userAccessUserRepository) {
        this.userAccessUserRepository = userAccessUserRepository;
    }

    @Override
    public User save(User user) {
        var userAccessUser = new com.visma.useraccess.kalmar.api.user.User(
                user.idUser(),
                user.idLanguage(),
                user.email(),
                user.firstName(),
                user.lastName(),
                user.recordVersion(),
                user.whenEdited()
        );

        var savedUser = userAccessUserRepository.save(userAccessUser);

        return new User(
                savedUser.getIdUser(),
                savedUser.getIdLanguage(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getRecordVersion(),
                savedUser.getWhenEdited()

        );
    }

    @Override
    public User findById(UUID userId) {
        return userAccessUserRepository.findById(userId)
                .map(this::toDomainUser)
                .orElseThrow(() -> new ResourceNotFoundException("User", "User not found with id: " + userId));
    }

    @Override
    public User findByEmail(String email) {
        return userAccessUserRepository.findByEmail(email)
                .map(this::toDomainUser)
                .orElseThrow(() -> new ResourceNotFoundException("User", "User not found with email: " + email));
    }

    @Override
    public User update(User user) {
        // Verify user exists first
        userAccessUserRepository.findById(user.idUser())
                .orElseThrow(() -> new ResourceNotFoundException("User", "User not found with id: " + user.idUser()));

        var userAccessUser = new com.visma.useraccess.kalmar.api.user.User(
                user.idUser(),
                user.idLanguage(),
                user.email(),
                user.firstName(),
                user.lastName(),
                user.recordVersion(),
                user.whenEdited()
        );

        var updatedUser = userAccessUserRepository.save(userAccessUser);

        return new User(
                updatedUser.getIdUser(),
                updatedUser.getIdLanguage(),
                updatedUser.getEmail(),
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getRecordVersion(),
                updatedUser.getWhenEdited()
        );
    }

    @Override
    public boolean existsByEmail(String email) {
        return userAccessUserRepository.existsByEmail(email);
    }

    @Override
    public boolean existsById(UUID userId) {
        return userAccessUserRepository.existsById(userId);
    }

    @Override
    public void deleteById(UUID userId) {
        // Find the user to verify it exists before deleting
        var userAccessUser = userAccessUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User",
                        "User not found with id: " + userId));

        // Delete the user entity
        userAccessUserRepository.delete(userAccessUser);
    }

    private User toDomainUser(com.visma.useraccess.kalmar.api.user.User userAccessUser) {
        return new User(
                userAccessUser.getIdUser(),
                userAccessUser.getIdLanguage(),
                userAccessUser.getEmail(),
                userAccessUser.getFirstName(),
                userAccessUser.getLastName(),
                userAccessUser.getRecordVersion(),
                userAccessUser.getWhenEdited()
        );
    }
}
