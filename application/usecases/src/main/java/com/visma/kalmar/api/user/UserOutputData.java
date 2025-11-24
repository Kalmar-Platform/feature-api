package com.visma.kalmar.api.user;

/**
 * Output data for user use cases.
 * Contains user information and operation status.
 */
public record UserOutputData(
        String userId,
        String email,
        String firstName,
        String lastName,
        String languageCode,
        boolean created
) {
}
