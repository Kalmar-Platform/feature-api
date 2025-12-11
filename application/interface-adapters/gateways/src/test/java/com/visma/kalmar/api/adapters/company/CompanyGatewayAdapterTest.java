package com.visma.kalmar.api.adapters.company;

import com.visma.kalmar.api.entities.company.Company;
import com.visma.kalmar.api.entities.context.Context;
import com.visma.kalmar.api.exception.ResourceNotFoundException;
import com.visma.feature.kalmar.api.company.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CompanyGatewayAdapterTest {

    private static final UUID COMPANY_ID = UUID.randomUUID();
    private static final UUID CONTEXT_TYPE_ID = UUID.randomUUID();
    private static final UUID PARENT_CONTEXT_ID = UUID.randomUUID();
    private static final UUID COUNTRY_ID = UUID.randomUUID();
    private static final String COMPANY_NAME = "Acme Company";
    private static final String ORG_NUMBER = "987654321";

    @Mock
    private CompanyRepository companyRepository;

    private CompanyGatewayAdapter companyGatewayAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        companyGatewayAdapter = new CompanyGatewayAdapter(companyRepository);
    }

    @Test
    void save_withValidContext_success() {
        Context domainContext = createDomainContext();
        com.visma.feature.kalmar.api.company.Company jpaCompany = createJpaCompany();

        when(companyRepository.save(any(com.visma.feature.kalmar.api.company.Company.class)))
                .thenReturn(jpaCompany);

        Company result = companyGatewayAdapter.save(domainContext);

        assertNotNull(result);
        assertEquals(COMPANY_ID, result.idContext());

        ArgumentCaptor<com.visma.feature.kalmar.api.company.Company> captor =
                ArgumentCaptor.forClass(com.visma.feature.kalmar.api.company.Company.class);
        verify(companyRepository, times(1)).save(captor.capture());

        com.visma.feature.kalmar.api.company.Company savedEntity = captor.getValue();
        assertEquals(COMPANY_ID, savedEntity.getIdContext());
        assertEquals(COMPANY_NAME, savedEntity.getName());
        assertEquals(ORG_NUMBER, savedEntity.getOrganizationNumber());
        assertEquals(COUNTRY_ID, savedEntity.getIdCountry());
        assertEquals(CONTEXT_TYPE_ID, savedEntity.getIdContextType());
        assertEquals(PARENT_CONTEXT_ID, savedEntity.getIdContextParent());
    }

    @Test
    void save_withNullParentContext_success() {
        Context domainContext = createDomainContextWithoutParent();
        com.visma.feature.kalmar.api.company.Company jpaCompany = createJpaCompanyWithoutParent();

        when(companyRepository.save(any(com.visma.feature.kalmar.api.company.Company.class)))
                .thenReturn(jpaCompany);

        Company result = companyGatewayAdapter.save(domainContext);

        assertNotNull(result);
        assertEquals(COMPANY_ID, result.idContext());

        ArgumentCaptor<com.visma.feature.kalmar.api.company.Company> captor =
                ArgumentCaptor.forClass(com.visma.feature.kalmar.api.company.Company.class);
        verify(companyRepository, times(1)).save(captor.capture());

        com.visma.feature.kalmar.api.company.Company savedEntity = captor.getValue();
        assertNull(savedEntity.getIdContextParent());
    }

    @Test
    void findById_companyExists_success() {
        com.visma.feature.kalmar.api.company.Company jpaCompany = createJpaCompany();
        when(companyRepository.findById(COMPANY_ID)).thenReturn(Optional.of(jpaCompany));

        Company result = companyGatewayAdapter.findById(COMPANY_ID);

        assertNotNull(result);
        assertEquals(COMPANY_ID, result.idContext());
        verify(companyRepository, times(1)).findById(COMPANY_ID);
    }

    @Test
    void findById_companyNotFound_returnsNull() {
        when(companyRepository.findById(COMPANY_ID)).thenReturn(Optional.empty());

        Company result = companyGatewayAdapter.findById(COMPANY_ID);

        assertNull(result);
        verify(companyRepository, times(1)).findById(COMPANY_ID);
    }

    @Test
    void deleteById_companyExists_success() {
        when(companyRepository.existsById(COMPANY_ID)).thenReturn(true);

        companyGatewayAdapter.deleteById(COMPANY_ID);

        verify(companyRepository, times(1)).existsById(COMPANY_ID);
        verify(companyRepository, times(1)).deleteById(COMPANY_ID);
    }

    @Test
    void deleteById_companyNotFound_throwsResourceNotFoundException() {
        when(companyRepository.existsById(COMPANY_ID)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> companyGatewayAdapter.deleteById(COMPANY_ID));

        assertTrue(exception.getMessage().contains("Company not found"));
        assertTrue(exception.getMessage().contains(COMPANY_ID.toString()));
        verify(companyRepository, times(1)).existsById(COMPANY_ID);
        verify(companyRepository, never()).deleteById(any());
    }

    @Test
    void existsByNameAndParent_returnsTrue() {
        when(companyRepository.existsByNameAndIdContextParent(COMPANY_NAME, PARENT_CONTEXT_ID)).thenReturn(true);

        boolean result = companyGatewayAdapter.existsByNameAndParent(COMPANY_NAME, PARENT_CONTEXT_ID);

        assertTrue(result);
        verify(companyRepository, times(1)).existsByNameAndIdContextParent(COMPANY_NAME, PARENT_CONTEXT_ID);
    }

    @Test
    void existsByNameAndParent_returnsFalse() {
        when(companyRepository.existsByNameAndIdContextParent(COMPANY_NAME, PARENT_CONTEXT_ID)).thenReturn(false);

        boolean result = companyGatewayAdapter.existsByNameAndParent(COMPANY_NAME, PARENT_CONTEXT_ID);

        assertFalse(result);
        verify(companyRepository, times(1)).existsByNameAndIdContextParent(COMPANY_NAME, PARENT_CONTEXT_ID);
    }

    @Test
    void existsByOrganizationNumberAndCountryAndParent_returnsTrue() {
        when(companyRepository.existsByOrganizationNumberAndIdCountryAndIdContextParent(ORG_NUMBER, COUNTRY_ID, PARENT_CONTEXT_ID))
                .thenReturn(true);

        boolean result = companyGatewayAdapter.existsByOrganizationNumberAndCountryAndParent(
                ORG_NUMBER, COUNTRY_ID, PARENT_CONTEXT_ID);

        assertTrue(result);
        verify(companyRepository, times(1))
                .existsByOrganizationNumberAndIdCountryAndIdContextParent(ORG_NUMBER, COUNTRY_ID, PARENT_CONTEXT_ID);
    }

    @Test
    void existsByOrganizationNumberAndCountryAndParent_returnsFalse() {
        when(companyRepository.existsByOrganizationNumberAndIdCountryAndIdContextParent(ORG_NUMBER, COUNTRY_ID, PARENT_CONTEXT_ID))
                .thenReturn(false);

        boolean result = companyGatewayAdapter.existsByOrganizationNumberAndCountryAndParent(
                ORG_NUMBER, COUNTRY_ID, PARENT_CONTEXT_ID);

        assertFalse(result);
        verify(companyRepository, times(1))
                .existsByOrganizationNumberAndIdCountryAndIdContextParent(ORG_NUMBER, COUNTRY_ID, PARENT_CONTEXT_ID);
    }

    @Test
    void toJpaEntity_mapsAllFieldsCorrectly() {
        Context domainContext = createDomainContext();

        when(companyRepository.save(any(com.visma.feature.kalmar.api.company.Company.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        companyGatewayAdapter.save(domainContext);

        ArgumentCaptor<com.visma.feature.kalmar.api.company.Company> captor =
                ArgumentCaptor.forClass(com.visma.feature.kalmar.api.company.Company.class);
        verify(companyRepository).save(captor.capture());

        com.visma.feature.kalmar.api.company.Company jpaEntity = captor.getValue();
        assertEquals(COMPANY_ID, jpaEntity.getIdContext());
        assertEquals(COMPANY_NAME, jpaEntity.getName());
        assertEquals(ORG_NUMBER, jpaEntity.getOrganizationNumber());
        assertEquals(COUNTRY_ID, jpaEntity.getIdCountry());
        assertEquals(CONTEXT_TYPE_ID, jpaEntity.getIdContextType());
        assertEquals(PARENT_CONTEXT_ID, jpaEntity.getIdContextParent());
    }

    @Test
    void toDomainEntity_returnsCompanyWithCorrectId() {
        com.visma.feature.kalmar.api.company.Company jpaCompany = createJpaCompany();
        when(companyRepository.findById(COMPANY_ID)).thenReturn(Optional.of(jpaCompany));

        Company result = companyGatewayAdapter.findById(COMPANY_ID);

        assertNotNull(result);
        assertEquals(COMPANY_ID, result.idContext());
    }

    @Test
    void save_multipleCompanies_success() {
        Context domainContext1 = createDomainContext();
        Context domainContext2 = createDomainContextWithDifferentId();

        com.visma.feature.kalmar.api.company.Company jpaCompany1 = createJpaCompany();
        com.visma.feature.kalmar.api.company.Company jpaCompany2 = createJpaCompanyWithDifferentId();

        when(companyRepository.save(any(com.visma.feature.kalmar.api.company.Company.class)))
                .thenReturn(jpaCompany1, jpaCompany2);

        Company result1 = companyGatewayAdapter.save(domainContext1);
        Company result2 = companyGatewayAdapter.save(domainContext2);

        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(COMPANY_ID, result1.idContext());
        assertNotEquals(result1.idContext(), result2.idContext());
        verify(companyRepository, times(2)).save(any(com.visma.feature.kalmar.api.company.Company.class));
    }

    private Context createDomainContext() {
        return new Context(
                COMPANY_ID,
                CONTEXT_TYPE_ID,
                PARENT_CONTEXT_ID,
                COUNTRY_ID,
                COMPANY_NAME,
                ORG_NUMBER
        );
    }

    private Context createDomainContextWithoutParent() {
        return new Context(
                COMPANY_ID,
                CONTEXT_TYPE_ID,
                null,
                COUNTRY_ID,
                COMPANY_NAME,
                ORG_NUMBER
        );
    }

    private Context createDomainContextWithDifferentId() {
        return new Context(
                UUID.randomUUID(),
                CONTEXT_TYPE_ID,
                PARENT_CONTEXT_ID,
                COUNTRY_ID,
                "Different Company",
                "111222333"
        );
    }

    private com.visma.feature.kalmar.api.company.Company createJpaCompany() {
        com.visma.feature.kalmar.api.company.Company company = new com.visma.feature.kalmar.api.company.Company();
        company.setIdContext(COMPANY_ID);
        company.setIdContextType(CONTEXT_TYPE_ID);
        company.setIdContextParent(PARENT_CONTEXT_ID);
        company.setIdCountry(COUNTRY_ID);
        company.setName(COMPANY_NAME);
        company.setOrganizationNumber(ORG_NUMBER);
        return company;
    }

    private com.visma.feature.kalmar.api.company.Company createJpaCompanyWithoutParent() {
        com.visma.feature.kalmar.api.company.Company company = new com.visma.feature.kalmar.api.company.Company();
        company.setIdContext(COMPANY_ID);
        company.setIdContextType(CONTEXT_TYPE_ID);
        company.setIdContextParent(null);
        company.setIdCountry(COUNTRY_ID);
        company.setName(COMPANY_NAME);
        company.setOrganizationNumber(ORG_NUMBER);
        return company;
    }

    private com.visma.feature.kalmar.api.company.Company createJpaCompanyWithDifferentId() {
        com.visma.feature.kalmar.api.company.Company company = new com.visma.feature.kalmar.api.company.Company();
        UUID differentId = UUID.randomUUID();
        company.setIdContext(differentId);
        company.setIdContextType(CONTEXT_TYPE_ID);
        company.setIdContextParent(PARENT_CONTEXT_ID);
        company.setIdCountry(COUNTRY_ID);
        company.setName("Different Company");
        company.setOrganizationNumber("111222333");
        return company;
    }
}
