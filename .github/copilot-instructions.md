# AI Assistant Development Guidelines: Feature API

## 1. Project Overview

This document provides the context and operational guidelines for AI-assisted development on the Visma Kalmar Feature API.

The **Kalmar Feature Management System** is an enterprise-grade, multi-tenant Spring Boot application designed for B2B Feature management across Nordic markets (Norway, Sweden, Denmark, Finland). It provides a secure, multi-market backend solution for the complete lifecycle management of customers, customer contacts, and their feature.

The architecture is designed for a single, unified codebase to be deployed across all markets without country-specific modifications. It leverages **Clean Architecture** principles to ensure scalability, maintainability, and separation of concerns. Security is paramount, with **OAuth 2.0 (Client Credentials Flow)** and **JWT** serving as the foundation for authentication and authorization.

---

## 2. Technology Stack

The project is built upon the following technologies:

| Category                 | Technology / Tool         | Purpose                                        |
|--------------------------|---------------------------|------------------------------------------------|
| **Language & Core**      | `Java 21`                 | Primary programming language                   |
|                          | `Spring Boot 3.x`         | Application framework                          |
|                          | `Spring Data`             | Data access and repository abstraction         |
|                          | `Spring Security`         | Security and authentication framework          |
| **Build & Dependencies** | `Maven`                   | Project build and dependency management        |
| **Database**             | `AWS Aurora MySQL 8.0+`   | Primary datastore for production environments  |
|                          | `MySQL 8.0+` (in Docker)  | Local development and integration testing      |
| **API & Documentation**  | `OpenAPI 3.0`             | API specification and documentation            |
| **Testing**              | `JUnit 5` <br/>`Mockito`  | Unit and integration testing framework         |
| **Infrastructure**       | `AWS SES`                 | Email delivery service                         |                
|                          | `AWS S3`                  | Temporary file storage                         |
|                          | `Datadog`                 | Application performance monitoring and metrics |
| **Logging**              | `SLF4J`<br/> `Logback`    | Logging facade and implementation              |

---
## 3. Security Architecture

The application's security is anchored by OAuth 2.0 and JWT, configured in:
`external-adapters/boot/src/main/java/com/visma/api/feature/externaladapters/boot/config/SecurityConfig.java`

Key security features include:
* **Authentication**: OAuth 2.0 Client Credentials Flow for service-to-service communication.
* **Authorization**: Role-Based Access Control (RBAC) enforced at the API endpoint level.
* **Token Management**: JWT validation, including signature, issuer, and expiration checks.
* **Web Security**: Cross-Site Request Forgery (CSRF) protection and Cross-Origin Resource Sharing (CORS) configuration.

### 3.1. Integration with Visma Connect (Identity Provider)

The `connect-adapter` module (`application/interface-adapters/connect-adapter`) is responsible for all interactions with Visma Connect. Its primary duties are:
* Implementing the OAuth 2.0 client credentials flow to acquire access tokens.
* Validating incoming JWTs received from clients.
* Managing the token lifecycle, including the automatic refreshment of expired tokens using Spring Security's built-in mechanisms.

---

## 4. AI Assistant (Copilot) Directives

**Your primary directive is to adhere strictly to the rules and guidelines outlined in the referenced instruction files. Precision and compliance are paramount.**

### 4.1. Core Principles

1.  **Follow Instructions Explicitly**: Do not deviate from the provided guidelines. All generated code must conform to the standards defined in the rule files.
2.  **No Unsolicited Modifications**: Do not refactor, optimize, or alter existing code unless specifically instructed to do so.
3.  **Clarify Ambiguity**: If a task is unclear or requires assumptions, you must ask for clarification. Do not infer or guess intent.
4.  **Perform Only Requested Tasks**: Do not add "extra" features, code, or comments. Your output should be limited to the scope of the explicit instructions.
5.  **No Code Comments**: Do not add comments to the code unless explicitly requested as part of the task.
6.  **Language**: All communication, code, and documentation must be in English.

### 4.2. Global Requirements & Constraints

These are default requirements that apply to all code generation tasks unless explicitly overridden by a specific prompt.

#### 1. Data Transfer Objects (DTOs)
* The `InputData` and `OutputData` objects (or equivalent DTOs) must be designed to be **generic and reusable** across different use cases and methods where applicable.

#### 2. Presenter & HTTP Response
* The `Presenter` implementation for the API must also be designed for **generic reusability**.
* It must respond with **`HTTP 201 Created`** when a new resource is successfully created.
* It must respond with **`HTTP 200 OK`** when returning an existing resource or on successful completion of an action that doesn't create a new resource.

### 4.3. Workflow and Procedures

1.  **Acknowledge Guiding Documents**: **This is a mandatory first step.** Before generating any code, you must declare which instruction files you have consulted to guide your implementation.
 * *Example: `Instructions used: [clean-architecture.instructions.md, api-guidelines.instructions.md]`*

2.  **Consult Reference Materials**: The following files, located in the `.github/instructions/` directory, are the source of truth for all development standards.
 * `clean-architecture.instructions.md`: Defines rules for implementing Clean Architecture, use cases, and domain logic.
 * `api-guidelines.instructions.md`: Specifies conventions for API endpoint design, request/response models, and versioning.
 * `exception-handling-guidelines.instructions.md`: Outlines the strategy for handling and surfacing exceptions.
 * `repositories-guidelines.instructions.md`: Governs the implementation of repository and data access patterns.
 * `gateway-guidelines.instructions.md`: Provides instructions for implementing external service gateways.
 * `data-model.instructions.md`: Contains the reference SQL schema for generating ORM entities.
 * `unit-testing-guidelines.instructions.md`: Guidelines for writing unit tests for all modules *except* `usecases`.
 * `unit-testing-usecases.guidelines.instructions.md`: Specific guidelines for testing `usecases`.

3.  **Build and Validate**: After completing a task, always validate the project by running a full Maven build. Use the Maven wrapper.
    ```bash
    ./mvnw clean install
    ```
    You must resolve any compilation errors or test failures before concluding the task.

4.  **Unit Testing**: Do not generate any unit tests unless you are specifically instructed to do so.