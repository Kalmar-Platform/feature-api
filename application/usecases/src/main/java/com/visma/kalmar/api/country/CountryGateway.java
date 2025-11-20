package com.visma.kalmar.api.country;

import com.visma.kalmar.api.entities.country.Country;

import java.util.List;
import java.util.UUID;

public interface CountryGateway {
    List<Country> findAll();

    Country findByCode(String countryCode);
    
    Country findById(UUID countryId);
    
    boolean existsByCode(String countryCode);
}
