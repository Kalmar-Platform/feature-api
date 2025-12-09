package com.visma.kalmar.api.customer;

import com.visma.kalmar.api.constants.ContextTypeName;
import com.visma.kalmar.api.context.InMemoryContextGatewayAdapter;
import com.visma.kalmar.api.contexttype.InMemoryContextTypeGatewayAdapter;
import com.visma.kalmar.api.country.InMemoryCountryGatewayAdapter;
import com.visma.kalmar.api.entities.context.Context;
import com.visma.kalmar.api.entities.contexttype.ContextType;
import com.visma.kalmar.api.entities.country.Country;
import com.visma.kalmar.api.entities.customer.Customer;
import com.visma.kalmar.api.exception.InvalidInputDataException;
import com.visma.kalmar.api.exception.ResourceAlreadyExistsException;
import com.visma.kalmar.api.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class CreateCustomerUseCaseTest {

    private static final String CUSTOMER_NAME = "Acme Corporation";
    private static final String ORG_NUMBER = "123456789";
    private static final String COUNTRY_CODE = "NO";
    private static final UUID COUNTRY_ID = UUID.randomUUID();
    private static final UUID CONTEXT_TYPE_ID = UUID.randomUUID();

    private InMemoryCustomerGatewayAdapter customerGateway;
    private InMemoryContextGatewayAdapter contextGateway;
    private InMemoryContextTypeGatewayAdapter contextTypeGateway;
    private InMemoryCountryGatewayAdapter countryGateway;
    private CreateCustomerUseCase useCase;

    @BeforeEach
    void setUp() {
        customerGateway = new InMemoryCustomerGatewayAdapter();
        contextGateway = new InMemoryContextGatewayAdapter();
        contextTypeGateway = new InMemoryContextTypeGatewayAdapter();
        countryGateway = new InMemoryCountryGatewayAdapter();
        useCase = new CreateCustomerUseCase(customerGateway, contextGateway, contextTypeGateway, countryGateway);

        ContextType customerContextType = new ContextType(CONTEXT_TYPE_ID, ContextTypeName.CUSTOMER.getValue());
        contextTypeGateway.save(customerContextType);
        
        Country norway = new Country(COUNTRY_ID, "Norway", COUNTRY_CODE);
        countryGateway.save(norway);
    }

    @Test
    void createCustomer_withValidCountryCode_createsCustomerSuccessfully() {
        UUID customerId = UUID.randomUUID();
        UUID parentContextId = UUID.randomUUID();
        Context parentContext = new Context(parentContextId, CONTEXT_TYPE_ID, null, COUNTRY_ID, "Parent Corp", "987654321");
        contextGateway.save(parentContext);
        var inputData = new CreateCustomerInputPort.CreateCustomerInputData(
                customerId, COUNTRY_CODE, parentContextId, ORG_NUMBER, CUSTOMER_NAME
        );

        final AtomicReference<Customer> resultCustomer = new AtomicReference<>();
        final AtomicReference<Context> resultContext = new AtomicReference<>();
        final AtomicReference<Boolean> resultCreated = new AtomicReference<>();

        useCase.createCustomer(inputData, (customer, context, created) -> {
            resultCustomer.set(customer);
            resultContext.set(context);
            resultCreated.set(created);
        });

        assertNotNull(resultCustomer.get());
        assertEquals(customerId, resultCustomer.get().idContext());
        assertNotNull(resultContext.get());
        assertEquals(CUSTOMER_NAME, resultContext.get().name());
        assertEquals(ORG_NUMBER, resultContext.get().organizationNumber());
        assertEquals(COUNTRY_ID, resultContext.get().idCountry());
        assertEquals(CONTEXT_TYPE_ID, resultContext.get().idContextType());
        assertEquals(parentContextId, resultContext.get().idContextParent());
        assertTrue(resultCreated.get());

        assertTrue(customerGateway.exists(customerId));
    }

    @Test
    void createCustomer_withNullCountryCode_usesParentCountry() {
        UUID parentContextId = UUID.randomUUID();
        Context parentContext = new Context(parentContextId, CONTEXT_TYPE_ID, null, COUNTRY_ID, "Parent Corp", "987654321");
        contextGateway.save(parentContext);

        UUID customerId = UUID.randomUUID();
        var inputData = new CreateCustomerInputPort.CreateCustomerInputData(
                customerId, null, parentContextId, ORG_NUMBER, CUSTOMER_NAME
        );

        final AtomicReference<Context> resultContext = new AtomicReference<>();

        useCase.createCustomer(inputData, (customer, context, created) -> {
            resultContext.set(context);
        });

        assertNotNull(resultContext.get());
        assertEquals(parentContextId, resultContext.get().idContextParent());
        assertEquals(COUNTRY_ID, resultContext.get().idCountry());
    }

    @Test
    void createCustomer_withNonExistentParentContext_throwsResourceNotFoundException() {
        UUID nonExistentParentId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        var inputData = new CreateCustomerInputPort.CreateCustomerInputData(
                customerId, COUNTRY_CODE, nonExistentParentId, ORG_NUMBER, CUSTOMER_NAME
        );

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> useCase.createCustomer(inputData, (customer, context, created) -> {
                }));

        assertTrue(exception.getMessage().contains("Parent context not found"));
        assertFalse(customerGateway.exists(customerId));
    }

    @Test
    void createCustomer_withNullIdCustomer_generatesNewId() {
        UUID parentContextId = UUID.randomUUID();
        Context parentContext = new Context(parentContextId, CONTEXT_TYPE_ID, null, COUNTRY_ID, "Parent Corp", "987654321");
        contextGateway.save(parentContext);
        var inputData = new CreateCustomerInputPort.CreateCustomerInputData(
                null, COUNTRY_CODE, parentContextId, ORG_NUMBER, CUSTOMER_NAME
        );

        final AtomicReference<Customer> resultCustomer = new AtomicReference<>();

        useCase.createCustomer(inputData, (customer, context, created) -> {
            resultCustomer.set(customer);
        });

        assertNotNull(resultCustomer.get());
        assertNotNull(resultCustomer.get().idContext());
        assertTrue(customerGateway.exists(resultCustomer.get().idContext()));
    }

    @Test
    void createCustomer_withInvalidCountryCode_throwsResourceNotFoundException() {
        UUID parentContextId = UUID.randomUUID();
        Context parentContext = new Context(parentContextId, CONTEXT_TYPE_ID, null, COUNTRY_ID, "Parent Corp", "987654321");
        contextGateway.save(parentContext);
        
        var inputData = new CreateCustomerInputPort.CreateCustomerInputData(
                UUID.randomUUID(), "XX", parentContextId, ORG_NUMBER, CUSTOMER_NAME
        );

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> useCase.createCustomer(inputData, (customer, context, created) -> {}));

        assertTrue(exception.getMessage().contains("Country not found with code"));
    }

    @Test
    void createCustomer_withNullOrganizationNumber_throwsInvalidInputDataException() {
        var exception = assertThrows(InvalidInputDataException.class,
                () -> new CreateCustomerInputPort.CreateCustomerInputData(
                        UUID.randomUUID(), COUNTRY_CODE, UUID.randomUUID(), null, CUSTOMER_NAME
                ));

        assertTrue(exception.getMessage().contains("organizationNumber is mandatory"));
    }

    @Test
    void createCustomer_withBlankOrganizationNumber_throwsInvalidInputDataException() {
        var exception = assertThrows(InvalidInputDataException.class,
                () -> new CreateCustomerInputPort.CreateCustomerInputData(
                        UUID.randomUUID(), COUNTRY_CODE, UUID.randomUUID(), "  ", CUSTOMER_NAME
                ));

        assertTrue(exception.getMessage().contains("organizationNumber is mandatory"));
    }

    @Test
    void createCustomer_withNullName_throwsInvalidInputDataException() {
        var exception = assertThrows(InvalidInputDataException.class,
                () -> new CreateCustomerInputPort.CreateCustomerInputData(
                        UUID.randomUUID(), COUNTRY_CODE, UUID.randomUUID(), ORG_NUMBER, null
                ));

        assertTrue(exception.getMessage().contains("name is mandatory"));
    }

    @Test
    void createCustomer_withBlankName_throwsInvalidInputDataException() {
        var exception = assertThrows(InvalidInputDataException.class,
                () -> new CreateCustomerInputPort.CreateCustomerInputData(
                        UUID.randomUUID(), COUNTRY_CODE, UUID.randomUUID(), ORG_NUMBER, "  "
                ));

        assertTrue(exception.getMessage().contains("name is mandatory"));
    }

    @Test
    void createCustomer_withExistingCustomerId_throwsResourceAlreadyExistsException() {
        UUID customerId = UUID.randomUUID();
        UUID parentContextId = UUID.randomUUID();
        Context parentContext = new Context(parentContextId, CONTEXT_TYPE_ID, null, COUNTRY_ID, "Parent Corp", "987654321");
        contextGateway.save(parentContext);
        
        Context existingContext = new Context(customerId, CONTEXT_TYPE_ID, parentContextId, COUNTRY_ID, "Existing Customer", "111111111");
        customerGateway.save(existingContext);
        
        var inputData = new CreateCustomerInputPort.CreateCustomerInputData(
                customerId, COUNTRY_CODE, parentContextId, ORG_NUMBER, CUSTOMER_NAME
        );

        var exception = assertThrows(ResourceAlreadyExistsException.class,
                () -> useCase.createCustomer(inputData, (customer, context, created) -> {
                }));

        assertTrue(exception.getMessage().contains("Customer already exists with id: " + customerId));
    }

    @Test
    void createCustomer_withNonExistentContextType_throwsResourceNotFoundException() {
        contextTypeGateway.clear();

        UUID customerId = UUID.randomUUID();
        UUID parentContextId = UUID.randomUUID();
        Context parentContext = new Context(parentContextId, CONTEXT_TYPE_ID, null, COUNTRY_ID, "Parent Corp", "987654321");
        contextGateway.save(parentContext);
        var inputData = new CreateCustomerInputPort.CreateCustomerInputData(
                customerId, COUNTRY_CODE, parentContextId, ORG_NUMBER, CUSTOMER_NAME
        );

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> useCase.createCustomer(inputData, (customer, context, created) -> {
                }));

        assertTrue(exception.getMessage().contains("ContextType not found"));
        assertFalse(customerGateway.exists(customerId));
    }

}
