package com.visma.kalmar.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.visma.kalmar.api.connect.dto.ConnectUserResponse;
import com.visma.kalmar.api.entities.language.Language;
import com.visma.kalmar.api.entities.user.User;
import com.visma.kalmar.api.exception.ConnectUserException;
import com.visma.kalmar.api.language.LanguageGateway;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

class VismaConnectUserGatewayAdapterTest {

    private static final UUID USER_ID = UUID.fromString("64dd3e56-4c27-4ca2-8c15-528cfbaf6565");
    private static final UUID LANGUAGE_ID = UUID.fromString("74dd3e56-4c27-4ca2-8c15-528cfbaf6575");
    private static final String USER_EMAIL = "test.user@example.com";
    private static final String FIRST_NAME = "Test";
    private static final String LAST_NAME = "User";
    private static final String LANGUAGE_CODE = "no";
    private static final String COUNTRY_CODE = "NO";
    private static final String PREFERRED_LANGUAGE = "nb-NO";
    private static final String ERROR_BODY = "{\"error_code\" : \"ERROR_INVALID_USER\"}";
    private static MockWebServer mockWebServer;
    private ObjectMapper objectMapper;
    @Mock
    private LanguageGateway languageGateway;
    private VismaConnectUserGatewayAdapter vismaConnectUserGatewayAdapter;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        WebClient webClient =
                WebClient.create(String.format("http://localhost:%s", mockWebServer.getPort()));
        vismaConnectUserGatewayAdapter = new VismaConnectUserGatewayAdapter(webClient, languageGateway);
    }

    @Test
    void createUser_ValidInput_UserIdReturned() throws JsonProcessingException {
        mockWebServer.enqueue(
                new MockResponse()
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setResponseCode(HttpStatus.CREATED.value())
                        .setBody(objectMapper.writeValueAsString(getConnectUserResponse())));

        var userId = vismaConnectUserGatewayAdapter.createUser(getTestUser(), LANGUAGE_CODE);

        assertNotNull(userId);
        assertEquals(USER_ID, userId);
    }

    @Test
    void createUser_4xxErrorFromConnect_ThrowsException() {
        mockWebServer.enqueue(
                new MockResponse()
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setResponseCode(HttpStatus.BAD_REQUEST.value())
                        .setBody(ERROR_BODY));

        assertThrows(
                ConnectUserException.class,
                () -> vismaConnectUserGatewayAdapter.createUser(getTestUser(), LANGUAGE_CODE));
    }

    @Test
    void createUser_5xxErrorFromConnect_ThrowsException() {
        mockWebServer.enqueue(
                new MockResponse()
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .setBody(ERROR_BODY));

        assertThrows(
                ConnectUserException.class,
                () -> vismaConnectUserGatewayAdapter.createUser(getTestUser(), LANGUAGE_CODE));
    }

    @Test
    void updateUser_ValidInput_Success() {
        mockWebServer.enqueue(
                new MockResponse()
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setResponseCode(HttpStatus.OK.value()));

        assertDoesNotThrow(() -> vismaConnectUserGatewayAdapter.updateUser(getTestUser(), LANGUAGE_CODE));
    }

    @Test
    void updateUser_4xxErrorFromConnect_ThrowsException() {
        mockWebServer.enqueue(
                new MockResponse()
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setResponseCode(HttpStatus.BAD_REQUEST.value())
                        .setBody(ERROR_BODY));

        assertThrows(
                ConnectUserException.class,
                () -> vismaConnectUserGatewayAdapter.updateUser(getTestUser(), LANGUAGE_CODE));
    }

    @Test
    void updateUser_5xxErrorFromConnect_ThrowsException() {
        mockWebServer.enqueue(
                new MockResponse()
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .setBody(ERROR_BODY));

        assertThrows(
                ConnectUserException.class,
                () -> vismaConnectUserGatewayAdapter.updateUser(getTestUser(), LANGUAGE_CODE));
    }

    @Test
    void findUserById_ValidInput_UserReturned() throws JsonProcessingException {
        when(languageGateway.findByCode(LANGUAGE_CODE)).thenReturn(getTestLanguage());

        mockWebServer.enqueue(
                new MockResponse()
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setResponseCode(HttpStatus.OK.value())
                        .setBody(objectMapper.writeValueAsString(getConnectUserResponse())));

        var user = vismaConnectUserGatewayAdapter.findUserById(USER_ID.toString());

        assertNotNull(user);
        assertEquals(USER_ID, user.idUser());
        assertEquals(LANGUAGE_ID, user.idLanguage());
        assertEquals(USER_EMAIL, user.email());
        assertEquals(FIRST_NAME, user.firstName());
        assertEquals(LAST_NAME, user.lastName());
    }

    @Test
    void findUserById_4xxErrorFromConnect_ThrowsException() {
        mockWebServer.enqueue(
                new MockResponse()
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setResponseCode(HttpStatus.BAD_REQUEST.value())
                        .setBody(ERROR_BODY));

        assertThrows(
                ConnectUserException.class,
                () -> vismaConnectUserGatewayAdapter.findUserById(USER_ID.toString()));
    }

    @Test
    void findUserById_5xxErrorFromConnect_ThrowsException() {
        mockWebServer.enqueue(
                new MockResponse()
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .setBody(ERROR_BODY));

        assertThrows(
                ConnectUserException.class,
                () -> vismaConnectUserGatewayAdapter.findUserById(USER_ID.toString()));
    }

    @Test
    void findUserByEmail_ValidInput_UserReturned() throws JsonProcessingException {
        var connectUserResponses = new ConnectUserResponse[]{getConnectUserResponse()};

        mockWebServer.enqueue(
                new MockResponse()
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setResponseCode(HttpStatus.OK.value())
                        .setBody(objectMapper.writeValueAsString(connectUserResponses)));

        var userOptional = vismaConnectUserGatewayAdapter.findUserByEmail(USER_EMAIL);

        assertTrue(userOptional.isPresent());
        var user = userOptional.get();
        assertEquals(USER_ID, user.idUser());
        assertEquals(USER_EMAIL, user.email());
        assertEquals(FIRST_NAME, user.firstName());
        assertEquals(LAST_NAME, user.lastName());
    }

    @Test
    void findUserByEmail_ValidInput_UserNotFound() {
        mockWebServer.enqueue(
                new MockResponse()
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setResponseCode(HttpStatus.NO_CONTENT.value())
                        .setBody("")
        );

        var userOptional = vismaConnectUserGatewayAdapter.findUserByEmail(USER_EMAIL);

        assertFalse(userOptional.isPresent());
    }

    @Test
    void findUserByEmail_NoContentResponse_EmptyOptionalReturned() {
        mockWebServer.enqueue(
                new MockResponse()
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setResponseCode(HttpStatus.NO_CONTENT.value()));

        var userOptional = vismaConnectUserGatewayAdapter.findUserByEmail(USER_EMAIL);

        assertFalse(userOptional.isPresent());
    }

    @Test
    void findUserByEmail_EmptyArrayResponse_EmptyOptionalReturned() throws JsonProcessingException {
        var emptyResponses = new ConnectUserResponse[]{};

        mockWebServer.enqueue(
                new MockResponse()
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setResponseCode(HttpStatus.OK.value())
                        .setBody(objectMapper.writeValueAsString(emptyResponses)));

        var userOptional = vismaConnectUserGatewayAdapter.findUserByEmail(USER_EMAIL);

        assertFalse(userOptional.isPresent());
    }

    @Test
    void findUserByEmail_4xxErrorFromConnect_ThrowsException() {
        mockWebServer.enqueue(
                new MockResponse()
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setResponseCode(HttpStatus.BAD_REQUEST.value())
                        .setBody(ERROR_BODY));

        assertThrows(
                ConnectUserException.class,
                () -> vismaConnectUserGatewayAdapter.findUserByEmail(USER_EMAIL));
    }

    @Test
    void findUserByEmail_5xxErrorFromConnect_ThrowsException() {
        mockWebServer.enqueue(
                new MockResponse()
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .setBody(ERROR_BODY));

        assertThrows(
                ConnectUserException.class,
                () -> vismaConnectUserGatewayAdapter.findUserByEmail(USER_EMAIL));
    }

    private ConnectUserResponse getConnectUserResponse() {
        return new ConnectUserResponse(
                USER_ID,
                USER_EMAIL,
                true,
                LocalDateTime.now(),
                FIRST_NAME + " " + LAST_NAME,
                FIRST_NAME,
                LAST_NAME,
                COUNTRY_CODE,
                PREFERRED_LANGUAGE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                "1",
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    private User getTestUser() {
        return new User(USER_ID, LANGUAGE_ID, USER_EMAIL, FIRST_NAME, LAST_NAME, null, null);
    }

    private Language getTestLanguage() {
        return new Language(LANGUAGE_ID, "Norwegian", LANGUAGE_CODE);
    }


}