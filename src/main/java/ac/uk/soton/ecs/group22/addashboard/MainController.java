package ac.uk.soton.ecs.group22.addashboard;

import ac.uk.soton.ecs.group22.addashboard.controller.Settings;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class MainController implements Initializable {

  @FXML
  private TabPane mainTabPane;

  @FXML
  private BorderPane metricTabPane;
  @FXML
  private MetricController metricController;

  @FXML
  private VBox compareTabPane;
  @FXML
  private CompareController compareController;

  @FXML
  private BorderPane dataTabPane;
  @FXML
  private LoadDataController loadDataController;

  @FXML
  private TabPane settingsTabPane;
  @FXML
  private SettingController settingController;

  @FXML
  private BorderPane printTabPane;
  @FXML
  private PrintController PrintController;


  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
  }

  /**
   * used for saving metric and compare image
   * @param parent the current node
   * @param file
   */
  public static void printImage(Parent parent, File file) {
    Alert alertTypeE = new Alert(AlertType.ERROR);
    WritableImage image = ac.uk.soton.ecs.group22.addashboard.PrintController.screenShot(parent);
    try {
      ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
    } catch (IOException ex) {
      alertTypeE.setTitle("Failed to Save As Image");
      alertTypeE.setContentText("Unknown Error");
      alertTypeE.show();
    }
  }

  /**
   * used for saving as PDF
   * @param i which PDF type prefer "Each has a page","Merge into one page","Split as PDFs"
   * @param fileName filename to save
   */
  public static void savePDF (int i,String fileName) {
    File file = new File(Settings.getInstance().getImageFilePath() + "/" + fileName + ".png");
    File file1 = new File(Settings.getInstance().getImageFilePath() + "/" + "1.png");
    File file2 = new File(Settings.getInstance().getImageFilePath() + "/" + "2.png");
    Alert alertTypeW = new Alert(AlertType.WARNING);
    Alert alertTypeE = new Alert(AlertType.ERROR);

    if (Settings.getInstance().getImageFilePath() == null) {
      alertTypeW.setTitle("Invalid File Path");
      alertTypeW.setContentText("Please click 'Select Save Location' button first to input a valid filePath to save image");
      alertTypeW.show();
    }else{
      try {
        if (fileName.equals("All")){
          ImageIO.write(SwingFXUtils.fromFXImage(Settings.getInstance().getMetricImage(), null), "png", file1);
          ImageIO.write(SwingFXUtils.fromFXImage(Settings.getInstance().getCompareImage(), null), "png", file2);
        }else if (fileName.equals("Metric")){
          ImageIO.write(SwingFXUtils.fromFXImage(Settings.getInstance().getMetricImage(), null), "png", file);
        } else if (fileName.equals("Compare")){
          ImageIO.write(SwingFXUtils.fromFXImage(Settings.getInstance().getCompareImage(), null), "png", file);
        }
      } catch (IOException ex) {
        alertTypeE.setTitle("Failed to Save As Image");
        alertTypeE.setContentText("Unknown Error");
        alertTypeE.show();
      }
    }

    PDDocument doc = new PDDocument();
    PDPage page = new PDPage();
    PDPageContentStream content;
    if (i == 2) {
      try {
        PDImageXObject pdimage = PDImageXObject.createFromFile(
            Settings.getInstance().getImageFilePath() + "/" + fileName + ".png", doc);
        content = new PDPageContentStream(doc, page);
        content.drawImage(pdimage, 10, 410, 533, 366);
        content.close();
        doc.addPage(page);
        doc.save(Settings.getInstance().getImageFilePath() + "/" + fileName + ".pdf");
        doc.close();
        file.delete();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }else if (i == 1){
      try {
        PDImageXObject pdimage1 = PDImageXObject.createFromFile(
            Settings.getInstance().getImageFilePath() + "/" + "1.png", doc);
        PDImageXObject pdimage2 = PDImageXObject.createFromFile(
            Settings.getInstance().getImageFilePath() + "/" + "2.png", doc);
        content = new PDPageContentStream(doc, page);
        content.drawImage(pdimage1, 10, 410, 533, 366);
        content.drawImage(pdimage2, 10, 10, 533, 366);
        content.close();
        doc.addPage(page);
        doc.save(Settings.getInstance().getImageFilePath() + "/" + "all.pdf");
        doc.close();
        file1.delete();
        file2.delete();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }else if (i == 0){
      try {
        PDImageXObject pdimage1 = PDImageXObject.createFromFile(
            Settings.getInstance().getImageFilePath() + "/" + "1.png", doc);
        PDImageXObject pdimage2 = PDImageXObject.createFromFile(
            Settings.getInstance().getImageFilePath() + "/" + "2.png", doc);
        content = new PDPageContentStream(doc, page);
        content.drawImage(pdimage1, 10, 410, 533, 366);
        content.close();

        PDPage page2 = new PDPage();
        content = new PDPageContentStream(doc, page2);
        content.drawImage(pdimage2, 10, 410, 533, 366);
        content.close();
        doc.addPage(page);
        doc.addPage(page2);
        doc.save(Settings.getInstance().getImageFilePath() + "/" + "all.pdf");
        doc.close();
        file1.delete();
        file2.delete();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    Settings.getInstance().setMetricImage(null);
    Settings.getInstance().setCompareImage(null);
  }

  /**
   *
   * @param parent
   */
  public static void updateChildrenStyles(Parent parent) {
    for(Node node : parent.getChildrenUnmodifiable()) {
      if(node instanceof Parent) {
        updateChildrenStyles((Parent) node);
      }
      updateStyles(node);
    }
    updateStyles(parent);
  }

  /**
   *
   * @param node
   */
  private static void updateStyles(Node node) {
    if(node.getStyleClass().contains("primary-colour")) {
      node.setStyle("-fx-background-color: " + Settings.colorToHex(Settings.getInstance().getPrimaryColor()) + ";");
    }

    if(node.getStyleClass().contains("secondary-colour")) {
      node.setStyle("-fx-background-color: " + Settings.colorToHex(Settings.getInstance().getSecondaryColor()) + ";");
    }

    if(node.getStyleClass().contains("background-colour")) {
      node.setStyle("-fx-background-color: " + Settings.colorToHex(Settings.getInstance().getBackgroundColor()) + ";");
    }

    if(node.getStyleClass().contains("font-colour")) {
      node.setStyle("-fx-fill: " + Settings.colorToHex(Settings.getInstance().getFontColor()) + ";");
    }
  }

  /**
   * generate a file chooser to get save image path and generate a image to the path
   * @param pane
   * @param defaultFileName
   */
  public static void saveToImage(Pane pane, String defaultFileName) {
    FileChooser directoryChooser = new FileChooser();
    directoryChooser.setTitle("Specify Image Save Location");
    directoryChooser.setInitialFileName(defaultFileName + ".png");

    ExtensionFilter extensionFilter = new ExtensionFilter("PNG", "*.png");

    directoryChooser.setSelectedExtensionFilter(extensionFilter);
    File file = directoryChooser.showSaveDialog(App.getRoot().getScene().getWindow());

    if (file != null) {
      MainController.printImage(pane, file);
    }
  }

}
