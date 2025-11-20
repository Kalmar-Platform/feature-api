# Use Case Guidelines

## 1. Core Principles

Use cases contain all application-specific business logic. They are the core of the application and **must** remain
completely independent of external frameworks like Spring, databases, or API details.

* **Location:** All use case components (`InputPort`, `UseCase` implementation, `InputData`, `OutputPort`, `OutputData`)
  **must** be located in the `usecases` module.
* **Organization:** Group all related use case components by domain concept in packages (e.g.,
  `com.visma.kalmar.api.usecases.customer`).
* **Dependencies:** A use case **must** only depend on `Gateway` interfaces for data access. It **must not** depend on
  controllers or repositories directly.
* **Configuration:** Every `UseCase` implementation **must** be exposed as a `@Bean` in a `@Configuration` class within
  the `external-interfaces/boot` module to enable dependency injection.

---

## 2. The Input/Output Port Pattern

Use cases are exposed to outer layers via the `InputPort` interface, which defines a clear contract for executing the
business logic.

### 2.1. Defining the `InputPort`

* **Purpose:** The `InputPort` is the public API of the use case.
* **Naming:** The interface name **must** describe its action and end with `InputPort` (e.g.,
  `CreateCustomerInputPort`).
* **Method:** The interface **must** contain a single method that returns `void`. This method takes two parameters: an
  `InputData` object and an `OutputPort` interface.
    * **Exception:** For use cases that produce no output (e.g., delete operations), the `OutputPort` parameter can be
      omitted.

#### `InputPort` Example

```java
public interface CreateCustomerInputPort {
    void execute(CreateCustomerInputData inputData, CreateCustomerOutputPort outputPort);
}
```

### 2.2. Defining `InputData` and `OutputData`

* **Purpose:** These objects define the data contract for the use case. They contain no logic.
* **Implementation:** They **must** be implemented as immutable Java `record` classes.
* **Naming:** Use generic names where possible to promote reusability (e.g., `CustomerInputData` is preferable to
  `CreateCustomerInputData` if it can be reused by an update use case).

#### `InputData` / `OutputData` Example

```java
// Contains data needed to execute the use case
public record CustomerInputData(String name, String orgNumber, String countryCode) {
}

// Contains data produced by the use case
public record CustomerOutputData(String customerId, String name) {
}
```

### 2.3. Defining the `OutputPort`

* **Purpose:** The `OutputPort` is a callback interface that the use case calls to return its result.
* **Naming:** The interface name **must** correspond to the use case and end with `OutputPort` (e.g.,
  `CreateCustomerOutputPort`).
* **Method:** It **must** contain a single method named `present` which takes an `OutputData` record as a parameter and
  returns `void`.

#### `OutputPort` Example

```java
public interface CreateCustomerOutputPort {
    void present(CustomerOutputData outputData);
}
```

--- 

## 3. Implementing the Use Case

The use case implementation contains the actual business logic.

* **Naming:** The class **must** implement the `InputPort` interface and have the same name with a `UseCase` suffix (
  e.g., `CreateCustomerUseCase`).
* **Logic:**
    1. Inject required `Gateway` interfaces via the constructor.
    2. Implement the business logic method.
    3. Perform business validations (e.g., check if a resource exists using `gateway.existsBy(...)`).
    4. Call gateway methods to interact with data sources.
    5. Create the `OutputData` record with the result.
    6. Call `outputPort.present(outputData)` to return the result.

#### `UseCase` Implementation Example

```java
public class CreateCustomerUseCase implements CreateCustomerInputPort {
    private final CustomerGateway customerGateway;
    private final CountryGateway countryGateway;

    public CreateCustomerUseCase(CustomerGateway customerGateway, CountryGateway countryGateway) {
        this.customerGateway = customerGateway;
        this.countryGateway = countryGateway;
    }

    @Override
    public void createCustomer(CustomerInputData inputData, CreateCustomerOutputPort outputPort) {
        // Business logic: validation
        if (customerGateway.existsByOrgNumber(inputData.orgNumber())) {
            throw new ResourceAlreadyExistsException("Customer", "...");
        }

        // Business logic: data transformation and persistence
        Country country = countryGateway.findByCode(inputData.countryCode());
        Customer newCustomer = new Customer(inputData.name(), country.getId());
        customerGateway.save(newCustomer);

        // Prepare and present output
        var outputData = new CustomerOutputData(newCustomer.getId(), newCustomer.getName());
        outputPort.present(outputData);
    }
}
```

---

## 4. Implementing the Presenter

The presenter is the implementation of the `OutputPort` interface. Its primary role is to adapt the use case's pure data
output (`OutputData`) into an HTTP-specific format (`ResponseEntity`) for the API layer.

* **Location:** The presenter class **must** be located in the `interface-adapters` module, in the same package as the
  controller that uses it.
* **Naming:** The class name **must** reflect the domain it serves and end with a `Presenter` suffix (e.g.,
  `CustomerPresenter`).

### 4.1. Reusability and Generic Design

To reduce code duplication, presenters **should be designed to be as generic as possible**. A single presenter should
handle the output for multiple related use cases.

For example, a single `CustomerPresenter` can be used for `CreateCustomerUseCase`, `GetCustomerUseCase`, and
`UpdateCustomerUseCase`.

To achieve this, the `OutputData` record should include a field that signals the outcome of the operation. The presenter
then uses this field to return the appropriate HTTP status code.

### 4.2. Functionality and Implementation Pattern

1. **Implement the `OutputPort` interface(s).** A generic presenter might implement multiple `OutputPort` interfaces if
   their `present` methods have different signatures.
2. **The `present` method** converts the `OutputData` record into an API `Response` DTO.
3. It then inspects a field on the `OutputData` (e.g., a boolean flag like `isCreated`) to determine the correct
   `HttpStatus`.
4. Finally, it builds and stores the final `ResponseEntity`.
5. A public `getResponse()` method allows the controller to retrieve the result.

#### Updated `OutputData` with Outcome Flag

The `OutputData` now includes a boolean to signal if the resource was newly created.

```java
// A reusable OutputData record
public record CustomerOutputData(
                String customerId,
                String name,
                boolean isCreated // Flag to indicate if the resource was just created
        ) {
}
```

#### Reusable Presenter Example

This `CustomerPresenter` can now handle both creation (`201 CREATED`) and retrieval (`200 OK`) scenarios.

```java
public class CustomerPresenter implements CustomerOutputPort {
    private ResponseEntity<CustomerResponse> response;

    @Override
    public void present(CustomerOutputData outputData) {

        var customerResponse = new CustomerResponse();
        customerResponse.setCustomerId(outputData.customerId());
        customerResponse.setName(outputData.name());

        HttpStatus status = outputData.isCreated() ? HttpStatus.CREATED : HttpStatus.OK;

        this.response = ResponseEntity.status(status).body(customerResponse);
    }
}
```