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
import com.visma.kalmar.api.exception.ResourceAlreadyExistsException;
import com.visma.kalmar.api.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class CreateCompanyUseCaseTest {

    private static final String COMPANY_NAME = "Subsidiary Corp";
    private static final String ORG_NUMBER = "987654321";
    private static final String COUNTRY_CODE = "NO";
    private static final UUID COUNTRY_ID = UUID.randomUUID();
    private static final UUID COMPANY_CONTEXT_TYPE_ID = UUID.randomUUID();
    private static final UUID CUSTOMER_CONTEXT_TYPE_ID = UUID.randomUUID();

    private InMemoryCompanyGatewayAdapter companyGateway;
    private InMemoryCustomerGatewayAdapter customerGateway;
    private InMemoryContextGatewayAdapter contextGateway;
    private InMemoryContextTypeGatewayAdapter contextTypeGateway;
    private InMemoryCountryGatewayAdapter countryGateway;
    private CreateCompanyUseCase useCase;

    @BeforeEach
    void setUp() {
        companyGateway = new InMemoryCompanyGatewayAdapter();
        customerGateway = new InMemoryCustomerGatewayAdapter();
        contextGateway = new InMemoryContextGatewayAdapter();
        contextTypeGateway = new InMemoryContextTypeGatewayAdapter();
        countryGateway = new InMemoryCountryGatewayAdapter();
        useCase = new CreateCompanyUseCase(companyGateway, customerGateway, contextGateway, contextTypeGateway, countryGateway);

        ContextType companyContextType = new ContextType(COMPANY_CONTEXT_TYPE_ID, ContextTypeName.COMPANY.getValue());
        contextTypeGateway.save(companyContextType);
        
        ContextType customerContextType = new ContextType(CUSTOMER_CONTEXT_TYPE_ID, ContextTypeName.CUSTOMER.getValue());
        contextTypeGateway.save(customerContextType);
        
        Country norway = new Country(COUNTRY_ID, "Norway", COUNTRY_CODE);
        countryGateway.save(norway);
    }

    @Test
    void createCompany_withValidCountryCode_createsCompanySuccessfully() {
        UUID customerId = createCustomer("Customer Corp", "111111111");
        UUID companyId = UUID.randomUUID();
        
        var inputData = new CreateCompanyInputPort.CreateCompanyInputData(
                companyId, COUNTRY_CODE, customerId, ORG_NUMBER, COMPANY_NAME
        );

        final AtomicReference<Company> resultCompany = new AtomicReference<>();
        final AtomicReference<Context> resultContext = new AtomicReference<>();
        final AtomicReference<Boolean> resultCreated = new AtomicReference<>();

        useCase.createCompany(inputData, (company, context, created) -> {
            resultCompany.set(company);
            resultContext.set(context);
            resultCreated.set(created);
        });

        assertNotNull(resultCompany.get());
        assertEquals(companyId, resultCompany.get().idContext());
        assertNotNull(resultContext.get());
        assertEquals(COMPANY_NAME, resultContext.get().name());
        assertEquals(ORG_NUMBER, resultContext.get().organizationNumber());
        assertEquals(COUNTRY_ID, resultContext.get().idCountry());
        assertEquals(COMPANY_CONTEXT_TYPE_ID, resultContext.get().idContextType());
        assertEquals(customerId, resultContext.get().idContextParent());
        assertTrue(resultCreated.get());

        assertTrue(companyGateway.exists(companyId));
    }

    @Test
    void createCompany_withNullCountryCode_usesCustomerCountry() {
        UUID customerId = createCustomer("Customer Corp", "111111111");
        UUID companyId = UUID.randomUUID();
        
        var inputData = new CreateCompanyInputPort.CreateCompanyInputData(
                companyId, null, customerId, ORG_NUMBER, COMPANY_NAME
        );

        final AtomicReference<Context> resultContext = new AtomicReference<>();

        useCase.createCompany(inputData, (company, context, created) -> {
            resultContext.set(context);
        });

        assertNotNull(resultContext.get());
        assertEquals(customerId, resultContext.get().idContextParent());
        assertEquals(COUNTRY_ID, resultContext.get().idCountry());
    }

    @Test
    void createCompany_withNonExistentCustomer_throwsResourceNotFoundException() {
        UUID nonExistentCustomerId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();
        
        var inputData = new CreateCompanyInputPort.CreateCompanyInputData(
                companyId, COUNTRY_CODE, nonExistentCustomerId, ORG_NUMBER, COMPANY_NAME
        );

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> useCase.createCompany(inputData, (company, context, created) -> {
                }));

        assertTrue(exception.getMessage().contains("Parent context is not a valid customer"));
        assertFalse(companyGateway.exists(companyId));
    }

    @Test
    void createCompany_withNullIdCompany_generatesNewId() {
        UUID customerId = createCustomer("Customer Corp", "111111111");
        
        var inputData = new CreateCompanyInputPort.CreateCompanyInputData(
                null, COUNTRY_CODE, customerId, ORG_NUMBER, COMPANY_NAME
        );

        final AtomicReference<Company> resultCompany = new AtomicReference<>();

        useCase.createCompany(inputData, (company, context, created) -> {
            resultCompany.set(company);
        });

        assertNotNull(resultCompany.get());
        assertNotNull(resultCompany.get().idContext());
        assertTrue(companyGateway.exists(resultCompany.get().idContext()));
    }

    @Test
    void createCompany_withInvalidCountryCode_throwsResourceNotFoundException() {
        UUID customerId = createCustomer("Customer Corp", "111111111");
        UUID companyId = UUID.randomUUID();
        
        var inputData = new CreateCompanyInputPort.CreateCompanyInputData(
                companyId, "XX", customerId, ORG_NUMBER, COMPANY_NAME
        );

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> useCase.createCompany(inputData, (company, context, created) -> {}));

        assertTrue(exception.getMessage().contains("Country not found with code"));
    }

    @Test
    void createCompany_withNullOrganizationNumber_throwsInvalidInputDataException() {
        UUID customerId = createCustomer("Customer Corp", "111111111");
        
        var exception = assertThrows(InvalidInputDataException.class,
                () -> new CreateCompanyInputPort.CreateCompanyInputData(
                        UUID.randomUUID(), COUNTRY_CODE, customerId, null, COMPANY_NAME
                ));

        assertTrue(exception.getMessage().contains("organizationNumber is mandatory"));
    }

    @Test
    void createCompany_withBlankOrganizationNumber_throwsInvalidInputDataException() {
        UUID customerId = createCustomer("Customer Corp", "111111111");
        
        var exception = assertThrows(InvalidInputDataException.class,
                () -> new CreateCompanyInputPort.CreateCompanyInputData(
                        UUID.randomUUID(), COUNTRY_CODE, customerId, "  ", COMPANY_NAME
                ));

        assertTrue(exception.getMessage().contains("organizationNumber is mandatory"));
    }

    @Test
    void createCompany_withNullName_throwsInvalidInputDataException() {
        UUID customerId = createCustomer("Customer Corp", "111111111");
        
        var exception = assertThrows(InvalidInputDataException.class,
                () -> new CreateCompanyInputPort.CreateCompanyInputData(
                        UUID.randomUUID(), COUNTRY_CODE, customerId, ORG_NUMBER, null
                ));

        assertTrue(exception.getMessage().contains("name is mandatory"));
    }

    @Test
    void createCompany_withBlankName_throwsInvalidInputDataException() {
        UUID customerId = createCustomer("Customer Corp", "111111111");
        
        var exception = assertThrows(InvalidInputDataException.class,
                () -> new CreateCompanyInputPort.CreateCompanyInputData(
                        UUID.randomUUID(), COUNTRY_CODE, customerId, ORG_NUMBER, "  "
                ));

        assertTrue(exception.getMessage().contains("name is mandatory"));
    }

    @Test
    void createCompany_withNullIdContextParent_throwsInvalidInputDataException() {
        var exception = assertThrows(InvalidInputDataException.class,
                () -> new CreateCompanyInputPort.CreateCompanyInputData(
                        UUID.randomUUID(), COUNTRY_CODE, null, ORG_NUMBER, COMPANY_NAME
                ));

        assertTrue(exception.getMessage().contains("idContextParent is mandatory"));
    }

    @Test
    void createCompany_withDuplicateName_throwsResourceAlreadyExistsException() {
        UUID customerId = createCustomer("Customer Corp", "111111111");
        UUID companyId1 = UUID.randomUUID();
        
        var inputData1 = new CreateCompanyInputPort.CreateCompanyInputData(
                companyId1, COUNTRY_CODE, customerId, ORG_NUMBER, COMPANY_NAME
        );
        useCase.createCompany(inputData1, (company, context, created) -> {});

        UUID companyId2 = UUID.randomUUID();
        var inputData2 = new CreateCompanyInputPort.CreateCompanyInputData(
                companyId2, COUNTRY_CODE, customerId, "222222222", COMPANY_NAME
        );

        var exception = assertThrows(ResourceAlreadyExistsException.class,
                () -> useCase.createCompany(inputData2, (company, context, created) -> {}));

        assertTrue(exception.getMessage().contains("Company with name '" + COMPANY_NAME + "' already exists"));
        assertFalse(companyGateway.exists(companyId2));
    }

    @Test
    void createCompany_withDuplicateOrgNumberAndCountry_throwsResourceAlreadyExistsException() {
        UUID customerId = createCustomer("Customer Corp", "111111111");
        UUID companyId1 = UUID.randomUUID();
        
        var inputData1 = new CreateCompanyInputPort.CreateCompanyInputData(
                companyId1, COUNTRY_CODE, customerId, ORG_NUMBER, COMPANY_NAME
        );
        useCase.createCompany(inputData1, (company, context, created) -> {});

        UUID companyId2 = UUID.randomUUID();
        var inputData2 = new CreateCompanyInputPort.CreateCompanyInputData(
                companyId2, COUNTRY_CODE, customerId, ORG_NUMBER, "Different Company Name"
        );

        var exception = assertThrows(ResourceAlreadyExistsException.class,
                () -> useCase.createCompany(inputData2, (company, context, created) -> {}));

        assertTrue(exception.getMessage().contains("Company with organization number '" + ORG_NUMBER + "'"));
        assertTrue(exception.getMessage().contains("already exists under this customer"));
        assertFalse(companyGateway.exists(companyId2));
    }

    @Test
    void createCompany_withExistingCompanyId_throwsResourceAlreadyExistsException() {
        UUID customerId = createCustomer("Customer Corp", "111111111");
        UUID companyId = UUID.randomUUID();
        
        var inputData1 = new CreateCompanyInputPort.CreateCompanyInputData(
                companyId, COUNTRY_CODE, customerId, ORG_NUMBER, COMPANY_NAME
        );
        useCase.createCompany(inputData1, (company, context, created) -> {});
        
        var inputData2 = new CreateCompanyInputPort.CreateCompanyInputData(
                companyId, COUNTRY_CODE, customerId, "333333333", "Another Name"
        );

        var exception = assertThrows(ResourceAlreadyExistsException.class,
                () -> useCase.createCompany(inputData2, (company, context, created) -> {}));

        assertTrue(exception.getMessage().contains("Company already exists with id: " + companyId));
    }

    @Test
    void createCompany_withNonExistentContextType_throwsResourceNotFoundException() {
        contextTypeGateway.clear();
        UUID customerId = createCustomerWithoutContextType("Customer Corp", "111111111");
        UUID companyId = UUID.randomUUID();
        
        var inputData = new CreateCompanyInputPort.CreateCompanyInputData(
                companyId, COUNTRY_CODE, customerId, ORG_NUMBER, COMPANY_NAME
        );

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> useCase.createCompany(inputData, (company, context, created) -> {}));

        assertTrue(exception.getMessage().contains("ContextType not found"));
        assertFalse(companyGateway.exists(companyId));
    }

    @Test
    void createCompany_withNullCountryCodeAndNonExistentParentContext_throwsResourceNotFoundException() {
        UUID customerId = UUID.randomUUID();
        Context customerContext = new Context(customerId, CUSTOMER_CONTEXT_TYPE_ID, null, COUNTRY_ID, "Customer Corp", "111111111");
        customerGateway.save(customerContext);
        
        UUID companyId = UUID.randomUUID();
        var inputData = new CreateCompanyInputPort.CreateCompanyInputData(
                companyId, null, customerId, ORG_NUMBER, COMPANY_NAME
        );

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> useCase.createCompany(inputData, (company, context, created) -> {}));

        assertTrue(exception.getMessage().contains("Parent context not found with id"));
        assertTrue(exception.getMessage().contains(customerId.toString()));
        assertFalse(companyGateway.exists(companyId));
    }

    private UUID createCustomer(String name, String orgNumber) {
        UUID customerId = UUID.randomUUID();
        Context customerContext = new Context(customerId, CUSTOMER_CONTEXT_TYPE_ID, null, COUNTRY_ID, name, orgNumber);
        customerGateway.save(customerContext);
        contextGateway.save(customerContext);
        return customerId;
    }

    private UUID createCustomerWithoutContextType(String name, String orgNumber) {
        UUID customerId = UUID.randomUUID();
        Context customerContext = new Context(customerId, CUSTOMER_CONTEXT_TYPE_ID, null, COUNTRY_ID, name, orgNumber);
        customerGateway.save(customerContext);
        contextGateway.save(customerContext);
        return customerId;
    }
}
