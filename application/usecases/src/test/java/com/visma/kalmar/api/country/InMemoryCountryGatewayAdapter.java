package com.visma.kalmar.api.country;

import com.visma.kalmar.api.entities.country.Country;
import com.visma.kalmar.api.exception.ResourceNotFoundException;

import java.util.*;

public class InMemoryCountryGatewayAdapter implements CountryGateway {

    private final Map<String, Country> countriesByCode = new HashMap<>();
    private final Map<UUID, Country> countriesById = new HashMap<>();

    @Override
    public List<Country> findAll() {
        return new ArrayList<>(countriesByCode.values());
    }

    @Override
    public Country findByCode(String countryCode) {
        Country country = countriesByCode.get(countryCode);
        if (country == null) {
            throw new ResourceNotFoundException("Country", 
                    "Country not found with code: " + countryCode);
        }
        return country;
    }

    @Override
    public Country findById(UUID countryId) {
        var country = countriesById.get(countryId);
        if (country == null) {
            throw new ResourceNotFoundException("Country",
                    "Country not found with ID: " + countryId);
        }
        return country;
    }

    @Override
    public boolean existsByCode(String countryCode) {
        return countriesByCode.containsKey(countryCode);
    }

    public void save(Country country) {
        countriesByCode.put(country.getCode(), country);
        countriesById.put(country.getIdCountry(), country);
    }

    public void clear() {
        countriesByCode.clear();
        countriesById.clear();
    }
}
