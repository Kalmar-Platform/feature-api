# Gateway Guidelines

## 1. Core Principles
Gateways are interfaces that abstract all communication with external data sources (e.g., databases, third-party APIs). Their purpose is to decouple business logic from data access technology.

* **Abstraction:** Gateways **must** hide the implementation details of data retrieval and persistence.
* **Separation:** Each gateway should correspond to a single domain entity or data source (e.g., `CustomerGateway` for the `Customer` table).
* **Reusability:** Before creating a new gateway, you **must** check if one already exists for the entity you are working with, even if it's in another package.

---

## 2. Defining Gateway Interfaces
Gateway interfaces define the contract for data operations required by the use cases.

* **Location:** Gateway interfaces **must** be located in the `use-cases` module, within the same package as the primary use case that requires them.
* **Content:** Interfaces should define methods for CRUD operations and any specific queries needed by the business logic (e.g., `findCustomerById()`).

#### Interface Example
```java
public interface CustomerGateway {
    Customer findById(String customerId);
    List<Customer> findAll();
    void save(Customer customer);
    void delete(String customerId);
    boolean existsById(String customerId);
}
```

### 2.1. Multi-Database Naming Convention
When an entity exists in multiple databases (e.g., `Feature` ), you **must** prefix method names accordingly to avoid ambiguity.

* Methods for the `Feature` database: `findFeatureCustomerById(...)`
* Operations that apply to both (like `save`) can remain unprefixed.

---

## 3. Implementing Gateways
Gateway implementations contain the concrete logic for interacting with a data source.

* **Location:** Implementations **must** be located in the `interface-adapters` module, under the `gateways` submodule.
* **Naming Convention:** The class name **must** be the interface name with the suffix `GatewayAdapter` (e.g., `CustomerGatewayAdapter`).
* **Dependencies:** Gateway adapters **must** only inject `Spring Data` repositories.
* **Default Database:** When fetching data from tables common to both databases (e.g., `Customer`, `Country`), the implementation **must** default to the `Feature` database unless a method prefix specifies otherwise.

### 3.1. Implementation Rules & Patterns
* **Exception Handling:** All `find` methods that return a single entity **must** handle empty `Optional` results by throwing a `ResourceNotFoundException`. This is a non-negotiable rule.
* **Existence Checks:** To check if an entity exists, you **must** use the `existsById(...)` or `existsBy{FieldName}(...)` methods from the repository. Do not fetch a full entity just to check for its existence.
* **Data Mapping:** The adapter is responsible for mapping data between the database entity format and the domain model format.

#### Implementation Example
```java
public class CustomerGatewayAdapter implements CustomerGateway {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerGatewayAdapter(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer findById(String customerId) {
        return customerRepository.findById(customerId)
                .map(this::toDomain) // Assumes a mapping method
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "Customer not found with id: %s", customerId));
    }

    @Override
    public void save(Customer customer) {
        CustomerEntity dbEntity = toEntity(customer); // Assumes a mapping method
        customerRepository.save(dbEntity);
    }
    
    @Override
    public boolean existsById(String customerId) {
        return customerRepository.existsById(customerId);
    }

    // Other method implementations...
}
```
---
## 4. Testing with Mock Gateways
For unit testing use cases, you **must** provide an in-memory mock implementation of the gateway.

* **Location:** Mock implementations **must** be located in the `use-cases` module's test source folder.
* **Naming Convention:** The mock class name **must** be prefixed with `InMemory` and suffixed with `Adapter` (e.g., `InMemoryCustomerGatewayAdapter`).
* **Implementation:** The mock **must** implement the gateway interface and use an in-memory collection (like a `List` or `Map`) to simulate database behavior.

#### Mock Gateway Example
```java
public class InMemoryCustomerGatewayAdapter implements CustomerGateway {

    private final Map<String, Customer> database = new ConcurrentHashMap<>();

    @Override
    public Customer findById(String customerId) {
        Customer customer = database.get(customerId);
        if (customer == null) {
            throw new ResourceNotFoundException("Customer", "Customer not found with id: %s", customerId);
        }
        return customer;
    }

    @Override
    public void save(Customer customer) {
        database.put(customer.getId(), customer);
    }

    // Other method implementations...
    
    // Helper method for setting up test data
    public void clear() {
        database.clear();
    }
}
```
