package com.visma.kalmar.api.company;

import com.visma.kalmar.api.entities.company.Company;
import com.visma.kalmar.api.entities.context.Context;

public interface CompanyOutputPort {

    void present(Company company, Context context, boolean created);
}
