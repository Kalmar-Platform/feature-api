# Clean Architecture Guidelines

## 1. Core Principles

This project strictly adheres to Clean Architecture. All code must be organized into the specified layers, and **The
Dependency Rule** must be followed at all times: **dependencies must only point inwards**. Outer layers depend on inner
layers, never the other way around.

---

## 2. Project Structure

The solution is organized into four primary Maven modules, representing the architectural layers.

* **`entities`**: The innermost layer containing core domain objects.
* **`usecases`**: The application business logic layer.
* **`interface-adapters`**: The layer for controllers, gateways, and other adapters.
* **`external-interfaces`**: The outermost layer for frameworks, databases and external systems.

#### Foldder & Package Structure

The code is located in the `application/` folder. While modules separate layers, packages within them should be
organized by domain concepts (e.g., `customer`, `feature`).

```plaintext
feature-api/
└── application/
    ├── entities/
    ├── usecases/
    ├── interface-adapters/
    |   ├── controllers/
    |   ├── gateways/
    |   └── connect-adapter/
    └── external-interfaces/
        ├── boot/
        └── databases/
```

---

## 3. Layer Responsibilities

### 3.1. Entities Layer

* **Purpose:** Contains the core domain objects of the application, representing fundamental business concepts.
* **Implementation:** These objects must be implemented as immutable Java `record` classes.

*Example `Customer` Entity:*

```java
public record Customer(UUID idCustomer, String name, String organizationNumber) {
}
```

### 3.2. Use Cases Layer

* **Purpose:** Contains all application-specific business logic. Each use case defines a single, specific operation (
  e.g., `CreateCustomer`, `GetUserDetails`).
* **Constraints:** This layer **must remain independent** of any frameworks, databases, or UI concerns.
* **Guidelines:** For detailed implementation rules, refer to the *
  *[Use Case Guidelines](usecase-guidelines.instructions.md)**.

### 3.3. Interface Adapters Layer

* **Purpose:** Connects the core application (use cases) to external systems by converting data to the appropriate
  formats.

* **Controllers:**
    * **Function:** Expose use cases as RESTful APIs. They are responsible for handling HTTP requests and converting
      data between DTOs and domain models.
    * **Naming:** Interfaces must end with `Api` (e.g., `CustomerApi`). Implementations must end with `ApiController` (
      e.g., `CustomerApiController`).
    * **Guidelines:** For implementation, refer to the **[API Guidelines](api-guidelines.instructions.md)**.

* **Gateways:**
    * **Function:** Provide an abstraction layer for data persistence, hiding database specifics from the use cases.
    * **Guidelines:** For implementation, refer to the **[Gateway Guidelines](gateway-guidelines.instructions.md)**.

* **Connect Adapter:**
    * **Function:** Handles all communication with the external Visma Connect API for identity and authentication.

### 3.4. External Interfaces Layer

* **Purpose:** The outermost layer containing frameworks, drivers, and external tools.

* **Boot:**
    * **Function:** Contains the application's main entry point (`@SpringBootApplication`) and all Spring
      `@Configuration` classes for dependency injection (bean definitions).

* **Databases:**
    - **Function:** Contains database-specific connectors and configurations for `Aurora MySQL` and `H2 Embedded`.
---

## 4. Dependency Management

* **Tool:** `Maven` is used for dependency management.
* **Rule:** Each module's `pom.xml` must only declare dependencies on layers that are further inward. For example,
  `interface-adapters` can depend on `usecases`, but `usecases` **cannot** depend on `interface-adapters`.
* **Versions:** Use the `dependencyManagement` section in the root `pom.xml` to control dependency versions globally.