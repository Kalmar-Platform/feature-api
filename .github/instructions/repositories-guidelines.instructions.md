# Repository & ORM Entity Guidelines

## 1. Core Principles
All database interaction is managed through **Spring Data `JpaRepository`** interfaces. These repositories abstract the data persistence logic and provide a standardized way to perform CRUD operations.

---

## 2. Location & Package Structure
The location of repository and entity files is critical for the multi-database configuration to work correctly.

* **Base Module:** All repositories and their entities **must** be located in the `interface-adapters/gateways/` module.
* **Database-Specific Packages:** The project connects to two separate databases. Repositories and their entities **must** be placed in the package matching the database they connect to:
    * **Feature DB:** `com.visma.feature.kalmar.api.**`

---

## 3. Defining Repositories
* **Interface:** Repository interfaces **must** extend `JpaRepository<Entity, IdType>`.
* **Query Methods:**
    * For simple queries, use Spring Data's **query inference** by naming methods according to the convention (e.g., `findById`, `existsByEmail`, `findAllByIdContextParent`).
    * For complex queries, you **must** use the `@Query` annotation with JPQL.

#### Repository Example
```java
// Located in: com.visma.feature.kalmar.api.customer
public interface CustomerRepository extends JpaRepository<Customer, String> {
    
    // Inferred Query
    boolean existsById(String id);

    // Inferred Query
    List<Customer> findAllByIdContextParent(String contextId);

    // Explicit JPQL Query
    @Query("SELECT c FROM Customer c WHERE c.name LIKE %:name%")
    List<Customer> findByNameContains(@Param("name") String name);
}
```
---

## 4. Defining ORM Entities
* **Annotations:** Entities **must** be annotated with `@Entity`, `@Table(name = "TableName")`, and `@Data`.
* **Location:** Entities **must** be placed in the same database-specific package as their corresponding repository.

### 4.1. Mandatory UUID Handling
This is a critical, project-specific rule that **must** be followed for all entities.

* **Problem:** MySQL stores `UUID` as `CHAR(36)`, but Hibernate can misinterpret this.
* **Solution:** All `UUID` primary key fields **must** be annotated with `@JdbcTypeCode(SqlTypes.CHAR)` to ensure correct persistence.

#### Entity Example with UUID Rule
```java
@Entity
@Table(name = "Country")
@Data
public class FeatureCountry {
    
    @Id
    @Column(name = "IdCountry", columnDefinition = "CHAR(36)")
    @JdbcTypeCode(SqlTypes.CHAR) // <-- THIS IS MANDATORY FOR UUIDs
    private UUID idCountry;

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "Code", nullable = false, length = 2, unique = true)
    private String code;

    @PrePersist
    public void prePersist() {
        if (idCountry == null) {
            idCountry = UUID.randomUUID();
        }
    }
}
```

## 5. Multi-Database Configuration Pattern
The application uses dedicated `@Configuration` classes to wire repositories to the correct database. When creating new repositories, ensure they are in the correct package to be picked up by the configuration.

* **Configuration Classes:** Separate `@Configuration` classes exist for each database (e.g., `UserAccessDatabaseConfig.java`).
* **`@EnableJpaRepositories`:** This annotation is used with the `basePackages` attribute to scan the correct package for each database.
* **`@Primary`:** The `UserAccess` database is the default and its configuration beans are marked as `@Primary`.

You will typically not need to modify this configuration, but you **must** place your repositories and entities in the correct packages for this system to function.