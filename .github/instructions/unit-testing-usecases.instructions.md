# Unit Testing Guidelines for Use Cases

## 1. Core Testing Philosophy: No Mocking

Unit tests for the `usecases` module **must not** use mocking frameworks like Mockito. This is a strict, non-negotiable
rule.

* Tests **must** instantiate the real `UseCase` implementation.
* All `Gateway` dependencies **must** be fulfilled using real, **in-memory adapter implementations**.
* The goal is to test the actual behavior and integration between the use case and its data contracts, not to verify
  mock interactions.

---

## 2. Test Class Structure

* **Naming:** Test classes **must** be named `{UseCaseClassName}Test` (e.g., `CreateCustomerUseCaseTest`).
* **Location:** Test files **must** mirror the main source package structure under `src/test/java/`.
* **Visibility:** Test classes and methods **must** be `package-private` (default visibility).

#### Test Class Setup

The `setUp()` method, annotated with `@BeforeEach`, is used to initialize the `UseCase` and its in-memory gateway
dependencies.

```java
class CreateCustomerUseCaseTest {
    // In-memory implementations, not mocks
    private InMemoryCustomerGatewayAdapter customerGateway;
    private InMemoryCountryGatewayAdapter countryGateway;

    // The real UseCase instance
    private CreateCustomerUseCase useCase;

    @BeforeEach
    void setUp() {
        // Instantiate the in-memory gateways
        customerGateway = new InMemoryCustomerGatewayAdapter();
        countryGateway = new InMemoryCountryGatewayAdapter();

        // Inject real in-memory gateways into the real use case
        useCase = new CreateCustomerUseCase(customerGateway, countryGateway);

        // Seed the in-memory gateways with necessary test data
        countryGateway.save(new Country(COUNTRY_ID, "Test Country", "TC"));
    }

    // ... test methods
}
```

---

## 3. In-Memory Gateway Implementations

Instead of mocks, you **must** use or create in-memory gateway adapters for testing.

* **Location:** In-memory adapters **must** reside in the `test` source folder, mirroring the package structure of the
  gateway interface they implement.
* **Naming:** The class name **must** follow the pattern `InMemory{GatewayName}Adapter` (e.g.,
  `InMemoryCustomerGatewayAdapter`).
* **Implementation:**
    * The class **must** implement the production `Gateway` interface.
    * It **must** use in-memory collections (`Map`, `List`) to store data.
    * It **must** replicate the behavior of the real gateway, including throwing `ResourceNotFoundException` when an
      entity is not found.

#### In-Memory Gateway Example

```java
public class InMemoryCustomerGatewayAdapter implements CustomerGateway {
    private final Map<UUID, Customer> database = new ConcurrentHashMap<>();

    @Override
    public Customer findById(UUID id) {
        Customer customer = database.get(id);
        if (customer == null) {
            throw new ResourceNotFoundException("Customer", "Customer not found with id: %s", id);
        }
        return customer;
    }

    @Override
    public void save(Customer customer) {
        database.put(customer.getId(), customer);
    }

    public void clear() {
        database.clear();
    }

    // ... other implemented methods
}
```
---

## 4. Test Method Pattern

Each test method validates a single scenario by executing the use case and then asserting the state of the in-memory
gateway.

### 4.1. Handling `OutputPort`s

You **must** use a lambda expression to provide a simple, inline implementation for the `OutputPort`. **Do not** create
separate mock objects or anonymous inner classes.

### 4.2. "Arrange, Act, Assert" Pattern

1. **Arrange:** Set up the `InputData` for the use case.
2. **Act:** Execute the use case method, passing the input data and a lambda for the output port.
3. **Assert:**
    * Verify the direct output of the use case if applicable.
    * Fetch the entity from the in-memory gateway to verify it was saved correctly and its state is as expected.
4. **Do not add any Arrange, Act, Assert comments in the code.** The structure should be clear from the code itself.

#### Test Method Example

```java
@Test
void execute_withValidData_createsAndSavesCustomer() {
    
    var inputData = new CustomerInputData("New Customer", "12345", "TC");
    final AtomicReference<CustomerOutputData> result = new AtomicReference<>();
    
    useCase.execute(inputData, output -> result.set(output));
    
    assertNotNull(result.get());
    
    var savedCustomer = customerGateway.findById(result.get().customerId()).orElseThrow();
    assertEquals("New Customer", savedCustomer.getName());
}
 ```
---
## 5. Exception Testing
To test error conditions, set up the in-memory gateway in a state that will cause the use case to fail, and then use `assertThrows` to verify the correct exception is thrown.

```java
@Test
void execute_withNonExistentCountry_throwsException() {
    // Arrange: Input data with a country code that does not exist in the gateway
    var inputData = new CustomerInputData("Another Customer", "54321", "XX");

    // Act & Assert
    var exception = assertThrows(ResourceNotFoundException.class, 
        () -> useCase.execute(inputData, output -> {}));
        
    assertEquals("Country not found with code: XX", exception.getMessage());
}
```
  