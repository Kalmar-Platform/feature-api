package com.visma.kalmar.api.company;

import com.visma.kalmar.api.country.CountryGateway;
import com.visma.kalmar.api.entities.company.Company;
import com.visma.kalmar.api.entities.context.Context;
import com.visma.kalmar.api.entities.country.Country;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompanyPresenterTest {

    private static final UUID COMPANY_ID = UUID.randomUUID();
    private static final UUID CONTEXT_TYPE_ID = UUID.randomUUID();
    private static final UUID PARENT_CONTEXT_ID = UUID.randomUUID();
    private static final UUID COUNTRY_ID = UUID.randomUUID();
    private static final String COUNTRY_CODE = "NO";
    private static final String COMPANY_NAME = "Acme Company";
    private static final String ORG_NUMBER = "987654321";

    private CompanyPresenter companyPresenter;
    private CountryGateway countryGateway;

    @BeforeEach
    void setUp() {
        countryGateway = mock(CountryGateway.class);
        companyPresenter = new CompanyPresenter(countryGateway);
        
        Country norway = new Country(COUNTRY_ID, "Norway", COUNTRY_CODE);
        when(countryGateway.findById(COUNTRY_ID)).thenReturn(norway);
    }

    @Test
    void present_withCreatedTrue_returnsHttpCreated() {
        Company company = new Company(COMPANY_ID);
        Context context = new Context(
                COMPANY_ID,
                CONTEXT_TYPE_ID,
                PARENT_CONTEXT_ID,
                COUNTRY_ID,
                COMPANY_NAME,
                ORG_NUMBER
        );

        companyPresenter.present(company, context, true);
        ResponseEntity<CompanyResponse> response = companyPresenter.getResponse();

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(COMPANY_ID, response.getBody().idContext());
        assertEquals(COMPANY_NAME, response.getBody().name());
        assertEquals(ORG_NUMBER, response.getBody().organizationNumber());
        assertEquals(COUNTRY_CODE, response.getBody().countryCode());
        assertEquals(PARENT_CONTEXT_ID, response.getBody().idContextParent());
    }

    @Test
    void present_withCreatedFalse_returnsHttpOk() {
        Company company = new Company(COMPANY_ID);
        Context context = new Context(
                COMPANY_ID,
                CONTEXT_TYPE_ID,
                PARENT_CONTEXT_ID,
                COUNTRY_ID,
                COMPANY_NAME,
                ORG_NUMBER
        );

        companyPresenter.present(company, context, false);
        ResponseEntity<CompanyResponse> response = companyPresenter.getResponse();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(COMPANY_ID, response.getBody().idContext());
    }

    @Test
    void present_withNullParentContext_mapsFieldsCorrectly() {
        Company company = new Company(COMPANY_ID);
        Context context = new Context(
                COMPANY_ID,
                CONTEXT_TYPE_ID,
                PARENT_CONTEXT_ID,
                COUNTRY_ID,
                COMPANY_NAME,
                ORG_NUMBER
        );

        companyPresenter.present(company, context, true);
        ResponseEntity<CompanyResponse> response = companyPresenter.getResponse();

        CompanyResponse companyResponse = response.getBody();
        assertNotNull(companyResponse);
        assertEquals(COMPANY_ID, companyResponse.idContext());
        assertEquals(PARENT_CONTEXT_ID, companyResponse.idContextParent());
        assertEquals(COUNTRY_CODE, companyResponse.countryCode());
        assertEquals(COMPANY_NAME, companyResponse.name());
        assertEquals(ORG_NUMBER, companyResponse.organizationNumber());
    }

    @Test
    void present_mapsAllFieldsCorrectly() {
        Company company = new Company(COMPANY_ID);
        Context context = new Context(
                COMPANY_ID,
                CONTEXT_TYPE_ID,
                PARENT_CONTEXT_ID,
                COUNTRY_ID,
                COMPANY_NAME,
                ORG_NUMBER
        );

        companyPresenter.present(company, context, false);
        ResponseEntity<CompanyResponse> response = companyPresenter.getResponse();

        CompanyResponse companyResponse = response.getBody();
        assertNotNull(companyResponse);
        assertEquals(context.idContext(), companyResponse.idContext());
        assertEquals(context.idContextParent(), companyResponse.idContextParent());
        assertEquals(COUNTRY_CODE, companyResponse.countryCode());
        assertEquals(context.name(), companyResponse.name());
        assertEquals(context.organizationNumber(), companyResponse.organizationNumber());
    }

    @Test
    void present_multipleCalls_overridesPreviousResponse() {
        Company company1 = new Company(COMPANY_ID);
        Context context1 = new Context(
                COMPANY_ID,
                CONTEXT_TYPE_ID,
                PARENT_CONTEXT_ID,
                COUNTRY_ID,
                COMPANY_NAME,
                ORG_NUMBER
        );

        UUID company2Id = UUID.randomUUID();
        Company company2 = new Company(company2Id);
        Context context2 = new Context(
                company2Id,
                CONTEXT_TYPE_ID,
                PARENT_CONTEXT_ID,
                COUNTRY_ID,
                "Different Company",
                "111222333"
        );

        companyPresenter.present(company1, context1, true);
        companyPresenter.present(company2, context2, false);

        ResponseEntity<CompanyResponse> response = companyPresenter.getResponse();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(company2Id, response.getBody().idContext());
        assertEquals("Different Company", response.getBody().name());
        assertEquals("111222333", response.getBody().organizationNumber());
    }

    @Test
    void present_usesCompanyIdForResponse() {
        Company company = new Company(COMPANY_ID);
        Context context = new Context(
                COMPANY_ID,
                CONTEXT_TYPE_ID,
                PARENT_CONTEXT_ID,
                COUNTRY_ID,
                COMPANY_NAME,
                ORG_NUMBER
        );

        companyPresenter.present(company, context, true);
        ResponseEntity<CompanyResponse> response = companyPresenter.getResponse();

        assertEquals(COMPANY_ID, response.getBody().idContext());
        assertEquals(company.idContext(), response.getBody().idContext());
    }

    @Test
    void present_responseBodyContainsAllRequiredFields() {
        Company company = new Company(COMPANY_ID);
        Context context = new Context(
                COMPANY_ID,
                CONTEXT_TYPE_ID,
                PARENT_CONTEXT_ID,
                COUNTRY_ID,
                COMPANY_NAME,
                ORG_NUMBER
        );

        companyPresenter.present(company, context, true);
        ResponseEntity<CompanyResponse> response = companyPresenter.getResponse();

        CompanyResponse body = response.getBody();
        assertNotNull(body);
        assertNotNull(body.idContext());
        assertNotNull(body.name());
        assertNotNull(body.organizationNumber());
        assertNotNull(body.countryCode());
        assertNotNull(body.idContextParent());
    }

    @Test
    void getResponse_calledMultipleTimes_returnsSameInstance() {
        Company company = new Company(COMPANY_ID);
        Context context = new Context(
                COMPANY_ID,
                CONTEXT_TYPE_ID,
                PARENT_CONTEXT_ID,
                COUNTRY_ID,
                COMPANY_NAME,
                ORG_NUMBER
        );

        companyPresenter.present(company, context, true);
        ResponseEntity<CompanyResponse> response1 = companyPresenter.getResponse();
        ResponseEntity<CompanyResponse> response2 = companyPresenter.getResponse();

        assertSame(response1, response2);
    }

    @Test
    void present_withEmptyStrings_preservesEmptyValues() {
        Company company = new Company(COMPANY_ID);
        Context context = new Context(
                COMPANY_ID,
                CONTEXT_TYPE_ID,
                PARENT_CONTEXT_ID,
                COUNTRY_ID,
                "",
                ""
        );

        companyPresenter.present(company, context, true);
        ResponseEntity<CompanyResponse> response = companyPresenter.getResponse();

        CompanyResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("", body.name());
        assertEquals("", body.organizationNumber());
    }

    @Test
    void present_createdStatus_matchesCreatedFlag() {
        Company company = new Company(COMPANY_ID);
        Context context = new Context(
                COMPANY_ID,
                CONTEXT_TYPE_ID,
                PARENT_CONTEXT_ID,
                COUNTRY_ID,
                COMPANY_NAME,
                ORG_NUMBER
        );

        companyPresenter.present(company, context, true);
        ResponseEntity<CompanyResponse> createdResponse = companyPresenter.getResponse();
        assertEquals(HttpStatus.CREATED, createdResponse.getStatusCode());

        companyPresenter.present(company, context, false);
        ResponseEntity<CompanyResponse> okResponse = companyPresenter.getResponse();
        assertEquals(HttpStatus.OK, okResponse.getStatusCode());
    }

    @Test
    void present_resolvesCountryCodeCorrectly() {
        Company company = new Company(COMPANY_ID);
        Context context = new Context(
                COMPANY_ID,
                CONTEXT_TYPE_ID,
                PARENT_CONTEXT_ID,
                COUNTRY_ID,
                COMPANY_NAME,
                ORG_NUMBER
        );

        companyPresenter.present(company, context, true);
        ResponseEntity<CompanyResponse> response = companyPresenter.getResponse();

        assertEquals(COUNTRY_CODE, response.getBody().countryCode());
        verify(countryGateway, times(1)).findById(COUNTRY_ID);
    }

    @Test
    void present_withoutCreatedParameter_returnsHttpOk() {
        Company company = new Company(COMPANY_ID);
        Context context = new Context(
                COMPANY_ID,
                CONTEXT_TYPE_ID,
                PARENT_CONTEXT_ID,
                COUNTRY_ID,
                COMPANY_NAME,
                ORG_NUMBER
        );

        companyPresenter.present(company, context);
        ResponseEntity<CompanyResponse> response = companyPresenter.getResponse();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(COMPANY_ID, response.getBody().idContext());
        assertEquals(COMPANY_NAME, response.getBody().name());
    }

    @Test
    void present_asGetCompanyOutputPort_returnsHttpOk() {
        GetCompanyOutputPort outputPort = companyPresenter;
        Company company = new Company(COMPANY_ID);
        Context context = new Context(
                COMPANY_ID,
                CONTEXT_TYPE_ID,
                PARENT_CONTEXT_ID,
                COUNTRY_ID,
                COMPANY_NAME,
                ORG_NUMBER
        );

        outputPort.present(company, context);
        ResponseEntity<CompanyResponse> response = companyPresenter.getResponse();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(COMPANY_ID, response.getBody().idContext());
        assertEquals(COMPANY_NAME, response.getBody().name());
        assertEquals(ORG_NUMBER, response.getBody().organizationNumber());
        assertEquals(COUNTRY_CODE, response.getBody().countryCode());
    }
}
