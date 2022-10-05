package ac.uk.soton.ecs.group22.addashboard;

import ac.uk.soton.ecs.group22.addashboard.controller.Campaign;
import ac.uk.soton.ecs.group22.addashboard.controller.Settings;
import ac.uk.soton.ecs.group22.addashboard.controller.filter.DateFilter;
import ac.uk.soton.ecs.group22.addashboard.data.csv.click.ClickEntry;
import ac.uk.soton.ecs.group22.addashboard.data.csv.server.ServerEntry;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.*;
import ac.uk.soton.ecs.group22.addashboard.events.AppearanceSettingsChangeEvent;
import ac.uk.soton.ecs.group22.addashboard.events.ClearDataEvent;
import ac.uk.soton.ecs.group22.addashboard.events.FilterEvent;
import ac.uk.soton.ecs.group22.addashboard.events.LoadDataEvent;
import ac.uk.soton.ecs.group22.addashboard.events.PrintClickEvent;
import ac.uk.soton.ecs.group22.addashboard.ui.CountChart;
import ac.uk.soton.ecs.group22.addashboard.ui.FilterOptions;
import ac.uk.soton.ecs.group22.addashboard.ui.FinancialChart;
import ac.uk.soton.ecs.group22.addashboard.ui.Granularity;
import com.sun.tools.javac.Main;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;
import lombok.SneakyThrows;

/**
 * <h1> Metric Controller </h1>
 * Controller for the metric tab
 *
 * @see Initializable
 */
public class MetricController implements Initializable {

    @FXML
    private BorderPane rootPane;

    /* Top Panel */
    @FXML
    private Text topTotalImpressions;
    @FXML
    private Text topTotalClicks;
    @FXML
    private Text topTotalCost;
    @FXML
    private Text topTotalBounces;
    @FXML
    private Text topBounceRate;
    @FXML
    private Text topTotalUniques;
    @FXML
    private Text topCTR;
    @FXML
    private Text topCPC;
    @FXML
    private Text topCPM;
    @FXML
    private Text topCPA;
    @FXML
    private Text topConversions;

    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;

    @FXML
    private VBox filterScrollPaneContent;

    /* Cost pane */
    @FXML
    private BarChart<String, Integer> costHistogram;

    @FXML
    private BorderPane keyMetricPane;
    @FXML
    private BorderPane financialMetricPane;
  @FXML
  private BorderPane costsPane;

    private CountChart keyCountMetrics;
    private FinancialChart keyFinancialMetrics;
    @FXML
    private ComboBox<Granularity> granularityKeyPicker;
    @FXML
    private ComboBox<Granularity> granularityFinancialPicker;

    @FXML
    private Button keyMetricsExportButton;
    @FXML
    private Button financialMetricsExportButton;
    @FXML
    private Button costsExportButton;

    /**
     * clears the data and calls a UI update
     */
    @FXML
    public void clearData() {
        Campaign.getInstance().getImpressionManager().clear();
        Campaign.getInstance().getClickManager().clear();
        Campaign.getInstance().getServerManager().clear();

        new ClearDataEvent().call();

        // Force-update all data
        new FilterEvent().call();
    }

    /**
     * Updates the metrics in the top panel
     */
    public void updateTopPanel() {
        MainController.updateChildrenStyles(rootPane);

        if (Campaign.getInstance().getImpressionManager().hasLoadedInData()) {
            topTotalImpressions.setText(Campaign.getInstance().getImpressionManager().fetchMatches().size() + "");
            topTotalUniques.setText(Campaign.getInstance().getImpressionManager().getUniqueCount() + "");
        } else {
            topTotalImpressions.setText("n/a");
            topTotalUniques.setText("n/a");
        }

        if (Campaign.getInstance().getServerManager().hasLoadedInData()) {
            topTotalBounces.setText(Campaign.getInstance().getServerManager().fetchBounces().size() + "");
            //System.out.println(Campaign.getInstance().getServerManager().fetchMatch(bounceFilter));
        } else {
            topTotalBounces.setText("n/a");
        }

        if (Campaign.getInstance().getServerManager().hasLoadedInData()) {
            topBounceRate.setText(
                    Math.round(10000d
                            * Campaign.getInstance().getServerManager().fetchBounces().size()
                            / Campaign.getInstance().getServerManager().fetchMatches().size())
                            / 100d
                            + "%");

            topConversions.setText(Campaign.getInstance().getServerManager().fetchConversions(true).size() + "");
        } else {
            topBounceRate.setText("n/a");
            topConversions.setText("n/a");
        }

        if (Campaign.getInstance().getClickManager().hasLoadedInData()) {
            topTotalClicks.setText(Campaign.getInstance().getClickManager().fetchMatches(true).size() + "");
            topTotalCost.setText("£" + Math.round(Campaign.getInstance().getClickManager().getTotalCost(true)) / 100d);
            topCPC.setText("£" + Campaign.getInstance().getClickManager().getCPC(true));

        } else {
            topTotalCost.setText("n/a");

            topTotalClicks.setText("n/a");

            topCPC.setText("n/a");
        }

        if (Campaign.getInstance().getClickManager().hasLoadedInData() && Campaign.getInstance().getServerManager().hasLoadedInData()) {
            topCPA.setText("£" + Campaign.getInstance().getClickManager().getCPA(true));
        } else {
            topCPA.setText("n/a");
        }

        if (Campaign.getInstance().getClickManager().hasLoadedInData() && Campaign.getInstance().getImpressionManager().hasLoadedInData()) {
            topCTR.setText(Campaign.getInstance().getClickManager().getCTR(true) + "%");
            topCPM.setText("£" + Campaign.getInstance().getClickManager().getCPM(true));
        } else {
            topCTR.setText("n/a");
            topCPM.setText("n/a");
        }
    }

