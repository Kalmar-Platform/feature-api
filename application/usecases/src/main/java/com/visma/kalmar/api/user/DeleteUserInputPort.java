package com.visma.kalmar.api.user;

import java.util.UUID;

public interface DeleteUserInputPort {

    void deleteUser(UUID userId);
}
