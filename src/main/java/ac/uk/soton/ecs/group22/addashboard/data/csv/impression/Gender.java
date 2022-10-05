package ac.uk.soton.ecs.group22.addashboard.data.csv.impression;

import lombok.Getter;

public enum Gender {
  MALE("Male"),
  FEMALE("Female");

  @Getter private final String formatted;

  Gender(String formatted) {
    this.formatted = formatted;
  }

}
