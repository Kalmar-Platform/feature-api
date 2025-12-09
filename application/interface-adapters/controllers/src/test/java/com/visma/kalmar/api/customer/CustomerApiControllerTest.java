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
    private static final String COUNTRY_CODE = "NO";
    private static final UUID PARENT_CONTEXT_ID = UUID.randomUUID();
    private static final String CUSTOMER_NAME = "Acme Corporation";
    private static final String ORG_NUMBER = "123456789";

    @Mock
    private CreateCustomerInputPort createCustomerInputPort;

    @Mock
    private GetCustomerInputPort getCustomerInputPort;

    @Mock
    private UpdateCustomerInputPort updateCustomerInputPort;

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
                getCustomerInputPort,
                updateCustomerInputPort,
                deleteCustomerInputPort,
                customerPresenter
        );
    }

    @Test
    void createCustomer_withValidData_success() {
        CustomerRequest request = new CustomerRequest(CUSTOMER_ID, COUNTRY_CODE, PARENT_CONTEXT_ID, ORG_NUMBER, CUSTOMER_NAME);
        CustomerResponse expectedResponse = new CustomerResponse(CUSTOMER_ID, CUSTOMER_NAME, ORG_NUMBER, COUNTRY_CODE, PARENT_CONTEXT_ID);
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
        assertEquals(COUNTRY_CODE, capturedInputData.countryCode());
        assertEquals(PARENT_CONTEXT_ID, capturedInputData.idContextParent());
        assertEquals(ORG_NUMBER, capturedInputData.organizationNumber());
        assertEquals(CUSTOMER_NAME, capturedInputData.name());
    }

    @Test
    void createCustomer_withNullParentContext_success() {
        CustomerRequest request = new CustomerRequest(CUSTOMER_ID, COUNTRY_CODE, null, ORG_NUMBER, CUSTOMER_NAME);
        CustomerResponse expectedResponse = new CustomerResponse(CUSTOMER_ID, CUSTOMER_NAME, ORG_NUMBER, COUNTRY_CODE, null);
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
        CustomerRequest request = new CustomerRequest(CUSTOMER_ID, COUNTRY_CODE, PARENT_CONTEXT_ID, ORG_NUMBER, CUSTOMER_NAME);
        ResponseEntity<CustomerResponse> expectedEntity = ResponseEntity.status(HttpStatus.CREATED).body(
                new CustomerResponse(CUSTOMER_ID, CUSTOMER_NAME, ORG_NUMBER, COUNTRY_CODE, PARENT_CONTEXT_ID));

        when(customerPresenter.getResponse()).thenReturn(expectedEntity);

        customerApiController.createCustomer(request);

        verify(customerPresenter, times(1)).getResponse();
    }

    @Test
    void getCustomer_withValidId_success() {
        CustomerResponse expectedResponse = new CustomerResponse(CUSTOMER_ID, CUSTOMER_NAME, ORG_NUMBER, COUNTRY_CODE, PARENT_CONTEXT_ID);
        ResponseEntity<CustomerResponse> expectedEntity = ResponseEntity.ok(expectedResponse);

        when(customerPresenter.getResponse()).thenReturn(expectedEntity);

        ResponseEntity<CustomerResponse> response = customerApiController.getCustomer(CUSTOMER_ID.toString());

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());

        verify(getCustomerInputPort, times(1)).getCustomer(eq(CUSTOMER_ID), eq(customerPresenter));
    }

    @Test
    void getCustomer_withInvalidId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> customerApiController.getCustomer("invalid-uuid"));

        verify(getCustomerInputPort, never()).getCustomer(any(), any());
    }

    @Test
    void getCustomer_callsPresenterToGetResponse() {
        ResponseEntity<CustomerResponse> expectedEntity = ResponseEntity.ok(
                new CustomerResponse(CUSTOMER_ID, CUSTOMER_NAME, ORG_NUMBER, COUNTRY_CODE, PARENT_CONTEXT_ID));

        when(customerPresenter.getResponse()).thenReturn(expectedEntity);

        customerApiController.getCustomer(CUSTOMER_ID.toString());

        verify(customerPresenter, times(1)).getResponse();
    }

    @Test
    void updateCustomer_withValidData_success() {
        CustomerRequest request = new CustomerRequest(CUSTOMER_ID, COUNTRY_CODE, PARENT_CONTEXT_ID, ORG_NUMBER, CUSTOMER_NAME);
        CustomerResponse expectedResponse = new CustomerResponse(CUSTOMER_ID, CUSTOMER_NAME, ORG_NUMBER, COUNTRY_CODE, PARENT_CONTEXT_ID);
        ResponseEntity<CustomerResponse> expectedEntity = ResponseEntity.ok(expectedResponse);

        when(customerPresenter.getResponse()).thenReturn(expectedEntity);

        ResponseEntity<CustomerResponse> response = customerApiController.updateCustomer(CUSTOMER_ID.toString(), request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());

        ArgumentCaptor<UpdateCustomerInputPort.UpdateCustomerInputData> inputDataCaptor =
                ArgumentCaptor.forClass(UpdateCustomerInputPort.UpdateCustomerInputData.class);
        verify(updateCustomerInputPort, times(1)).updateCustomer(inputDataCaptor.capture(), eq(customerPresenter));

        UpdateCustomerInputPort.UpdateCustomerInputData capturedInputData = inputDataCaptor.getValue();
        assertEquals(CUSTOMER_ID, capturedInputData.idCustomer());
        assertEquals(COUNTRY_CODE, capturedInputData.countryCode());
        assertEquals(PARENT_CONTEXT_ID, capturedInputData.idContextParent());
        assertEquals(ORG_NUMBER, capturedInputData.organizationNumber());
        assertEquals(CUSTOMER_NAME, capturedInputData.name());
    }

    @Test
    void updateCustomer_withNullParentContext_success() {
        CustomerRequest request = new CustomerRequest(CUSTOMER_ID, COUNTRY_CODE, null, ORG_NUMBER, CUSTOMER_NAME);
        CustomerResponse expectedResponse = new CustomerResponse(CUSTOMER_ID, CUSTOMER_NAME, ORG_NUMBER, COUNTRY_CODE, null);
        ResponseEntity<CustomerResponse> expectedEntity = ResponseEntity.ok(expectedResponse);

        when(customerPresenter.getResponse()).thenReturn(expectedEntity);

        ResponseEntity<CustomerResponse> response = customerApiController.updateCustomer(CUSTOMER_ID.toString(), request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ArgumentCaptor<UpdateCustomerInputPort.UpdateCustomerInputData> inputDataCaptor =
                ArgumentCaptor.forClass(UpdateCustomerInputPort.UpdateCustomerInputData.class);
        verify(updateCustomerInputPort, times(1)).updateCustomer(inputDataCaptor.capture(), eq(customerPresenter));

        UpdateCustomerInputPort.UpdateCustomerInputData capturedInputData = inputDataCaptor.getValue();
        assertNull(capturedInputData.idContextParent());
    }

    @Test
    void updateCustomer_withInvalidId_throwsIllegalArgumentException() {
        CustomerRequest request = new CustomerRequest(CUSTOMER_ID, COUNTRY_CODE, null, ORG_NUMBER, CUSTOMER_NAME);

        assertThrows(IllegalArgumentException.class, () -> customerApiController.updateCustomer("invalid-uuid", request));

        verify(updateCustomerInputPort, never()).updateCustomer(any(), any());
    }

    @Test
    void updateCustomer_callsPresenterToGetResponse() {
        CustomerRequest request = new CustomerRequest(CUSTOMER_ID, COUNTRY_CODE, null, ORG_NUMBER, CUSTOMER_NAME);
        ResponseEntity<CustomerResponse> expectedEntity = ResponseEntity.ok(
                new CustomerResponse(CUSTOMER_ID, CUSTOMER_NAME, ORG_NUMBER, COUNTRY_CODE, null));

        when(customerPresenter.getResponse()).thenReturn(expectedEntity);

        customerApiController.updateCustomer(CUSTOMER_ID.toString(), request);

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
