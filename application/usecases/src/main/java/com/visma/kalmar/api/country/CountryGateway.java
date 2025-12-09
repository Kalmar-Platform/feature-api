package com.visma.kalmar.api.country;

import com.visma.kalmar.api.entities.country.Country;

import java.util.UUID;

public interface CountryGateway {
    Country findByCode(String code);
    Country findById(UUID idCountry);
}
