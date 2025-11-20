package com.visma.kalmar.api.entities.country;

import java.util.UUID;

public class Country {
  private final UUID idCountry;
  private final String name;
  private final String code;

  public Country(UUID idCountry, String name, String code) {
    this.idCountry = idCountry;
    this.name = name;
    this.code = code;
  }

  public UUID getIdCountry() {
    return idCountry;
  }

  public String getName() {
    return name;
  }

  public String getCode() {
    return code;
  }
}
