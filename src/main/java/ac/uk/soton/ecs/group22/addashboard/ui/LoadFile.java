package ac.uk.soton.ecs.group22.addashboard.ui;

import ac.uk.soton.ecs.group22.addashboard.controller.FileType;
import ac.uk.soton.ecs.group22.addashboard.events.InvalidFileEvent;
import ac.uk.soton.ecs.group22.addashboard.events.LoadDataEvent;
import java.io.File;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import lombok.SneakyThrows;

public abstract class LoadFile extends BorderPane {

  private final String name;
  private final FileType fileType;
  private final FileChooser fileChooser;
  private TextField pathField;

  protected LoadFile(String name, FileType fileType, String existingPath) {
    this.name = name;
    this.fileType = fileType;
    this.fileChooser = new FileChooser();

    init(existingPath);

    InvalidFileEvent.getListeners().add(invalidFileEvent -> {
      if(invalidFileEvent.getFileType() == fileType) {
        pathField.setText("");
      }
    });

    setPadding(new Insets(5, 0, 5, 0));
  }

  private void init(String existingPath) {
    Text text = new Text(name);
    text.getStyleClass().add("file-name");

    this.setLeft(text);

    HBox hBox = new HBox();
    this.setRight(hBox);

    this.pathField = new TextField(existingPath);
    pathField.setEditable(false);
    pathField.setDisable(true);

    hBox.getChildren().add(pathField);

    fileChooser.setTitle(name);
    fileChooser.setSelectedExtensionFilter(new ExtensionFilter("All Files", "*.xml"));

    Button button = new Button("Select");
    button.setPadding(new Insets(5, 20, 5, 20));

    VBox vBox = new VBox();
    vBox.setPadding(new Insets(0, 0, 0, 10));
    vBox.getChildren().add(button);

    hBox.getChildren().add(vBox);

    button.setOnAction(actionEvent -> {
      File file = fileChooser.showOpenDialog(getScene().getWindow());

      if (file == null) {
        return;
      }

      if(!onSelect(file)) {
        return;
      }

      pathField.setText(file.toString());

      LoadDataEvent loadDataEvent = new LoadDataEvent(fileType);
      loadDataEvent.call();
    });
  }

  public void clear() {
    if (pathField != null) {pathField.clear();}
  }

  public abstract boolean onSelect(File file);

}