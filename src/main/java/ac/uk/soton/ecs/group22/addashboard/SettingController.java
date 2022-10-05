package ac.uk.soton.ecs.group22.addashboard;

import ac.uk.soton.ecs.group22.addashboard.controller.Settings;
import ac.uk.soton.ecs.group22.addashboard.events.AppearanceSettingsChangeEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import ac.uk.soton.ecs.group22.addashboard.events.FilterEvent;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.beans.binding.Bindings;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.SneakyThrows;

public class SettingController implements Initializable {
  @FXML
  private TabPane tabPane;
  @FXML
  @Getter
  private Label bounceTitleLabel = new Label();
  @FXML
  @Getter
  private Label bouncePagesLabel1 = new Label();
  @FXML
  @Getter
  private TextField pagesInput = new TextField();
  @FXML
  @Getter
  private Label bouncePagesLabel2 = new Label();
  @FXML
  @Getter
  private Label bounceTimeLabel1 = new Label();
  @FXML
  @Getter
  private HBox bounceTimeHBox = new HBox();
  @FXML
  @Getter
  private TextField hourInput = new TextField();
  @FXML
  @Getter
  private Label timeSeparator1 = new Label();
  @FXML
  @Getter
  private Label timeSeparator2 = new Label();
  @FXML
  @Getter
  private TextField minInput = new TextField();
  @FXML
  @Getter
  private TextField secInput = new TextField();
  @FXML
  @Getter
  private Label bounceTimeLabel2 = new Label();
  @FXML
  private Label bounceTipsLabel = new Label();
  @FXML
  private Button saveBounceBtn = new Button();
  @FXML
  private Label fontSizeLabel = new Label();
  @FXML
  private Label fontFamilyLabel = new Label();
  @FXML
  private Button saveAppearanceBtn = new Button();
  @FXML
  @Getter
  private TextField fontSizeText = new TextField();
  @FXML
  private Label bounceTips = new Label();
  @FXML
  private ColorPicker fontColorPicker;
  @FXML
  private ColorPicker backgroundColorPicker;
  @FXML
  private ColorPicker primaryColorPicker;
  @FXML
  private ColorPicker secondaryColorPicker;
  @FXML
  @Getter
  private MenuButton fontFamilySelect = new MenuButton();
  private double lastChangeSize = 10;

  @Getter
  static SimpleIntegerProperty fontSize = new SimpleIntegerProperty(10);
  @Getter
  static SimpleStringProperty fontFamily = new SimpleStringProperty("Arial");

  public SettingController() {
  }

  /**
   * initialize every variable from Settings which store the settings
   * @param url
   * @param resourceBundle
   */
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    pagesInput.setText(Settings.getInstance().getPagesForBounce() + "");
    hourInput.setText(TimeUnit.MILLISECONDS.toHours(Settings.getInstance().getTimeForBounce()) + "");
    minInput.setText(TimeUnit.MILLISECONDS.toMinutes(Settings.getInstance().getTimeForBounce()) % 60 + "");
    secInput.setText(TimeUnit.MILLISECONDS.toSeconds(Settings.getInstance().getTimeForBounce()) % 60 + "");
    fontSizeText.setText(Settings.getInstance().getFontForSize() + "");

    fontColorPicker.setValue(Settings.getInstance().getFontColor());
    backgroundColorPicker.setValue(Settings.getInstance().getBackgroundColor());
    primaryColorPicker.setValue(Settings.getInstance().getPrimaryColor());
    secondaryColorPicker.setValue(Settings.getInstance().getSecondaryColor());
    fontFamilySelect.setText(Settings.getInstance().getFontForFamily() + "");
    reLayoutAppearance(null);
    reLayoutBounce(null);

