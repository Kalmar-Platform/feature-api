package com.visma.kalmar.api.vismaconnect;

import com.visma.kalmar.api.entities.user.User;

import java.util.Optional;
import java.util.UUID;

public interface VismaConnectUserGateway {
    UUID createUser(User user, String languageCode);
    void updateUser(User user, String languageCode);
    void unlinkClient(UUID userId);
    
    User findUserById(String userId);
    Optional<User> findUserByEmail(String userEmail);
    void sendEmailActivation(String connectUserId, String applicationName, String redirectUri, String state, String recipient);
}
