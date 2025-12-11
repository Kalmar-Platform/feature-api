package com.visma.kalmar.api.company;

import com.visma.kalmar.api.constants.ContextTypeName;
import com.visma.kalmar.api.context.InMemoryContextGatewayAdapter;
import com.visma.kalmar.api.contexttype.InMemoryContextTypeGatewayAdapter;
import com.visma.kalmar.api.country.InMemoryCountryGatewayAdapter;
import com.visma.kalmar.api.customer.InMemoryCustomerGatewayAdapter;
import com.visma.kalmar.api.entities.company.Company;
import com.visma.kalmar.api.entities.context.Context;
import com.visma.kalmar.api.entities.contexttype.ContextType;
import com.visma.kalmar.api.entities.country.Country;
import com.visma.kalmar.api.exception.InvalidInputDataException;
import com.visma.kalmar.api.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UpdateCompanyUseCaseTest {

    private static final UUID COUNTRY_ID_NO = UUID.randomUUID();
    private static final UUID COUNTRY_ID_SE = UUID.randomUUID();
    private static final UUID COMPANY_CONTEXT_TYPE_ID = UUID.randomUUID();
    private static final UUID CUSTOMER_CONTEXT_TYPE_ID = UUID.randomUUID();
    private static final String COUNTRY_CODE_NO = "NO";
    private static final String COUNTRY_CODE_SE = "SE";
    private static final String UPDATED_NAME = "Updated Company Name";
    private static final String UPDATED_ORG_NUMBER = "111111111";

    private InMemoryCompanyGatewayAdapter companyGateway;
    private InMemoryCustomerGatewayAdapter customerGateway;
    private InMemoryContextGatewayAdapter contextGateway;
    private InMemoryContextTypeGatewayAdapter contextTypeGateway;
    private InMemoryCountryGatewayAdapter countryGateway;
    private UpdateCompanyUseCase updateUseCase;
    private CreateCompanyUseCase createUseCase;
    private TestCompanyOutputPort outputPort;

    @BeforeEach
    void setUp() {
        companyGateway = new InMemoryCompanyGatewayAdapter();
        customerGateway = new InMemoryCustomerGatewayAdapter();
        contextGateway = new InMemoryContextGatewayAdapter();
        contextTypeGateway = new InMemoryContextTypeGatewayAdapter();
        countryGateway = new InMemoryCountryGatewayAdapter();
        outputPort = new TestCompanyOutputPort();

        updateUseCase = new UpdateCompanyUseCase(companyGateway, contextGateway, countryGateway);
        createUseCase = new CreateCompanyUseCase(companyGateway, customerGateway, contextGateway, contextTypeGateway, countryGateway);

        ContextType companyContextType = new ContextType(COMPANY_CONTEXT_TYPE_ID, ContextTypeName.COMPANY.getValue());
        contextTypeGateway.save(companyContextType);

        ContextType customerContextType = new ContextType(CUSTOMER_CONTEXT_TYPE_ID, ContextTypeName.CUSTOMER.getValue());
        contextTypeGateway.save(customerContextType);

        Country norway = new Country(COUNTRY_ID_NO, "Norway", COUNTRY_CODE_NO);
        countryGateway.save(norway);

        Country sweden = new Country(COUNTRY_ID_SE, "Sweden", COUNTRY_CODE_SE);
        countryGateway.save(sweden);
    }

    @Test
    void updateCompany_success() {
        UUID customerId = createCustomer("Customer Corp", "999999999");
        UUID companyId = UUID.randomUUID();
        createCompany(companyId, customerId, "Original Name", "222222222", COUNTRY_CODE_NO);

        var inputData = new UpdateCompanyInputPort.UpdateCompanyInputData(
                companyId,
                UPDATED_NAME,
                UPDATED_ORG_NUMBER,
                COUNTRY_CODE_NO
        );

        updateUseCase.updateCompany(customerId, inputData, outputPort);

        assertNotNull(outputPort.company);
        assertNotNull(outputPort.context);
        assertFalse(outputPort.created);
        assertEquals(companyId, outputPort.context.idContext());
        assertEquals(UPDATED_NAME, outputPort.context.name());
        assertEquals(UPDATED_ORG_NUMBER, outputPort.context.organizationNumber());
        assertEquals(COUNTRY_ID_NO, outputPort.context.idCountry());
    }

    @Test
    void updateCompany_companyNotFound_throwsResourceNotFoundException() {
        UUID customerId = createCustomer("Customer Corp", "999999999");
        UUID nonExistentCompanyId = UUID.randomUUID();

        var inputData = new UpdateCompanyInputPort.UpdateCompanyInputData(
                nonExistentCompanyId,
                UPDATED_NAME,
                UPDATED_ORG_NUMBER,
                COUNTRY_CODE_NO
        );

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> updateUseCase.updateCompany(customerId, inputData, outputPort));

        assertTrue(exception.getMessage().contains("Company not found"));
        assertTrue(exception.getMessage().contains(nonExistentCompanyId.toString()));
    }

    @Test
    void updateCompany_contextNotFound_throwsResourceNotFoundException() {
        UUID customerId = createCustomer("Customer Corp", "999999999");
        UUID companyId = UUID.randomUUID();

        Company company = new Company(companyId);
        companyGateway.save(new Context(companyId, COMPANY_CONTEXT_TYPE_ID, customerId, COUNTRY_ID_NO, "Test", "123"));

        var inputData = new UpdateCompanyInputPort.UpdateCompanyInputData(
                companyId,
                UPDATED_NAME,
                UPDATED_ORG_NUMBER,
                COUNTRY_CODE_NO
        );

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> updateUseCase.updateCompany(customerId, inputData, outputPort));

        assertTrue(exception.getMessage().contains("Context not found"));
    }

    @Test
    void updateCompany_parentContextMismatch_throwsResourceNotFoundException() {
        UUID customerId = createCustomer("Customer Corp", "999999999");
        UUID wrongCustomerId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();
        createCompany(companyId, customerId, "Original Name", "222222222", COUNTRY_CODE_NO);

        var inputData = new UpdateCompanyInputPort.UpdateCompanyInputData(
                companyId,
                UPDATED_NAME,
                UPDATED_ORG_NUMBER,
                COUNTRY_CODE_NO
        );

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> updateUseCase.updateCompany(wrongCustomerId, inputData, outputPort));

        assertTrue(exception.getMessage().contains("Company not found"));
        assertTrue(exception.getMessage().contains(companyId.toString()));
    }

    @Test
    void updateCompany_nullParentContext_throwsResourceNotFoundException() {
        UUID customerId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();
        Context companyContext = new Context(companyId, COMPANY_CONTEXT_TYPE_ID, null, COUNTRY_ID_NO, "Orphan Company", "333333333");

        contextGateway.save(companyContext);
        companyGateway.save(companyContext);

        var inputData = new UpdateCompanyInputPort.UpdateCompanyInputData(
                companyId,
                UPDATED_NAME,
                UPDATED_ORG_NUMBER,
                COUNTRY_CODE_NO
        );

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> updateUseCase.updateCompany(customerId, inputData, outputPort));

        assertTrue(exception.getMessage().contains("Company not found"));
    }

    @Test
    void updateCompany_withDifferentCountryCode() {
        UUID customerId = createCustomer("Customer Corp", "999999999");
        UUID companyId = UUID.randomUUID();
        createCompany(companyId, customerId, "Original Name", "222222222", COUNTRY_CODE_NO);

        var inputData = new UpdateCompanyInputPort.UpdateCompanyInputData(
                companyId,
                UPDATED_NAME,
                UPDATED_ORG_NUMBER,
                COUNTRY_CODE_SE
        );

        updateUseCase.updateCompany(customerId, inputData, outputPort);

        assertNotNull(outputPort.context);
        assertEquals(COUNTRY_ID_SE, outputPort.context.idCountry());
        assertEquals(UPDATED_NAME, outputPort.context.name());
        assertEquals(UPDATED_ORG_NUMBER, outputPort.context.organizationNumber());
    }

    @Test
    void updateCompany_withNullCountryCode_keepsExistingCountry() {
        UUID customerId = createCustomer("Customer Corp", "999999999");
        UUID companyId = UUID.randomUUID();
        createCompany(companyId, customerId, "Original Name", "222222222", COUNTRY_CODE_NO);

        var inputData = new UpdateCompanyInputPort.UpdateCompanyInputData(
                companyId,
                UPDATED_NAME,
                UPDATED_ORG_NUMBER,
                null
        );

        updateUseCase.updateCompany(customerId, inputData, outputPort);

        assertNotNull(outputPort.context);
        assertEquals(COUNTRY_ID_NO, outputPort.context.idCountry());
    }

    @Test
    void updateCompany_withEmptyCountryCode_keepsExistingCountry() {
        UUID customerId = createCustomer("Customer Corp", "999999999");
        UUID companyId = UUID.randomUUID();
        createCompany(companyId, customerId, "Original Name", "222222222", COUNTRY_CODE_NO);

        var inputData = new UpdateCompanyInputPort.UpdateCompanyInputData(
                companyId,
                UPDATED_NAME,
                UPDATED_ORG_NUMBER,
                ""
        );

        updateUseCase.updateCompany(customerId, inputData, outputPort);

        assertNotNull(outputPort.context);
        assertEquals(COUNTRY_ID_NO, outputPort.context.idCountry());
    }

    @Test
    void updateCompany_withNullIdCompany_throwsInvalidInputDataException() {
        UUID customerId = createCustomer("Customer Corp", "999999999");

        var exception = assertThrows(InvalidInputDataException.class,
                () -> new UpdateCompanyInputPort.UpdateCompanyInputData(
                        null,
                        UPDATED_NAME,
                        UPDATED_ORG_NUMBER,
                        COUNTRY_CODE_NO
                ));

        assertTrue(exception.getMessage().contains("idCompany is mandatory"));
    }

    @Test
    void updateCompany_withNullName_throwsInvalidInputDataException() {
        UUID companyId = UUID.randomUUID();

        var exception = assertThrows(InvalidInputDataException.class,
                () -> new UpdateCompanyInputPort.UpdateCompanyInputData(
                        companyId,
                        null,
                        UPDATED_ORG_NUMBER,
                        COUNTRY_CODE_NO
                ));

        assertTrue(exception.getMessage().contains("name is mandatory"));
    }

    @Test
    void updateCompany_withBlankName_throwsInvalidInputDataException() {
        UUID companyId = UUID.randomUUID();

        var exception = assertThrows(InvalidInputDataException.class,
                () -> new UpdateCompanyInputPort.UpdateCompanyInputData(
                        companyId,
                        "   ",
                        UPDATED_ORG_NUMBER,
                        COUNTRY_CODE_NO
                ));

        assertTrue(exception.getMessage().contains("name is mandatory"));
    }

    @Test
    void updateCompany_withNullOrganizationNumber_throwsInvalidInputDataException() {
        UUID companyId = UUID.randomUUID();

        var exception = assertThrows(InvalidInputDataException.class,
                () -> new UpdateCompanyInputPort.UpdateCompanyInputData(
                        companyId,
                        UPDATED_NAME,
                        null,
                        COUNTRY_CODE_NO
                ));

        assertTrue(exception.getMessage().contains("organizationNumber is mandatory"));
    }

    @Test
    void updateCompany_withBlankOrganizationNumber_throwsInvalidInputDataException() {
        UUID companyId = UUID.randomUUID();

        var exception = assertThrows(InvalidInputDataException.class,
                () -> new UpdateCompanyInputPort.UpdateCompanyInputData(
                        companyId,
                        UPDATED_NAME,
                        "   ",
                        COUNTRY_CODE_NO
                ));

        assertTrue(exception.getMessage().contains("organizationNumber is mandatory"));
    }

    private UUID createCustomer(String name, String orgNumber) {
        UUID customerId = UUID.randomUUID();
        Context customerContext = new Context(customerId, CUSTOMER_CONTEXT_TYPE_ID, null, COUNTRY_ID_NO, name, orgNumber);
        customerGateway.save(customerContext);
        contextGateway.save(customerContext);
        return customerId;
    }

    private void createCompany(UUID companyId, UUID customerId, String name, String orgNumber, String countryCode) {
        var inputData = new CreateCompanyInputPort.CreateCompanyInputData(
                companyId, countryCode, customerId, orgNumber, name
        );
        createUseCase.createCompany(inputData, (company, context, created) -> {
            contextGateway.save(context);
        });
    }

    private static class TestCompanyOutputPort implements CompanyOutputPort {
        Company company;
        Context context;
        boolean created;

        @Override
        public void present(Company company, Context context, boolean created) {
            this.company = company;
            this.context = context;
            this.created = created;
        }
    }
}