    AppearanceSettingsChangeEvent.getListeners().add(appearanceSettingsChangeEvent -> {
      MainController.updateChildrenStyles(tabPane);

      tabPane.applyCss();
      tabPane.layout();
    });
  }

  /**
   * set some default value for controls
   * @param root always be App.getRoot()
   */
  public void reLayoutAppearance(Parent root) {
    if (App.getPrimary() == null) { return; }

    if (root != null) {
      root.applyCss();
      root.layout();
    }
  }

  /**
   * set some default value for controls
   * @param root always be App.getRoot()
   */
  public void reLayoutBounce(Parent root) {
    if (root != null) {
      root.applyCss();
      root.layout();
    }
    double em = fontSize.getValue();
    pagesInput.setPrefWidth(em*4);
    hourInput.setPrefWidth(em*4);
    minInput.setPrefWidth(em*4);
    secInput.setPrefWidth(em*4);
  }

  /**
   * update changes to Setting to store
   * @param pages from user input
   * @param time from user input
   */
  public void onUpdateBounce(int pages, long time) {
    Settings.getInstance().setTimeForBounce(time);
    Settings.getInstance().setPagesForBounce(pages);
  }

  /**
   * update changes to Setting to store
   * @param fontSize from user input
   * @param fontFamily from user input
   */
  public void onUpdateAppearance(int fontSize, String fontFamily) {
    Settings.getInstance().setFontForSize(fontSize);
    Settings.getInstance().setFontForFamily(fontFamily);
  }

  /**
   * when click "save" button in bounce setting will call this method
   */
  @FXML
  private void saveBounceChange() {
    String p = pagesInput.getText();
    String h = hourInput.getText();
    String m = minInput.getText();
    String s = secInput.getText();
    onUpdateBounce(StringToInt(p), 1000 * (StringToLong(h) * 60 * 60 + StringToLong(m) * 60 + StringToLong(s)));
    new FilterEvent().call();
  }

  /**
   * when click "save" button in appearance setting will call this method
   */
  @FXML
  private void saveAppearanceChange() {
    warningForFontSize(fontSizeText.getText());
    String s = fontSizeText.getText();
    String f;
    if (fontFamilySelect.getText().contains("\"")){
      f = fontFamilySelect.getText();
    }else {
      f = "\"" + fontFamilySelect.getText() + "\"";
    }
    fontSize.setValue(StringToInt1(s));
    fontFamily.setValue(f);
    App.getRoot().styleProperty().bind(Bindings.concat(
        "-fx-font-size: ", fontSize, "pt;",
        "-fx-font-family: ", fontFamily, ";"));

    //color setting
    Settings.getInstance().setFontColor(fontColorPicker.getValue());
    Settings.getInstance().setBackgroundColor(backgroundColorPicker.getValue());
    Settings.getInstance().setPrimaryColor(primaryColorPicker.getValue());
    Settings.getInstance().setSecondaryColor(secondaryColorPicker.getValue());

    new AppearanceSettingsChangeEvent().call();

    onUpdateAppearance(StringToInt1(s), f);
    reLayoutAppearance(App.getRoot());
  }


  /**
   * check if user input error fontSize, like too big or small or alphabet
   * @param input user input
   */
  public void warningForFontSize (String input){
    Alert alertType = new Alert(AlertType.WARNING);
    if (input.equals("")) {
      alertType.setTitle("Font Size Warning");
      alertType.setContentText("Font size can't be 0, now the size has reset to 7 which is smallest.");
      fontSizeText.setText(String.valueOf(7));
      alertType.show();
    } else if (!input.matches("^[0-9]*$")){
      alertType.setTitle("Font Size Warning");
      alertType.setContentText("Font size can't be contain anything without numbers, now the size has reset to 10 which is default.");
      fontSizeText.setText(String.valueOf(10));
      alertType.show();
    }else if(Integer.parseInt(input) > 13){
      alertType.setTitle("Font Size Warning");
      alertType.setContentText("Font size can't be bigger than 13pt, now the size has reset to 13 which is biggest.");
      fontSizeText.setText(String.valueOf(13));
      alertType.show();
    }else if(Integer.parseInt(input) < 7){
      alertType.setTitle("Font Size Warning");
      alertType.setContentText("Font size can't be smaller than 7pt, now the size has reset to 7 which is smallest.");
      fontSizeText.setText(String.valueOf(7));
      alertType.show();
    }
  }

  /**
   * check if user has unsaved change in font family or size when user move mouse out of Setting TabPane
   * @throws IOException
   */
  public void warningForSaveFont() throws IOException {
    Alert confirmation = new Alert(AlertType.CONFIRMATION,"",ButtonType.APPLY, ButtonType.CANCEL);
    if (!fontSize.getValue().equals(Settings.getInstance().getFontForSize()) || !fontFamily.getValue().equals(Settings.getInstance().getFontForFamily())) {
      confirmation.setTitle("Unsaved Changes");
      confirmation.setContentText("Font size or family have unsaved changes, do you want to save all changes?");
      Optional<ButtonType> result = confirmation.showAndWait();
      if (result.get() == ButtonType.APPLY) {
        saveAppearanceChange();
        App.getRoot().styleProperty().bind(Bindings.concat(
            "-fx-font-size: ", fontSize, "pt;",
            "-fx-font-family: ", fontFamily, ";"));
      }else {
        fontSizeText.setText(Settings.getInstance().getFontForSize().toString());
        fontFamilySelect.setText(Settings.getInstance().getFontForFamily());
        SettingMouseClicked(null);
      }

    /*
         }  else if (!fontFamily.getValue().equals(Settings.getInstance().getFontForFamily())) {
      confirmation.setTitle("Unsaved Changes");
      confirmation.setContentText("Font family has unsaved changes, do you want to save change");
      Optional<ButtonType> result = confirmation.showAndWait();
      if (result.get() == ButtonType.YES) {
        saveAppearanceChange();
        App.getRoot().styleProperty().bind(Bindings.concat("-fx-font-family: ", fontFamily, ";"));
      }else {
        fontFamilySelect.setText(Settings.getInstance().getFontForFamily());
        SettingMouseClicked(null);
      }
    } else if (!fontSize.getValue().equals(Settings.getInstance().getFontForSize())) {
      confirmation.setTitle("Unsaved Changes");
      confirmation.setContentText("Font size has unsaved changes, do you want to save change");
      Optional<ButtonType> result = confirmation.showAndWait();
      if (result.get() == ButtonType.YES) {
        saveAppearanceChange();
        App.getRoot().styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize, "pt;"));
      }else {

        fontSizeText.setText(Settings.getInstance().getFontForSize().toString());
        SettingMouseClicked(null);
      }

     */
    }else{
      App.getRoot().styleProperty().bind(Bindings.concat(
          "-fx-font-size: ", fontSize, "pt;",
          "-fx-font-family: ", fontFamily, ";"));
    }

  }

  /**
   * check if user move mouse out of Setting TabPane
   * @param event
   * @throws IOException
   */
  public void SettingMouseExit(MouseEvent event) throws IOException {
    warningForSaveFont();
  }

  /**
   * converter
   * @param input time input which is time in ms
   * @return
   */
  private long StringToLong(String input) {
    if (input.equals("")) {
      return 0;
    }
    return Long.parseLong(input);
  }

  /**
   * converter
   * @param input pages input
   * @return
   */
  private int StringToInt(String input) {
    if (input.equals("")) {
      return 0;
    }
    return Integer.parseInt(input);
  }

  /**
   * for converter font size at first, it cannot be 0
   * @param input font size input
   * @return
   */
  private int StringToInt1(String input) {
    if (input.equals("")) {
      return 1;
    }
    return Integer.parseInt(input);
  }

  /**
   * check if user click mouse in setting TabPane except button or other controls
   * @param event
   */
  public void SettingMouseClicked(MouseEvent event) {
    warningForFontSize(fontSizeText.getText());
    if (StringToInt1(fontSizeText.getText()) != fontSize.getValue()) {
      fontSize.setValue(StringToInt1(fontSizeText.getText()));

/*
      if (fontSize.getValue() > Settings.getInstance().getFontForSize()) {
        App.getRoot().getScene().getWindow().setHeight(App.getRoot().getScene().getWindow().getHeight() * fontSize.getValue() / Settings.getInstance().getFontForSize());
      }else if(fontSize.getValue() < Settings.getInstance().getFontForSize()){
        App.getRoot().getScene().getWindow().setHeight(App.getRoot().getScene().getWindow().getHeight() * Settings.getInstance().getFontForSize() / fontSize.getValue());
      }
      App.getRoot().getScene().getWindow().setWidth(App.getRoot().getScene().getWindow().getHeight() * App.getScreenXY()[2]);
      App.getRoot().getScene().getWindow().centerOnScreen();

 */
      App.getRoot().styleProperty().bind(Bindings.concat(
          "-fx-font-size: ", fontSize, "pt;",
          "-fx-font-family: ", fontFamily, ";"));


      reLayoutAppearance(App.getRoot());
      reLayoutBounce(App.getRoot());
    }
  }

  /**
   * monitor TextFiled for pages
   * @param event KeyTyped Event
   */
  public void OnlyNum1(KeyEvent event) {
    OnlyNum(event, pagesInput, 0);
  }
  /**
   * monitor TextFiled for pages
   * @param event KeyTyped Event
   */
  public void OnlyNum2(KeyEvent event) {
    OnlyNum(event, hourInput, 0);
  }
  /**
   * monitor TextFiled for min input
   * @param event KeyTyped Event
   */
  public void OnlyNum3(KeyEvent event) {
    OnlyNum(event, minInput, 0);
    NumUnder(event, minInput, 60);
  }
  /**
   * monitor TextFiled for sec input
   * @param event KeyTyped Event
   */
  public void OnlyNum4(KeyEvent event) {
    OnlyNum(event, secInput, 0);
    NumUnder(event, secInput, 60);
  }

  /**
   * check if the typed value should be consumed
   * @param event KeyTyped Event
   * @param input
   * @param bound
   */
  private void NumUnder(KeyEvent event, TextField input, int bound) {
    if (event.getCharacter().matches("^[0-9]*$") && !input.getText().equals("")) {
      if (Long.parseLong(input.getText()) >= bound || input.getText().length() > 2) {
        event.consume();
        input.setText(String.valueOf(bound - 1));
        bounceTipsLabel.setText("(The minute and second setting cannot exceed 2 characters or the value is greater than 59)");
      }
    }
  }

  /**
   * check if the typed value should be consumed
   * @param event KeyTyped Event
   * @param input
   * @param bound
   */
  private void OnlyNum(KeyEvent event, TextField input, int bound) {
    if (!event.getCharacter().matches("^[0-9]*$")) {
      event.consume();
      input.setText(String.valueOf(bound));
      bounceTipsLabel.setText("(When entering a value other than a number, the current TextBox will be reset to 0)");
    }
  }

  @FXML
  @SneakyThrows
  private void switchToPrimary() throws IOException {
    warningForSaveFont();
  }

  /**
   * a method from MenuItem
   */
  @FXML
  private void toArialFont() {
    toDifferentFontFamily("Arial");
  }
  /**
   * a method from MenuItem
   */
  @FXML
  private void toBigCaslonFont() {
    toDifferentFontFamily("Big Caslon");
  }
  /**
   * a method from MenuItem
   */
  @FXML
  private void toDINAlternateFont() {
    toDifferentFontFamily("DIN Alternate");
  }
  /**
   * a method from MenuItem
   */
  @FXML
  private void toFuturaFont() {
    toDifferentFontFamily("Futura");
  }

  /**
   * preview new font family
   * @param f name of font family
   */
  private void toDifferentFontFamily(String f) {
    fontFamilySelect.setText(f);
    fontFamily.setValue("\"" + f + "\"");
    App.getRoot().styleProperty().bind(Bindings.concat(
        "-fx-font-size: ", fontSize, "pt;",
        "-fx-font-family: ", fontFamily, ";"));
    reLayoutAppearance(App.getRoot());
  }

  private void NumBigger(TextField input, int bound) {
    if (input.getText().equals("") || Long.parseLong(input.getText()) <= bound) {
      input.setText(String.valueOf(bound + 1));
    }
  }

}