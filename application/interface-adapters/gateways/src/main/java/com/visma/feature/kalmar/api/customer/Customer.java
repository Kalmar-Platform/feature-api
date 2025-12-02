package com.visma.feature.kalmar.api.customer;

import com.visma.feature.kalmar.api.context.Context;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "Customer")
@PrimaryKeyJoinColumn(name = "IdCustomer", referencedColumnName = "IdContext")
public class Customer extends Context {
  public UUID getIdCustomer() {
    return getIdContext();
  }

  public void setIdCustomer(UUID idCustomer) {
    super.setIdContext(idCustomer);
  }
}
