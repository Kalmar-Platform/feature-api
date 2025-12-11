package com.visma.feature.kalmar.api.company;

import com.visma.feature.kalmar.api.context.Context;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "Company")
@PrimaryKeyJoinColumn(name = "IdCompany", referencedColumnName = "IdContext")
public class Company extends Context {
  public UUID getIdCompany() {
    return getIdContext();
  }

  public void setIdCompany(UUID idCompany) {
    super.setIdContext(idCompany);
  }
}
