package com.visma.kalmar.api.user;

import com.visma.kalmar.api.exception.ConnectUserException;
import com.visma.kalmar.api.vismaconnect.VismaConnectUserGateway;

import java.util.UUID;

/**
 * Use case for deleting an existing user.
 * Handles user deletion from both local database and Visma Connect.
 * The user will be unlinked from Visma Connect and deleted from the UserAccess database.
 */
public class DeleteUserUseCase implements DeleteUserInputPort {

    private final UserGateway userGateway;
    private final VismaConnectUserGateway vismaConnectUserGateway;

    public DeleteUserUseCase(UserGateway userGateway,
                             VismaConnectUserGateway vismaConnectUserGateway) {
        this.userGateway = userGateway;
        this.vismaConnectUserGateway = vismaConnectUserGateway;
    }

    @Override
    public void deleteUser(UUID userId) {
        // Check if user exists (this will throw ResourceNotFoundException if not found)
        userGateway.findById(userId);

        // Unlink user from Visma Connect first
        try {
            vismaConnectUserGateway.unlinkClient(userId);
        } catch (ConnectUserException ex) {
            // Check if the error is ERROR_USER_UNLINKED_FROM_CLIENT (409)
            // This is considered a valid response as mentioned in requirements
            if (ex.getStatusCode() == 409 && "ERROR_USER_UNLINKED_FROM_CLIENT".equals(ex.getMessage())) {
                // Continue with deletion as this is acceptable
            } else {
                // Re-throw for other errors
                throw ex;
            }
        }

        // Delete user from database (we know Connect unlink succeeded or was already unlinked)
        userGateway.deleteById(userId);

        // No need to present result for delete operations that return 204 No Content
    }
}
