package ac.uk.soton.ecs.group22.addashboard.controller.filter;

import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.AgeRange;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.Context;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.Gender;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.ImpressionEntry;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.Income;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;

/**
 * Filters the data by impression information such as context, income and age.
 */
public class ImpressionFilter {

  @Getter
  private final Set<Context> contexts = new HashSet<>();
  @Getter
  private final Set<Income> incomes = new HashSet<>();
  @Getter
  private final Set<AgeRange> ages = new HashSet<>();
  @Getter
  private final Set<Gender> genders = new HashSet<>();

  public ImpressionFilter() {

  }

  /**
   * Adds the context to the filter. Only contexts which are this context, or other contexts in the
   * filter, will be shown.
   *
   * @param context The context to remove.
   */
  public void add(Context context) {
    contexts.add(context);
  }

  /**
   * Remove context from the filter.
   *
   * @param context The context to remove.
   */
  public void remove(Context context) {
    contexts.remove(context);
  }

  /**
   * Adds the context to the income. Only incomes which are this income, or other incomes in the
   * filter, will be shown.
   *
   * @param income The income to remove.
   */
  public void add(Income income) {
    incomes.add(income);
  }

  /**
   * Remove income from the filter.
   *
   * @param income The income to remove.
   */
  public void remove(Income income) {
    incomes.remove(income);
  }

  /**
   * Adds the age range to the income. Only age ranges which are this age range, or other age ranges
   * in the filter, will be shown.
   *
   * @param ageRange The age range to remove.
   */
  public void add(AgeRange ageRange) {
    ages.add(ageRange);
  }

  /**
   * Remove age range from the filter.
   *
   * @param ageRange The age range to remove.
   */
  public void remove(AgeRange ageRange) {
    ages.remove(ageRange);
  }

  /**
   * Adds the gender to the income. Only genders which are this gender, or other genders in the
   * filter, will be shown.
   *
   * @param gender The gender to remove.
   */
  public void add(Gender gender) {
    genders.add(gender);
  }

  /**
   * Remove gender from the filter.
   *
   * @param gender The age range to remove.
   */
  public void remove(Gender gender) {
    genders.remove(gender);
  }

  /**
   * Returns whether the impression entry is allowed by the filter.
   * <p>
   * If the filter is empty, all impressions will be allowed.
   *
   * @param entry The entry to check.
   * @return Whether the entry passes the filter.
   */
  public boolean matches(ImpressionEntry entry) {
    if (!contexts.isEmpty() && !contexts.contains(entry.getContext())) {
      return false;
    }

    if (!genders.isEmpty() && !genders.contains(entry.getGender())) {
      return false;
    }

    if (!ages.isEmpty() && !ages.contains(entry.getAge())) {
      return false;
    }

    return incomes.isEmpty() || incomes.contains(entry.getIncome());
  }

  /**
   * Reset the filter.
   * <p>
   * This will allow anything through the filter.
   */
  public void reset() {
    genders.clear();
    incomes.clear();
    ages.clear();
    contexts.clear();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ImpressionFilter that = (ImpressionFilter) o;
    return contexts.equals(that.contexts) && incomes.equals(that.incomes) && ages.equals(that.ages) && genders.equals(that.genders);
  }

  @Override
  public int hashCode() {
    return Objects.hash(contexts, incomes, ages, genders);
  }

}