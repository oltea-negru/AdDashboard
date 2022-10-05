package ac.uk.soton.ecs.group22.addashboard;

import ac.uk.soton.ecs.group22.addashboard.controller.filter.ImpressionFilter;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.AgeRange;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.Context;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.Gender;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.ImpressionEntry;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.Income;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class ImpressionFilterTest {

  private static ImpressionFilter filter;

  @BeforeAll
  static void setup() {
    filter = new ImpressionFilter();
  }

  @DisplayName("Adding and removing context to filter is persistent")
  @ParameterizedTest
  @EnumSource(Context.class)
  public void isContextPersistent(Context context) {
    filter.add(context);
    Assertions.assertTrue(filter.getContexts().contains(context));

    filter.remove(context);
    Assertions.assertFalse(filter.getContexts().contains(context));
  }

  @DisplayName("Adding and removing incomes to filter is persistent")
  @ParameterizedTest
  @EnumSource(Income.class)
  public void isIncomePersistent(Income income) {
    filter.add(income);
    Assertions.assertTrue(filter.getIncomes().contains(income));

    filter.remove(income);
    Assertions.assertFalse(filter.getIncomes().contains(income));
  }

  @DisplayName("Adding and removing ages to filter is persistent")
  @ParameterizedTest
  @EnumSource(AgeRange.class)
  public void isIncomePersistent(AgeRange ageRange) {
    filter.add(ageRange);
    Assertions.assertTrue(filter.getAges().contains(ageRange));

    filter.remove(ageRange);
    Assertions.assertFalse(filter.getAges().contains(ageRange));
  }

  @DisplayName("Adding and removing gender to filter is persistent")
  @ParameterizedTest
  @EnumSource(Gender.class)
  public void isGenderPersistent(Gender gender) {
    filter.add(gender);
    Assertions.assertTrue(filter.getGenders().contains(gender));

    filter.remove(gender);
    Assertions.assertFalse(filter.getGenders().contains(gender));
  }

  @DisplayName("Resetting filter clears all filters")
  @Test
  public void resetClears() {
    filter.add(Context.BLOG);
    filter.add(Income.HIGH);
    filter.add(AgeRange.LESS_THAN_TWENTY_FIVE);
    filter.add(Gender.MALE);

    filter.reset();

    Assertions.assertTrue(filter.getContexts().isEmpty());
    Assertions.assertTrue(filter.getIncomes().isEmpty());
    Assertions.assertTrue(filter.getAges().isEmpty());
    Assertions.assertTrue(filter.getGenders().isEmpty());
  }

  @DisplayName("Filter#equals rejects non-filter primitives")
  @Test
  public void equalsRejectsNonFilterPrimitive() {
    Assertions.assertNotEquals(5, filter);
  }

  @DisplayName("Filter#equals rejects null objects")
  @Test
  public void equalsRejectsNull() {
    Assertions.assertFalse(filter.equals(null));
  }

  @DisplayName("Filter#equals rejects non-filter objects")
  @Test
  public void equalsRejectsNonFilterObject() {
    Assertions.assertFalse(filter.equals(new Object()));
  }

  @DisplayName("Filter#equals accepts itself")
  @Test
  public void equalAcceptsItself() {
    Assertions.assertTrue(filter.equals(filter));
  }

  @DisplayName("Filter#equals rejects dissimilar empty filter")
  @Test
  public void equalRejectsDissimilarEmpty() {
    filter.add(Gender.FEMALE);

    Assertions.assertFalse(filter.equals(new ImpressionFilter()));

    filter.remove(Gender.FEMALE);
  }

  @DisplayName("Empty Filter#equals accepts empty filter")
  @Test
  public void emptyEqualAcceptsEmpty() {
    Assertions.assertEquals(new ImpressionFilter(), new ImpressionFilter());
  }

  @DisplayName("Filter#equals accepts similar filter")
  @Test
  public void equalAcceptsSimilar() {
    checkFilter(Gender.FEMALE);
    checkFilter(Context.BLOG);
    checkFilter(AgeRange.FOURTY_FIVE_TO_FIFTY_FOUR);
    checkFilter(Income.LOW);
  }

  @SneakyThrows
  private void checkFilter(Enum enumb) {
    ImpressionFilter other = new ImpressionFilter();
    filter.getClass().getMethod("add", enumb.getClass())
        .invoke(filter, enumb);
    other.getClass().getMethod("add", enumb.getClass())
        .invoke(other, enumb);

    Assertions.assertTrue(filter.equals(other));

    filter.getClass().getMethod("remove", enumb.getClass())
        .invoke(filter, enumb);
  }

  @DisplayName("Filter#equals rejects dissimilar filter")
  @Test
  public void equalRejectsDissimilar() {
    checkDissimilarFilter(Gender.FEMALE, Context.BLOG);
    checkDissimilarFilter(Context.BLOG, AgeRange.LESS_THAN_TWENTY_FIVE);
    checkDissimilarFilter(AgeRange.FOURTY_FIVE_TO_FIFTY_FOUR, Gender.FEMALE);
    checkDissimilarFilter(Income.LOW, AgeRange.THIRTY_FIVE_TO_FOURTY_FOUR);
  }

  @SneakyThrows
  private void checkDissimilarFilter(Enum enumb, Enum otherEnum) {
    ImpressionFilter other = new ImpressionFilter();
    filter.getClass().getMethod("add", enumb.getClass())
        .invoke(filter, enumb);
    other.getClass().getMethod("add", enumb.getClass())
        .invoke(other, enumb);
    other.getClass().getMethod("add", otherEnum.getClass())
        .invoke(other, otherEnum);

    Assertions.assertFalse(filter.equals(other));

    filter.getClass().getMethod("remove", enumb.getClass())
        .invoke(filter, enumb);
  }

  @DisplayName("Filter filters gender")
  @Test
  public void filterGender() {
    filter.reset();
    filter.add(Gender.MALE);

    Assertions.assertFalse(filter.matches(new ImpressionEntry(System.currentTimeMillis(), 1,
        Gender.FEMALE,
        AgeRange.LESS_THAN_TWENTY_FIVE,
        Income.HIGH,
        Context.BLOG,
        0)));

    Assertions.assertTrue(filter.matches(new ImpressionEntry(System.currentTimeMillis(), 1,
        Gender.MALE,
        AgeRange.LESS_THAN_TWENTY_FIVE,
        Income.HIGH,
        Context.BLOG,
        0)));
  }

  @DisplayName("Filter filters context")
  @Test
  public void filterContext() {
    filter.reset();
    filter.add(Context.BLOG);

    Assertions.assertFalse(filter.matches(new ImpressionEntry(System.currentTimeMillis(), 1,
        Gender.FEMALE,
        AgeRange.LESS_THAN_TWENTY_FIVE,
        Income.HIGH,
        Context.SHOPPING,
        0)));

    Assertions.assertTrue(filter.matches(new ImpressionEntry(System.currentTimeMillis(), 1,
        Gender.FEMALE,
        AgeRange.LESS_THAN_TWENTY_FIVE,
        Income.HIGH,
        Context.BLOG,
        0)));
  }

  @DisplayName("Filter filters age range")
  @Test
  public void filterAgeRange() {
    filter.reset();
    filter.add(AgeRange.MORE_THAN_FIFTY_FOUR);

    Assertions.assertFalse(filter.matches(new ImpressionEntry(System.currentTimeMillis(), 1,
        Gender.FEMALE,
        AgeRange.LESS_THAN_TWENTY_FIVE,
        Income.HIGH,
        Context.SHOPPING,
        0)));

    Assertions.assertTrue(filter.matches(new ImpressionEntry(System.currentTimeMillis(), 1,
        Gender.FEMALE,
        AgeRange.MORE_THAN_FIFTY_FOUR,
        Income.HIGH,
        Context.SHOPPING,
        0)));
  }

  @DisplayName("Filter filters income")
  @Test
  public void filterIncome() {
    filter.reset();
    filter.add(Income.MEDIUM);

    Assertions.assertFalse(filter.matches(new ImpressionEntry(System.currentTimeMillis(), 1,
        Gender.FEMALE,
        AgeRange.LESS_THAN_TWENTY_FIVE,
        Income.HIGH,
        Context.SHOPPING,
        0)));

    Assertions.assertTrue(filter.matches(new ImpressionEntry(System.currentTimeMillis(), 1,
        Gender.FEMALE,
        AgeRange.LESS_THAN_TWENTY_FIVE,
        Income.MEDIUM,
        Context.SHOPPING,
        0)));
  }

}
