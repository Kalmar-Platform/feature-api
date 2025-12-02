package com.visma.kalmar.api.constants;

public enum ContextTypeName {
    CUSTOMER("Customer"),
    COMPANY("Company"),
    COMPANY_GROUP("CompanyGroup");

    private final String value;

    ContextTypeName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
