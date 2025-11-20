package com.visma.subscription.kalmar.api.role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "Role")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
  @Id
  @Column(name = "IdRole", columnDefinition = "CHAR(36)")
  @JdbcTypeCode(SqlTypes.CHAR)
  private UUID idRole;

  @Column(name = "Name", nullable = false, length = 255)
  private String name;

  @Column(name = "UserNameChangedBy", length = 255)
  private String userNameChangedBy;

  @Column(name = "WhenEdited")
  @Temporal(TemporalType.TIMESTAMP)
  private Date whenEdited;

  @PrePersist
  public void prePersist() {
    if (idRole == null) {
      idRole = UUID.randomUUID();
    }
    if (whenEdited == null) {
      whenEdited = new Date();
    }
  }
}
