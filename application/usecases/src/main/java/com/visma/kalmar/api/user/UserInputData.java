package com.visma.kalmar.api.user;

public record UserInputData(
        String userId,
        String email,
        String firstName,
        String lastName,
        String languageCode
) {

    // Factory method for create operations (no userId)
    public static UserInputData forCreate(String email, String firstName, String lastName, String languageCode) {
        return new UserInputData(null, email, firstName, lastName, languageCode);
    }

    // Factory method for update operations (with userId)
    public static UserInputData forUpdate(String userId, String email, String firstName, String lastName, String languageCode) {
        return new UserInputData(userId, email, firstName, lastName, languageCode);
    }
}
