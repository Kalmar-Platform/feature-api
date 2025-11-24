package com.visma.kalmar.api.user;

import java.util.UUID;

public interface GetUserInputPort {

    void getUserById(UUID userId, UserOutputPort outputPort);

    void getUserByEmail(String email, UserOutputPort outputPort);
}
