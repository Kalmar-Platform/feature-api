package com.visma.kalmar.api.adapters.country;

import com.visma.kalmar.api.country.CountryGateway;
import com.visma.kalmar.api.entities.country.Country;
import com.visma.kalmar.api.exception.ResourceNotFoundException;
import com.visma.feature.kalmar.api.country.CountryRepository;

import java.util.UUID;

public class CountryGatewayAdapter implements CountryGateway {

    private final CountryRepository countryRepository;

    public CountryGatewayAdapter(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public Country findByCode(String code) {
        var jpaEntity = countryRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Country", "Country not found with code: " + code));
        
        return toDomainEntity(jpaEntity);
    }

    @Override
    public Country findById(UUID idCountry) {
        var jpaEntity = countryRepository.findById(idCountry)
                .orElseThrow(() -> new ResourceNotFoundException("Country", "Country not found with id: " + idCountry));
        
        return toDomainEntity(jpaEntity);
    }

    private Country toDomainEntity(com.visma.feature.kalmar.api.country.Country jpaEntity) {
        return new Country(
                jpaEntity.getIdCountry(),
                jpaEntity.getName(),
                jpaEntity.getCode()
        );
    }
}
