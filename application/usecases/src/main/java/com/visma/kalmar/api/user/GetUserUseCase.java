package com.visma.kalmar.api.user;

import com.visma.kalmar.api.entities.user.User;
import com.visma.kalmar.api.language.LanguageGateway;

import java.util.UUID;

public class GetUserUseCase implements GetUserInputPort {

    private final UserGateway userGateway;
    private final LanguageGateway languageGateway;

    public GetUserUseCase(UserGateway userGateway, LanguageGateway languageGateway) {
        this.userGateway = userGateway;
        this.languageGateway = languageGateway;
    }

    @Override
    public void getUserById(UUID userId, UserOutputPort outputPort) {
        User user = userGateway.findById(userId);
        outputPort.present(buildOutputData(user));
    }

    @Override
    public void getUserByEmail(String email, UserOutputPort outputPort) {
        User user = userGateway.findByEmail(email);
        outputPort.present(buildOutputData(user));
    }

    private UserOutputData buildOutputData(User user) {
        String languageCode = languageGateway.findById(user.idLanguage()).code();

        return new UserOutputData(
                user.idUser().toString(),
                user.email(),
                user.firstName(),
                user.lastName(),
                languageCode,
                false
        );
    }
}
