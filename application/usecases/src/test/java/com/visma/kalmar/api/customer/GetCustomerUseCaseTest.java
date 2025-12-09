package com.visma.kalmar.api.customer;

import com.visma.kalmar.api.context.InMemoryContextGatewayAdapter;
import com.visma.kalmar.api.entities.context.Context;
import com.visma.kalmar.api.entities.customer.Customer;
import com.visma.kalmar.api.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class GetCustomerUseCaseTest {

    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final UUID CONTEXT_TYPE_ID = UUID.randomUUID();
    private static final UUID PARENT_CONTEXT_ID = UUID.randomUUID();
    private static final UUID COUNTRY_ID = UUID.randomUUID();
    private static final String CUSTOMER_NAME = "Acme Corporation";
    private static final String ORG_NUMBER = "123456789";

    private InMemoryCustomerGatewayAdapter customerGateway;
    private InMemoryContextGatewayAdapter contextGateway;
    private GetCustomerUseCase useCase;

    @BeforeEach
    void setUp() {
        customerGateway = new InMemoryCustomerGatewayAdapter();
        contextGateway = new InMemoryContextGatewayAdapter();
        useCase = new GetCustomerUseCase(customerGateway, contextGateway);
    }

    @Test
    void getCustomer_withExistingId_returnsCustomerSuccessfully() {
        Context context = new Context(CUSTOMER_ID, CONTEXT_TYPE_ID, PARENT_CONTEXT_ID, COUNTRY_ID, CUSTOMER_NAME, ORG_NUMBER);
        contextGateway.save(context);
        customerGateway.save(context);

        final AtomicReference<Customer> resultCustomer = new AtomicReference<>();
        final AtomicReference<Context> resultContext = new AtomicReference<>();

        useCase.getCustomer(CUSTOMER_ID, (customer, ctx) -> {
            resultCustomer.set(customer);
            resultContext.set(ctx);
        });

        assertNotNull(resultCustomer.get());
        assertEquals(CUSTOMER_ID, resultCustomer.get().idContext());
        assertNotNull(resultContext.get());
        assertEquals(CUSTOMER_NAME, resultContext.get().name());
        assertEquals(ORG_NUMBER, resultContext.get().organizationNumber());
        assertEquals(COUNTRY_ID, resultContext.get().idCountry());
        assertEquals(PARENT_CONTEXT_ID, resultContext.get().idContextParent());
    }

    @Test
    void getCustomer_withNonExistentId_throwsResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> useCase.getCustomer(nonExistentId, (customer, context) -> {
                }));

        assertTrue(exception.getMessage().contains("Customer not found"));
        assertTrue(exception.getMessage().contains(nonExistentId.toString()));
    }

    @Test
    void getCustomer_withExistingCustomerButNoContext_throwsResourceNotFoundException() {
        Context context = new Context(CUSTOMER_ID, CONTEXT_TYPE_ID, PARENT_CONTEXT_ID, COUNTRY_ID, CUSTOMER_NAME, ORG_NUMBER);
        customerGateway.save(context);

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> useCase.getCustomer(CUSTOMER_ID, (customer, ctx) -> {
                }));

        assertTrue(exception.getMessage().contains("Context not found"));
        assertTrue(exception.getMessage().contains(CUSTOMER_ID.toString()));
    }

    @Test
    void getCustomer_callsOutputPortPresent() {
        Context context = new Context(CUSTOMER_ID, CONTEXT_TYPE_ID, PARENT_CONTEXT_ID, COUNTRY_ID, CUSTOMER_NAME, ORG_NUMBER);
        contextGateway.save(context);
        customerGateway.save(context);

        final AtomicReference<Boolean> presentCalled = new AtomicReference<>(false);

        useCase.getCustomer(CUSTOMER_ID, (customer, ctx) -> presentCalled.set(true));

        assertTrue(presentCalled.get());
    }

    @Test
    void getCustomer_withNullParentContext_returnsCustomerSuccessfully() {
        Context context = new Context(CUSTOMER_ID, CONTEXT_TYPE_ID, null, COUNTRY_ID, CUSTOMER_NAME, ORG_NUMBER);
        contextGateway.save(context);
        customerGateway.save(context);

        final AtomicReference<Context> resultContext = new AtomicReference<>();

        useCase.getCustomer(CUSTOMER_ID, (customer, ctx) -> resultContext.set(ctx));

        assertNotNull(resultContext.get());
        assertNull(resultContext.get().idContextParent());
    }
}
