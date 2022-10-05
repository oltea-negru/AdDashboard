package ac.uk.soton.ecs.group22.addashboard;

import javafx.fxml.FXMLLoader;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CompareTabTest {

  private final CompareController compareController;

  @SneakyThrows
  public CompareTabTest() {
    FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("compare.fxml"));
    compareController = fxmlLoader.getController();
  }

  @DisplayName("Test filter graphs are loaded")
  @Test
  public void filterGraphsLoaded() {

  }





}
