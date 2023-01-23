package com.example.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Person {
  private String lastName;
  private String firstName;

  @Override
  public String toString() {
    return String.format("{ \"firstName\": \"%s\", \"lastName\": \"%s\"}", getFirstName(), getLastName());
  }
}
