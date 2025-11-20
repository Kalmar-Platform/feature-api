package com.visma.kalmar.api.config;

import com.visma.kalmar.api.country.*;
import com.visma.subscription.kalmar.api.country.CountryGatewayAdapter;
import com.visma.useraccess.kalmar.api.country.UserCountryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CountryConfig {
  @Bean
  public CountryGateway countryGateway(
      UserCountryRepository userAccessUserCountryRepository) {
    return new CountryGatewayAdapter(userAccessUserCountryRepository);
  }
}
