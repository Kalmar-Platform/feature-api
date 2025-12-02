package com.visma.kalmar.api.customer;

import com.visma.kalmar.api.entities.context.Context;
import com.visma.kalmar.api.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class DeleteCustomerUseCaseTest {

    private InMemoryCustomerGatewayAdapter customerGateway;
    private DeleteCustomerUseCase useCase;

    @BeforeEach
    void setUp() {
        customerGateway = new InMemoryCustomerGatewayAdapter();
        useCase = new DeleteCustomerUseCase(customerGateway);
    }

    @Test
    void deleteCustomer_withExistingId_deletesCustomerSuccessfully() {
        UUID customerId = UUID.randomUUID();
        Context context = new Context(customerId, UUID.randomUUID(), null, UUID.randomUUID(), "Test Customer", "123456789");
        customerGateway.save(context);

        assertTrue(customerGateway.exists(customerId));

        final AtomicBoolean deletedCalled = new AtomicBoolean(false);

        useCase.deleteCustomer(customerId, () -> deletedCalled.set(true));

        assertTrue(deletedCalled.get());
        assertFalse(customerGateway.exists(customerId));
    }

    @Test
    void deleteCustomer_withNonExistentId_throwsResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> useCase.deleteCustomer(nonExistentId, () -> {
                }));

        assertTrue(exception.getMessage().contains("Customer not found"));
    }

    @Test
    void deleteCustomer_callsOutputPortPresentDeleted() {
        UUID customerId = UUID.randomUUID();
        Context context = new Context(customerId, UUID.randomUUID(), null, UUID.randomUUID(), "Test Customer", "123456789");
        customerGateway.save(context);

        final AtomicBoolean presentDeletedCalled = new AtomicBoolean(false);

        useCase.deleteCustomer(customerId, () -> presentDeletedCalled.set(true));

        assertTrue(presentDeletedCalled.get());
    }

    @Test
    void deleteCustomer_multipleCustomers_deletesOnlySpecifiedOne() {
        UUID customerId1 = UUID.randomUUID();
        UUID customerId2 = UUID.randomUUID();
        
        Context context1 = new Context(customerId1, UUID.randomUUID(), null, UUID.randomUUID(), "Customer 1", "111111111");
        Context context2 = new Context(customerId2, UUID.randomUUID(), null, UUID.randomUUID(), "Customer 2", "222222222");
        
        customerGateway.save(context1);
        customerGateway.save(context2);

        assertTrue(customerGateway.exists(customerId1));
        assertTrue(customerGateway.exists(customerId2));

        useCase.deleteCustomer(customerId1, () -> {});

        assertFalse(customerGateway.exists(customerId1));
        assertTrue(customerGateway.exists(customerId2));
    }
}
