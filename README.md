# Kalmar: Feature Management System

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/spring%20boot-%236DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/spring%20Security-%236db33f.svg?style=for-the-badge&logo=springsecurity&logoColor=white)
![SQL](https://img.shields.io/badge/SQL-%23007ACC.svg?style=for-the-badge&logo=mysql&logoColor=white)

A subscription management platform tailored for Nordic markets (🇳🇴 Norway, 🇸🇪 Sweden, 🇩🇰 Denmark, 🇫🇮 Finland). This system provides a robust and scalable solution for managing users, customers and subscriptions.

Built with **Spring Boot** and leveraging **Clean Architecture**, the Subscription system is designed for maintainability, testability, and high performance. It features secure authentication using **OAuth2/JWT** and offers comprehensive API documentation via **OpenAPI 3.0**.

---

## ✨ Key Features

* **Full CRUD Operations**: Comprehensive APIs for managing Users, Customers and Subscriptions.
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
subscription-api/
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
This command builds an image named subscription-api and saves it to your local Docker daemon.

```shell
./mvnw clean install
./mvnw compile com.google.cloud.tools:jib-maven-plugin:dockerBuild -pl application/external-interfaces/boot -Dimage=subscription-api
```
### Build and Push to AWS ECR
This command is used in CI/CD pipelines to build the image and push it directly to an Amazon ECR repository.

```# Replace {awsAccountId} with your AWS Account ID
./mvnw compile com.google.cloud.tools:jib-maven-plugin:build -pl application/external-interfaces/boot -Dimage={awsAccountId}.dkr.ecr.eu-west-1.amazonaws.com/subscription-api
```
## 📖 API Documentation & Health

Once the application is running, you can access the API documentation and health check endpoints.

Swagger UI: http://localhost:8080/swagger-ui.html

OpenAPI Spec: http://localhost:8080/v3/api-docs

Health Check: http://localhost:8080/actuator/health

## ☁️ Infrastructure
The infrastructure for the test environment is provisioned on AWS (Account ID: `602259772901`).
  
The current infrastructure is managed with `Terraform`, and the code is available in the `subscription-api-infrastructure` repository. There are ongoing plans to migrate this setup to a common infrastructure based on `Terragrunt` and `OpenTofu`.

## 🙌 Contributing
 We welcome contributions! Please follow the guidelines below:

**Coding Style**: Adhere to the standards outlined in .github/instructions/coding-style-java.instructions.md.

**Architecture**: Respect the Clean Architecture principles detailed in .github/instructions/clean-architecture.instructions.md.

To contribute, please fork the repository, create a new branch for your feature or bug fix, and submit a pull request.

## 🤖 Use of AI Agents
 Use of AI agents is encouraged.
