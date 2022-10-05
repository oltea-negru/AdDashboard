package ac.uk.soton.ecs.group22.addashboard.ui;

import ac.uk.soton.ecs.group22.addashboard.controller.Campaign;
import ac.uk.soton.ecs.group22.addashboard.controller.filter.DateFilter;
import ac.uk.soton.ecs.group22.addashboard.controller.filter.ImpressionFilter;
import ac.uk.soton.ecs.group22.addashboard.data.csv.click.ClickEntry;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.ImpressionEntry;
import ac.uk.soton.ecs.group22.addashboard.data.csv.server.ServerEntry;
import com.sun.javafx.charts.Legend;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.LongStream;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import lombok.Getter;

public class CountChart extends BaseChart<String, Long> {

  public CountChart(ImpressionFilter impressionFilter, DateFilter dateFilter) {
    super(impressionFilter, dateFilter);
    init();
  }

  public CountChart() {
    super();
    init();
  }

  private void init() {
    setLegendVisible(true);
  }

  public void updateChart() {
    this.getData().clear();

    Map<String, long[]> metrics = getCountMetrics();

    for (String key : metrics.keySet()) {
      long[] array = metrics.get(key);

      XYChart.Series<String, Long> series = new XYChart.Series<>();
      series.setName(key);

      if (hiddenGraphMetrics.contains(key)) {
        for (int i = 0; i < array.length; i++) {
          series.getData().add(new Data<>(getXValues()[i], 0L));
        }
      } else {
        for (int i = 0; i < array.length; i++) {
          series.getData().add(new Data<>(getXValues()[i], array[i]));
        }
      }

      this.getData().add(series);
      toolTipKeyMetricsLong(series);

      this.setLegendVisible(true);

      if (hiddenGraphMetrics.contains(key)) {
        series.getNode().setVisible(false);

        for (XYChart.Data<String, Long> d : series.getData()) {
          if (d.getNode() != null) {
            d.getNode().setVisible(false);
          }
        }
      }
    }

    setToggleableLongChart(this);
  }

  private class CountData {
    /*@Getter
    private final LongStream stream;
    private final long[]*/
  }


  private Map<String, long[]> getCountMetrics() {
    Map<String, long[]> metrics = new HashMap<>();
    long[] clickArray = null;
    long[] bounceArray = null;

    if (dateFilter != null && impressionFilter != null) {
      if(Campaign.getInstance().getImpressionManager().hasLoadedInData()) metrics.put("Impressions", periodMetric(Campaign.getInstance().getImpressionManager().fetchMatches(impressionFilter, dateFilter).stream().mapToLong(ImpressionEntry::getDate)));
      if(Campaign.getInstance().getClickManager().hasLoadedInData()) metrics.put("Clicks", periodMetric(Campaign.getInstance().getClickManager().fetchMatches(impressionFilter, dateFilter).stream().mapToLong(ClickEntry::getDate)));
      if(Campaign.getInstance().getImpressionManager().hasLoadedInData()) metrics.put("Uniques", periodMetric(Campaign.getInstance().getImpressionManager().fetchUniqueImpressions(impressionFilter, dateFilter).stream().mapToLong(ImpressionEntry::getDate)));
      if(Campaign.getInstance().getServerManager().hasLoadedInData()) metrics.put("Bounces", periodMetric(Campaign.getInstance().getServerManager().fetchBounces(impressionFilter, dateFilter).stream().mapToLong(ServerEntry::getEntryDate)));
      if(Campaign.getInstance().getServerManager().hasLoadedInData()) metrics.put("Conversions", periodMetric(Campaign.getInstance().getServerManager().fetchConversions(impressionFilter, dateFilter).stream().mapToLong(ServerEntry::getEntryDate)));

      if (Campaign.getInstance().getServerManager().hasLoadedInData() && Campaign.getInstance().getClickManager().hasLoadedInData())
      {
        clickArray = periodMetric(Campaign.getInstance().getClickManager().fetchMatches(impressionFilter, dateFilter)
                .stream().mapToLong(ClickEntry::getDate));
        bounceArray = periodMetric(Campaign.getInstance().getServerManager().fetchBounces(impressionFilter, dateFilter)
                .stream().mapToLong(ServerEntry::getEntryDate));
      }
    } else {
      if(Campaign.getInstance().getImpressionManager().hasLoadedInData()) metrics.put("Impressions", periodMetric(Campaign.getInstance().getImpressionManager().fetchMatches().stream().mapToLong(ImpressionEntry::getDate)));
      if(Campaign.getInstance().getClickManager().hasLoadedInData()) metrics.put("Clicks", periodMetric(Campaign.getInstance().getClickManager().fetchMatches(true).stream().mapToLong(ClickEntry::getDate)));
      if(Campaign.getInstance().getImpressionManager().hasLoadedInData()) metrics.put("Uniques", periodMetric(Campaign.getInstance().getImpressionManager().fetchUniqueImpressions().stream().mapToLong(ImpressionEntry::getDate)));
      if(Campaign.getInstance().getServerManager().hasLoadedInData()) metrics.put("Bounces", periodMetric(Campaign.getInstance().getServerManager().fetchBounces().stream().mapToLong(ServerEntry::getEntryDate)));
      if(Campaign.getInstance().getServerManager().hasLoadedInData()) metrics.put("Conversions", periodMetric(Campaign.getInstance().getServerManager().fetchConversions(true).stream().mapToLong(ServerEntry::getEntryDate)));

      if (Campaign.getInstance().getServerManager().hasLoadedInData() && Campaign.getInstance().getClickManager().hasLoadedInData())
      {
        clickArray = periodMetric(Campaign.getInstance().getClickManager().fetchMatches(true).stream().mapToLong(ClickEntry::getDate));
        bounceArray = periodMetric(Campaign.getInstance().getServerManager().fetchBounces().stream().mapToLong(ServerEntry::getEntryDate));
      }
    }

    if(clickArray != null && bounceArray != null) {
      long[] bounceRateArray = new long[clickArray.length];

      for (int i = 0; i < Integer.min(clickArray.length, bounceArray.length); i++) {
        if (bounceArray[i] == 0 || clickArray[i] == 0) {
          bounceRateArray[i] = 0;
        } else {
          bounceRateArray[i] = (long) (((double) bounceArray[i] / (double) clickArray[i]) * 1000);
        }
      }

      metrics.put("Bounce Rate", bounceRateArray);
    }

    return metrics;
  }

  /**
   * Enables tooltips on hover for y values of a graph
   *
   * @param series Graph to apply tooltip to
   */
  private void toolTipKeyMetricsLong(XYChart.Series<String, Long> series) {
    for (Data<String, Long> entry : series.getData()) {
      Tooltip tooltip = new Tooltip(entry.getYValue().toString());
      Tooltip.install(entry.getNode(), tooltip);
    }
  }

  private void setToggleableLongChart(LineChart<String, Long> chart) {
    for (Node node : chart.getChildrenUnmodifiable()) {
      if (!(node instanceof Legend)) {
        continue;
      }

      Legend legend = (Legend) node;

      for (Legend.LegendItem li : legend.getItems()) {
        for (XYChart.Series<String, Long> series : chart.getData()) {
          if (!series.getName().equals(li.getText())) {
            continue;
          }

          if(!series.getNode().isVisible()) {
            li.getSymbol().getStyleClass().add("disabled-legend");
          } else {
            li.getSymbol().getStyleClass().remove("disabled-legend");
          }

          li.getSymbol().setCursor(Cursor.HAND);

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
