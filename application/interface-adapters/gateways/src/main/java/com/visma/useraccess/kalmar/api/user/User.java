package com.visma.useraccess.kalmar.api.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "User")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column(name = "IdUser", columnDefinition = "CHAR(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID idUser;

    @Column(name = "IdLanguage", nullable = false, columnDefinition = "CHAR(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID idLanguage;

    @Column(name = "Email", nullable = false, length = 255)
    private String email;

    @Column(name = "FirstName", nullable = false, length = 50)
    private String firstName;

    @Column(name = "LastName", nullable = false, length = 50)
    private String lastName;
    @Column
    @Version
    private long recordVersion;
    @Temporal(TemporalType.TIMESTAMP)
    private Date whenEdited;

    @PrePersist
    public void prePersist() {
        if (idUser == null) {
            idUser = UUID.randomUUID();
        }
    }
}
