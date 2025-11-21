package com.visma.kalmar.api.vismaconnect;

import com.visma.kalmar.api.entities.user.User;
import com.visma.kalmar.api.exception.ConnectUserException;
import com.visma.kalmar.api.exception.ResourceNotFoundException;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryVismaConnectUserGatewayAdapter implements VismaConnectUserGateway {

    private final Map<UUID, User> users = new ConcurrentHashMap<>();
    private final Map<String, User> usersByEmail = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> unlinkedUsers = new ConcurrentHashMap<>();
    
    private boolean shouldFailOnCreate = false;
    private boolean shouldFailOnUpdate = false;
    private boolean shouldFailOnUnlink = false;
    private boolean shouldReturnAlreadyUnlinked = false;

    @Override
    public UUID createUser(User user, String languageCode) {
        if (shouldFailOnCreate) {
            throw new ConnectUserException("Failed to create user in Visma Connect", 500, "ERROR_INTERNAL");
        }
        
        UUID userId = user.idUser() != null ? user.idUser() : UUID.randomUUID();
        User createdUser = new User(
                userId,
                user.idLanguage(),
                user.email(),
                user.firstName(),
                user.lastName(),
                user.recordVersion(),
                user.whenEdited()
        );
        users.put(userId, createdUser);
        usersByEmail.put(user.email(), createdUser);
        return userId;
    }

    @Override
    public void updateUser(User user, String languageCode) {
        if (shouldFailOnUpdate) {
            throw new ConnectUserException("Failed to update user in Visma Connect", 500, "ERROR_INTERNAL");
        }
        
        if (!users.containsKey(user.idUser())) {
            throw new ResourceNotFoundException("User", "User not found in Visma Connect with id: " + user.idUser());
        }
        
        users.put(user.idUser(), user);
        usersByEmail.put(user.email(), user);
    }

    @Override
    public void unlinkClient(UUID userId) {
        if (shouldReturnAlreadyUnlinked) {
            throw new ConnectUserException("ERROR_USER_UNLINKED_FROM_CLIENT", 409, "ERROR_USER_UNLINKED_FROM_CLIENT");
        }
        
        if (shouldFailOnUnlink) {
            throw new ConnectUserException("Failed to unlink user from Visma Connect", 500, "ERROR_INTERNAL");
        }
        
        if (!users.containsKey(userId)) {
            throw new ResourceNotFoundException("User", "User not found in Visma Connect with id: " + userId);
        }
        
        unlinkedUsers.put(userId, true);
    }

    @Override
    public User findUserById(String userId) {
        UUID userUuid = UUID.fromString(userId);
        User user = users.get(userUuid);
        if (user == null) {
            throw new ResourceNotFoundException("User", "User not found in Visma Connect with id: " + userId);
        }
        return user;
    }

    @Override
    public Optional<User> findUserByEmail(String userEmail) {
        return Optional.ofNullable(usersByEmail.get(userEmail));
    }

    @Override
    public void sendEmailActivation(String connectUserId, String applicationName, String redirectUri, String state, String recipient) {
    }

    public void setShouldFailOnCreate(boolean shouldFail) {
        this.shouldFailOnCreate = shouldFail;
    }

    public void setShouldFailOnUpdate(boolean shouldFail) {
        this.shouldFailOnUpdate = shouldFail;
    }

    public void setShouldFailOnUnlink(boolean shouldFail) {
        this.shouldFailOnUnlink = shouldFail;
    }

    public void setShouldReturnAlreadyUnlinked(boolean shouldReturn) {
        this.shouldReturnAlreadyUnlinked = shouldReturn;
    }

    public boolean isUserUnlinked(UUID userId) {
        return unlinkedUsers.getOrDefault(userId, false);
    }

    public void clear() {
        users.clear();
        usersByEmail.clear();
        unlinkedUsers.clear();
        shouldFailOnCreate = false;
        shouldFailOnUpdate = false;
        shouldFailOnUnlink = false;
        shouldReturnAlreadyUnlinked = false;
    }
}
