---
applyTo: '**/*.java'
---
# Java Coding Guidelines

## 1. Primary Standard
The **[Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)** is the authoritative source for all formatting, naming, and style conventions. Adhere to it strictly. The rules below highlight key project-specific standards.

---

## 2. Naming Conventions
* **Clarity is paramount.** Names for classes, methods, and variables must be descriptive and unambiguous.
* **Variables:** Use `camelCase`. Avoid single-letter names except for loop counters or lambda parameters.
* **Type Inference:** Use the `var` keyword for local variable declarations only when the type is explicitly clear from the right-hand side of the statement.

---

## 3. Code & Package Structure
* **Root Package:** All package names **must** start with `com.visma.kalmar.api`.
* **Organization:** Group files by feature or domain concept (e.g., `com.visma.kalmar.api.customer`). Place all related types for a feature within the same package.
* **ORM Entities:** When defining a JPA entity, the class **must** be annotated with `@Entity`, and its name must exactly match the corresponding database table name.