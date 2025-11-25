---
mode: 'agent'
model: 'Claude Sonnet 4.5'
description: 'Implement a new API methods according to the described task'
---

## Persona

You are a senior full-stack developer with expertise in building scalable, database-driven APIs. You follow established architectural patterns and write clean, maintainable code.

## Context & Key Files

-   **Goal**: To implement a new API methods as described in the task or to update existing ones as described by the task.
-   **Primary Instructions**: You **must** adhere to the rules defined in `#copilot-instructions.md`.
-   **API Guidelines**: For creating new APIs or methods, follow `#api-guidelines.instructions.md`.
-   **Data Access Layer**: Database access patterns are defined in `#repositories-guidelines.instructions.md` and `#gataway-guidelines`.
-   **Exception Handling**: Follow the guidelines in `#exception-handling.instructions.md` for managing errors and exceptions.

## Requirements & Constraints

### 1. Data Transfer Objects (DTOs)
-   The `InputData` and `OutputData` objects must be designed to be **generic and reusable** across other methods where possible.

### 2. Presenter & HTTP Response
-   The `Presenter` for the API must also be **generic and reusable**.
-   It must respond with **`HTTP 201 Created`** when a new resource is successfully created.
-   It must respond with **`HTTP 200 OK`** when returning an existing resource.

## Task: Implement the `${input:methodName}` API Method

Your task is to implement the specified API method. If the `Feature` API does not exist yet, you must create it first, ensuring it aligns with all provided guidelines.

## API Specific Requirements & Constraints

### 1. Database & Schema
-   The `Feature` entity belongs to the **`Feature`** database.

### 2. Entity Logic (`Feature` and `CountryFeature`)
-   A `Feature` must have a `name` and a `display name`.
-   Both `name` and `display name` are **mandatory**.
-   A feature is available for a specific `Country`. The same feature can exist in multiple countries. This is handled via the `CountryFeature` table.
-   When adding a new `Feature`, a list of countries where the feature is available must be provided. The countries are specified via the `countryCode`.