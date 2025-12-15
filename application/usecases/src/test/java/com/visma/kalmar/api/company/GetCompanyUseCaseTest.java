package com.visma.kalmar.api.company;

import com.visma.kalmar.api.context.ContextGateway;
import com.visma.kalmar.api.entities.company.Company;
import com.visma.kalmar.api.entities.context.Context;
import com.visma.kalmar.api.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GetCompanyUseCaseTest {

    private static final UUID COMPANY_ID = UUID.randomUUID();
    private static final UUID CONTEXT_TYPE_ID = UUID.randomUUID();
    private static final UUID PARENT_CONTEXT_ID = UUID.randomUUID();
    private static final UUID COUNTRY_ID = UUID.randomUUID();
    private static final String COMPANY_NAME = "Test Company";
    private static final String ORG_NUMBER = "123456789";

    private InMemoryCompanyGatewayAdapter companyGateway;
    private InMemoryContextGatewayAdapter contextGateway;
    private TestGetCompanyOutputPort outputPort;
    private GetCompanyUseCase getCompanyUseCase;

    @BeforeEach
    void setUp() {
        companyGateway = new InMemoryCompanyGatewayAdapter();
        contextGateway = new InMemoryContextGatewayAdapter();
        outputPort = new TestGetCompanyOutputPort();
        getCompanyUseCase = new GetCompanyUseCase(companyGateway, contextGateway);
    }

    @Test
    void getCompany_success() {
        Context context = createContext();
        contextGateway.addContext(context);
        companyGateway.save(context);

        getCompanyUseCase.getCompany(PARENT_CONTEXT_ID, COMPANY_ID, outputPort);

        assertNotNull(outputPort.company);
        assertNotNull(outputPort.context);
        assertEquals(COMPANY_ID, outputPort.company.idContext());
        assertEquals(COMPANY_ID, outputPort.context.idContext());
        assertEquals(COMPANY_NAME, outputPort.context.name());
        assertEquals(ORG_NUMBER, outputPort.context.organizationNumber());
    }

    @Test
    void getCompany_companyNotFound_throwsResourceNotFoundException() {
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> getCompanyUseCase.getCompany(PARENT_CONTEXT_ID, COMPANY_ID, outputPort)
        );

        assertTrue(exception.getMessage().contains("Company not found"));
        assertTrue(exception.getMessage().contains(COMPANY_ID.toString()));
    }

    @Test
    void getCompany_contextNotFound_throwsResourceNotFoundException() {
        Context context = createContext();
        companyGateway.save(context);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> getCompanyUseCase.getCompany(PARENT_CONTEXT_ID, COMPANY_ID, outputPort)
        );

        assertTrue(exception.getMessage().contains("Context not found"));
        assertTrue(exception.getMessage().contains(COMPANY_ID.toString()));
    }

    @Test
    void getCompany_multipleCompanies_returnsCorrectOne() {
        UUID company1Id = UUID.randomUUID();
        UUID company2Id = UUID.randomUUID();

        Context context1 = new Context(company1Id, CONTEXT_TYPE_ID, PARENT_CONTEXT_ID, COUNTRY_ID, "Company 1", "111111111");
        Context context2 = new Context(company2Id, CONTEXT_TYPE_ID, PARENT_CONTEXT_ID, COUNTRY_ID, "Company 2", "222222222");

        contextGateway.addContext(context1);
        contextGateway.addContext(context2);
        companyGateway.save(context1);
        companyGateway.save(context2);

        getCompanyUseCase.getCompany(PARENT_CONTEXT_ID, company2Id, outputPort);

        assertEquals(company2Id, outputPort.company.idContext());
        assertEquals("Company 2", outputPort.context.name());
        assertEquals("222222222", outputPort.context.organizationNumber());
    }

    @Test
    void getCompany_withDifferentCountryIds() {
        UUID norwayId = UUID.randomUUID();
        Context context = new Context(COMPANY_ID, CONTEXT_TYPE_ID, PARENT_CONTEXT_ID, norwayId, COMPANY_NAME, ORG_NUMBER);

        contextGateway.addContext(context);
        companyGateway.save(context);

        getCompanyUseCase.getCompany(PARENT_CONTEXT_ID, COMPANY_ID, outputPort);

        assertEquals(norwayId, outputPort.context.idCountry());
    }

    @Test
    void getCompany_parentContextMismatch_throwsResourceNotFoundException() {
        UUID wrongCustomerId = UUID.randomUUID();
        Context context = createContext();
        contextGateway.addContext(context);
        companyGateway.save(context);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> getCompanyUseCase.getCompany(wrongCustomerId, COMPANY_ID, outputPort)
        );

        assertTrue(exception.getMessage().contains("Company not found"));
        assertTrue(exception.getMessage().contains(COMPANY_ID.toString()));
    }

    @Test
    void getCompany_nullParentContext_throwsResourceNotFoundException() {
        Context context = new Context(COMPANY_ID, CONTEXT_TYPE_ID, null, COUNTRY_ID, COMPANY_NAME, ORG_NUMBER);
        contextGateway.addContext(context);
        companyGateway.save(context);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> getCompanyUseCase.getCompany(PARENT_CONTEXT_ID, COMPANY_ID, outputPort)
        );

        assertTrue(exception.getMessage().contains("Company not found"));
        assertTrue(exception.getMessage().contains(COMPANY_ID.toString()));
    }

    private Context createContext() {
        return new Context(COMPANY_ID, CONTEXT_TYPE_ID, PARENT_CONTEXT_ID, COUNTRY_ID, COMPANY_NAME, ORG_NUMBER);
    }

    private static class TestGetCompanyOutputPort implements GetCompanyOutputPort {
        Company company;
        Context context;

        @Override
        public void present(Company company, Context context) {
            this.company = company;
            this.context = context;
        }
    }

    private static class InMemoryContextGatewayAdapter implements ContextGateway {
        private final java.util.Map<UUID, Context> store = new java.util.HashMap<>();

        public void addContext(Context context) {
            store.put(context.idContext(), context);
        }

        @Override
        public Context findById(UUID idContext) {
            return store.get(idContext);
        }

        @Override
        public boolean existsById(UUID idContext) {
            return store.containsKey(idContext);
        }
    }
}
