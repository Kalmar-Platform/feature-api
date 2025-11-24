package com.visma.kalmar.api.user;

import com.visma.kalmar.api.user.dto.UserRequest;
import com.visma.kalmar.api.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@Tag(name = "User API", description = "User management operations")
@SecurityRequirement(name = "jwt")
public interface UserApi {

    @PostMapping("/users")
    @Operation(
            summary = "Create a new user",
            description = "Create a new user with the provided details. The user will be created in the UserAccess database and synchronized with Visma Connect identity provider. " +
                    "The language code will be converted to a language ID when saving the user, and the user will be synchronized with Visma Connect using the locale format.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or language code not found"),
            @ApiResponse(responseCode = "409", description = "User with the same email already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error or Visma Connect integration error")
    })
    ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserRequest userRequest);

    @PutMapping("/users/{userId}")
    @Operation(
            summary = "Update an existing user",
            description = "Update an existing user with the provided details. The user will be updated in the UserAccess database and synchronized with Visma Connect identity provider. " +
                    "The language code will be converted to a language ID when saving the user, and the user will be synchronized with Visma Connect using the locale format.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or language code not found"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error or Visma Connect integration error")
    })
    ResponseEntity<UserResponse> updateUser(
            @PathVariable("userId") String userId,
            @Valid @RequestBody UserRequest userRequest);

    @DeleteMapping("/users/{userId}")
    @Operation(
            summary = "Delete an existing user",
            description = "Delete an existing user by ID. The user will be unlinked from Visma Connect identity provider and removed from the UserAccess database. " +
                    "If the user is already unlinked from Visma Connect (409 ERROR_USER_UNLINKED_FROM_CLIENT), this is considered a valid response and the deletion will proceed.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error or Visma Connect integration error")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    ResponseEntity<Void> deleteUser(@PathVariable("userId") String userId);

    @GetMapping("/users/{userId}")
    @Operation(
            summary = "Get user by ID",
            description = "Retrieve a user by their unique identifier. Returns the user's details including ID, email, first name, last name, and language code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<UserResponse> getUserById(@PathVariable("userId") String userId);

    @GetMapping("/users")
    @Operation(
            summary = "Get user by email",
            description = "Retrieve a user by their email address. Returns the user's details including ID, email, first name, last name, and language code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<UserResponse> getUserByEmail(@RequestParam("email") String email);
}
