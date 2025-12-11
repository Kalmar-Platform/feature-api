package com.visma.kalmar.api.company;

import com.visma.kalmar.api.exception.InvalidInputDataException;
import com.visma.kalmar.api.exception.ResourceNotFoundException;

import java.util.UUID;

public interface CreateCompanyInputPort {

    void createCompany(CreateCompanyInputData inputData, CompanyOutputPort outputPort)
            throws InvalidInputDataException, ResourceNotFoundException;

    record CreateCompanyInputData(
            UUID idCompany,
            String countryCode,
            UUID idContextParent,
            String organizationNumber,
            String name
    ) {
        public CreateCompanyInputData {
            if (idCompany == null) {
                idCompany = UUID.randomUUID();
            }
            if (idContextParent == null) {
                throw new InvalidInputDataException("Company", "idContextParent is mandatory");
            }
            if (organizationNumber == null || organizationNumber.isBlank()) {
                throw new InvalidInputDataException("Company", "organizationNumber is mandatory");
            }
            if (name == null || name.isBlank()) {
                throw new InvalidInputDataException("Company", "name is mandatory");
            }
        }
    }
}
