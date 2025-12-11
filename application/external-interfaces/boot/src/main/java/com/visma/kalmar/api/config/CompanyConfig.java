package com.visma.kalmar.api.config;

import com.visma.kalmar.api.adapters.company.CompanyGatewayAdapter;
import com.visma.kalmar.api.company.CompanyGateway;
import com.visma.kalmar.api.company.CreateCompanyInputPort;
import com.visma.kalmar.api.company.CreateCompanyUseCase;
import com.visma.kalmar.api.company.DeleteCompanyInputPort;
import com.visma.kalmar.api.company.DeleteCompanyUseCase;
import com.visma.kalmar.api.company.GetCompanyInputPort;
import com.visma.kalmar.api.company.GetCompanyUseCase;
import com.visma.kalmar.api.company.UpdateCompanyInputPort;
import com.visma.kalmar.api.company.UpdateCompanyUseCase;
import com.visma.kalmar.api.context.ContextGateway;
import com.visma.kalmar.api.contexttype.ContextTypeGateway;
import com.visma.kalmar.api.country.CountryGateway;
import com.visma.kalmar.api.customer.CustomerGateway;
import com.visma.feature.kalmar.api.company.CompanyRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CompanyConfig {

    @Bean
    public CompanyGateway companyGateway(CompanyRepository companyRepository) {
        return new CompanyGatewayAdapter(companyRepository);
    }

    @Bean
    public CreateCompanyInputPort createCompanyInputPort(
            CompanyGateway companyGateway,
            CustomerGateway customerGateway,
            ContextGateway contextGateway,
            ContextTypeGateway contextTypeGateway,
            CountryGateway countryGateway) {
        return new CreateCompanyUseCase(companyGateway, customerGateway, contextGateway, contextTypeGateway, countryGateway);
    }

    @Bean
    public GetCompanyInputPort getCompanyInputPort(
            CompanyGateway companyGateway,
            ContextGateway contextGateway) {
        return new GetCompanyUseCase(companyGateway, contextGateway);
    }

    @Bean
    public UpdateCompanyInputPort updateCompanyInputPort(
            CompanyGateway companyGateway,
            ContextGateway contextGateway,
            CountryGateway countryGateway) {
        return new UpdateCompanyUseCase(companyGateway, contextGateway, countryGateway);
    }

    @Bean
    public DeleteCompanyInputPort deleteCompanyInputPort(
            CompanyGateway companyGateway,
            ContextGateway contextGateway) {
        return new DeleteCompanyUseCase(companyGateway, contextGateway);
    }
}
