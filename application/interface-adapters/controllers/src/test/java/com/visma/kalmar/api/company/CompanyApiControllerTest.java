package com.visma.kalmar.api.company;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CompanyApiControllerTest {

    private static final UUID COMPANY_ID = UUID.randomUUID();
    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final String COUNTRY_CODE = "NO";
    private static final UUID PARENT_CONTEXT_ID = UUID.randomUUID();
    private static final String COMPANY_NAME = "Acme Company";
    private static final String ORG_NUMBER = "987654321";

    @Mock
    private CreateCompanyInputPort createCompanyInputPort;

    @Mock
    private GetCompanyInputPort getCompanyInputPort;

    @Mock
    private DeleteCompanyInputPort deleteCompanyInputPort;

    @Mock
    private UpdateCompanyInputPort updateCompanyInputPort;

    @Mock
    private CompanyPresenter companyPresenter;

    private CompanyApiController companyApiController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        companyApiController = new CompanyApiController(
                createCompanyInputPort,
                getCompanyInputPort,
                deleteCompanyInputPort,
                updateCompanyInputPort,
                companyPresenter
        );
    }

    @Test
    void createCompany_withValidData_success() {
        CompanyRequest request = new CompanyRequest(COMPANY_ID, COUNTRY_CODE, PARENT_CONTEXT_ID, ORG_NUMBER, COMPANY_NAME);
        CompanyResponse expectedResponse = new CompanyResponse(COMPANY_ID, COMPANY_NAME, ORG_NUMBER, COUNTRY_CODE, PARENT_CONTEXT_ID);
        ResponseEntity<CompanyResponse> expectedEntity = ResponseEntity.status(HttpStatus.CREATED).body(expectedResponse);

        when(companyPresenter.getResponse()).thenReturn(expectedEntity);

        ResponseEntity<CompanyResponse> response = companyApiController.createCompany(request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());

        ArgumentCaptor<CreateCompanyInputPort.CreateCompanyInputData> inputDataCaptor =
                ArgumentCaptor.forClass(CreateCompanyInputPort.CreateCompanyInputData.class);
        verify(createCompanyInputPort, times(1)).createCompany(inputDataCaptor.capture(), eq(companyPresenter));

        CreateCompanyInputPort.CreateCompanyInputData capturedInputData = inputDataCaptor.getValue();
        assertEquals(COMPANY_ID, capturedInputData.idCompany());
        assertEquals(COUNTRY_CODE, capturedInputData.countryCode());
        assertEquals(PARENT_CONTEXT_ID, capturedInputData.idContextParent());
        assertEquals(ORG_NUMBER, capturedInputData.organizationNumber());
        assertEquals(COMPANY_NAME, capturedInputData.name());
    }

    @Test
    void createCompany_withNullIdCompany_success() {
        CompanyRequest request = new CompanyRequest(null, COUNTRY_CODE, PARENT_CONTEXT_ID, ORG_NUMBER, COMPANY_NAME);
        CompanyResponse expectedResponse = new CompanyResponse(COMPANY_ID, COMPANY_NAME, ORG_NUMBER, COUNTRY_CODE, PARENT_CONTEXT_ID);
        ResponseEntity<CompanyResponse> expectedEntity = ResponseEntity.status(HttpStatus.CREATED).body(expectedResponse);

        when(companyPresenter.getResponse()).thenReturn(expectedEntity);

        ResponseEntity<CompanyResponse> response = companyApiController.createCompany(request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        ArgumentCaptor<CreateCompanyInputPort.CreateCompanyInputData> inputDataCaptor =
                ArgumentCaptor.forClass(CreateCompanyInputPort.CreateCompanyInputData.class);
        verify(createCompanyInputPort, times(1)).createCompany(inputDataCaptor.capture(), eq(companyPresenter));

        CreateCompanyInputPort.CreateCompanyInputData capturedInputData = inputDataCaptor.getValue();
        assertNotNull(capturedInputData.idCompany());
    }

    @Test
    void createCompany_withNullCountryCode_success() {
        CompanyRequest request = new CompanyRequest(COMPANY_ID, null, PARENT_CONTEXT_ID, ORG_NUMBER, COMPANY_NAME);
        CompanyResponse expectedResponse = new CompanyResponse(COMPANY_ID, COMPANY_NAME, ORG_NUMBER, COUNTRY_CODE, PARENT_CONTEXT_ID);
        ResponseEntity<CompanyResponse> expectedEntity = ResponseEntity.status(HttpStatus.CREATED).body(expectedResponse);

        when(companyPresenter.getResponse()).thenReturn(expectedEntity);

        ResponseEntity<CompanyResponse> response = companyApiController.createCompany(request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        ArgumentCaptor<CreateCompanyInputPort.CreateCompanyInputData> inputDataCaptor =
                ArgumentCaptor.forClass(CreateCompanyInputPort.CreateCompanyInputData.class);
        verify(createCompanyInputPort, times(1)).createCompany(inputDataCaptor.capture(), eq(companyPresenter));

        CreateCompanyInputPort.CreateCompanyInputData capturedInputData = inputDataCaptor.getValue();
        assertNull(capturedInputData.countryCode());
    }

    @Test
    void createCompany_callsPresenterToGetResponse() {
        CompanyRequest request = new CompanyRequest(COMPANY_ID, COUNTRY_CODE, PARENT_CONTEXT_ID, ORG_NUMBER, COMPANY_NAME);
        ResponseEntity<CompanyResponse> expectedEntity = ResponseEntity.status(HttpStatus.CREATED).body(
                new CompanyResponse(COMPANY_ID, COMPANY_NAME, ORG_NUMBER, COUNTRY_CODE, PARENT_CONTEXT_ID));

        when(companyPresenter.getResponse()).thenReturn(expectedEntity);

        companyApiController.createCompany(request);

        verify(companyPresenter, times(1)).getResponse();
    }

    @Test
    void createCompany_mapsAllFieldsFromRequestToInputData() {
        CompanyRequest request = new CompanyRequest(COMPANY_ID, COUNTRY_CODE, PARENT_CONTEXT_ID, ORG_NUMBER, COMPANY_NAME);
        ResponseEntity<CompanyResponse> expectedEntity = ResponseEntity.status(HttpStatus.CREATED).body(
                new CompanyResponse(COMPANY_ID, COMPANY_NAME, ORG_NUMBER, COUNTRY_CODE, PARENT_CONTEXT_ID));

        when(companyPresenter.getResponse()).thenReturn(expectedEntity);

        companyApiController.createCompany(request);

        ArgumentCaptor<CreateCompanyInputPort.CreateCompanyInputData> inputDataCaptor =
                ArgumentCaptor.forClass(CreateCompanyInputPort.CreateCompanyInputData.class);
        verify(createCompanyInputPort, times(1)).createCompany(inputDataCaptor.capture(), eq(companyPresenter));

        CreateCompanyInputPort.CreateCompanyInputData capturedInputData = inputDataCaptor.getValue();
        assertEquals(request.idContext(), capturedInputData.idCompany());
        assertEquals(request.countryCode(), capturedInputData.countryCode());
        assertEquals(request.idContextParent(), capturedInputData.idContextParent());
        assertEquals(request.organizationNumber(), capturedInputData.organizationNumber());
        assertEquals(request.name(), capturedInputData.name());
    }

    @Test
    void deleteCompany_withValidId_success() {
        ResponseEntity<Void> response = companyApiController.deleteCompany(CUSTOMER_ID.toString(), COMPANY_ID.toString());

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(deleteCompanyInputPort, times(1)).deleteCompany(eq(CUSTOMER_ID), eq(COMPANY_ID));
    }

    @Test
    void deleteCompany_withInvalidId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> companyApiController.deleteCompany(CUSTOMER_ID.toString(), "invalid-uuid"));

        verify(deleteCompanyInputPort, never()).deleteCompany(any(), any());
    }

    @Test
    void deleteCompany_convertsStringIdToUuid() {
        UUID expectedCustomerId = UUID.randomUUID();
        UUID expectedCompanyId = UUID.randomUUID();
        String customerIdString = expectedCustomerId.toString();
        String companyIdString = expectedCompanyId.toString();

        companyApiController.deleteCompany(customerIdString, companyIdString);

        ArgumentCaptor<UUID> customerIdCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<UUID> companyIdCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(deleteCompanyInputPort, times(1)).deleteCompany(customerIdCaptor.capture(), companyIdCaptor.capture());

        assertEquals(expectedCustomerId, customerIdCaptor.getValue());
        assertEquals(expectedCompanyId, companyIdCaptor.getValue());
    }

    @Test
    void deleteCompany_returnsNoContentStatus() {
        ResponseEntity<Void> response = companyApiController.deleteCompany(CUSTOMER_ID.toString(), COMPANY_ID.toString());

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void deleteCompany_callsDeleteInputPortWithCorrectId() {
        UUID customerId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        companyApiController.deleteCompany(customerId.toString(), companyId.toString());

        verify(deleteCompanyInputPort, times(1)).deleteCompany(customerId, companyId);
    }

    @Test
    void createCompany_withAllOptionalFieldsNull_success() {
        CompanyRequest request = new CompanyRequest(null, null, PARENT_CONTEXT_ID, ORG_NUMBER, COMPANY_NAME);
        CompanyResponse expectedResponse = new CompanyResponse(COMPANY_ID, COMPANY_NAME, ORG_NUMBER, COUNTRY_CODE, PARENT_CONTEXT_ID);
        ResponseEntity<CompanyResponse> expectedEntity = ResponseEntity.status(HttpStatus.CREATED).body(expectedResponse);

        when(companyPresenter.getResponse()).thenReturn(expectedEntity);

        ResponseEntity<CompanyResponse> response = companyApiController.createCompany(request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        ArgumentCaptor<CreateCompanyInputPort.CreateCompanyInputData> inputDataCaptor =
                ArgumentCaptor.forClass(CreateCompanyInputPort.CreateCompanyInputData.class);
        verify(createCompanyInputPort, times(1)).createCompany(inputDataCaptor.capture(), eq(companyPresenter));

        CreateCompanyInputPort.CreateCompanyInputData capturedInputData = inputDataCaptor.getValue();
        assertNotNull(capturedInputData.idCompany());
        assertNull(capturedInputData.countryCode());
        assertNotNull(capturedInputData.idContextParent());
    }

    @Test
    void createCompany_returnsResponseFromPresenter() {
        CompanyRequest request = new CompanyRequest(COMPANY_ID, COUNTRY_CODE, PARENT_CONTEXT_ID, ORG_NUMBER, COMPANY_NAME);
        ResponseEntity<CompanyResponse> expectedEntity = ResponseEntity.status(HttpStatus.CREATED).body(
                new CompanyResponse(COMPANY_ID, COMPANY_NAME, ORG_NUMBER, COUNTRY_CODE, PARENT_CONTEXT_ID));

        when(companyPresenter.getResponse()).thenReturn(expectedEntity);

        ResponseEntity<CompanyResponse> actualResponse = companyApiController.createCompany(request);

        assertSame(expectedEntity, actualResponse);
    }

    @Test
    void getCompany_withValidId_success() {
        CompanyResponse expectedResponse = new CompanyResponse(COMPANY_ID, COMPANY_NAME, ORG_NUMBER, COUNTRY_CODE, PARENT_CONTEXT_ID);
        ResponseEntity<CompanyResponse> expectedEntity = ResponseEntity.ok(expectedResponse);

        when(companyPresenter.getResponse()).thenReturn(expectedEntity);

        ResponseEntity<CompanyResponse> response = companyApiController.getCompany(CUSTOMER_ID.toString(), COMPANY_ID.toString());

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());

        verify(getCompanyInputPort, times(1)).getCompany(eq(CUSTOMER_ID), eq(COMPANY_ID), eq(companyPresenter));
    }

    @Test
    void getCompany_withInvalidId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> companyApiController.getCompany(CUSTOMER_ID.toString(), "invalid-uuid"));

        verify(getCompanyInputPort, never()).getCompany(any(), any(), any());
    }

    @Test
    void getCompany_callsPresenterToGetResponse() {
        ResponseEntity<CompanyResponse> expectedEntity = ResponseEntity.ok(
                new CompanyResponse(COMPANY_ID, COMPANY_NAME, ORG_NUMBER, COUNTRY_CODE, PARENT_CONTEXT_ID));

        when(companyPresenter.getResponse()).thenReturn(expectedEntity);

        companyApiController.getCompany(CUSTOMER_ID.toString(), COMPANY_ID.toString());

        verify(companyPresenter, times(1)).getResponse();
    }

    @Test
    void getCompany_convertsStringIdToUuid() {
        UUID expectedCustomerId = UUID.randomUUID();
        UUID expectedCompanyId = UUID.randomUUID();
        String customerIdString = expectedCustomerId.toString();
        String companyIdString = expectedCompanyId.toString();
        ResponseEntity<CompanyResponse> expectedEntity = ResponseEntity.ok(
                new CompanyResponse(expectedCompanyId, COMPANY_NAME, ORG_NUMBER, COUNTRY_CODE, PARENT_CONTEXT_ID));

        when(companyPresenter.getResponse()).thenReturn(expectedEntity);

        companyApiController.getCompany(customerIdString, companyIdString);

        ArgumentCaptor<UUID> customerIdCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<UUID> companyIdCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(getCompanyInputPort, times(1)).getCompany(customerIdCaptor.capture(), companyIdCaptor.capture(), eq(companyPresenter));

        assertEquals(expectedCustomerId, customerIdCaptor.getValue());
        assertEquals(expectedCompanyId, companyIdCaptor.getValue());
    }

    @Test
    void getCompany_returnsResponseFromPresenter() {
        ResponseEntity<CompanyResponse> expectedEntity = ResponseEntity.ok(
                new CompanyResponse(COMPANY_ID, COMPANY_NAME, ORG_NUMBER, COUNTRY_CODE, PARENT_CONTEXT_ID));

        when(companyPresenter.getResponse()).thenReturn(expectedEntity);

        ResponseEntity<CompanyResponse> actualResponse = companyApiController.getCompany(CUSTOMER_ID.toString(), COMPANY_ID.toString());

        assertSame(expectedEntity, actualResponse);
    }

    @Test
    void getCompany_withInvalidCustomerId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> companyApiController.getCompany("invalid-uuid", COMPANY_ID.toString()));

        verify(getCompanyInputPort, never()).getCompany(any(), any(), any());
    }

    @Test
    void deleteCompany_withInvalidCustomerId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> companyApiController.deleteCompany("invalid-uuid", COMPANY_ID.toString()));

        verify(deleteCompanyInputPort, never()).deleteCompany(any(), any());
    }

    @Test
    void updateCompany_withValidData_success() {
        String updatedName = "Updated Company";
        String updatedOrgNumber = "111111111";
        CompanyRequest request = new CompanyRequest(COMPANY_ID, COUNTRY_CODE, CUSTOMER_ID, updatedOrgNumber, updatedName);
        CompanyResponse expectedResponse = new CompanyResponse(COMPANY_ID, updatedName, updatedOrgNumber, COUNTRY_CODE, CUSTOMER_ID);
        ResponseEntity<CompanyResponse> expectedEntity = ResponseEntity.ok(expectedResponse);

        when(companyPresenter.getResponse()).thenReturn(expectedEntity);

        ResponseEntity<CompanyResponse> response = companyApiController.updateCompany(CUSTOMER_ID.toString(), COMPANY_ID.toString(), request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());

        ArgumentCaptor<UUID> customerIdCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<UpdateCompanyInputPort.UpdateCompanyInputData> inputDataCaptor =
                ArgumentCaptor.forClass(UpdateCompanyInputPort.UpdateCompanyInputData.class);
        verify(updateCompanyInputPort, times(1)).updateCompany(customerIdCaptor.capture(), inputDataCaptor.capture(), eq(companyPresenter));

        assertEquals(CUSTOMER_ID, customerIdCaptor.getValue());
        UpdateCompanyInputPort.UpdateCompanyInputData capturedInputData = inputDataCaptor.getValue();
        assertEquals(COMPANY_ID, capturedInputData.idCompany());
        assertEquals(updatedName, capturedInputData.name());
        assertEquals(updatedOrgNumber, capturedInputData.organizationNumber());
        assertEquals(COUNTRY_CODE, capturedInputData.countryCode());
    }

    @Test
    void updateCompany_withInvalidCompanyId_throwsIllegalArgumentException() {
        CompanyRequest request = new CompanyRequest(COMPANY_ID, COUNTRY_CODE, CUSTOMER_ID, ORG_NUMBER, COMPANY_NAME);

        assertThrows(IllegalArgumentException.class, () -> companyApiController.updateCompany(CUSTOMER_ID.toString(), "invalid-uuid", request));

        verify(updateCompanyInputPort, never()).updateCompany(any(), any(), any());
    }

    @Test
    void updateCompany_withInvalidCustomerId_throwsIllegalArgumentException() {
        CompanyRequest request = new CompanyRequest(COMPANY_ID, COUNTRY_CODE, CUSTOMER_ID, ORG_NUMBER, COMPANY_NAME);

        assertThrows(IllegalArgumentException.class, () -> companyApiController.updateCompany("invalid-uuid", COMPANY_ID.toString(), request));

        verify(updateCompanyInputPort, never()).updateCompany(any(), any(), any());
    }

    @Test
    void updateCompany_callsPresenterToGetResponse() {
        CompanyRequest request = new CompanyRequest(COMPANY_ID, COUNTRY_CODE, CUSTOMER_ID, ORG_NUMBER, COMPANY_NAME);
        ResponseEntity<CompanyResponse> expectedEntity = ResponseEntity.ok(
                new CompanyResponse(COMPANY_ID, COMPANY_NAME, ORG_NUMBER, COUNTRY_CODE, CUSTOMER_ID));

        when(companyPresenter.getResponse()).thenReturn(expectedEntity);

        companyApiController.updateCompany(CUSTOMER_ID.toString(), COMPANY_ID.toString(), request);

        verify(companyPresenter, times(1)).getResponse();
    }

    @Test
    void updateCompany_convertsStringIdsToUuid() {
        UUID expectedCustomerId = UUID.randomUUID();
        UUID expectedCompanyId = UUID.randomUUID();
        String customerIdString = expectedCustomerId.toString();
        String companyIdString = expectedCompanyId.toString();
        CompanyRequest request = new CompanyRequest(expectedCompanyId, COUNTRY_CODE, expectedCustomerId, ORG_NUMBER, COMPANY_NAME);
        ResponseEntity<CompanyResponse> expectedEntity = ResponseEntity.ok(
                new CompanyResponse(expectedCompanyId, COMPANY_NAME, ORG_NUMBER, COUNTRY_CODE, expectedCustomerId));

        when(companyPresenter.getResponse()).thenReturn(expectedEntity);

        companyApiController.updateCompany(customerIdString, companyIdString, request);

        ArgumentCaptor<UUID> customerIdCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<UpdateCompanyInputPort.UpdateCompanyInputData> inputDataCaptor =
                ArgumentCaptor.forClass(UpdateCompanyInputPort.UpdateCompanyInputData.class);
        verify(updateCompanyInputPort, times(1)).updateCompany(customerIdCaptor.capture(), inputDataCaptor.capture(), eq(companyPresenter));

        assertEquals(expectedCustomerId, customerIdCaptor.getValue());
        assertEquals(expectedCompanyId, inputDataCaptor.getValue().idCompany());
    }

    @Test
    void updateCompany_returnsResponseFromPresenter() {
        CompanyRequest request = new CompanyRequest(COMPANY_ID, COUNTRY_CODE, CUSTOMER_ID, ORG_NUMBER, COMPANY_NAME);
        ResponseEntity<CompanyResponse> expectedEntity = ResponseEntity.ok(
                new CompanyResponse(COMPANY_ID, COMPANY_NAME, ORG_NUMBER, COUNTRY_CODE, CUSTOMER_ID));

        when(companyPresenter.getResponse()).thenReturn(expectedEntity);

        ResponseEntity<CompanyResponse> actualResponse = companyApiController.updateCompany(CUSTOMER_ID.toString(), COMPANY_ID.toString(), request);

        assertSame(expectedEntity, actualResponse);
    }

    @Test
    void updateCompany_mapsAllFieldsFromRequestToInputData() {
        String updatedName = "New Company Name";
        String updatedOrgNumber = "555555555";
        String updatedCountryCode = "SE";
        CompanyRequest request = new CompanyRequest(COMPANY_ID, updatedCountryCode, CUSTOMER_ID, updatedOrgNumber, updatedName);
        ResponseEntity<CompanyResponse> expectedEntity = ResponseEntity.ok(
                new CompanyResponse(COMPANY_ID, updatedName, updatedOrgNumber, updatedCountryCode, CUSTOMER_ID));

        when(companyPresenter.getResponse()).thenReturn(expectedEntity);

        companyApiController.updateCompany(CUSTOMER_ID.toString(), COMPANY_ID.toString(), request);

        ArgumentCaptor<UpdateCompanyInputPort.UpdateCompanyInputData> inputDataCaptor =
                ArgumentCaptor.forClass(UpdateCompanyInputPort.UpdateCompanyInputData.class);
        verify(updateCompanyInputPort, times(1)).updateCompany(any(), inputDataCaptor.capture(), eq(companyPresenter));

        UpdateCompanyInputPort.UpdateCompanyInputData capturedInputData = inputDataCaptor.getValue();
        assertEquals(COMPANY_ID, capturedInputData.idCompany());
        assertEquals(updatedName, capturedInputData.name());
        assertEquals(updatedOrgNumber, capturedInputData.organizationNumber());
        assertEquals(updatedCountryCode, capturedInputData.countryCode());
    }
}
