package com.visma.useraccess.kalmar.api.country;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "Country")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Country {

    @Id
    @Column(name = "IdCountry", columnDefinition = "CHAR(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID idCountry;

    @Column(name = "Name", nullable = false, length = 255)
    private String name;

    @Column(name = "Code", nullable = false, length = 2, unique = true)
    private String code;

    @PrePersist
    public void prePersist() {
        if (idCountry == null) {
            idCountry = UUID.randomUUID();
        }
    }
}

