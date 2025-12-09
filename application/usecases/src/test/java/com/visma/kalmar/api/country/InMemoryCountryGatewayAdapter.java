package com.visma.kalmar.api.country;

import com.visma.kalmar.api.entities.country.Country;
import com.visma.kalmar.api.exception.ResourceNotFoundException;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCountryGatewayAdapter implements CountryGateway {

    private final ConcurrentHashMap<String, Country> countryStoreByCode = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Country> countryStoreById = new ConcurrentHashMap<>();

    @Override
    public Country findByCode(String code) {
        Country country = countryStoreByCode.get(code);
        if (country == null) {
            throw new ResourceNotFoundException("Country", "Country not found with code: " + code);
        }
        return country;
    }

    @Override
    public Country findById(UUID idCountry) {
        Country country = countryStoreById.get(idCountry);
        if (country == null) {
            throw new ResourceNotFoundException("Country", "Country not found with id: " + idCountry);
        }
        return country;
    }

    public void save(Country country) {
        countryStoreByCode.put(country.code(), country);
        countryStoreById.put(country.idCountry(), country);
    }

    public void clear() {
        countryStoreByCode.clear();
        countryStoreById.clear();
    }
}
