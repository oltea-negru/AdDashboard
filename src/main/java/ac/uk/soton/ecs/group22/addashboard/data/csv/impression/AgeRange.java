package ac.uk.soton.ecs.group22.addashboard.data.csv.impression;

import lombok.Getter;

public enum AgeRange {
  LESS_THAN_TWENTY_FIVE("< 25"),
  TWENTY_FIVE_TO_THIRTY_FOUR("25 - 34"),
  THIRTY_FIVE_TO_FOURTY_FOUR("35 - 44"),
  FOURTY_FIVE_TO_FIFTY_FOUR("45 - 54"),
  MORE_THAN_FIFTY_FOUR("> 54");

  @Getter
  private final String formatted;

  AgeRange(String formatted) {
    this.formatted = formatted;
  }

}
