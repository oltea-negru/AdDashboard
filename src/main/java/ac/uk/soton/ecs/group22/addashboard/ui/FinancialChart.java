package ac.uk.soton.ecs.group22.addashboard.ui;

import ac.uk.soton.ecs.group22.addashboard.controller.Campaign;
import ac.uk.soton.ecs.group22.addashboard.controller.filter.DateFilter;
import ac.uk.soton.ecs.group22.addashboard.controller.filter.ImpressionFilter;
import ac.uk.soton.ecs.group22.addashboard.data.csv.click.ClickEntry;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.ImpressionEntry;
import ac.uk.soton.ecs.group22.addashboard.data.csv.server.ServerEntry;
import com.sun.javafx.charts.Legend;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.LongStream;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;

public class FinancialChart extends BaseChart<String, Double> {

  public FinancialChart() {
    super();
    init();
  }

  public FinancialChart(ImpressionFilter impressionFilter, DateFilter dateFilter) {
    super(impressionFilter, dateFilter);
    init();
  }

  private void init() {
    setLegendVisible(true);
  }

  public void updateChart() {
    this.getData().clear();

    Map<String, long[]> financialMetrics = getFinancialMetrics();

    for (String key : financialMetrics.keySet()) {
      long[] misplacedArray = financialMetrics.get(key);

      double[] array = Arrays.stream(misplacedArray)
          .mapToDouble(val -> (double) val / 1000)
          .toArray();

      XYChart.Series<String, Double> series = new XYChart.Series<>();
      series.setName(key);

      if (hiddenGraphMetrics.contains(key)) {
        for (int i = 0; i < array.length; i++) {
          series.getData().add(new Data<>(getXValues()[i], 0D));
        }
      } else {
        for (int i = 0; i < array.length; i++) {
          series.getData().add(new Data<>(getXValues()[i], array[i]));
        }
      }

      this.getData().add(series);
      toolTipKeyMetricsDouble(series);

      this.setLegendVisible(true);

      if (hiddenGraphMetrics.contains(key)) {
        series.getNode().setVisible(false);

        for (XYChart.Data<String, Double> d : series.getData()) {
          if (d.getNode() != null) {
            d.getNode().setVisible(false);
          }
        }
      }
    }

    setToggleableDoubleChart(this);
  }

  private void toolTipKeyMetricsDouble(XYChart.Series<String, Double> series) {
    for (Data<String, Double> entry : series.getData()) {
      Tooltip tooltip = new Tooltip("£" + entry.getYValue().toString());
      Tooltip.install(entry.getNode(), tooltip);
    }
  }

