package com.visma.feature.kalmar.api.context;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public class Context implements Serializable {

    @Id
    @Column(name = "IdContext", columnDefinition = "CHAR(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID idContext;

    @Column(name = "IdContextType", columnDefinition = "CHAR(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID idContextType;

    @Column(name = "IdContextParent", columnDefinition = "CHAR(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID idContextParent;

    @Column(name = "IdCountry", nullable = false, columnDefinition = "CHAR(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID idCountry;

    @Column(name = "Name", nullable = false, length = 255)
    private String name;

    @Column(name = "OrganizationNumber", nullable = false, length = 255)
    private String organizationNumber;

    @PrePersist
    public void prePersist() {
        if (idContext == null) {
            idContext = UUID.randomUUID();
        }
    }
}
