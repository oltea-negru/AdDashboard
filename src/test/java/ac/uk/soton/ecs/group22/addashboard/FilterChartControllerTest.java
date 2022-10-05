package ac.uk.soton.ecs.group22.addashboard;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.assertions.api.Assertions.assertThat;

import ac.uk.soton.ecs.group22.addashboard.ui.BaseChart;
import ac.uk.soton.ecs.group22.addashboard.ui.CountChart;
import ac.uk.soton.ecs.group22.addashboard.ui.FinancialChart;
import ac.uk.soton.ecs.group22.addashboard.ui.Granularity;
import ac.uk.soton.ecs.group22.addashboard.ui.MetricType;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

@ExtendWith(ApplicationExtension.class)
class FilterChartControllerTest {


  @Start
  void start(Stage stage) throws IOException {

    Scene scene = App.loadScene("filter-chart", 800, 250);
    stage.setScene(scene);
    stage.show();

  }

  @AfterAll
  static void tearDown() throws TimeoutException {
    FxToolkit.hideStage();
  }

  @Test
  void defaultedToKeyChart(FxRobot robot) {
    assertEquals(robot.lookup("#chartPane").queryAs(BorderPane.class).getCenter(), robot.lookup("#countChart").queryAs(CountChart.class));
  }

  @Test
  void ChangeToFinancialChart(FxRobot robot) {
    robot.clickOn("#metricPicker");
    robot.type(KeyCode.DOWN);
    robot.type(KeyCode.ENTER);
    assertEquals(robot.lookup("#chartPane").queryAs(BorderPane.class).getCenter(), robot.lookup("#financialChart").queryAs(FinancialChart.class));
  }

  @Test
  void GranularityChangesChart(FxRobot robot) {
    robot.clickOn("#granularityPicker");
    robot.type(KeyCode.DOWN);
    robot.type(KeyCode.ENTER);
    assertEquals(robot.lookup("#countChart").queryAs(BaseChart.class).granularity().getValue(), Granularity.Daily);
  }

}