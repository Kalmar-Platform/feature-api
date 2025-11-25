# API Design & Implementation Guidelines

## 1. Core Principles

All APIs must be **simple**, **consistent**, and **usable**. Adhere strictly to these core standards:

* **Specification:** Use **OpenAPI 3.0**.
* **Architecture:** Implement **RESTful** services around business resources.
* **Language:** Use **U.S. English** for all definitions, paths, and schemas.
* **Security:** Enforce **JWT authentication** on all endpoints.

---

## 2. API Structure & Naming

Organize your API code with the following structure.

* **API Interface:**
    * **Location:** `controllers` module (`interface-adapter` layer), specific package based on resource (e.g.,
      `com.visma.api.feature.application.interfaceadapters.controllers.customer`).
    * **Name:** Suffix with `Api` (e.g., `CustomerApi`).
    * **Annotations:**
        * `@Validated` for input validation.
        * `@Tag(name = "...")` to group endpoints.
        * `@SecurityRequirement(name = "jwt")` for security.
        * `@Operation(summary = "...")` on each method.

* **API Controller:**
    * **Location:** Same package as the interface it implements.
    * **Name:** Suffix with `ApiController` (e.g., `CustomerApiController`).
    * **Annotations:**: `@RestController`


* **Versioning & Base Path:**
    * **Path:** All endpoints must be versioned in the URI, defined with `@RequestMapping("/api/v1")` on the controller.
    * **Resource URI:** Group endpoints by functionality (e.g., `/api/v1/customers`).

---

## 3. URI Path Design

URIs must identify resources, not actions.

* **Use Nouns:** Paths must be plural nouns (e.g., `/customers`).
* **Identify Items:** Use a path variable for a specific item (e.g., `/customers/{customerId}`).
* **Show Relationships:** Use hierarchy for related resources (e.g., `/customers/{customerId}/orders`).
* **Use Query Params:** For filtering and sorting, use query parameters (e.g., `/customers?status=active`).

---

## 4. Data Transfer Objects (DTOs)

Use DTOs to define the API's data contract. DTOs are separate from internal domain models.

* **Location:** A `dto` package, located alongside the API interface.
* **Naming:** Suffix with `Request` or `Response` (e.g., `CustomerRequest`, `CustomerResponse`).
* **Required Annotations:**
    * `@Data`: (Lombok) For boilerplate code.
    * `@JsonInclude(JsonInclude.Include.NON_NULL)`: To exclude null fields.
    * `@JsonProperty("fieldName")`: **Mandatory for every field** to define a stable JSON name.
    * `@Schema`: To provide OpenAPI metadata.
    * **Validation:** Use `@NotNull`, `@Size`, etc., for request DTOs.
* **Generic and reusable:** Design DTOs so that they can be reused across different api methods where applicable.

#### DTO Example

```java

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerRequest {
    @Schema(description = "The name of the customer.", required = true)
    @NotNull
    @JsonProperty("name")
    private String name;

    @Schema(description = "The two-letter country code.", required = true)
    @NotNull
    @JsonProperty("countryCode")
    private String countryCode;
}
```

## 5. Responses & Error Handling

### HTTP Status Codes

Use the correct HTTP status code for every response.

* **200 OK:** Successful `GET`.
* **201 Created:** Successful `POST`.
* **204 No Content:** Successful `DELETE` or action with no return body.
* **400 Bad Request:** Invalid request from the client.
* **401 Unauthorized:** Authentication failed.
* **403 Forbidden:** Authenticated user lacks permissions.
* **404 Not Found:** Resource does not exist.
* **409 Conflict:** Resource creation failed due to a conflict (e.g., already exists).

### Paginated Responses

For endpoints that return a list, pagination is **mandatory**. The response body must contain:

* `content`: A `List` of the DTOs for the current page.
* `totalNumberOfRecords`: A `long` with the total count of all available records.

### Error Responses

All `4xx` error responses must return a standard JSON error object:

```json
{
  "code": "RESOURCE_NOT_FOUND",
  "message": "Customer with ID {customerId} was not found.",
  "messageParameters": [
    "123e4567-e89b-12d3-a456-426614174000"
  ]
}
```

## 6. Controller Implementation Pattern

The controller's role is to adapt HTTP requests into application use case calls.

1. **Inject** the required `InputPort` via the constructor.
2. Use the constructor to also **instantiate a `Presenter`**.
3. **Map** the incoming `Request` DTO to `InputData`.
4. **Execute** the use case by calling the `InputPort` with the `InputData` and `Presenter`.
5. **Return** the `ResponseEntity` from the `presenter.getResponse()` method.

#### Controller Example

```java
@RestController
@RequestMapping("/api/v1")
public class CustomerApiController implements CustomerApi {

    private final CreateCustomerInputPort createCustomerInputPort;
    private final CustomerPresenter customerPresenter;
    // declare other input ports and presenters as needed ...

    // Constructor injection for input ports
    @Autowired
    public CustomerApiController(CreateCustomerInputPort createCustomerInputPort) {
        this(createCustomerInputPort, new CustomerPresenter());
    }
    
    // Initialize input ports and presenters
     /* Package private constructor for testing purposes */
    CustomerApiController( CreateCustomerInputPort createCustomerInputPort,
                           CustomerPresenter customerPresenter) {
        this.createCustomerInputPort = createCustomerInputPort;
        this.customerPresenter = customerPresenter;
     }

    @Override
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CustomerRequest customerRequest) {
        // 1. Map request to input data
        var inputData = toCustomerInputData(customerRequest);
        
        // 2. Execute use case
        createCustomerInputPort.createCustomer(inputData, presenter);
        
        // 3. Return formatted response
        return presenter.present();
    }
    
    private CustomerInputData toCustomerInputData(String customerId, CustomerRequest customerRequest) {
        var customerInputData = new CustomerInputData();
        customerInputData.setCustomerId(customerId);
        customerInputData.setName(customerRequest.getName());
        // map other fileds as needed ...  
        return customerInputData;
    }
}
```