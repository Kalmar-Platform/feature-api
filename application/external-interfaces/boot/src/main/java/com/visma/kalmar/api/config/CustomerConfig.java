package com.visma.kalmar.api.config;

import com.visma.kalmar.api.adapters.context.ContextGatewayAdapter;
import com.visma.kalmar.api.adapters.contexttype.ContextTypeGatewayAdapter;
import com.visma.kalmar.api.adapters.country.CountryGatewayAdapter;
import com.visma.kalmar.api.adapters.customer.CustomerGatewayAdapter;
import com.visma.kalmar.api.context.ContextGateway;
import com.visma.kalmar.api.contexttype.ContextTypeGateway;
import com.visma.kalmar.api.country.CountryGateway;
import com.visma.kalmar.api.customer.CreateCustomerInputPort;
import com.visma.kalmar.api.customer.CreateCustomerUseCase;
import com.visma.kalmar.api.customer.CustomerGateway;
import com.visma.kalmar.api.customer.DeleteCustomerInputPort;
import com.visma.kalmar.api.customer.DeleteCustomerUseCase;
import com.visma.kalmar.api.customer.GetCustomerInputPort;
import com.visma.kalmar.api.customer.GetCustomerUseCase;
import com.visma.kalmar.api.customer.UpdateCustomerInputPort;
import com.visma.kalmar.api.customer.UpdateCustomerUseCase;
import com.visma.feature.kalmar.api.context.ContextRepository;
import com.visma.feature.kalmar.api.contexttype.ContextTypeRepository;
import com.visma.feature.kalmar.api.country.CountryRepository;
import com.visma.feature.kalmar.api.customer.CustomerRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerConfig {

    @Bean
    public ContextTypeGateway contextTypeGateway(ContextTypeRepository contextTypeRepository) {
        return new ContextTypeGatewayAdapter(contextTypeRepository);
    }

    @Bean
    public ContextGateway contextGateway(ContextRepository contextRepository) {
        return new ContextGatewayAdapter(contextRepository);
    }

    @Bean
    public CountryGateway countryGateway(CountryRepository countryRepository) {
        return new CountryGatewayAdapter(countryRepository);
    }

    @Bean
    public CustomerGateway customerGateway(CustomerRepository customerRepository) {
        return new CustomerGatewayAdapter(customerRepository);
    }

    @Bean
    public CreateCustomerInputPort createCustomerInputPort(
            CustomerGateway customerGateway,
            ContextGateway contextGateway,
            ContextTypeGateway contextTypeGateway,
            CountryGateway countryGateway) {
        return new CreateCustomerUseCase(customerGateway, contextGateway, contextTypeGateway, countryGateway);
    }

    @Bean
    public GetCustomerInputPort getCustomerInputPort(
            CustomerGateway customerGateway,
            ContextGateway contextGateway) {
        return new GetCustomerUseCase(customerGateway, contextGateway);
    }

    @Bean
    public UpdateCustomerInputPort updateCustomerInputPort(
            CustomerGateway customerGateway,
            ContextGateway contextGateway,
            CountryGateway countryGateway) {
        return new UpdateCustomerUseCase(customerGateway, contextGateway, countryGateway);
    }

    @Bean
    public DeleteCustomerInputPort deleteCustomerInputPort(CustomerGateway customerGateway) {
        return new DeleteCustomerUseCase(customerGateway);
    }
}
