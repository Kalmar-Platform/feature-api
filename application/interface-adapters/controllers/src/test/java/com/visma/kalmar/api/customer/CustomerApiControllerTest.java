package com.visma.kalmar.api.customer;

import com.visma.kalmar.api.exception.InvalidInputDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CustomerApiControllerTest {

    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final UUID COUNTRY_ID = UUID.randomUUID();
    private static final UUID PARENT_CONTEXT_ID = UUID.randomUUID();
    private static final String CUSTOMER_NAME = "Acme Corporation";
    private static final String ORG_NUMBER = "123456789";

    @Mock
    private CreateCustomerInputPort createCustomerInputPort;

    @Mock
    private DeleteCustomerInputPort deleteCustomerInputPort;

    @Mock
    private CustomerPresenter customerPresenter;

    private CustomerApiController customerApiController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        customerApiController = new CustomerApiController(
                createCustomerInputPort,
                deleteCustomerInputPort,
                customerPresenter
        );
    }

    @Test
    void createCustomer_withValidData_success() {
        CustomerRequest request = new CustomerRequest(CUSTOMER_ID, COUNTRY_ID, PARENT_CONTEXT_ID, ORG_NUMBER, CUSTOMER_NAME);
        CustomerResponse expectedResponse = new CustomerResponse(CUSTOMER_ID, CUSTOMER_NAME, ORG_NUMBER, COUNTRY_ID, PARENT_CONTEXT_ID);
        ResponseEntity<CustomerResponse> expectedEntity = ResponseEntity.status(HttpStatus.CREATED).body(expectedResponse);

        when(customerPresenter.getResponse()).thenReturn(expectedEntity);

        ResponseEntity<CustomerResponse> response = customerApiController.createCustomer(request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());

        ArgumentCaptor<CreateCustomerInputPort.CreateCustomerInputData> inputDataCaptor =
                ArgumentCaptor.forClass(CreateCustomerInputPort.CreateCustomerInputData.class);
        verify(createCustomerInputPort, times(1)).createCustomer(inputDataCaptor.capture(), eq(customerPresenter));

        CreateCustomerInputPort.CreateCustomerInputData capturedInputData = inputDataCaptor.getValue();
        assertEquals(CUSTOMER_ID, capturedInputData.idCustomer());
        assertEquals(COUNTRY_ID, capturedInputData.idCountry());
        assertEquals(PARENT_CONTEXT_ID, capturedInputData.idContextParent());
        assertEquals(ORG_NUMBER, capturedInputData.organizationNumber());
        assertEquals(CUSTOMER_NAME, capturedInputData.name());
    }

    @Test
    void createCustomer_withNullParentContext_success() {
        CustomerRequest request = new CustomerRequest(CUSTOMER_ID, COUNTRY_ID, null, ORG_NUMBER, CUSTOMER_NAME);
        CustomerResponse expectedResponse = new CustomerResponse(CUSTOMER_ID, CUSTOMER_NAME, ORG_NUMBER, COUNTRY_ID, null);
        ResponseEntity<CustomerResponse> expectedEntity = ResponseEntity.status(HttpStatus.CREATED).body(expectedResponse);

        when(customerPresenter.getResponse()).thenReturn(expectedEntity);

        ResponseEntity<CustomerResponse> response = customerApiController.createCustomer(request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        ArgumentCaptor<CreateCustomerInputPort.CreateCustomerInputData> inputDataCaptor =
                ArgumentCaptor.forClass(CreateCustomerInputPort.CreateCustomerInputData.class);
        verify(createCustomerInputPort, times(1)).createCustomer(inputDataCaptor.capture(), eq(customerPresenter));

        CreateCustomerInputPort.CreateCustomerInputData capturedInputData = inputDataCaptor.getValue();
        assertNull(capturedInputData.idContextParent());
    }

    @Test
    void createCustomer_callsPresenterToGetResponse() {
        CustomerRequest request = new CustomerRequest(CUSTOMER_ID, COUNTRY_ID, null, ORG_NUMBER, CUSTOMER_NAME);
        ResponseEntity<CustomerResponse> expectedEntity = ResponseEntity.status(HttpStatus.CREATED).body(
                new CustomerResponse(CUSTOMER_ID, CUSTOMER_NAME, ORG_NUMBER, COUNTRY_ID, null));

        when(customerPresenter.getResponse()).thenReturn(expectedEntity);

        customerApiController.createCustomer(request);

        verify(customerPresenter, times(1)).getResponse();
    }

    @Test
    void deleteCustomer_withValidId_success() {
        ResponseEntity<Void> expectedEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        when(customerPresenter.getDeleteResponse()).thenReturn(expectedEntity);

        ResponseEntity<Void> response = customerApiController.deleteCustomer(CUSTOMER_ID.toString());

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(deleteCustomerInputPort, times(1)).deleteCustomer(eq(CUSTOMER_ID), eq(customerPresenter));
    }

    @Test
    void deleteCustomer_withInvalidId_throwsInvalidInputDataException() {
        assertThrows(IllegalArgumentException.class, () -> customerApiController.deleteCustomer("invalid-uuid"));

        verify(deleteCustomerInputPort, never()).deleteCustomer(any(), any());
    }

    @Test
    void deleteCustomer_callsPresenterToGetDeleteResponse() {
        ResponseEntity<Void> expectedEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        when(customerPresenter.getDeleteResponse()).thenReturn(expectedEntity);

        customerApiController.deleteCustomer(CUSTOMER_ID.toString());

        verify(customerPresenter, times(1)).getDeleteResponse();
    }
}
