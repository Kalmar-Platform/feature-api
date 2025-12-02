package com.visma.feature.kalmar.api.contexttype;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "ContextType", indexes = {
        @Index(name = "UX_ContextType_Name", columnList = "Name", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContextType {

    @Id
    @Column(name = "IdContextType", columnDefinition = "CHAR(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID idContextType;

    @Column(name = "Name", nullable = false, length = 255, unique = true)
    private String name;

    @PrePersist
    public void prePersist() {
        if (idContextType == null) {
            idContextType = UUID.randomUUID();
        }
    }
}
