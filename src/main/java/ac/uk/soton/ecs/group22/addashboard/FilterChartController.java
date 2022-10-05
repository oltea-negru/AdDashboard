package ac.uk.soton.ecs.group22.addashboard;

import ac.uk.soton.ecs.group22.addashboard.controller.Campaign;
import ac.uk.soton.ecs.group22.addashboard.controller.filter.DateFilter;
import ac.uk.soton.ecs.group22.addashboard.controller.filter.ImpressionFilter;
import ac.uk.soton.ecs.group22.addashboard.data.csv.click.ClickEntry;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.AgeRange;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.Context;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.Gender;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.ImpressionEntry;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.Income;
import ac.uk.soton.ecs.group22.addashboard.data.csv.server.ServerEntry;
import ac.uk.soton.ecs.group22.addashboard.events.CompareEvent;
import ac.uk.soton.ecs.group22.addashboard.events.FilterEvent;
import ac.uk.soton.ecs.group22.addashboard.events.LoadDataEvent;
import ac.uk.soton.ecs.group22.addashboard.ui.CountChart;
import ac.uk.soton.ecs.group22.addashboard.ui.FilterOptions;
import ac.uk.soton.ecs.group22.addashboard.ui.FinancialChart;
import ac.uk.soton.ecs.group22.addashboard.ui.Granularity;
import ac.uk.soton.ecs.group22.addashboard.ui.MetricType;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import lombok.Getter;
import lombok.SneakyThrows;

/**
 * <H1> Filter Chart Controller </H1>
 * Controls an instance of a chart combined with a filter
 * @see Initializable
 */
public class FilterChartController implements Initializable {

  @FXML
  VBox filterScrollPaneContent;
  @FXML
  DatePicker startDatePicker;
  @FXML
  DatePicker endDatePicker;

  @FXML
  @Getter
  ImpressionFilter impressionFilter = new ImpressionFilter();
  @FXML
  @Getter
  DateFilter dateFilter = new DateFilter();

  @FXML
  protected BorderPane chartPane;
  protected FinancialChart financialChart;
  protected CountChart countChart;
  @FXML
  protected ComboBox<Granularity> granularityPicker;
  @FXML
  protected ComboBox<MetricType> metricPicker;

  /**
   * Initialises the controller on load
   * @param url
   * @param resourceBundle
   */
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    financialChart = new FinancialChart(impressionFilter, dateFilter);
    financialChart.setId("financialChart");
    countChart = new CountChart(impressionFilter, dateFilter);
    countChart.setId("countChart");

    granularityPicker.setItems(FXCollections.observableArrayList(Granularity.values()));
    granularityPicker.setValue(Granularity.Hourly);
    financialChart.granularity().bind(granularityPicker.valueProperty());
    countChart.granularity().bind(granularityPicker.valueProperty());
    granularityPicker.setOnAction(e -> new CompareEvent().call());

    metricPicker.setItems(FXCollections.observableArrayList(MetricType.values()));
    metricPicker.setValue(MetricType.Key);
    setCount();
    metricPicker.setOnAction(e -> {
      switch (metricPicker.getValue()) {
        case Key -> {
          setCount();
        }
        case Financial -> {
          setFinancial();
        }
      }
    });

    startDatePicker.getEditor().setDisable(true);
    startDatePicker.getEditor().setOpacity(1);
    endDatePicker.getEditor().setDisable(true);
    endDatePicker.getEditor().setOpacity(1);
    addFilters();
    CompareEvent.getListeners().add(compareEvent -> {
      switch (metricPicker.getValue()) {
        case Key -> {
          countChart.updateChart();
        }
        case Financial -> {
          financialChart.updateChart();
        }
      }
    });

