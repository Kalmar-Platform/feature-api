package com.visma.kalmar.api.role;

import com.visma.kalmar.api.role.dto.RoleResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;


class RolePresenterTest {

    private static final String ROLE_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final String NAME = "Administrator";
    private static final String INVARIANT_KEY = "ADMIN";
    private static final String DESCRIPTION = "Full system access";

    private RolePresenter rolePresenter;

    @BeforeEach
    void setUp() {
        rolePresenter = new RolePresenter();
    }

    @Test
    void present_withCreatedFlagTrue_returns201Created() {
        var outputData = new RoleOutputData(
                ROLE_ID,
                NAME,
                INVARIANT_KEY,
                DESCRIPTION,
                true
        );

        rolePresenter.present(outputData);

        ResponseEntity<RoleResponse> response = rolePresenter.getResponse();

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void present_withCreatedFlagFalse_returns200OK() {
        var outputData = new RoleOutputData(
                ROLE_ID,
                NAME,
                INVARIANT_KEY,
                DESCRIPTION,
                false
        );

        rolePresenter.present(outputData);

        ResponseEntity<RoleResponse> response = rolePresenter.getResponse();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void present_mapsAllFieldsCorrectly() {
        var outputData = new RoleOutputData(
                ROLE_ID,
                NAME,
                INVARIANT_KEY,
                DESCRIPTION,
                false
        );

        rolePresenter.present(outputData);

        ResponseEntity<RoleResponse> response = rolePresenter.getResponse();
        RoleResponse body = response.getBody();

        assertNotNull(body);
        assertEquals(ROLE_ID, body.getRoleId());
        assertEquals(NAME, body.getName());
        assertEquals(INVARIANT_KEY, body.getInvariantKey());
        assertEquals(DESCRIPTION, body.getDescription());
    }

    @Test
    void present_withNullDescription_mapsNullCorrectly() {
        var outputData = new RoleOutputData(
                ROLE_ID,
                NAME,
                INVARIANT_KEY,
                null,
                true
        );

        rolePresenter.present(outputData);

        ResponseEntity<RoleResponse> response = rolePresenter.getResponse();
        RoleResponse body = response.getBody();

        assertNotNull(body);
        assertNull(body.getDescription());
    }

    @Test
    void getResponse_returnsStoredResponseEntity() {
        var outputData = new RoleOutputData(
                ROLE_ID,
                NAME,
                INVARIANT_KEY,
                DESCRIPTION,
                true
        );

        rolePresenter.present(outputData);

        ResponseEntity<RoleResponse> firstCall = rolePresenter.getResponse();
        ResponseEntity<RoleResponse> secondCall = rolePresenter.getResponse();

        assertSame(firstCall, secondCall);
    }

    @Test
    void present_withNewData_updatesResponse() {
        var firstOutputData = new RoleOutputData(
                ROLE_ID,
                "First Role",
                "FIRST",
                "First Description",
                true
        );

        rolePresenter.present(firstOutputData);
        ResponseEntity<RoleResponse> firstResponse = rolePresenter.getResponse();

        var secondOutputData = new RoleOutputData(
                "different-id",
                "Second Role",
                "SECOND",
                "Second Description",
                false
        );

        rolePresenter.present(secondOutputData);
        ResponseEntity<RoleResponse> secondResponse = rolePresenter.getResponse();

        assertNotSame(firstResponse, secondResponse);
        assertEquals(HttpStatus.CREATED, firstResponse.getStatusCode());
        assertEquals(HttpStatus.OK, secondResponse.getStatusCode());
        assertEquals("First Role", firstResponse.getBody().getName());
        assertEquals("Second Role", secondResponse.getBody().getName());
    }

    @Test
    void present_createsNewRoleResponseObject() {
        var outputData = new RoleOutputData(
                ROLE_ID,
                NAME,
                INVARIANT_KEY,
                DESCRIPTION,
                false
        );

        rolePresenter.present(outputData);

        ResponseEntity<RoleResponse> response = rolePresenter.getResponse();

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertInstanceOf(RoleResponse.class, response.getBody());
    }
}
