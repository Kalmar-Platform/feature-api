package com.visma.kalmar.api.company;

import com.visma.kalmar.api.exception.InvalidInputDataException;

import java.util.UUID;

public interface UpdateCompanyInputPort {

    void updateCompany(UUID idCustomer, UpdateCompanyInputData inputData, CompanyOutputPort outputPort);

    record UpdateCompanyInputData(
            UUID idCompany,
            String name,
            String organizationNumber,
            String countryCode
    ) {
        public UpdateCompanyInputData {
            if (idCompany == null) {
                throw new InvalidInputDataException("Company", "idCompany is mandatory for update operation");
            }
            if (name == null || name.isBlank()) {
                throw new InvalidInputDataException("Company", "name is mandatory");
            }
            if (organizationNumber == null || organizationNumber.isBlank()) {
                throw new InvalidInputDataException("Company", "organizationNumber is mandatory");
            }
        }
    }
}
