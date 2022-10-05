package ac.uk.soton.ecs.group22.addashboard;

import ac.uk.soton.ecs.group22.addashboard.controller.Settings;
import ac.uk.soton.ecs.group22.addashboard.events.AppearanceSettingsChangeEvent;
import ac.uk.soton.ecs.group22.addashboard.events.PrintClickEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import lombok.Getter;

/**
 * <h1> Compare Controller </h1>
 * Controller for the compare tab
 * @see Initializable
 */
public class CompareController implements Initializable {

  @FXML
  protected VBox layout;
  @FXML
  @Getter
  protected AnchorPane topPane;
  @FXML
  @Getter
  protected AnchorPane bottomPane;
  @FXML
  protected Button exportButton;

  /**
   * initialises on load
   * @param url
   * @param resourceBundle
   */
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    AppearanceSettingsChangeEvent.getListeners().add(appearanceSettingsChangeEvent -> {
      MainController.updateChildrenStyles(layout);
      layout.applyCss();
      layout.layout();
    });

    exportButton.setOnAction(e -> MainController.saveToImage(layout, "Compare Graphs"));

    PrintClickEvent.getListeners0().add(printMetricImageEvent -> {
      WritableImage image = PrintController.screenShot(layout);
      Settings.getInstance().setCompareImage(image);
    });
    PrintClickEvent.getListeners1().add(printMetricImageEvent -> {
      WritableImage image = PrintController.screenShot(layout);
      Settings.getInstance().setCompareImage(image);
    });
    PrintClickEvent.getListeners2().add(printMetricImageEvent -> {
      WritableImage image = PrintController.screenShot(layout);
      Settings.getInstance().setCompareImage(image);
    });
  }

}
