# Guidelines for Visma Connect Integration

## 1. Core Principles
The project uses **Visma Connect** as its exclusive Identity Provider (IDP). All user and tenant management **must** be synchronized with Visma Connect via its **Public REST API**.

* **Authentication:** Communication with the API **must** be secured using the **OAuth2 Client Credentials Flow**. The `Spring Security` configuration handles token acquisition and refresh automatically.
* **Technology:** All API interactions **must** be implemented using the non-blocking `Spring WebClient`.

---

## 2. Configuration
The `OAuth2` client configuration is defined in the `application-{env}.yml` file for each environment. Ensure the following properties are correctly set under `spring.security.oauth2.client`:

* `isuer-uri`
* `token-uri`

---

## 3. Implementation Pattern
Integration is handled through two dedicated gateways. Any use case needing to interact with Visma Connect **must** inject the appropriate gateway interface.

### 3.1. Required Gateways
* **`VismaConnectUserGateway`**: For all user-related operations (create, update, fetch).
* **`VismaConnectTenantGateway`**: For all tenant-related operations.

### 3.2. Location and Structure
* **Interfaces:** The gateway interfaces **must** be defined in the `use-cases` module.
* **Implementations:** The gateway implementations **must** be located in the `connect-adapter` module. This module also contains all required DTOs.

### 3.3. Data Transfer Objects (DTOs)
* You **must** create dedicated request and response DTOs that precisely match the Visma Connect API's JSON structure.
* All DTOs **must** be implemented as immutable Java `record` classes.
* Create separate request and response DTOs, even if their fields are identical.

---

## 4. Error Handling
Properly handling errors from the Visma Connect API is critical. Gateway implementations are responsible for this.

### 4.1. The `ConnectUserException`
When an API call to the `VismaConnectUserGateway` fails, the implementation **must** catch the error from `WebClient` and throw a `ConnectUserException`.

This custom exception **must** be populated with the following information:
* `statusCode`: The integer HTTP status code from the API response (e.g., `400`, `409`).
* `message`: The human-readable error message from the API response body.
* `errorType`: A custom string identifying the failed operation (e.g., `"CREATE_USER"`, `"UPDATE_USER"`).

#### Custom Exception Definition
```java
public class ConnectUserException extends RuntimeException {
    private final int statusCode;
    private final String errorType;

    public ConnectUserException(String message, int statusCode, String errorType) {
        super(message);
        this.statusCode = statusCode;
        this.errorType = errorType;
    }

    // Getters...
}
```

#### Error Handling Example in Gateway
```java
public Mono<UserResponse> createUser(UserRequest userRequest) {
    return webClient.post()
        .uri("/users")
        .bodyValue(userRequest)
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> 
            clientResponse.bodyToMono(ErrorResponse.class)
                .flatMap(errorBody -> {
                    String message = "Failed to create user in Visma Connect. Reason: " + errorBody.getErrorCode();
                    int statusCode = clientResponse.statusCode().value();
                    var exception = new ConnectUserException(message, statusCode, "CREATE_USER");
                    return Mono.error(exception);
                })
        )
        .bodyToMono(UserResponse.class);
}
```