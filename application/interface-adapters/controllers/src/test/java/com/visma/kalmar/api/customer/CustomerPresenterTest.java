package com.visma.kalmar.api.customer;

import com.visma.kalmar.api.country.CountryGateway;
import com.visma.kalmar.api.entities.context.Context;
import com.visma.kalmar.api.entities.country.Country;
import com.visma.kalmar.api.entities.customer.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerPresenterTest {

    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final UUID CONTEXT_TYPE_ID = UUID.randomUUID();
    private static final UUID PARENT_CONTEXT_ID = UUID.randomUUID();
    private static final UUID COUNTRY_ID = UUID.randomUUID();
    private static final String COUNTRY_CODE = "NO";
    private static final String CUSTOMER_NAME = "Acme Corporation";
    private static final String ORG_NUMBER = "123456789";

    private CustomerPresenter customerPresenter;
    private CountryGateway countryGateway;

    @BeforeEach
    void setUp() {
        countryGateway = mock(CountryGateway.class);
        customerPresenter = new CustomerPresenter(countryGateway);
        
        Country norway = new Country(COUNTRY_ID, "Norway", COUNTRY_CODE);
        when(countryGateway.findById(COUNTRY_ID)).thenReturn(norway);
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
        assertEquals(COUNTRY_CODE, response.getBody().countryCode());
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
        assertEquals(COUNTRY_CODE, customerResponse.countryCode());
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
        assertEquals(COUNTRY_CODE, customerResponse.countryCode());
        assertEquals(context.name(), customerResponse.name());
        assertEquals(context.organizationNumber(), customerResponse.organizationNumber());
    }

    @Test
    void present_withoutCreatedFlag_returnsHttpOk() {
        Customer customer = new Customer(CUSTOMER_ID);
        Context context = new Context(
                CUSTOMER_ID,
                CONTEXT_TYPE_ID,
                PARENT_CONTEXT_ID,
                COUNTRY_ID,
                CUSTOMER_NAME,
                ORG_NUMBER
        );

        customerPresenter.present(customer, context);
        ResponseEntity<CustomerResponse> response = customerPresenter.getResponse();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(CUSTOMER_ID, response.getBody().idContext());
        assertEquals(CUSTOMER_NAME, response.getBody().name());
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

    @Test
    void present_multipleCalls_overridesPreviousResponse() {
        Customer customer1 = new Customer(CUSTOMER_ID);
        Context context1 = new Context(
                CUSTOMER_ID,
                CONTEXT_TYPE_ID,
                PARENT_CONTEXT_ID,
                COUNTRY_ID,
                CUSTOMER_NAME,
                ORG_NUMBER
        );

        UUID customer2Id = UUID.randomUUID();
        Customer customer2 = new Customer(customer2Id);
        Context context2 = new Context(
                customer2Id,
                CONTEXT_TYPE_ID,
                null,
                COUNTRY_ID,
                "Different Corp",
                "987654321"
        );

        customerPresenter.present(customer1, context1, true);
        customerPresenter.present(customer2, context2, false);

        ResponseEntity<CustomerResponse> response = customerPresenter.getResponse();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(customer2Id, response.getBody().idContext());
        assertEquals("Different Corp", response.getBody().name());
        assertEquals("987654321", response.getBody().organizationNumber());
    }

    @Test
    void present_usesCustomerIdForResponse() {
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

        assertEquals(CUSTOMER_ID, response.getBody().idContext());
        assertEquals(customer.idContext(), response.getBody().idContext());
    }

    @Test
    void present_responseBodyContainsAllRequiredFields() {
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

        CustomerResponse body = response.getBody();
        assertNotNull(body);
        assertNotNull(body.idContext());
        assertNotNull(body.name());
        assertNotNull(body.organizationNumber());
        assertNotNull(body.countryCode());
        assertNotNull(body.idContextParent());
    }

    @Test
    void getResponse_calledMultipleTimes_returnsSameInstance() {
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
        ResponseEntity<CustomerResponse> response1 = customerPresenter.getResponse();
        ResponseEntity<CustomerResponse> response2 = customerPresenter.getResponse();

        assertSame(response1, response2);
    }

    @Test
    void getDeleteResponse_calledMultipleTimes_returnsSameInstance() {
        customerPresenter.presentDeleted();
        ResponseEntity<Void> response1 = customerPresenter.getDeleteResponse();
        ResponseEntity<Void> response2 = customerPresenter.getDeleteResponse();

        assertSame(response1, response2);
    }

    @Test
    void present_withEmptyStrings_preservesEmptyValues() {
        Customer customer = new Customer(CUSTOMER_ID);
        Context context = new Context(
                CUSTOMER_ID,
                CONTEXT_TYPE_ID,
                PARENT_CONTEXT_ID,
                COUNTRY_ID,
                "",
                ""
        );

        customerPresenter.present(customer, context, true);
        ResponseEntity<CustomerResponse> response = customerPresenter.getResponse();

        CustomerResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("", body.name());
        assertEquals("", body.organizationNumber());
    }

    @Test
    void present_createdStatus_matchesCreatedFlag() {
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
        ResponseEntity<CustomerResponse> createdResponse = customerPresenter.getResponse();
        assertEquals(HttpStatus.CREATED, createdResponse.getStatusCode());

        customerPresenter.present(customer, context, false);
        ResponseEntity<CustomerResponse> okResponse = customerPresenter.getResponse();
        assertEquals(HttpStatus.OK, okResponse.getStatusCode());
    }

    @Test
    void presentDeleted_noContentStatus_hasNoBody() {
        customerPresenter.presentDeleted();
        ResponseEntity<Void> response = customerPresenter.getDeleteResponse();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        assertFalse(response.hasBody());
    }
}
