# Exception Handling Guidelines

## 1. Core Principle
This project uses a **centralized exception handling mechanism**. All exceptions must be caught and handled by a global exception handler annotated with `@RestControllerAdvice`. This ensures all API clients receive consistent, meaningful error responses.

---

## 2. Custom Exceptions
The primary method for signaling errors is by throwing specific, reusable custom exceptions.

* **Location:** All custom exceptions **must** be located in the `com.visma.kalmar.api.exception` package.
* **Inheritance:** All custom exceptions **must** extend `CustomRuntimeException`.
* **Purpose:** Create exceptions to represent specific, common business rule violations or data access errors (e.g., `ResourceNotFoundException`, `ResourceAlreadyExistsException`).

#### Custom Exception Example
```java
public class ResourceNotFoundException extends CustomRuntimeException {
    private final String resourceType;

    public ResourceNotFoundException(String resourceType, String message, Object... arguments) {
        super(message, arguments); // Supports formatted messages
        this.resourceType = resourceType;
    }

    public String getResourceType() {
        return resourceType;
    }
}
```
---
## 3. Layer-Specific Responsibilities

### 3.1. Gateway Layer (Data Access)
Gateways **must** translate low-level data access exceptions into high-level custom exceptions.

* When a repository `find` operation returns an empty `Optional`, you **must** throw a `ResourceNotFoundException`.
* Catch any `DataAccessException` and re-throw it as an appropriate custom exception.

#### Gateway Example
```java
@Override
public Customer findById(String customerId) {
    return customerRepository.findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", "Customer not found with id: %s", customerId));
}
```
### 3.2. Use Case Layer (Business Logic)
Use cases **must** validate business rules and throw custom exceptions when those rules are violated.

* Before creating a resource, check for its existence. If it already exists, **must** throw a `ResourceAlreadyExistsException`.
* Validate business constraints, throwing exceptions for failures.

#### Use Case Example
```java
@Override
public User createUser(User user) {
    if (userRepository.existsByEmail(user.getEmail())) {
        throw new ResourceAlreadyExistsException("User", "User with email %s already exists.", user.getEmail());
    }
    return userRepository.save(user);
}
```
---
## 4. The Global Exception Handler
The `GlobalExceptionHandler` class, annotated with `@RestControllerAdvice`, is the single point of entry for handling all exceptions.

* For each custom exception, there **must** be a corresponding handler method annotated with `@ExceptionHandler`.
* Each handler method **must** return a `ResponseEntity` containing a standard `ErrorResponse` object and the appropriate HTTP status code.

#### Exception Handler Example
```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(ex.getResourceType(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleResourceExists(ResourceAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(ex.getResourceType(), ex.getMessage());
```

