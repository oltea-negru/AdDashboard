package ac.uk.soton.ecs.group22.addashboard.data.csv.click;

import lombok.Getter;

/**
 * Enum for the different cost categories the clicks can fall into
 *
 * @author Jack
 * @version 1.0
 * @since 2022-03-07
 */
public enum CostBin {
  ZERO("0", -Double.MAX_VALUE, 0d),
  ZERO_TO_TWO("0-2", 0d, 2d),
  TWO_TO_FOUR("2-4", 2d, 4d),
  FOUR_TO_SIX("4-6", 4d, 6d),
  SIX_TO_EIGHT("6-8", 6d, 8d),
  EIGHT_TO_TEN("8-10", 8d, 10d),
  TEN_TO_TWELVE("10-12", 10d, 12d),
  TWELVE_TO_FOURTEEN("12-14", 12d, 14d),
  FOURTEEN_TO_SIXTEEN("14-16", 14d, 16d),
  OVER_SIXTEEN(">16", 16d, Double.MAX_VALUE);

  @Getter
  private final String formatted;
  @Getter
  private final double lower;
  @Getter
  private final double upper;

  CostBin(String formatted, double lower, double upper) {
    this.formatted = formatted;
    this.lower = lower;
    this.upper = upper;
  }

  @Override
  public String toString() { return formatted; }

  /**
   * Returns the relevant bin for a given number
   * @param val The value to categorise
   * @return CostBin The bin
   */
  public static CostBin getBin(double val) {
    for (CostBin e: CostBin.values()) {
      if (val > e.lower && val <= e.upper) return e;
    }
    return null;
  }
}