    LoadDataEvent.getListeners().add(loadDataEvent -> {
      List<Long> dates;

      if(!Campaign.getInstance().getImpressionManager().getImpressions().isEmpty()) {
        dates = Campaign.getInstance().getImpressionManager().getImpressions()
            .stream()
            .map(ImpressionEntry::getDate)
            .collect(Collectors.toList());
      } else if(!Campaign.getInstance().getClickManager().getClicks().isEmpty()) {
        dates = Campaign.getInstance().getClickManager().getClicks()
            .stream()
            .map(ClickEntry::getDate)
            .collect(Collectors.toList());
      } else if(!Campaign.getInstance().getServerManager().getServer().isEmpty()) {
        dates = Campaign.getInstance().getServerManager().getServer()
            .stream()
            .map(ServerEntry::getEntryDate)
            .collect(Collectors.toList());
      } else {
        return;
      }

      LocalDate minDate = DateFilter.toDate(dates.stream().mapToLong(value -> value).min().getAsLong());
      LocalDate maxDate = DateFilter.toDate(dates.stream().mapToLong(value -> value).max().getAsLong());

      startDatePicker.setValue(minDate);
      endDatePicker.setValue(maxDate);

      Callback<DatePicker, DateCell> restrictOutOfBoundsFactoryMin = new Callback<DatePicker, DateCell>() {
        @Override
        public DateCell call(DatePicker datePicker) {
          return new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
              super.updateItem(item, empty);
              if (item.isBefore(minDate) || item.isAfter(maxDate) || item.isAfter(endDatePicker.getValue())) {
                setDisable(true);
                setStyle("-fx-background-color: #eeeeee");
              }
            }
          };
        }
      };

      Callback<DatePicker, DateCell> restrictOutOfBoundsFactoryMax = new Callback<DatePicker, DateCell>() {
        @Override
        public DateCell call(DatePicker datePicker) {
          return new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
              super.updateItem(item, empty);
              if (item.isBefore(minDate) || item.isAfter(maxDate) || item.isBefore(startDatePicker.getValue())) {
                setDisable(true);
                setStyle("-fx-background-color: #eeeeee");
              }
            }
          };
        }
      };

      startDatePicker.setDayCellFactory(restrictOutOfBoundsFactoryMin);
      endDatePicker.setDayCellFactory(restrictOutOfBoundsFactoryMax);

      updateDateFilters();
    });
  }

  public void setCount() {
    chartPane.getChildren().clear();
    countChart.updateChart();
    chartPane.setCenter(countChart);
  }

  public void setFinancial() {
    chartPane.getChildren().clear();
    financialChart.updateChart();
    chartPane.setCenter(financialChart);
  }



  /**
   * Adds a section to the filter based on an enum
   * @param header the name
   * @param enumClazz the enum class
   * @param values the values
   */
  @SneakyThrows
  private void addFilter(String header, Class enumClazz, Enum[] values) {
    filterScrollPaneContent.getChildren().add(new FilterOptions(header, Arrays.stream(values)
        .map(new Function<Enum, String>() {
          @Override
          @SneakyThrows
          public String apply(Enum enumInst) {
            return (String) enumClazz.getMethod("getFormatted").invoke(enumInst);
          }
        }).collect(Collectors.toList())) {
      @Override
      @SneakyThrows
      public void onUpdate(String option, boolean selected) {
        for (Enum enumInst : values) {
          String formatted = (String) enumClazz.getMethod("getFormatted").invoke(enumInst);

          if (formatted.equals(option)) {
            if (selected) {
              impressionFilter
                  .getClass().getMethod("add", enumClazz)
                  .invoke(impressionFilter, enumInst);
            } else {
              impressionFilter
                  .getClass().getMethod("remove", enumClazz)
                  .invoke(impressionFilter, enumInst);
            }
          }
        }

        new CompareEvent().call();
      }
    });
  }

  /**
   * Adds all four filter sections
   */
  private void addFilters() {
    addFilter("Age Range", AgeRange.class, AgeRange.values());
    addFilter("Context", Context.class, Context.values());
    addFilter("Gender", Gender.class, Gender.values());
    addFilter("Income", Income.class, Income.values());
  }

  /**
   * Resets the filter and calls a UI update
   */
  @FXML
  public void resetFilter() {
    filterScrollPaneContent.getChildren().forEach(node -> {
      if(node instanceof FilterOptions) {
        ((FilterOptions) node).untickAll();
      }
    });

    impressionFilter.reset();

    new CompareEvent().call();
  }

  /**
   * Updates the date filter from the date pickers
   */
  @FXML
  public void updateDateFilters() {
    dateFilter.setStartDate(startDatePicker.getValue());
    dateFilter.setEndDate(endDatePicker.getValue());
    new CompareEvent().call();
  }
}
