# Kalmar: Feature Management System

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/spring%20boot-%236DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/spring%20Security-%236db33f.svg?style=for-the-badge&logo=springsecurity&logoColor=white)
![SQL](https://img.shields.io/badge/SQL-%23007ACC.svg?style=for-the-badge&logo=mysql&logoColor=white)

A feature management platform tailored for Nordic markets (🇳🇴 Norway, 🇸🇪 Sweden, 🇩🇰 Denmark, 🇫🇮 Finland). This system provides a robust and scalable solution for managing users, customers and feature.

Built with **Spring Boot** and leveraging **Clean Architecture**, the fFature system is designed for maintainability, testability, and high performance. It features secure authentication using **OAuth2/JWT** and offers comprehensive API documentation via **OpenAPI 3.0**.

---

## ✨ Key Features

* **Full CRUD Operations**: Comprehensive APIs for managing Users, Customers and Feature.
* **Secure by Design**: Implements OAuth2/JWT authentication and authorization powered by Spring Security.
* **Production-Ready**: Includes health checks, environment-specific profiles, and a containerized deployment pipeline.
* **Clean Architecture**: A well-structured, modular design that separates business logic from infrastructure concerns.
* **Comprehensive API Docs**: Automatically generated and interactive API documentation with Swagger UI.

---

## 🛠️ Technology Stack

| Component                | Technology                                                                          |
|--------------------------|-------------------------------------------------------------------------------------|
| **Core Framework**       | Java 21 <br/>Spring Boot 3.5.6                                                      |
| **Security**             | Spring Security with OAuth2/JWT (Visma Connect Integration)                         |
| **Data Persistence**     | Spring Data JPA, <br/>MySQL 8.0+ (AWS Aurora for production)                             |
| **Build & Dependencies** | Maven                                                                               |
| **API Documentation**    | OpenAPI 3.0 (Springdoc)                                                             |
| **Testing**              | JUnit 5 <br/>Mockito                                                                    |
| **Containerization**     | [Google Jib](https://github.com/GoogleContainerTools/jib) for Docker image creation |

---

## 🏗️ Architecture

The application adheres to **Clean Architecture** principles to promote separation of concerns and create a highly maintainable codebase. The core business logic is independent of frameworks and external interfaces.

The project structure is organized into the following layers:

```
feature-api/
├── application/
│   ├── entities/                 # Domain entities (innermost layer)
│   ├── usecases/                 # Business logic and application services
│   ├── interface-adapters/       # Controllers, gateways, and external API adapters
|   |   |── aws-s3-adapter/       # AWS S3 integration
|   |   ├── connect-adapter/      # Visma Connect integration
│   │   ├── controllers/          # REST API controllers
│   │   └── gateways/             # Database access layer
│   └── external-interfaces/      # External systems and infrastructure
│       ├── boot/                 # Spring Boot application
│       └── databases/            # Database configurations
```
---
## 🚀 Getting Started

Follow these instructions to set up and run the project on your local machine.

### Prerequisites

* **Java 21** or higher
* **Maven 3.6+**
* **MySQL 8.0+** running locally or an accessible Aurora MySQL instance for development

### Database Setup

The system requires two separate MySQL databases:
1.  **Feature Database**: Manages user accounts, roles, and permissions. It also serves as the default source for reference data (e.g., `Country`, `Language`).
2.  **Feature Database**: Manages customer and feature data.

To create the required schemas and tables, execute the SQL scripts located in `.github/instructions/data-model.instructions.md`.

### Environment Configuration

The application is deployed across multiple environments, managed via `Spring` Profiles.
Configuration is managed through `application.yml` files, with sensitive values supplied by environment variables.

Variables specific to a profile are defined in profile-specific files (e.g., `application-dev.yml`).

#### Deployment Environments

| Profile Name | Environment | Purpose                                              |
| ------------ | ----------- |------------------------------------------------------|
| `dev`        | Development | Local developer environments,                        |
| `int`        | Internal    | Internal testing and early-stage feature validation. |
| `stag`       | Staging     | Pre-production environment for final testing and QA. |
| `prod`       | Production  | Live customer-facing environment.                    |

#### Environment Variables defined in `application.yml` 

| Variable                   | Description                                                               | Required |
| -------------------------- | ------------------------------------------------------------------------- | :------: |
| `SPRING_PROFILES_ACTIVE`   | The active Spring profile (e.g., `dev`, `int`, `stag`, `prod`).           |   Yes    |
| `USER_ACCESS_DB_USERNAME`  | Username for the **Feature** database.                                 |   Yes    |
| `USER_ACCESS_DB_PASSWORD`  | Password for the **Feature** database.                                 |   Yes    |
| `CONNECT_CLIENT_ID`        | OAuth2 Client ID for Visma Connect APIs.                                  |   Yes    |
| `CONNECT_CLIENT_SECRET`    | OAuth2 Client Secret for Visma Connect.                                   |   Yes    |

#### Profile-Specific Environment Variables

Non-sensitive variables that differ per environment are defined in profile-specific files (e.g., `application-dev.yml`).
These are grouped under the following categories:
* **Visma Connect Resource Server Configuration** defined under `security.oauth2.resourceserver` section

    | Variable   | Description                                                                                                                                                                                                                                           |        Required         |
    |------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:-----------------------:|
    | `issuer`   | The main address for an Authorization Server. <br>Spring Security uses this single URI to automatically discover all the other endpoints it needs to function <br/>It does this by appending `/.well-known/openid-configuration` to the URI provided. |           Yes           | 
    | `audience` | Used by `Spring Security` to verify the token was issued for this API.                                                                                                                                                                                | For `stag` and `prod`   |

* **Visma Connect Client Configuration** defined under `security.oauth2.client` section

    | Variable         | Description                                                                         |        Required         |
    |------------------|-------------------------------------------------------------------------------------|:-----------------------:|
    | `scopes`         | The list of scopes required to call different methods from  the Visma Connect API's |           Yes           |
    | `token-uri`      | Token endpoint URL for Visma Connect IDP                                            |           Yes           |

* **Visma Connect API Endpoints** defined under `connect` section

    | Variable               | Description                           | Required           |
    |------------------------|---------------------------------------|:------------------:|
    | ` public-endpoint`     | URL for the Visma Connect Public API. |        Yes         |

* **Database URLs** defined under `datasource` section

    | Variable                  | Description                                 |        Required         |
    |---------------------------|---------------------------------------------|:-----------------------:|
     | `user-access-db-url`      | JDBC URL for the **Feature** database.   |           Yes           |
    
* **Logging Configuration** defined under `logging` section. Used for debugging on local environments. **Do not enable in `stag` or `prod` profiles!** as they generate a lot of logs and may expose sensitive data.

    | Variable                                                    | Description                                                                          |               Required               |
    |-------------------------------------------------------------|--------------------------------------------------------------------------------------|:------------------------------------:|
    | `logging.level.sql`                                         | Log all the SQL statements it generated and executed.                                | No, set to `debug` for `dev` profile |
    | `logging.level.org.hibernate.orm.jdbc.bind`                 | Log all parameter values that are being bound to the placeholders (?) in SQL queries | No, set to `trace` for `dev` profile |
    | `logging.level.org.springframework.transaction.interceptor` | Log Spring Transaction boundaries (begin, commit, rollback)                          | No, set to `debug` for `dev` profile |
    | `logging.level.reactor.netty.http.client`                   | Log HTTP client requests and responses made by the application                       | No, set to `debug` for `dev` profile |

#### Access to AWS Services

The AWS region is configured in `application.yaml` under the `aws` section and defaults to `eu-west-1` if not specified.

If you need to connect to AWS services locally, set the following environment variables:

| Variable                | Description                                                           |    Required     |
|-------------------------|-----------------------------------------------------------------------|:---------------:|
| `AWS_ACCESS_KEY_ID`     | AWS access key for local development (if connecting to AWS services). |  No             |
| `AWS_SECRET_ACCESS_KEY` | AWS secret key for local development.                                 |  No             |
    
These variables are not set in any configure files , but they need to be set in the local environment if AWS services are to be accessed locally. 
More details on how AWS Credentials work are available on the [AWS Credentials Chain](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials-chain.html) documentation page.

---

## ⚙️ Building and Running

### Build the Application

Compile the code, run tests, and package the application into a JAR file.

```bash
mvn clean install
```
### Run the Application
Launch the application using the Spring Boot Maven plugin. The correct configuration will be loaded based on the `SPRING_PROFILES_ACTIVE` environment variable.

```bash
mvn spring-boot:run -pl application/external-adapters/boot
```

### Run Tests
Execute the unit and integration test suite.

``` bash
mvn test
```
---
## 🐳 Containerization with Jib

This project uses Jib to build optimized Docker images directly from Maven without a Dockerfile.

### Build a Local Docker Image
This command builds an image named feature-api and saves it to your local Docker daemon.

```shell
./mvnw clean install
./mvnw compile com.google.cloud.tools:jib-maven-plugin:dockerBuild -pl application/external-interfaces/boot -Dimage=feature-api
```
### Build and Push to AWS ECR
This command is used in CI/CD pipelines to build the image and push it directly to an Amazon ECR repository.

```# Replace {awsAccountId} with your AWS Account ID
./mvnw compile com.google.cloud.tools:jib-maven-plugin:build -pl application/external-interfaces/boot -Dimage={awsAccountId}.dkr.ecr.eu-west-1.amazonaws.com/feature-api
```
## 📖 API Documentation & Health

Once the application is running, you can access the API documentation and health check endpoints.

Swagger UI: http://localhost:8080/swagger-ui.html

OpenAPI Spec: http://localhost:8080/v3/api-docs

Health Check: http://localhost:8080/actuator/health

## ☁️ Infrastructure
The infrastructure for the test environment is provisioned on AWS (Account ID: `602259772901`).
  
The current infrastructure is managed with `Terraform`, and the code is available in the `feature-api-infrastructure` repository. There are ongoing plans to migrate this setup to a common infrastructure based on `Terragrunt` and `OpenTofu`.

## 🙌 Contributing
 We welcome contributions! Please follow the guidelines below:

**Coding Style**: Adhere to the standards outlined in .github/instructions/coding-style-java.instructions.md.

**Architecture**: Respect the Clean Architecture principles detailed in .github/instructions/clean-architecture.instructions.md.

To contribute, please fork the repository, create a new branch for your feature or bug fix, and submit a pull request.
