package com.visma.kalmar.api.company;

import com.visma.kalmar.api.constants.ContextTypeName;
import com.visma.kalmar.api.context.InMemoryContextGatewayAdapter;
import com.visma.kalmar.api.contexttype.InMemoryContextTypeGatewayAdapter;
import com.visma.kalmar.api.country.InMemoryCountryGatewayAdapter;
import com.visma.kalmar.api.customer.InMemoryCustomerGatewayAdapter;
import com.visma.kalmar.api.entities.context.Context;
import com.visma.kalmar.api.entities.contexttype.ContextType;
import com.visma.kalmar.api.entities.country.Country;
import com.visma.kalmar.api.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DeleteCompanyUseCaseTest {

  private static final UUID COUNTRY_ID = UUID.randomUUID();
  private static final UUID COMPANY_CONTEXT_TYPE_ID = UUID.randomUUID();
  private static final UUID CUSTOMER_CONTEXT_TYPE_ID = UUID.randomUUID();
  private static final String COUNTRY_CODE = "NO";

  private InMemoryCompanyGatewayAdapter companyGateway;
  private InMemoryCustomerGatewayAdapter customerGateway;
  private InMemoryContextGatewayAdapter contextGateway;
  private InMemoryContextTypeGatewayAdapter contextTypeGateway;
  private InMemoryCountryGatewayAdapter countryGateway;
  private DeleteCompanyUseCase deleteUseCase;
  private CreateCompanyUseCase createUseCase;

  @BeforeEach
  void setUp() {
    companyGateway = new InMemoryCompanyGatewayAdapter();
    customerGateway = new InMemoryCustomerGatewayAdapter();
    contextGateway = new InMemoryContextGatewayAdapter();
    contextTypeGateway = new InMemoryContextTypeGatewayAdapter();
    countryGateway = new InMemoryCountryGatewayAdapter();

    deleteUseCase = new DeleteCompanyUseCase(companyGateway, contextGateway);
    createUseCase =
        new CreateCompanyUseCase(
            companyGateway, customerGateway, contextGateway, contextTypeGateway, countryGateway);

    ContextType companyContextType =
        new ContextType(COMPANY_CONTEXT_TYPE_ID, ContextTypeName.COMPANY.getValue());
    contextTypeGateway.save(companyContextType);

    ContextType customerContextType =
        new ContextType(CUSTOMER_CONTEXT_TYPE_ID, ContextTypeName.CUSTOMER.getValue());
    contextTypeGateway.save(customerContextType);

    Country norway = new Country(COUNTRY_ID, "Norway", COUNTRY_CODE);
    countryGateway.save(norway);
  }

  @Test
  void deleteCompany_withValidId_deletesSuccessfully() {
    UUID customerId = createCustomer("Customer Corp", "111111111");
    UUID companyId = UUID.randomUUID();

    var inputData =
        new CreateCompanyInputPort.CreateCompanyInputData(
            companyId, COUNTRY_CODE, customerId, "987654321", "Subsidiary Corp");
    createUseCase.createCompany(
        inputData,
        (company, context, created) -> {
          contextGateway.save(context);
        });

    assertTrue(companyGateway.exists(companyId));

    deleteUseCase.deleteCompany(customerId, companyId);

    assertFalse(companyGateway.exists(companyId));
  }

  @Test
  void deleteCompany_withNonExistentId_throwsResourceNotFoundException() {
    UUID customerId = createCustomer("Customer Corp", "111111111");
    UUID nonExistentCompanyId = UUID.randomUUID();

    var exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> deleteUseCase.deleteCompany(customerId, nonExistentCompanyId));

    assertTrue(exception.getMessage().contains("Company not found"));
    assertTrue(exception.getMessage().contains(nonExistentCompanyId.toString()));
  }

  @Test
  void deleteCompany_multipleCompanies_deletesOnlySpecified() {
    UUID customerId = createCustomer("Customer Corp", "111111111");
    UUID companyId1 = UUID.randomUUID();
    UUID companyId2 = UUID.randomUUID();

    var inputData1 =
        new CreateCompanyInputPort.CreateCompanyInputData(
            companyId1, COUNTRY_CODE, customerId, "987654321", "Subsidiary Corp 1");
    createUseCase.createCompany(
        inputData1,
        (company, context, created) -> {
          contextGateway.save(context);
        });

    var inputData2 =
        new CreateCompanyInputPort.CreateCompanyInputData(
            companyId2, COUNTRY_CODE, customerId, "888888888", "Subsidiary Corp 2");
    createUseCase.createCompany(
        inputData2,
        (company, context, created) -> {
          contextGateway.save(context);
        });

    assertTrue(companyGateway.exists(companyId1));
    assertTrue(companyGateway.exists(companyId2));

    deleteUseCase.deleteCompany(customerId, companyId1);

    assertFalse(companyGateway.exists(companyId1));
    assertTrue(companyGateway.exists(companyId2));
  }

  @Test
  void deleteCompany_alreadyDeleted_throwsResourceNotFoundException() {
    UUID customerId = createCustomer("Customer Corp", "111111111");
    UUID companyId = UUID.randomUUID();

    var inputData =
        new CreateCompanyInputPort.CreateCompanyInputData(
            companyId, COUNTRY_CODE, customerId, "987654321", "Subsidiary Corp");
    createUseCase.createCompany(
        inputData,
        (company, context, created) -> {
          contextGateway.save(context);
        });

    deleteUseCase.deleteCompany(customerId, companyId);

    var exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> deleteUseCase.deleteCompany(customerId, companyId));

    assertTrue(exception.getMessage().contains("Company not found"));
  }

  @Test
  void deleteCompany_parentContextMismatch_throwsResourceNotFoundException() {
    UUID customerId = createCustomer("Customer Corp", "111111111");
    UUID wrongCustomerId = UUID.randomUUID();
    UUID companyId = UUID.randomUUID();

    var inputData =
        new CreateCompanyInputPort.CreateCompanyInputData(
            companyId, COUNTRY_CODE, customerId, "987654321", "Subsidiary Corp");
    createUseCase.createCompany(
        inputData,
        (company, context, created) -> {
          contextGateway.save(context);
        });

    assertTrue(companyGateway.exists(companyId));

    var exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> deleteUseCase.deleteCompany(wrongCustomerId, companyId));

    assertTrue(exception.getMessage().contains("Company not found"));
    assertTrue(exception.getMessage().contains(companyId.toString()));
    assertTrue(companyGateway.exists(companyId));
  }

  @Test
  void deleteCompany_nullParentContext_throwsResourceNotFoundException() {
    UUID companyId = UUID.randomUUID();
    UUID customerId = UUID.randomUUID();
    Context companyContext =
        new Context(
            companyId, COMPANY_CONTEXT_TYPE_ID, null, COUNTRY_ID, "Orphan Company", "999999999");

    contextGateway.save(companyContext);
    companyGateway.save(companyContext);

    assertTrue(companyGateway.exists(companyId));

    var exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> deleteUseCase.deleteCompany(customerId, companyId));

    assertTrue(exception.getMessage().contains("Company not found"));
    assertTrue(companyGateway.exists(companyId));
  }

  @Test
  void deleteCompany_contextNotFound_throwsResourceNotFoundException() {
    UUID customerId = UUID.randomUUID();
    UUID companyId = UUID.randomUUID();
    Context companyContext =
        new Context(
            companyId,
            COMPANY_CONTEXT_TYPE_ID,
            customerId,
            COUNTRY_ID,
            "Company Without Context",
            "777777777");

    companyGateway.save(companyContext);

    assertTrue(companyGateway.exists(companyId));
    assertNull(contextGateway.findById(companyId));

    var exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> deleteUseCase.deleteCompany(customerId, companyId));

    assertTrue(exception.getMessage().contains("Context not found with id"));
    assertTrue(exception.getMessage().contains(companyId.toString()));
    assertTrue(companyGateway.exists(companyId));
  }

  private UUID createCustomer(String name, String orgNumber) {
    UUID customerId = UUID.randomUUID();
    Context customerContext =
        new Context(customerId, CUSTOMER_CONTEXT_TYPE_ID, null, COUNTRY_ID, name, orgNumber);
    customerGateway.save(customerContext);
    contextGateway.save(customerContext);
    return customerId;
  }
}
