package ac.uk.soton.ecs.group22.addashboard.controller.filter;

import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.ImpressionEntry;

import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import lombok.Getter;

/**
 * <h1> Date Filter </h1>
 * stores the filter information of dates
 */
public class DateFilter {

  @Getter
  private long startDate = 0L;

  @Getter
  private long endDate = Long.MAX_VALUE;

  public DateFilter() {}

  public boolean matches(long date) {
    return (date >= startDate && date <= endDate);
  }

  private long dateToLong(LocalDateTime date) {
    return date.toEpochSecond(ZoneOffset.UTC) * 1000;
  }

  public void setStartDate(LocalDate date) {
    if (date != null) {
      LocalDateTime newDate = date.atTime(LocalTime.MIN);
      startDate = dateToLong(newDate);
    } else {
      startDate = 0L;
    }
  }

  public void setEndDate(LocalDate date) {
    if(date != null) {
      LocalDateTime newDate = date.atTime(LocalTime.MAX);
      endDate = dateToLong(newDate);
    } else {
      startDate = Long.MAX_VALUE;
    }
  }

  public static LocalDate toDate(long time) {
    if(time < 0) {
      throw new IllegalArgumentException("Time cannot be negative");
    }

    LocalDateTime triggerTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(time),
            TimeZone.getDefault().toZoneId());

    return triggerTime.toLocalDate();
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DateFilter that = (DateFilter) o;
    return startDate == that.startDate && endDate == that.endDate;
  }

  @Override
  public int hashCode() {
    return Objects.hash(startDate, endDate);
  }

}
