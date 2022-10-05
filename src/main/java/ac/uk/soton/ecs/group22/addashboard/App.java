package ac.uk.soton.ecs.group22.addashboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.stage.StageStyle;
import lombok.Getter;

/**
 * JavaFX App
 */
public class App extends Application {

  @Getter
  private static Scene primary;

  @Getter
  public static Parent root;




  @Override
  public void start(Stage stage) throws IOException {
    primary = new Scene(loadFXML("main"), 800, 550);
    root = primary.getRoot();

    stage.setScene(primary);
    stage.setResizable(false);
    stage.show();
  }

  static void setRoot(String fxml) throws IOException {
    root = loadFXML(fxml);
    primary.setRoot(root);
  }

  private static Parent loadFXML(String fxml) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
    return fxmlLoader.load();
  }

  public static void main(String[] args) {
    launch();
  }

  public static Stage openStage(Scene scene) {
    Stage stage = new Stage();
    stage.setScene(scene);
    stage.show();

    return stage;
  }

  public static Scene loadScene(String fxml, int width, int height) throws IOException {
    return new Scene(loadFXML(fxml), width, height);
  }

}