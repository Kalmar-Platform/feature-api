package com.visma.kalmar.api.user;

import com.visma.kalmar.api.entities.user.User;
import com.visma.kalmar.api.language.LanguageGateway;
import com.visma.kalmar.api.vismaconnect.VismaConnectUserGateway;

import java.util.Date;
import java.util.UUID;

/**
 * Use case for updating an existing user.
 * Handles user updates in both local database and Visma Connect.
 */
public class UpdateUserUseCase implements UpdateUserInputPort {

    private final UserGateway userGateway;
    private final LanguageGateway languageGateway;
    private final VismaConnectUserGateway vismaConnectUserGateway;

    public UpdateUserUseCase(UserGateway userGateway,
                             LanguageGateway languageGateway,
                             VismaConnectUserGateway vismaConnectUserGateway) {
        this.userGateway = userGateway;
        this.languageGateway = languageGateway;
        this.vismaConnectUserGateway = vismaConnectUserGateway;
    }

    @Override
    public void updateUser(UserInputData inputData, UserOutputPort outputPort) {
        // Validate userId is provided
        if (inputData.userId() == null || inputData.userId().isEmpty()) {
            throw new IllegalArgumentException("User ID is required for update operation");
        }

        UUID userId = UUID.fromString(inputData.userId());

        // Check if user exists (this will throw ResourceNotFoundException if not found)
        var existingUser = userGateway.findById(userId);

        // Get language by code
        var language = languageGateway.findByCode(inputData.languageCode());

        // Create user entity with updated information
        var userToUpdate =
                new User(
                        userId,
                        language.idLanguage(),
                        inputData.email(),
                        inputData.firstName(),
                        inputData.lastName(),
                        existingUser.recordVersion(),
                        new Date());

        // Update user in Visma Connect first
        vismaConnectUserGateway.updateUser(
                userToUpdate,
                inputData.languageCode()
        );

        // Update user in database (we know Connect update succeeded)
        var updatedUser = userGateway.update(userToUpdate);

        // Prepare output data for update operation
        var outputData = new UserOutputData(
                updatedUser.idUser().toString(),
                updatedUser.email(),
                updatedUser.firstName(),
                updatedUser.lastName(),
                inputData.languageCode(), // Return original language code, not the language ID
                false // created = false for update operations
        );

        // Present the result
        outputPort.present(outputData);
    }
}
