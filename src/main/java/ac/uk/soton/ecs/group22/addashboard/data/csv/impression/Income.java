package ac.uk.soton.ecs.group22.addashboard.data.csv.impression;

import lombok.Getter;

public enum Income {
  LOW("Low"),
  MEDIUM("Medium"),
  HIGH("High");

  @Getter
  private final String formatted;

  Income(String formatted) {
    this.formatted = formatted;
  }

}
