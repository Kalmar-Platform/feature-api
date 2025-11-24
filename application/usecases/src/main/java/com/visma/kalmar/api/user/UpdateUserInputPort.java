package com.visma.kalmar.api.user;

/**
 * Input port for updating an existing user.
 * Defines the contract for the update user use case.
 */
public interface UpdateUserInputPort {

    /**
     * Update an existing user.
     *
     * @param inputData  The input data containing user ID and updated information
     * @param outputPort The output port to present the result
     */
    void updateUser(UserInputData inputData, UserOutputPort outputPort);
}
