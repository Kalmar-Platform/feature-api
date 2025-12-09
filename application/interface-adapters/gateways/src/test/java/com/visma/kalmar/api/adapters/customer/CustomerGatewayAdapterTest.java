package com.visma.kalmar.api.adapters.customer;

import com.visma.kalmar.api.entities.context.Context;
import com.visma.kalmar.api.entities.customer.Customer;
import com.visma.kalmar.api.exception.ResourceNotFoundException;
import com.visma.feature.kalmar.api.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerGatewayAdapterTest {

    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final UUID CONTEXT_TYPE_ID = UUID.randomUUID();
    private static final UUID PARENT_CONTEXT_ID = UUID.randomUUID();
    private static final UUID COUNTRY_ID = UUID.randomUUID();
    private static final String CUSTOMER_NAME = "Acme Corporation";
    private static final String ORG_NUMBER = "123456789";

    @Mock
    private CustomerRepository customerRepository;

    private CustomerGatewayAdapter customerGatewayAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        customerGatewayAdapter = new CustomerGatewayAdapter(customerRepository);
    }

    @Test
    void save_withValidContext_success() {
        Context domainContext = createDomainContext();
        com.visma.feature.kalmar.api.customer.Customer jpaCustomer = createJpaCustomer();

        when(customerRepository.save(any(com.visma.feature.kalmar.api.customer.Customer.class)))
                .thenReturn(jpaCustomer);

        Customer result = customerGatewayAdapter.save(domainContext);

        assertNotNull(result);
        assertEquals(CUSTOMER_ID, result.idContext());

        ArgumentCaptor<com.visma.feature.kalmar.api.customer.Customer> captor =
                ArgumentCaptor.forClass(com.visma.feature.kalmar.api.customer.Customer.class);
        verify(customerRepository, times(1)).save(captor.capture());

        com.visma.feature.kalmar.api.customer.Customer savedEntity = captor.getValue();
        assertEquals(CUSTOMER_ID, savedEntity.getIdContext());
        assertEquals(CUSTOMER_NAME, savedEntity.getName());
        assertEquals(ORG_NUMBER, savedEntity.getOrganizationNumber());
        assertEquals(COUNTRY_ID, savedEntity.getIdCountry());
        assertEquals(CONTEXT_TYPE_ID, savedEntity.getIdContextType());
        assertEquals(PARENT_CONTEXT_ID, savedEntity.getIdContextParent());
    }

    @Test
    void save_withNullParentContext_success() {
        Context domainContext = createDomainContextWithoutParent();
        com.visma.feature.kalmar.api.customer.Customer jpaCustomer = createJpaCustomerWithoutParent();

        when(customerRepository.save(any(com.visma.feature.kalmar.api.customer.Customer.class)))
                .thenReturn(jpaCustomer);

        Customer result = customerGatewayAdapter.save(domainContext);

        assertNotNull(result);
        assertEquals(CUSTOMER_ID, result.idContext());

        ArgumentCaptor<com.visma.feature.kalmar.api.customer.Customer> captor =
                ArgumentCaptor.forClass(com.visma.feature.kalmar.api.customer.Customer.class);
        verify(customerRepository, times(1)).save(captor.capture());

        com.visma.feature.kalmar.api.customer.Customer savedEntity = captor.getValue();
        assertNull(savedEntity.getIdContextParent());
    }

    @Test
    void findById_customerExists_success() {
        com.visma.feature.kalmar.api.customer.Customer jpaCustomer = createJpaCustomer();
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(jpaCustomer));

        Customer result = customerGatewayAdapter.findById(CUSTOMER_ID);

        assertNotNull(result);
        assertEquals(CUSTOMER_ID, result.idContext());
        verify(customerRepository, times(1)).findById(CUSTOMER_ID);
    }

    @Test
    void findById_customerNotFound_returnsNull() {
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());

        Customer result = customerGatewayAdapter.findById(CUSTOMER_ID);

        assertNull(result);
        verify(customerRepository, times(1)).findById(CUSTOMER_ID);
    }

    @Test
    void deleteById_customerExists_success() {
        when(customerRepository.existsById(CUSTOMER_ID)).thenReturn(true);

        customerGatewayAdapter.deleteById(CUSTOMER_ID);

        verify(customerRepository, times(1)).existsById(CUSTOMER_ID);
        verify(customerRepository, times(1)).deleteById(CUSTOMER_ID);
    }

    @Test
    void deleteById_customerNotFound_throwsResourceNotFoundException() {
        when(customerRepository.existsById(CUSTOMER_ID)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> customerGatewayAdapter.deleteById(CUSTOMER_ID));

        assertTrue(exception.getMessage().contains("Customer not found"));
        assertTrue(exception.getMessage().contains(CUSTOMER_ID.toString()));
        verify(customerRepository, times(1)).existsById(CUSTOMER_ID);
        verify(customerRepository, never()).deleteById(any());
    }

    @Test
    void toJpaEntity_mapsAllFieldsCorrectly() {
        Context domainContext = createDomainContext();

        when(customerRepository.save(any(com.visma.feature.kalmar.api.customer.Customer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        customerGatewayAdapter.save(domainContext);

        ArgumentCaptor<com.visma.feature.kalmar.api.customer.Customer> captor =
                ArgumentCaptor.forClass(com.visma.feature.kalmar.api.customer.Customer.class);
        verify(customerRepository).save(captor.capture());

        com.visma.feature.kalmar.api.customer.Customer jpaEntity = captor.getValue();
        assertEquals(CUSTOMER_ID, jpaEntity.getIdContext());
        assertEquals(CUSTOMER_NAME, jpaEntity.getName());
        assertEquals(ORG_NUMBER, jpaEntity.getOrganizationNumber());
        assertEquals(COUNTRY_ID, jpaEntity.getIdCountry());
        assertEquals(CONTEXT_TYPE_ID, jpaEntity.getIdContextType());
        assertEquals(PARENT_CONTEXT_ID, jpaEntity.getIdContextParent());
    }

    @Test
    void toDomainEntity_returnsCustomerWithCorrectId() {
        com.visma.feature.kalmar.api.customer.Customer jpaCustomer = createJpaCustomer();
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(jpaCustomer));

        Customer result = customerGatewayAdapter.findById(CUSTOMER_ID);

        assertNotNull(result);
        assertEquals(CUSTOMER_ID, result.idContext());
    }

    @Test
    void save_multipleCustomers_success() {
        Context domainContext1 = createDomainContext();
        Context domainContext2 = createDomainContextWithDifferentId();

        com.visma.feature.kalmar.api.customer.Customer jpaCustomer1 = createJpaCustomer();
        com.visma.feature.kalmar.api.customer.Customer jpaCustomer2 = createJpaCustomerWithDifferentId();

        when(customerRepository.save(any(com.visma.feature.kalmar.api.customer.Customer.class)))
                .thenReturn(jpaCustomer1, jpaCustomer2);

        Customer result1 = customerGatewayAdapter.save(domainContext1);
        Customer result2 = customerGatewayAdapter.save(domainContext2);

        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(CUSTOMER_ID, result1.idContext());
        assertNotEquals(result1.idContext(), result2.idContext());
        verify(customerRepository, times(2)).save(any(com.visma.feature.kalmar.api.customer.Customer.class));
    }

    private Context createDomainContext() {
        return new Context(
                CUSTOMER_ID,
                CONTEXT_TYPE_ID,
                PARENT_CONTEXT_ID,
                COUNTRY_ID,
                CUSTOMER_NAME,
                ORG_NUMBER
        );
    }

    private Context createDomainContextWithoutParent() {
        return new Context(
                CUSTOMER_ID,
                CONTEXT_TYPE_ID,
                null,
                COUNTRY_ID,
                CUSTOMER_NAME,
                ORG_NUMBER
        );
    }

    private Context createDomainContextWithDifferentId() {
        return new Context(
                UUID.randomUUID(),
                CONTEXT_TYPE_ID,
                PARENT_CONTEXT_ID,
                COUNTRY_ID,
                "Different Corp",
                "987654321"
        );
    }

    private com.visma.feature.kalmar.api.customer.Customer createJpaCustomer() {
        com.visma.feature.kalmar.api.customer.Customer customer = new com.visma.feature.kalmar.api.customer.Customer();
        customer.setIdContext(CUSTOMER_ID);
        customer.setIdContextType(CONTEXT_TYPE_ID);
        customer.setIdContextParent(PARENT_CONTEXT_ID);
        customer.setIdCountry(COUNTRY_ID);
        customer.setName(CUSTOMER_NAME);
        customer.setOrganizationNumber(ORG_NUMBER);
        return customer;
    }

    private com.visma.feature.kalmar.api.customer.Customer createJpaCustomerWithoutParent() {
        com.visma.feature.kalmar.api.customer.Customer customer = new com.visma.feature.kalmar.api.customer.Customer();
        customer.setIdContext(CUSTOMER_ID);
        customer.setIdContextType(CONTEXT_TYPE_ID);
        customer.setIdContextParent(null);
        customer.setIdCountry(COUNTRY_ID);
        customer.setName(CUSTOMER_NAME);
        customer.setOrganizationNumber(ORG_NUMBER);
        return customer;
    }

    private com.visma.feature.kalmar.api.customer.Customer createJpaCustomerWithDifferentId() {
        com.visma.feature.kalmar.api.customer.Customer customer = new com.visma.feature.kalmar.api.customer.Customer();
        UUID differentId = UUID.randomUUID();
        customer.setIdContext(differentId);
        customer.setIdContextType(CONTEXT_TYPE_ID);
        customer.setIdContextParent(PARENT_CONTEXT_ID);
        customer.setIdCountry(COUNTRY_ID);
        customer.setName("Different Corp");
        customer.setOrganizationNumber("987654321");
        return customer;
    }
}
