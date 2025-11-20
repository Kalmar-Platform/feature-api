package com.visma.subscription.kalmar.api.country;

import com.visma.kalmar.api.country.CountryGateway;
import com.visma.kalmar.api.exception.ResourceNotFoundException;
import com.visma.useraccess.kalmar.api.country.Country;
import com.visma.useraccess.kalmar.api.country.UserCountryRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


public class CountryGatewayAdapter implements CountryGateway {

    private final UserCountryRepository userAccessUserCountryRepository;

    public CountryGatewayAdapter(UserCountryRepository userAccessUserCountryRepository) {
        this.userAccessUserCountryRepository = userAccessUserCountryRepository;
    }
    
    @Override
    public List<com.visma.kalmar.api.entities.country.Country> findAll() {
        return userAccessUserCountryRepository.findAll()
                .stream()
                .map(this::toDomainCountry)
                .collect(Collectors.toList());
    }

    @Override
    public com.visma.kalmar.api.entities.country.Country findByCode(String countryCode) {
        return userAccessUserCountryRepository.findByCode(countryCode)
                .map(this::toDomainCountry)
                .orElseThrow(() -> new ResourceNotFoundException("Country", 
                        "Country not found with code: " + countryCode));
    }

    @Override
    public com.visma.kalmar.api.entities.country.Country findById(UUID countryId) {
        return userAccessUserCountryRepository.findById(countryId)
                .map(this::toDomainCountry)
                .orElseThrow(() -> new ResourceNotFoundException("Country", 
                        "Country not found with id: " + countryId));
    }

    @Override
    public boolean existsByCode(String countryCode) {
        return userAccessUserCountryRepository.existsByCode(countryCode);
    }

    private com.visma.kalmar.api.entities.country.Country toDomainCountry(Country userAccessCountry) {
        return new com.visma.kalmar.api.entities.country.Country(
                userAccessCountry.getIdCountry(),
                userAccessCountry.getName(),
                userAccessCountry.getCode()
        );
    }
}