  private Map<String, long[]> getFinancialMetrics() {
    Map<String, long[]> metrics = new HashMap<>();

    long[] clickArray = null;
    long[] impressionArray = null;
    long[] conversionArray = null;
    long[] clickCostArray = null;

    if (dateFilter != null && impressionFilter != null) {
      if(Campaign.getInstance().getClickManager().hasLoadedInData()) clickArray = periodMetric(Campaign.getInstance().getClickManager().fetchMatches(impressionFilter, dateFilter).stream().mapToLong(ClickEntry::getDate));
      if(Campaign.getInstance().getImpressionManager().hasLoadedInData()) impressionArray = periodMetric(Campaign.getInstance().getImpressionManager().fetchMatches(impressionFilter, dateFilter).stream().mapToLong(ImpressionEntry::getDate));
      if(Campaign.getInstance().getServerManager().hasLoadedInData()) conversionArray = periodMetric(Campaign.getInstance().getServerManager().fetchMatches(impressionFilter, dateFilter).stream().filter(ServerEntry::isConversion).mapToLong(ServerEntry::getExitDate));
      if(Campaign.getInstance().getClickManager().hasLoadedInData()) clickCostArray = periodMetric(Campaign.getInstance().getClickManager().fetchMatches(impressionFilter, dateFilter).stream()
          .flatMap(entry -> {
            List<Long> date = new ArrayList<>();

            for(double i = 0; i < entry.getClickCost(); i += 0.01) {
              date.add(entry.getDate());
            }

            return date.stream();
          }).mapToLong(entry -> entry));
    } else {
      if(Campaign.getInstance().getClickManager().hasLoadedInData()) clickArray = periodMetric(Campaign.getInstance().getClickManager().fetchMatches(true).stream().mapToLong(ClickEntry::getDate));
      if(Campaign.getInstance().getImpressionManager().hasLoadedInData()) impressionArray = periodMetric(Campaign.getInstance().getImpressionManager().fetchMatches().stream().mapToLong(ImpressionEntry::getDate));
      if(Campaign.getInstance().getServerManager().hasLoadedInData()) conversionArray = periodMetric(Campaign.getInstance().getServerManager().fetchMatches().stream().filter(ServerEntry::isConversion).mapToLong(ServerEntry::getExitDate));
      // $0.001 is equal to 1 in the array.
      if(Campaign.getInstance().getClickManager().hasLoadedInData()) clickCostArray = periodMetric(Campaign.getInstance().getClickManager().fetchMatches(true).stream()
          .flatMap(entry -> {
            List<Long> date = new ArrayList<>();

            for(double i = 0; i < entry.getClickCost(); i += 0.01) {
              date.add(entry.getDate());
            }

            return date.stream();
          }).mapToLong(entry -> entry));
    }

    if(clickArray != null && impressionArray != null) {
      /* CTR */
      long[] ctrArray = new long[impressionArray.length];

      for (int i = 0; i < Integer.min(clickArray.length, impressionArray.length); i++) {
        if (impressionArray[i] == 0 || clickArray[i] == 0) {
          ctrArray[i] = 0;
        } else {
          ctrArray[i] = (long) (((double) clickArray[i] / (double) impressionArray[i]) * 1000);
        }
      }

      metrics.put("CTR", ctrArray);
    }

    if(clickArray != null && conversionArray != null) {
      /* CPA */
      long[] cpaArray = new long[conversionArray.length];

      for (int i = 0; i < Integer.min(clickCostArray.length, conversionArray.length); i++) {
        if (clickCostArray[i] == 0 || conversionArray[i] == 0) {
          cpaArray[i] = 0;
        } else {
          cpaArray[i] = (long) (((double) clickCostArray[i] / (double) conversionArray[i]) * 1000);
        }
      }

      metrics.put("CPA", cpaArray);
    }

    if(clickCostArray != null && conversionArray != null) {
      /* CPC */
      long[] cpcArray = new long[clickCostArray.length];

      for (int i = 0; i < Integer.min(clickCostArray.length, clickArray.length); i++) {
        if (clickCostArray[i] == 0 || conversionArray[i] == 0) {
          cpcArray[i] = 0;
        } else {
          cpcArray[i] = (long) (((double) clickCostArray[i] / (double) clickArray[i]) * 1000);
        }
      }

      metrics.put("CPC", cpcArray);
    }

    if(clickCostArray != null && impressionArray != null) {
      /* CPM */
      long[] cpmArray = new long[clickCostArray.length];

      for (int i = 0; i < Integer.min(clickCostArray.length, impressionArray.length); i++) {
        if (clickCostArray[i] == 0 || conversionArray[i] == 0) {
          cpmArray[i] = 0;
        } else {
          cpmArray[i] = (long) (((double) clickCostArray[i] / ((double) impressionArray[i] * 1000))
              * 1000);
        }
      }

      metrics.put("CPM", cpmArray);
    }

    if(clickArray != null) {
      if (dateFilter != null && impressionFilter != null) {
        /* Total Cost */
        metrics.put("Total Cost", periodMetric(
            Campaign.getInstance().getClickManager().fetchMatches(impressionFilter, dateFilter)
                .stream()
                .flatMap(entry -> {
                  List<Long> date = new ArrayList<>();

                  for (double i = 0; i < entry.getClickCost(); i += 0.01) {
                    date.add(entry.getDate());
                  }

                  return date.stream();
                }).mapToLong(entry -> entry)));

      } else {
        /* Total Cost */
        metrics.put("Total Cost",
            periodMetric(Campaign.getInstance().getClickManager().fetchMatches(true).stream()
                .flatMap(entry -> {
                  List<Long> date = new ArrayList<>();

                  for (double i = 0; i < entry.getClickCost(); i += 0.01) {
                    date.add(entry.getDate());
                  }

                  return date.stream();
                }).mapToLong(entry -> entry)));
      }
    }

    return metrics;
  }

  private void setToggleableDoubleChart(LineChart<String, Double> chart) {
    for (Node node : chart.getChildrenUnmodifiable()) {
      if (!(node instanceof Legend)) {
        continue;
      }

      Legend legend = (Legend) node;

      for (Legend.LegendItem li : legend.getItems()) {
        for (XYChart.Series<String, Double> series : chart.getData()) {
          if (!series.getName().equals(li.getText())) {
            continue;
          }

          li.getSymbol().setCursor(Cursor.HAND);

          if(!series.getNode().isVisible()) {
            li.getSymbol().getStyleClass().add("disabled-legend");
          } else {
            li.getSymbol().getStyleClass().remove("disabled-legend");
          }

          li.getSymbol().setOnMouseClicked(me -> {
            if (me.getButton() != MouseButton.PRIMARY) {
              return;
            }

            if(hiddenGraphMetrics.contains(series.getName())) {
              hiddenGraphMetrics.remove(series.getName());
            } else {
              hiddenGraphMetrics.add(series.getName());
            }

            updateChart();
          });

          break;
        }
      }
    }
  }
}
