# Unit Testing Guidelines

## 1. Core Principles & Frameworks
All unit tests **must** be written using **JUnit 5** and **Mockito**. The primary goal is to test individual components in isolation.

* **Isolation:** All external dependencies **must** be mocked. This includes gateways, presenters, and other services.
* **Adherence to Existing Patterns:** You **must** follow the structure, naming, and patterns present in the existing test suite. **Do not** introduce new libraries (like AssertJ) or modern testing styles.
* **Code Style:** All test code **must** adhere to the **[Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)**.
* **Special Case:** Unit tests for the `usecases` layer have unique rules and do not use mocks. Refer to [`unit-testing-usecases.instructions.md`](unit-testing-usecases.instructions.md) for those specific instructions.

---

## 2. Test Class Structure & Setup
All test classes **must** follow this precise structure.

* **Visibility:** Test classes and methods **must** be `package-private` (default visibility). Do not use the `public` modifier.
* **Location:** Test classes **must** mirror the source package structure under `src/test/java/`.
* **Mock Initialization:**
    * Dependencies **must** be annotated with `@Mock`.
    * Mocks **must** be initialized in a `@BeforeEach` annotated method named `setUp()`.
    * The `setUp()` method **must** call `MockitoAnnotations.initMocks(this)`.
    * **Do not use** `@ExtendWith(MockitoExtension.class)`.

#### Mandatory Class Structure
```java
class CustomerApiControllerTest {

    @Mock
    private CreateCustomerInputPort createCustomerInputPort;

    @Mock
    private CustomerPresenter customerPresenter;
    
    // The class being tested
    private CustomerApiController customerApiController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        // Dependencies MUST be injected via a constructor
        customerApiController = new CustomerApiController(createCustomerInputPort, customerPresenter);
    }

    // ... test methods
}
```
### 2.1. Controller Constructor Refactoring
If a controller or other class does not have a constructor that accepts all its dependencies (including presenters), you **must** refactor it to add one. The existing `@Autowired` constructor should then call the new, test-friendly constructor.

---
## 3. Naming and Test Data Conventions

* **Test Class Names:** **Must** end with `Test` (e.g., `CustomerApiControllerTest`).
* **Test Method Names:** Use descriptive names that clearly state the scenario being tested (e.g., `testGetCustomerById_Success`, `testGetCustomerById_NotFound`).
* **Test Data:**
    * Use `private static final` constants for common, repeated test values (e.g., `CUSTOMER_NAME`).
    * Create `private` helper methods to generate test objects (e.g., `createTestCustomer()`).

---
## 4. Testing Patterns by Layer

### 4.1. Controller Tests
* **Goal:** Verify that the controller correctly invokes the `InputPort` and uses the `Presenter`.
* **Pattern:**
    1.  Mock all dependencies (`InputPort`s, `Presenter`s).
    2.  Stub the behavior of the presenter to return a pre-configured `ResponseEntity`.
    3.  Call the controller method.
    4.  Assert the `ResponseEntity` is what the presenter was stubbed to return.
    5.  **Verify** that the `InputPort` was called with the correct `InputData` and the presenter instance.

### 4.2. Presenter Tests
* **Goal:** Verify the presenter correctly maps `OutputData` to an API `Response` DTO within a `ResponseEntity`.
* **Pattern:**
    1.  Create an instance of the presenter.
    2.  Create a sample `OutputData` object.
    3.  Call the `present()` method.
    4.  Call the `getResponse()` method and assert that the `ResponseEntity` has the correct `HttpStatus` and that the body's fields match the `OutputData`.

### 4.3. Gateway Adapter Tests
* **Goal:** Verify the adapter correctly interacts with mocked repositories and maps data between domain objects and database entities.
* **Pattern:**
    1.  Mock the `JpaRepository` dependency.
    2.  Stub repository method calls (e.g., `when(repository.findById(...)).thenReturn(Optional.of(entity))`).
    3.  Call the gateway adapter method.
    4.  Assert that the returned domain object is correctly mapped from the entity.
    5.  **Verify** the repository method was called.

### 4.4. Visma Connect Gateway Tests
* **Goal:** Test the HTTP client interaction with the Visma Connect API.
* **Pattern:**
    1.  Use `okhttp3.MockWebServer` to simulate the API.
    2.  Start the server in a `@BeforeAll` method and shut it down in an `@AfterAll` method.
    3.  Enqueue mock HTTP responses on the server.
    4.  Call the gateway method and assert it correctly parses the response.

---

## 5. Mocking, Verification, and Assertions

### 5.1. Mocking and Stubbing
* Use `when(mock.method(...)).thenReturn(result)` to define mock behavior.

### 5.2. Verification
* After calling the method under test, you **must** verify interactions with mocks using `verify()`. This is a critical step.
* `verify(mockService).methodName(expectedArgument)`
* `verify(mockService, times(1)).methodName(any())`

### 5.3. Assertions
* Use standard JUnit 5 assertions only: `assertEquals`, `assertNotNull`, `assertTrue`, `assertThrows`.

### 5.4. Exception Testing
* To test for expected exceptions, you **must** use `assertThrows()`.

```java
@Test
void testGetCompany_NotFound() {
    // Stub the dependency to throw an exception
    when(companyGateway.findById(999L)).thenThrow(new ResourceNotFoundException("Company", "..."));
    
    // Assert that calling the method results in the expected exception
    assertThrows(ResourceNotFoundException.class, () -> companyUseCase.getCompany(999L, presenter));
}
```
## 6. Mandatory Rules Summary
* **Always mock dependencies.**
* **Use the specified `@BeforeEach` `setUp()` pattern.**
* **Classes and methods must be `package-private`.**
* **Inject dependencies via a constructor.** Refactor if necessary.
* **Always `verify()` mock interactions.**
* **Do not add `// Arrange, // Act, // Assert` comments.**
* **Adhere strictly to existing patterns and do not introduce new libraries or styles.**