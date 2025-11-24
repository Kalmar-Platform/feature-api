package com.visma.kalmar.api;

import com.visma.kalmar.api.connect.dto.ConnectErrorResponse;
import com.visma.kalmar.api.connect.dto.ConnectUserResponse;
import com.visma.kalmar.api.connect.dto.CreateUserRequest;
import com.visma.kalmar.api.connect.dto.UpdateUserRequest;
import com.visma.kalmar.api.entities.user.User;
import com.visma.kalmar.api.exception.ConnectUserException;
import com.visma.kalmar.api.language.LanguageGateway;
import com.visma.kalmar.api.vismaconnect.VismaConnectUserGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

public class VismaConnectUserGatewayAdapter implements VismaConnectUserGateway {

    private final WebClient webClient;
    private final LanguageGateway languageGateway;

    @Autowired
    public VismaConnectUserGatewayAdapter(WebClient webClient, LanguageGateway languageGateway) {
        this.webClient = webClient;
        this.languageGateway = languageGateway;
    }

    @Override
    public UUID createUser(User user, String languageCode) {
        String countryCode = getCountryCodeFromLanguage(languageCode);
        String preferredLanguage = convertLanguageCodeToLocale(languageCode);

        var request =
                new CreateUserRequest(
                        user.email(), countryCode, preferredLanguage, user.firstName(), user.lastName());

        ConnectUserResponse response =
                webClient
                        .post()
                        .uri("/v1.0/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .bodyValue(request)
                        .retrieve()
                        .onStatus(
                                status -> status.is4xxClientError() || status.is5xxServerError(),
                                clientResponse ->
                                        clientResponse
                                                .bodyToMono(ConnectErrorResponse.class)
                                                .flatMap(
                                                        errorResponse ->
                                                                Mono.error(
                                                                        new ConnectUserException(
                                                                                errorResponse.errorCode(),
                                                                                clientResponse.statusCode().value(),
                                                                                "CREATE_USER"))))
                        .bodyToMono(ConnectUserResponse.class)
                        .block();

        return response.id();
    }

    @Override
    public void updateUser(User user, String languageCode) {
        String countryCode = getCountryCodeFromLanguage(languageCode);
        String preferredLanguage = convertLanguageCodeToLocale(languageCode);

        var request =
                new UpdateUserRequest(user.firstName(), user.lastName(), countryCode, preferredLanguage);

        webClient
                .put()
                .uri("/v1.0/users/{user_id}", user.idUser())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse ->
                                clientResponse
                                        .bodyToMono(ConnectErrorResponse.class)
                                        .flatMap(
                                                errorResponse ->
                                                        Mono.error(
                                                                new ConnectUserException(
                                                                        errorResponse.errorCode(),
                                                                        clientResponse.statusCode().value(),
                                                                        "UPDATE_USER"))))
                .toBodilessEntity()
                .block();
    }

    @Override
    public User findUserById(String userId) {
        var connectUser =
                webClient
                        .get()
                        .uri("/v1.0/users/{user_id}", userId)
                        .retrieve()
                        .onStatus(
                                status -> status.is4xxClientError() || status.is5xxServerError(),
                                clientResponse ->
                                        clientResponse
                                                .bodyToMono(ConnectErrorResponse.class)
                                                .flatMap(
                                                        errorResponse ->
                                                                Mono.error(
                                                                        new ConnectUserException(
                                                                                errorResponse.errorCode(),
                                                                                clientResponse.statusCode().value(),
                                                                                "CREATE_USER"))))
                        .bodyToMono(ConnectUserResponse.class)
                        .block();
        var languageCode = convertLocaleToLanguageCode(connectUser.preferredLanguage());
        var language = languageGateway.findByCode(languageCode);

        return new User(
                connectUser.id(),
                language.idLanguage(),
                connectUser.email(),
                connectUser.firstName(),
                connectUser.lastName(),
                null,
                null);
    }

    @Override
    public Optional<User> findUserByEmail(String userEmail) {
        try {
            // Changed to handle a list of ConnectUserResponse objects
            ConnectUserResponse[] responses =
                    webClient
                            .get()
                            .uri("/v1.0/search/users?email={userEmail}", userEmail)
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .onStatus(
                                    status -> status.is4xxClientError() || status.is5xxServerError(),
                                    clientResponse ->
                                            clientResponse
                                                    .bodyToMono(ConnectErrorResponse.class)
                                                    .flatMap(
                                                            errorResponse ->
                                                                    Mono.error(
                                                                            new ConnectUserException(
                                                                                    errorResponse.errorCode(),
                                                                                    clientResponse.statusCode().value(),
                                                                                    "FIND_USER_BY_EMAIL"))))
                            .onStatus(
                                    status -> status.isSameCodeAs(HttpStatus.NO_CONTENT),
                                    clientResponse -> Mono.empty())
                            .bodyToMono(ConnectUserResponse[].class)
                            .block();

            if (responses != null && responses.length > 0) {
                // Take the first element from the list
                ConnectUserResponse response = responses[0];
                var user =
                        new User(
                                response.id(),
                                null, // idLanguage is not provided by ConnectUserResponse
                                response.email(),
                                response.firstName(),
                                response.lastName(), null, null);
                return Optional.of(user);
            }

            return Optional.empty();
        } catch (Exception e) {
            // Handle 204 No Content or other cases where no user is found
            if (e.getMessage() != null && e.getMessage().contains("204")) {
                return Optional.empty();
            }
            // Re-throw other exceptions
            throw e;
        }
    }

    /**
     * Convert language code to locale format for Visma Connect. Maps Nordic language codes to full
     * locale strings.
     */
    private String convertLanguageCodeToLocale(String languageCode) {
        return switch (languageCode.toLowerCase()) {
            case "en" -> "en-US";
            case "sv" -> "sv-SE";
            case "no", "nb" -> "nb-NO";
            case "da" -> "da-DK";
            case "fi" -> "fi-FI";
            default -> "en-US"; // Default fallback
        };
    }

    /**
     * Convert locale to language code format for Visma Connect. Maps Nordic locale to nordic language
     * codes.
     */
    private String convertLocaleToLanguageCode(String locale) {
        return switch (locale.toLowerCase()) {
            case "en-us" -> "en";
            case "sv-se" -> "sv";
            case "nb-no" -> "no";
            case "da-dk" -> "da";
            case "fi-fi" -> "fi";
            default -> "en"; // Default fallback
        };
    }

    /**
     * Map language codes to country codes for Nordic markets. Used to determine the appropriate
     * country context for Visma Connect.
     */
    private String getCountryCodeFromLanguage(String languageCode) {
        return switch (languageCode.toLowerCase()) {
            case "sv" -> "SE"; // Sweden
            case "no", "nb" -> "NO"; // Norway
            case "da" -> "DK"; // Denmark
            case "fi" -> "FI"; // Finland
            case "en" -> "NO"; // Default English to Norway
            default -> "NO"; // Default fallback to Norway
        };
    }
}
