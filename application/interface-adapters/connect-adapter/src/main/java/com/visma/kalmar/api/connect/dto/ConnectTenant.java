package com.visma.kalmar.api.connect.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ConnectTenant(
    @JsonProperty("id") String id,
    @JsonProperty("organization_name") String organizationName,
    @JsonProperty("organization_number") String organizationNumber,
    @JsonProperty("country_code") String countryCode,
    @JsonProperty("business_unit_name") String businessUnitName,
    @JsonProperty("parent_id") String parentId) {}
