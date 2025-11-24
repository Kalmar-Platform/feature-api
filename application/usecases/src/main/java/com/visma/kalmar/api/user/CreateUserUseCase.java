package com.visma.kalmar.api.user;

import com.visma.kalmar.api.entities.user.User;
import com.visma.kalmar.api.exception.ResourceAlreadyExistsException;
import com.visma.kalmar.api.language.LanguageGateway;
import com.visma.kalmar.api.vismaconnect.VismaConnectUserGateway;

import java.util.Date;
import java.util.UUID;

public class CreateUserUseCase implements CreateUserInputPort {

    private static final Long RECORD_VERSION = 1L;
    private final UserGateway userGateway;
    private final LanguageGateway languageGateway;
    private final VismaConnectUserGateway vismaConnectUserGateway;

    public CreateUserUseCase(UserGateway userGateway,
                             LanguageGateway languageGateway,
                             VismaConnectUserGateway vismaConnectUserGateway) {
        this.userGateway = userGateway;
        this.languageGateway = languageGateway;
        this.vismaConnectUserGateway = vismaConnectUserGateway;
    }

    @Override
    public void createUser(UserInputData inputData, UserOutputPort outputPort) {
        // Check if user already exists
        if (userGateway.existsByEmail(inputData.email())) {
            throw new ResourceAlreadyExistsException("User", "User with email: " + inputData.email() + " already exists.");
        }

        // Get language by code
        var language = languageGateway.findByCode(inputData.languageCode());

        // Create temporary user entity for Visma Connect (without connectUserId)
        var tempUser = new User(
                null, // No ID yet
                language.idLanguage(),
                inputData.email(),
                inputData.firstName(),
                inputData.lastName(),
                RECORD_VERSION,
                new Date()
        );

        // Create user in Visma Connect first - if this fails, we don't have any local data to clean up
        UUID connectUserId = vismaConnectUserGateway.createUser(
                tempUser,
                inputData.languageCode()
        );

        // Now create user entity with the connectUserId from Visma Connect
        var user = new User(
                connectUserId,
                language.idLanguage(),
                inputData.email(),
                inputData.firstName(),
                inputData.lastName(),
                RECORD_VERSION, new Date()
        );

        // Save user in database (we know Connect creation succeeded)
        var savedUser = userGateway.save(user);

        // Prepare output data for create operation
        var outputData = new UserOutputData(
                savedUser.idUser().toString(),
                savedUser.email(),
                savedUser.firstName(),
                savedUser.lastName(),
                inputData.languageCode(), // Return original language code, not the language ID
                true // created = true for create operations
        );

        // Present the result
        outputPort.present(outputData);
    }
}
