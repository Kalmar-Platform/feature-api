package com.visma.kalmar.api.customer;

import com.visma.kalmar.api.entities.context.Context;
import com.visma.kalmar.api.entities.customer.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerPresenterTest {

    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final UUID CONTEXT_TYPE_ID = UUID.randomUUID();
    private static final UUID PARENT_CONTEXT_ID = UUID.randomUUID();
    private static final UUID COUNTRY_ID = UUID.randomUUID();
    private static final String CUSTOMER_NAME = "Acme Corporation";
    private static final String ORG_NUMBER = "123456789";

    private CustomerPresenter customerPresenter;

    @BeforeEach
    void setUp() {
        customerPresenter = new CustomerPresenter();
    }

    @Test
    void present_withCreatedTrue_returnsHttpCreated() {
        Customer customer = new Customer(CUSTOMER_ID);
        Context context = new Context(
                CUSTOMER_ID,
                CONTEXT_TYPE_ID,
                PARENT_CONTEXT_ID,
                COUNTRY_ID,
                CUSTOMER_NAME,
                ORG_NUMBER
        );

        customerPresenter.present(customer, context, true);
        ResponseEntity<CustomerResponse> response = customerPresenter.getResponse();

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(CUSTOMER_ID, response.getBody().idContext());
        assertEquals(CUSTOMER_NAME, response.getBody().name());
        assertEquals(ORG_NUMBER, response.getBody().organizationNumber());
        assertEquals(COUNTRY_ID, response.getBody().idCountry());
        assertEquals(PARENT_CONTEXT_ID, response.getBody().idContextParent());
    }

    @Test
    void present_withCreatedFalse_returnsHttpOk() {
        Customer customer = new Customer(CUSTOMER_ID);
        Context context = new Context(
                CUSTOMER_ID,
                CONTEXT_TYPE_ID,
                PARENT_CONTEXT_ID,
                COUNTRY_ID,
                CUSTOMER_NAME,
                ORG_NUMBER
        );

        customerPresenter.present(customer, context, false);
        ResponseEntity<CustomerResponse> response = customerPresenter.getResponse();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(CUSTOMER_ID, response.getBody().idContext());
    }

    @Test
    void present_withNullParentContext_mapsFieldsCorrectly() {
        Customer customer = new Customer(CUSTOMER_ID);
        Context context = new Context(
                CUSTOMER_ID,
                CONTEXT_TYPE_ID,
                null,
                COUNTRY_ID,
                CUSTOMER_NAME,
                ORG_NUMBER
        );

        customerPresenter.present(customer, context, true);
        ResponseEntity<CustomerResponse> response = customerPresenter.getResponse();

        CustomerResponse customerResponse = response.getBody();
        assertNotNull(customerResponse);
        assertEquals(CUSTOMER_ID, customerResponse.idContext());
        assertNull(customerResponse.idContextParent());
        assertEquals(COUNTRY_ID, customerResponse.idCountry());
        assertEquals(CUSTOMER_NAME, customerResponse.name());
        assertEquals(ORG_NUMBER, customerResponse.organizationNumber());
    }

    @Test
    void present_mapsAllFieldsCorrectly() {
        Customer customer = new Customer(CUSTOMER_ID);
        Context context = new Context(
                CUSTOMER_ID,
                CONTEXT_TYPE_ID,
                PARENT_CONTEXT_ID,
                COUNTRY_ID,
                CUSTOMER_NAME,
                ORG_NUMBER
        );

        customerPresenter.present(customer, context, false);
        ResponseEntity<CustomerResponse> response = customerPresenter.getResponse();

        CustomerResponse customerResponse = response.getBody();
        assertNotNull(customerResponse);
        assertEquals(context.idContext(), customerResponse.idContext());
        assertEquals(context.idContextParent(), customerResponse.idContextParent());
        assertEquals(context.idCountry(), customerResponse.idCountry());
        assertEquals(context.name(), customerResponse.name());
        assertEquals(context.organizationNumber(), customerResponse.organizationNumber());
    }

    @Test
    void presentDeleted_returnsHttpNoContent() {
        customerPresenter.presentDeleted();
        ResponseEntity<Void> response = customerPresenter.getDeleteResponse();

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void presentDeleted_doesNotAffectResponse() {
        Customer customer = new Customer(CUSTOMER_ID);
        Context context = new Context(
                CUSTOMER_ID,
                CONTEXT_TYPE_ID,
                PARENT_CONTEXT_ID,
                COUNTRY_ID,
                CUSTOMER_NAME,
                ORG_NUMBER
        );

        customerPresenter.present(customer, context, true);
        ResponseEntity<CustomerResponse> createResponse = customerPresenter.getResponse();

        customerPresenter.presentDeleted();
        ResponseEntity<Void> deleteResponse = customerPresenter.getDeleteResponse();

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
    }
}
