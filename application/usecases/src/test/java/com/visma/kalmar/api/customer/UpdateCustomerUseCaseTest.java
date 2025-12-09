package com.visma.kalmar.api.customer;

import com.visma.kalmar.api.context.InMemoryContextGatewayAdapter;
import com.visma.kalmar.api.contexttype.InMemoryContextTypeGatewayAdapter;
import com.visma.kalmar.api.country.InMemoryCountryGatewayAdapter;
import com.visma.kalmar.api.entities.context.Context;
import com.visma.kalmar.api.entities.country.Country;
import com.visma.kalmar.api.entities.customer.Customer;
import com.visma.kalmar.api.exception.InvalidInputDataException;
import com.visma.kalmar.api.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class UpdateCustomerUseCaseTest {

    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final UUID CONTEXT_TYPE_ID = UUID.randomUUID();
    private static final UUID ORIGINAL_PARENT_ID = UUID.randomUUID();
    private static final UUID NEW_PARENT_ID = UUID.randomUUID();
    private static final UUID ORIGINAL_COUNTRY_ID = UUID.randomUUID();
    private static final UUID NEW_COUNTRY_ID = UUID.randomUUID();
    private static final String ORIGINAL_COUNTRY_CODE = "NO";
    private static final String NEW_COUNTRY_CODE = "SE";
    private static final String ORIGINAL_NAME = "Original Corp";
    private static final String UPDATED_NAME = "Updated Corporation";
    private static final String ORIGINAL_ORG_NUMBER = "111111111";
    private static final String UPDATED_ORG_NUMBER = "999999999";

    private InMemoryCustomerGatewayAdapter customerGateway;
    private InMemoryContextGatewayAdapter contextGateway;
    private InMemoryCountryGatewayAdapter countryGateway;
    private UpdateCustomerUseCase useCase;

    @BeforeEach
    void setUp() {
        customerGateway = new InMemoryCustomerGatewayAdapter();
        contextGateway = new InMemoryContextGatewayAdapter();
        countryGateway = new InMemoryCountryGatewayAdapter();
        useCase = new UpdateCustomerUseCase(customerGateway, contextGateway, countryGateway);
        
        Country norway = new Country(ORIGINAL_COUNTRY_ID, "Norway", ORIGINAL_COUNTRY_CODE);
        Country sweden = new Country(NEW_COUNTRY_ID, "Sweden", NEW_COUNTRY_CODE);
        countryGateway.save(norway);
        countryGateway.save(sweden);
    }

    @Test
    void updateCustomer_withValidCountryCode_updatesSuccessfully() {
        Context originalContext = new Context(CUSTOMER_ID, CONTEXT_TYPE_ID, ORIGINAL_PARENT_ID, ORIGINAL_COUNTRY_ID, ORIGINAL_NAME, ORIGINAL_ORG_NUMBER);
        contextGateway.save(originalContext);
        customerGateway.save(originalContext);

        Context newParentContext = new Context(NEW_PARENT_ID, CONTEXT_TYPE_ID, null, NEW_COUNTRY_ID, "Parent", "000000000");
        contextGateway.save(newParentContext);

        var inputData = new UpdateCustomerInputPort.UpdateCustomerInputData(
                CUSTOMER_ID, NEW_COUNTRY_CODE, NEW_PARENT_ID, UPDATED_ORG_NUMBER, UPDATED_NAME
        );

        final AtomicReference<Customer> resultCustomer = new AtomicReference<>();
        final AtomicReference<Context> resultContext = new AtomicReference<>();
        final AtomicReference<Boolean> resultCreated = new AtomicReference<>();

        useCase.updateCustomer(inputData, (customer, context, created) -> {
            resultCustomer.set(customer);
            resultContext.set(context);
            resultCreated.set(created);
        });

        assertNotNull(resultCustomer.get());
        assertEquals(CUSTOMER_ID, resultCustomer.get().idContext());
        assertNotNull(resultContext.get());
        assertEquals(UPDATED_NAME, resultContext.get().name());
        assertEquals(UPDATED_ORG_NUMBER, resultContext.get().organizationNumber());
        assertEquals(NEW_COUNTRY_ID, resultContext.get().idCountry());
        assertEquals(NEW_PARENT_ID, resultContext.get().idContextParent());
        assertFalse(resultCreated.get());

        Context updatedContext = customerGateway.getContextById(CUSTOMER_ID);
        assertEquals(UPDATED_NAME, updatedContext.name());
        assertEquals(UPDATED_ORG_NUMBER, updatedContext.organizationNumber());
    }

    @Test
    void updateCustomer_withNonExistentId_throwsResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();
        var inputData = new UpdateCustomerInputPort.UpdateCustomerInputData(
                nonExistentId, NEW_COUNTRY_CODE, null, UPDATED_ORG_NUMBER, UPDATED_NAME
        );

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> useCase.updateCustomer(inputData, (customer, context, created) -> {
                }));

        assertTrue(exception.getMessage().contains("Customer not found"));
        assertTrue(exception.getMessage().contains(nonExistentId.toString()));
    }

    @Test
    void updateCustomer_withNonExistentParentContext_throwsResourceNotFoundException() {
        Context originalContext = new Context(CUSTOMER_ID, CONTEXT_TYPE_ID, null, ORIGINAL_COUNTRY_ID, ORIGINAL_NAME, ORIGINAL_ORG_NUMBER);
        contextGateway.save(originalContext);
        customerGateway.save(originalContext);

        UUID nonExistentParentId = UUID.randomUUID();
        var inputData = new UpdateCustomerInputPort.UpdateCustomerInputData(
                CUSTOMER_ID, NEW_COUNTRY_CODE, nonExistentParentId, UPDATED_ORG_NUMBER, UPDATED_NAME
        );

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> useCase.updateCustomer(inputData, (customer, context, created) -> {
                }));

        assertTrue(exception.getMessage().contains("Parent context not found"));
        assertTrue(exception.getMessage().contains(nonExistentParentId.toString()));
    }

    @Test
    void updateCustomer_withNullIdCustomer_throwsInvalidInputDataException() {
        var exception = assertThrows(InvalidInputDataException.class,
                () -> new UpdateCustomerInputPort.UpdateCustomerInputData(
                        null, NEW_COUNTRY_CODE, null, UPDATED_ORG_NUMBER, UPDATED_NAME
                ));

        assertTrue(exception.getMessage().contains("idCustomer is mandatory"));
    }

    @Test
    void updateCustomer_withNullOrganizationNumber_throwsInvalidInputDataException() {
        var exception = assertThrows(InvalidInputDataException.class,
                () -> new UpdateCustomerInputPort.UpdateCustomerInputData(
                        CUSTOMER_ID, NEW_COUNTRY_CODE, null, null, UPDATED_NAME
                ));

        assertTrue(exception.getMessage().contains("organizationNumber is mandatory"));
    }

    @Test
    void updateCustomer_withBlankOrganizationNumber_throwsInvalidInputDataException() {
        var exception = assertThrows(InvalidInputDataException.class,
                () -> new UpdateCustomerInputPort.UpdateCustomerInputData(
                        CUSTOMER_ID, NEW_COUNTRY_CODE, null, "  ", UPDATED_NAME
                ));

        assertTrue(exception.getMessage().contains("organizationNumber is mandatory"));
    }

    @Test
    void updateCustomer_withNullName_throwsInvalidInputDataException() {
        var exception = assertThrows(InvalidInputDataException.class,
                () -> new UpdateCustomerInputPort.UpdateCustomerInputData(
                        CUSTOMER_ID, NEW_COUNTRY_CODE, null, UPDATED_ORG_NUMBER, null
                ));

        assertTrue(exception.getMessage().contains("name is mandatory"));
    }

    @Test
    void updateCustomer_withBlankName_throwsInvalidInputDataException() {
        var exception = assertThrows(InvalidInputDataException.class,
                () -> new UpdateCustomerInputPort.UpdateCustomerInputData(
                        CUSTOMER_ID, NEW_COUNTRY_CODE, null, UPDATED_ORG_NUMBER, "  "
                ));

        assertTrue(exception.getMessage().contains("name is mandatory"));
    }

    @Test
    void updateCustomer_withNullCountryCodeAndNullParent_keepsExistingCountry() {
        Context originalContext = new Context(CUSTOMER_ID, CONTEXT_TYPE_ID, ORIGINAL_PARENT_ID, ORIGINAL_COUNTRY_ID, ORIGINAL_NAME, ORIGINAL_ORG_NUMBER);
        contextGateway.save(originalContext);
        customerGateway.save(originalContext);

        var inputData = new UpdateCustomerInputPort.UpdateCustomerInputData(
                CUSTOMER_ID, null, null, UPDATED_ORG_NUMBER, UPDATED_NAME
        );

        final AtomicReference<Context> resultContext = new AtomicReference<>();

        useCase.updateCustomer(inputData, (customer, context, created) -> {
            resultContext.set(context);
        });

        assertNotNull(resultContext.get());
        assertNull(resultContext.get().idContextParent());
        assertEquals(ORIGINAL_COUNTRY_ID, resultContext.get().idCountry());
        assertEquals(UPDATED_NAME, resultContext.get().name());
    }

    @Test
    void updateCustomer_preservesContextType() {
        Context originalContext = new Context(CUSTOMER_ID, CONTEXT_TYPE_ID, null, ORIGINAL_COUNTRY_ID, ORIGINAL_NAME, ORIGINAL_ORG_NUMBER);
        contextGateway.save(originalContext);
        customerGateway.save(originalContext);

        var inputData = new UpdateCustomerInputPort.UpdateCustomerInputData(
                CUSTOMER_ID, NEW_COUNTRY_CODE, null, UPDATED_ORG_NUMBER, UPDATED_NAME
        );

        final AtomicReference<Context> resultContext = new AtomicReference<>();

        useCase.updateCustomer(inputData, (customer, context, created) -> {
            resultContext.set(context);
        });

        assertEquals(CONTEXT_TYPE_ID, resultContext.get().idContextType());
    }
}
