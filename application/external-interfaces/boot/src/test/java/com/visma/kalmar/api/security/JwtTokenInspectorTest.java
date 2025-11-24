package com.visma.kalmar.api.security;

import com.visma.kalmar.api.entities.user.User;
import com.visma.kalmar.api.vismaconnect.VismaConnectUserGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class JwtTokenInspectorTest {
    private static final String CONNECT_USER_ID = "3512f78d-0630-4ea4-a6ed-596dd68e2b0d";
    private static final String LANGUAGE_ID = "973154d4-1ea1-494d-807b-c2ebd16c74bd";
    private static final String CONNECT_USER_EMAIL = "unit.test@test.com";
    private static final String FIRST_NAME = "FirstName";
    private static final String LAST_NAME = "LastName";
    private static final Long RECORD_VERSION = 1L;
    @Mock
    private JwtAuthenticationToken jwtAuthenticationToken;
    @Mock
    private VismaConnectUserGateway vismaConnectUserGateway;
    private Map<String, Object> tokenAttributes;
    private JwtTokenInspector jwtTokenInspector;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        jwtTokenInspector = new JwtTokenInspector(vismaConnectUserGateway);

        tokenAttributes = new HashMap<>(); //trufflehog:ignore
        tokenAttributes.put("sub", CONNECT_USER_ID);

        when(jwtAuthenticationToken.getTokenAttributes()).thenReturn(tokenAttributes);
    }

    @Test
    void getConnectUserId_ValidInput_ConnectUserIdReturned() {
        var connectUserId = jwtTokenInspector.getConnectUserId(jwtAuthenticationToken);

        Assertions.assertNotNull(connectUserId);
        Assertions.assertEquals(CONNECT_USER_ID, connectUserId);
    }

    @Test
    void getConnectUserEmail_UserFound_emailReturned() {
        var user =
                new User(
                        UUID.fromString(CONNECT_USER_ID),
                        UUID.fromString(LANGUAGE_ID),
                        CONNECT_USER_EMAIL,
                        FIRST_NAME,
                        LAST_NAME,
                        RECORD_VERSION,
                        new Date());
        when(vismaConnectUserGateway.findUserById(anyString())).thenReturn(user);
        var email = jwtTokenInspector.getConnectUserEmail(jwtAuthenticationToken);
        Assertions.assertEquals(CONNECT_USER_EMAIL, email);
    }

    @Test
    void getConnectUserId_NullSubject_ReturnsNull() {
        tokenAttributes.put("sub", null);

        var connectUserId = jwtTokenInspector.getConnectUserId(jwtAuthenticationToken);

        Assertions.assertNull(connectUserId);
    }

    @Test
    void getConnectUserId_InvalidUuidFormat_ReturnsNull() {
        tokenAttributes.put("sub", "not-a-valid-uuid");

        var connectUserId = jwtTokenInspector.getConnectUserId(jwtAuthenticationToken);

        Assertions.assertNull(connectUserId);
    }

    @Test
    void getConnectUserEmail_NullSubject_ReturnsNull() {
        tokenAttributes.put("sub", null);

        var email = jwtTokenInspector.getConnectUserEmail(jwtAuthenticationToken);

        Assertions.assertNull(email);
    }

    @Test
    void getConnectUserEmail_InvalidUuidFormat_ReturnsNull() {
        tokenAttributes.put("sub", "invalid-uuid-format");

        var email = jwtTokenInspector.getConnectUserEmail(jwtAuthenticationToken);

        Assertions.assertNull(email);
    }
}
