package ac.uk.soton.ecs.group22.addashboard;

import ac.uk.soton.ecs.group22.addashboard.controller.AbstractManager;
import ac.uk.soton.ecs.group22.addashboard.controller.Campaign;
import ac.uk.soton.ecs.group22.addashboard.controller.FileType;
import ac.uk.soton.ecs.group22.addashboard.data.csv.AbstractLoader;
import ac.uk.soton.ecs.group22.addashboard.data.csv.click.ClickLoader;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.ImpressionLoader;
import ac.uk.soton.ecs.group22.addashboard.data.csv.server.ServerLoader;
import ac.uk.soton.ecs.group22.addashboard.events.AppearanceSettingsChangeEvent;
import ac.uk.soton.ecs.group22.addashboard.events.ClearDataEvent;
import ac.uk.soton.ecs.group22.addashboard.events.FilterEvent;
import ac.uk.soton.ecs.group22.addashboard.events.InvalidFileEvent;
import ac.uk.soton.ecs.group22.addashboard.events.LoadDataEvent;
import ac.uk.soton.ecs.group22.addashboard.ui.LoadFile;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoadDataController implements Initializable {

  @FXML
  private BorderPane layout;

  @FXML
  private VBox container;

  private Map<FileType, File> selectedFiles = new HashMap<>();

  @FXML
  public void load() throws IOException {
    if(selectedFiles.isEmpty()) {
      Alert alertType=new Alert(AlertType.WARNING);

      alertType.setTitle("No files loaded");
      alertType.setContentText("No files have been loaded. AdDashboard will not be functional until at least a file has been loaded.");

      alertType.show();

      return;
    }

    if(selectedFiles.size() < 3) {
      Alert alertType=new Alert(AlertType.WARNING);

      alertType.setTitle("Not all files loaded");
      alertType.setContentText("Not all files have been loaded, therefore AdDashboard will have some disabled features.");

      alertType.show();
    }

    loadFiles();
  }

  @FXML
  public void clearData() {

    Campaign.getInstance().getImpressionManager().clear();
    Campaign.getInstance().getClickManager().clear();
    Campaign.getInstance().getServerManager().clear();

    new ClearDataEvent().call();

    // Force-update all data
    new FilterEvent().call();
  }

  private void loadFiles() {
    for(FileType fileType : FileType.values()) {
      File file = selectedFiles.get(fileType);

      if(file == null) {
        continue;
      }

      AbstractManager manager = Campaign.getInstance().getManager(fileType);
      AbstractLoader loader = manager.createLoader(file);

      try {
        if(!loader.load()) {
          Alert alertType=new Alert(AlertType.ERROR);

          alertType.setTitle("Invalid file");
          alertType.setContentText("Unable to load in " + fileType.name() + " because it's file is of an invalid format.");

          alertType.show();

          new InvalidFileEvent(fileType).call();
          selectedFiles.remove(fileType);
          continue;
        }
      } catch (Exception e) {
        e.printStackTrace();
        continue;
      }

      if(fileType == FileType.IMPRESSION) {
        Campaign.getInstance().getImpressionManager().setImpressions((ImpressionLoader) loader);
      } else if(fileType == FileType.CLICK) {
        Campaign.getInstance().getClickManager().setClicks((ClickLoader) loader);
      } else if(fileType == FileType.SERVER) {
        Campaign.getInstance().getServerManager().setServer((ServerLoader) loader);
      }
    }

    //File type is not important? we only need to call the events once, and they never use the file type
    LoadDataEvent loadDataEvent = new LoadDataEvent(FileType.IMPRESSION);
    loadDataEvent.call();

    // Weird glitch that causes GUI values to not update, seems to fix it.
    FilterEvent filterEvent = new FilterEvent();
    filterEvent.call();
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    LoadFile impressionLoad = new LoadFile("Impressions Log", FileType.IMPRESSION, getExistingPath(FileType.IMPRESSION)) {
      @Override
      public boolean onSelect(File file) {
        selectedFiles.put(FileType.IMPRESSION, file);
        return true;
      }
    };

    LoadFile clicksLoad = new LoadFile("Clicks Log", FileType.CLICK, getExistingPath(FileType.CLICK)) {
      @Override
      public boolean onSelect(File file) {
        selectedFiles.put(FileType.CLICK, file);
        return true;
      }
    };

    LoadFile serverLoad = new LoadFile("Server Log", FileType.SERVER, getExistingPath(FileType.SERVER)) {
      @Override
      public boolean onSelect(File file) {
        selectedFiles.put(FileType.SERVER, file);
        return true;
      }
    };

    container.getChildren().add(impressionLoad);
    container.getChildren().add(clicksLoad);
    container.getChildren().add(serverLoad);

    ClearDataEvent.getListeners().add(clearDataEvent -> {
      selectedFiles.clear();
      impressionLoad.clear();
      clicksLoad.clear();
      serverLoad.clear();
    });

    AppearanceSettingsChangeEvent.getListeners().add(appearanceSettingsChangeEvent -> {
      MainController.updateChildrenStyles(layout);

      layout.applyCss();
      layout.layout();
    });
  }

  private String getExistingPath(FileType fileType) {
    return selectedFiles.get(fileType) != null ? selectedFiles.get(fileType).toString() : "";
  }

}