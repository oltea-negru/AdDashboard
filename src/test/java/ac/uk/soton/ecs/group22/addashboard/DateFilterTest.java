package ac.uk.soton.ecs.group22.addashboard;

import ac.uk.soton.ecs.group22.addashboard.controller.filter.DateFilter;
import ac.uk.soton.ecs.group22.addashboard.controller.filter.ImpressionFilter;
import ac.uk.soton.ecs.group22.addashboard.data.csv.click.ClickLoader;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.AgeRange;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.Context;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.Gender;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.ImpressionEntry;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.ImpressionLoader;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.Income;
import ac.uk.soton.ecs.group22.addashboard.data.csv.server.ServerLoader;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Locale;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class DateFilterTest {

  private static DateFilter filter;

  @BeforeAll
  static void setup() {
    filter = new DateFilter();
  }

  @DisplayName("Setting start date to filter is persistent")
  @Test
  public void isStartDatePersistent() {
    LocalDate now = LocalDate.now();
    filter.setStartDate(now);

    Assertions.assertEquals(filter.getStartDate(), now.atTime(LocalTime.MIN).toEpochSecond(ZoneOffset.UTC) * 1000);
    filter.setStartDate(null);
  }

  @DisplayName("Setting end date to filter is persistent")
  @Test
  public void isEndDatePersistent() {
    LocalDate now = LocalDate.now();
    filter.setEndDate(now);

    Assertions.assertEquals(filter.getEndDate(), now.atTime(LocalTime.MAX).toEpochSecond(ZoneOffset.UTC) * 1000);
    filter.setEndDate(null);
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
    filter.setStartDate(LocalDate.now());

    Assertions.assertFalse(filter.equals(new DateFilter()));

    filter.setStartDate(null);
  }

  @DisplayName("Empty Filter#equals accepts empty filter")
  @Test
  public void emptyEqualAcceptsEmpty() {
    Assertions.assertEquals(new DateFilter(), new DateFilter());
  }

  @DisplayName("Filter#equals accepts similar filter")
  @Test
  public void equalAcceptsSimilar() {
    LocalDate now = LocalDate.now();

    DateFilter other = new DateFilter();

    filter.setStartDate(now);
    filter.setEndDate(now);
    other.setStartDate(now);
    other.setEndDate(now);

    Assertions.assertTrue(filter.equals(other));

    filter.setStartDate(null);
    filter.setEndDate(null);
  }

  @DisplayName("Filter#equals rejects dissimilar filter")
  @Test
  public void equalRejectsDissimilar() {
    LocalDate now = LocalDate.now();

    DateFilter other = new DateFilter();

    filter.setStartDate(now);
    filter.setEndDate(now);
    other.setEndDate(now);

    Assertions.assertFalse(filter.equals(other));

    filter.setStartDate(now);
    filter.setEndDate(now);
    other.setStartDate(now);
    other.setEndDate(null);

    Assertions.assertFalse(filter.equals(other));

    filter.setStartDate(null);
    filter.setEndDate(null);
  }

  @DisplayName("Filter#equals rejects completely dissimlilar filter")
  @Test
  public void equalRejectsCompleteDissimilar() {
    LocalDate now = LocalDate.now();

    DateFilter other = new DateFilter();

    other.setStartDate(now);
    other.setEndDate(now);

    Assertions.assertFalse(filter.equals(other));
  }

  @SneakyThrows
  @DisplayName("Filter filters impressions")
  @Test
  public void filterImpressions() {
    ImpressionLoader impressionLoader = new ImpressionLoader(getImpressionTestFile());
    impressionLoader.load();

    LocalDate localDateStart = LocalDate.now()
        .withYear(2015)
        .withDayOfMonth(1)
        .withMonth(1);

    LocalDate localDateEnd = LocalDate.now()
        .withYear(2015)
        .withDayOfMonth(1)
        .withMonth(1);

    filter.setStartDate(localDateStart);
    filter.setEndDate(localDateEnd);

    Assertions.assertEquals(7, impressionLoader.getImpressions()
        .stream()
        .filter(impressionEntry -> filter.matches(impressionEntry.getDate()))
        .count());
  }

  @SneakyThrows
  @DisplayName("Filter filters clicks")
  @Test
  public void filterClicks() {
    ClickLoader clickLoader = new ClickLoader(getClickTestFile());
    clickLoader.load();

    LocalDate localDateStart = LocalDate.now()
        .withYear(2015)
        .withDayOfMonth(2)
        .withMonth(1);

    LocalDate localDateEnd = LocalDate.now()
        .withYear(2015)
        .withDayOfMonth(3)
        .withMonth(1);

    filter.setStartDate(localDateStart);
    filter.setEndDate(localDateEnd);

    Assertions.assertEquals(3, clickLoader.getClicks()
        .stream()
        .filter(clickEntry -> filter.matches(clickEntry.getDate()))
        .count());
  }

  @SneakyThrows
  @DisplayName("Filter filters servers")
  @Test
  public void filterServers() {
    ServerLoader serverLoader = new ServerLoader(getServerTestFile());
    serverLoader.load();

    LocalDate localDateStart = LocalDate.now()
        .withYear(2015)
        .withDayOfMonth(3)
        .withMonth(1);

    LocalDate localDateEnd = LocalDate.now()
        .withYear(2015)
        .withDayOfMonth(3)
        .withMonth(1);

    filter.setStartDate(localDateStart);
    filter.setEndDate(localDateEnd);

    Assertions.assertEquals(1, serverLoader.getServer()
        .stream()
        .filter(serverEntry -> filter.matches(serverEntry.getEntryDate()))
        .count());
  }

  @SneakyThrows
  @DisplayName("toDate returns correct date")
  @Test
  public void toDateCorrect() {
    LocalDate localDate = DateFilter.toDate(1651677500L * 1000);

    Assertions.assertEquals(2022, localDate.getYear());
    Assertions.assertEquals(5, localDate.getMonthValue());
    Assertions.assertEquals(4, localDate.getDayOfMonth());
  }

  @SneakyThrows
  @DisplayName("toDate returns error for invalid date")
  @Test
  public void toDateError() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> DateFilter.toDate(-1));
  }

  private File getImpressionTestFile() {
    return new File(getClass().getClassLoader().getResource("simple_impression_log.csv").getFile());
  }

  private File getClickTestFile() {
    return new File(getClass().getClassLoader().getResource("simple_click_log.csv").getFile());
  }

  private File getServerTestFile() {
    return new File(getClass().getClassLoader().getResource("simple_server_log.csv").getFile());
  }


}