    public void updateCostsTab() {
        if (Campaign.getInstance().getClickManager().hasLoadedInData()) {
            XYChart.Series<String, Integer> series = new XYChart.Series<>();
            Campaign.getInstance().getClickManager().getCostFrequencies(true).entrySet().stream()
                    .sorted(Comparator.comparing(e -> e.getKey().getLower()))
                    .forEach(e -> series.getData().add(new Data<>(e.getKey().getFormatted(), e.getValue())));
            costHistogram.getData().clear();
            costHistogram.getData().add(series);
            //shows y values on hover
            for (Data<String, Integer> entry : series.getData()) {
                Tooltip tooltip = new Tooltip(entry.getYValue().toString());
                Tooltip.install(entry.getNode(), tooltip);
            }
        } else {
            costHistogram.getData().clear();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
      updateTopPanel();
      addFilters();

      //no need to set filters
      keyCountMetrics = new CountChart();
      keyFinancialMetrics = new FinancialChart();
      granularityKeyPicker.setItems(FXCollections.observableArrayList(
          Granularity.values()
      ));
      granularityKeyPicker.setValue(Granularity.Hourly);
      granularityKeyPicker.setOnAction(a -> keyCountMetrics.updateChart());
      granularityFinancialPicker.setItems(FXCollections.observableArrayList(
          Granularity.values()
      ));
      granularityFinancialPicker.setOnAction(a -> keyFinancialMetrics.updateChart());
      granularityFinancialPicker.setValue(Granularity.Hourly);
      keyCountMetrics.granularity().bind(granularityKeyPicker.valueProperty());
      keyFinancialMetrics.granularity().bind(granularityFinancialPicker.valueProperty());

      keyMetricPane.setCenter(keyCountMetrics);
//      keyCountMetrics.setMinHeight(keyMetricPane.getHeight() - granularityKeyPicker.getHeight());
      financialMetricPane.setCenter(keyFinancialMetrics);
//      keyFinancialMetrics.setMinHeight(financialMetricPane.getHeight() - granularityFinancialPicker.getHeight());


      costHistogram.setLegendVisible(false);
      updateDateFilters();
      startDatePicker.getEditor().setDisable(true);
      startDatePicker.getEditor().setOpacity(1);
      endDatePicker.getEditor().setDisable(true);
      endDatePicker.getEditor().setOpacity(1);

    LoadDataEvent.getListeners().add(loadDataEvent -> updateTopPanel());
    LoadDataEvent.getListeners().add(loadDataEvent -> updateCostsTab());
    LoadDataEvent.getListeners().add(loadDataEvent -> keyCountMetrics.updateChart());
    LoadDataEvent.getListeners().add(loadDataEvent -> keyFinancialMetrics.updateChart());
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
    FilterEvent.getListeners().add(filterEvent -> keyCountMetrics.updateChart());
    FilterEvent.getListeners().add(filterEvent -> keyFinancialMetrics.updateChart());
    FilterEvent.getListeners().add(filterEvent -> updateTopPanel());
    FilterEvent.getListeners().add(filterEvent -> updateCostsTab());
    AppearanceSettingsChangeEvent.getListeners().add(appearanceSettingsChangeEvent -> {
      MainController.updateChildrenStyles(rootPane);

      rootPane.applyCss();
      rootPane.layout();
    });

    PrintClickEvent.getListeners0().add(printMetricImageEvent -> {
      WritableImage image = PrintController.screenShot(rootPane);
      Settings.getInstance().setMetricImage(image);
    });
    PrintClickEvent.getListeners1().add(printMetricImageEvent -> {
      WritableImage image = PrintController.screenShot(rootPane);
      Settings.getInstance().setMetricImage(image);
    });
    PrintClickEvent.getListeners2().add(printMetricImageEvent -> {
      WritableImage image = PrintController.screenShot(rootPane);
      Settings.getInstance().setMetricImage(image);
    });


      financialMetricsExportButton.setOnMouseClicked(mouseEvent -> MainController.saveToImage(financialMetricPane, "Financial Metrics"));
      keyMetricsExportButton.setOnMouseClicked(mouseEvent -> MainController.saveToImage(keyMetricPane, "Key Metrics"));
      costsExportButton.setOnMouseClicked(mouseEvent -> MainController.saveToImage(costsPane, "Costs"));
    }

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
                            Campaign.getInstance().getImpressionFilter()
                                    .getClass().getMethod("add", enumClazz)
                                    .invoke(Campaign.getInstance().getImpressionFilter(), enumInst);
                        } else {
                            Campaign.getInstance().getImpressionFilter()
                                    .getClass().getMethod("remove", enumClazz)
                                    .invoke(Campaign.getInstance().getImpressionFilter(), enumInst);
                        }
                    }
                }

                new FilterEvent().call();
            }
        });
    }

    private void addFilters() {
        addFilter("Age Range", AgeRange.class, AgeRange.values());
        addFilter("Context", Context.class, Context.values());
        addFilter("Gender", Gender.class, Gender.values());
        addFilter("Income", Income.class, Income.values());
    }

    @FXML
    public void resetFilter() {
        filterScrollPaneContent.getChildren().forEach(node -> {
            if (node instanceof FilterOptions) {
                ((FilterOptions) node).untickAll();
            }
        });

        Campaign.getInstance().getImpressionFilter().reset();

        new FilterEvent().call();
    }

  /**
   * Updates the date filter from the date pickers
   */
  @FXML
  public void updateDateFilters() {
    Campaign.getInstance().getDateFilter().setStartDate(startDatePicker.getValue());
    Campaign.getInstance().getDateFilter().setEndDate(endDatePicker.getValue());
    new FilterEvent().call();
  }
}