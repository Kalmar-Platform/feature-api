package com.visma.kalmar.api.entities.user;

import java.util.Date;
import java.util.UUID;

public record User(
        UUID idUser,
        UUID idLanguage,
        String email,
        String firstName,
        String lastName,
        Long recordVersion,
        Date whenEdited
) {

}
