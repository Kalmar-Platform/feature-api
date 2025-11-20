package com.visma.useraccess.kalmar.api.language;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "Language")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Language {

    @Id
    @Column(name = "IdLanguage", columnDefinition = "CHAR(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID idLanguage;

    @Column(name = "Name", nullable = false, length = 255)
    private String name;

    @Column(name = "Code", nullable = false, length = 2)
    private String code;

    @PrePersist
    public void prePersist() {
        if (idLanguage == null) {
            idLanguage = UUID.randomUUID();
        }
    }
}
