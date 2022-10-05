package ac.uk.soton.ecs.group22.addashboard.ui;

import ac.uk.soton.ecs.group22.addashboard.controller.filter.DateFilter;
import ac.uk.soton.ecs.group22.addashboard.controller.filter.ImpressionFilter;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.LongStream;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import lombok.Getter;
import lombok.Setter;

public class BaseChart<S extends String, D extends Number> extends LineChart<S, D> {

  protected Set<String> hiddenGraphMetrics = new HashSet<>();
  protected SimpleObjectProperty<Granularity> granularity = new SimpleObjectProperty<>(Granularity.Hourly);
  public SimpleObjectProperty<Granularity> granularity() { return granularity; }

  protected ImpressionFilter impressionFilter;
  protected DateFilter dateFilter;

  private Calendar calendarInst;

  public BaseChart() {
    super((Axis<S>) new CategoryAxis(), (Axis<D>) new NumberAxis());
    this.setAnimated(false);
  }

  public BaseChart(ImpressionFilter impressionFilter, DateFilter dateFilter) {
    super((Axis<S>) new CategoryAxis(), (Axis<D>) new NumberAxis());
    this.setAnimated(false);
    this.impressionFilter = impressionFilter;
    this.dateFilter = dateFilter;
  }

  protected long[] periodMetric(LongStream dates){
    long[] array;
    switch (granularity.getValue()) {
      case Hourly -> {
        array = new long[24];
        dates.mapToInt(e -> getGranularity(e, Calendar.HOUR_OF_DAY)).forEach(e -> array[e]++);
        return array;
      }
      case Daily -> {
        array = new long[7];
        dates.mapToInt(e -> getGranularity(e, 0)).forEach(e -> array[e]++);
        return array;
      }
      case Weekly -> {
        array = new long[52];
        dates.mapToInt(e -> getGranularity(e, Calendar.WEEK_OF_YEAR)).forEach(e -> array[e]++);
        return array;
      }
      case Monthly -> {
        array = new long[12];
        dates.mapToInt(e -> getGranularity(e, Calendar.MONTH)).forEach(e -> array[e]++);
        return array;
      }
    }
    return null;
  }

  /**
   * Gets the granularity of a date
   *
   * @param date        the date
   * @param granularity the granularity
   * @return the granularity expressed in milliseconds
   */
  protected Integer getGranularity(Long date, int granularity) {
    Calendar cal = getCalendar();
    calendarInst.setTimeInMillis(date);

    //Shifts the values of DAY OF WEEK so Monday is 0 and Sunday is 6 to align with the graphs
    if(granularity == 0) {
      int day = cal.get(Calendar.DAY_OF_WEEK) - 2;
      if (day == -1) {
        return 6;
      } else {
        return day;
      }
    }

    return cal.get(granularity);
  }

  protected synchronized Calendar getCalendar() {
    if(calendarInst != null) {
      return calendarInst;
    }

    calendarInst = Calendar.getInstance();
    calendarInst.setTimeZone(TimeZone.getTimeZone("UTC"));
    calendarInst.setFirstDayOfWeek(Calendar.MONDAY);

    return calendarInst;
  }

  /**
   * Gets x values for different time granularities
   *
   * @return an array of x-axis names for the given granularity
   */
  protected String[] getXValues() {
    switch (granularity.getValue()) {
      case Hourly -> {
        return new String[] {"00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00",
            "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00"};
      }
      case Daily -> {
        return new String[] {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
      }
      case Monthly -> {
        return new String[] {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
      }
      case Weekly -> {
        String[] array=new String[52];
        for(int i=0; i<52; i++){
          array[i]=Integer.toString(i+1);
        }
        return array;
      }
    }
    return new String[0];
  }
}
