package com.visma.kalmar.api.user;

public interface CreateUserInputPort {

    void createUser(UserInputData inputData, UserOutputPort outputPort);
}
