package ac.uk.soton.ecs.group22.addashboard;

import ac.uk.soton.ecs.group22.addashboard.controller.Settings;
import ac.uk.soton.ecs.group22.addashboard.events.AppearanceSettingsChangeEvent;
import ac.uk.soton.ecs.group22.addashboard.events.PrintClickEvent;
import java.awt.Desktop;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Transform;
import javafx.stage.DirectoryChooser;
import javax.print.PrintService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;

public class PrintController implements Initializable {
  @FXML
  private BorderPane printPane;
  @FXML
  private Button saveBtn = new Button();
  @FXML
  private CheckBox saveMetric = new CheckBox();
  @FXML
  private CheckBox saveCompare = new CheckBox();
  @FXML
  private TextField pathText = new TextField();
  @FXML
  private VBox vBox = new VBox();
  @FXML
  private ChoiceBox choiceBox = new ChoiceBox();


  public PrintController() {
  }

  /**
   * initialize every variable from Settings which store the settings
   * @param url
   * @param resourceBundle
   */
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    AppearanceSettingsChangeEvent.getListeners().add(appearanceSettingsChangeEvent -> {
      MainController.updateChildrenStyles(printPane);
      printPane.applyCss();
      printPane.layout();
    });
    vBox.setPrefWidth(Settings.getInstance().getWindowXY()[0]);
    ObservableList<String> options = FXCollections.observableArrayList("Selected tabs on a separate page","Merge tabs into one page","Selected tabs as separate PDFs");
    choiceBox.setValue("Selected tabs on a separate page"); // this statement shows default value
    choiceBox.setItems(options); // this statement adds all values in choiceBox
    saveMetric.setSelected(true);

  }


  /**
   * when click "save PDF" button in Print will call this method
   */
  @FXML
  private void savePDF() {
    save();
  }

  /**
   * when click "print PDF" button in Print will call this method
   */
  @FXML
  private void printPDF() {
    save();
    if (saveMetric.isSelected() && saveCompare.isSelected()) {
      printPDF(Settings.getInstance().getImageFilePath() + "/" + "all.pdf");
    }else if (saveMetric.isSelected()){
      printPDF(Settings.getInstance().getImageFilePath() + "/" + "Metric.pdf");
    }else if (saveCompare.isSelected()){
      printPDF(Settings.getInstance().getImageFilePath() + "/" + "Compare.pdf");
    }
  }

  /**
   * Prints the document in paper.
   */
  public static void printPDF(String filePath) {
    Alert alertType = new Alert(AlertType.WARNING);
    try {
        File pdfFile = new File(filePath);
        if (pdfFile.exists()) {
          if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(pdfFile);
          } else {
            alertType.setTitle("Awt Desktop exception");
            alertType.setContentText("Awt Desktop is not supported!");
            alertType.show();
          }
        } else {
          alertType.setTitle("File is not exists!");
          alertType.setContentText("File is not exists!");
          alertType.show();
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }

  }

  /**
   * when click "choose save location" button in Print will call this method
   * 'Invalid FilePath Warning' normally does not appear because we use fileChooser not user input
   */
  @FXML
  private void selectPath() {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    directoryChooser.setTitle("Choose PDF Save Location");
    File file = directoryChooser.showDialog(App.getRoot().getScene().getWindow());
    Alert alertType = new Alert(AlertType.WARNING);
    if (file != null) {
      String dirString = file.toString();
      Settings.getInstance().setImageFilePath(dirString);
      pathText.setText(dirString);
    }else{
      alertType.setTitle("Invalid FilePath");
      alertType.setContentText("Please Select/Enter a Valid FilePath.");
      alertType.show();
    }
  }

  /**
   * used for generating more clear screen shot image by scaling
   * @param node
   * @return
   */
  public static WritableImage screenShot(Node node) {
    WritableImage writableImage = new WritableImage((int) Math.rint(8 * node.getScene().getWidth()),
        (int) Math.rint(8* node.getScene().getHeight()));
    SnapshotParameters spa = new SnapshotParameters();
    spa.setTransform(Transform.scale(8, 8));
    return node.snapshot(spa, writableImage);
  }

  private void save(){
    Alert alertType = new Alert(AlertType.WARNING);
    if (choiceBox.getSelectionModel().getSelectedIndex() == 2) {
      if (saveMetric.isSelected()) {
        new PrintClickEvent().metricPDF();
      }
      if (saveCompare.isSelected()) {
        new PrintClickEvent().comparePDF();
      }
    }else if (choiceBox.getSelectionModel().getSelectedIndex() == 1){
      if (saveMetric.isSelected() && saveCompare.isSelected()) {
        new PrintClickEvent().PDF1();
      }else if (saveMetric.isSelected()) {
        new PrintClickEvent().metricPDF();
      }else if (saveCompare.isSelected()) {
        new PrintClickEvent().comparePDF();
      }
    }else if (choiceBox.getSelectionModel().getSelectedIndex() == 0){
      if (saveMetric.isSelected() && saveCompare.isSelected()) {
        new PrintClickEvent().PDF0();
      }else if (saveMetric.isSelected()) {
        new PrintClickEvent().metricPDF();
      }else if (saveCompare.isSelected()) {
        new PrintClickEvent().comparePDF();
      }
    }else {
      alertType.setTitle("Invalid PDF type");
      alertType.setContentText("Please Select a PDF type first.");
      alertType.show();
    }
  }
}


