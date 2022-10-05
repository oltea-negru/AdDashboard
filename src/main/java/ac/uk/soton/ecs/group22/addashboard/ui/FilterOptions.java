package ac.uk.soton.ecs.group22.addashboard.ui;

import java.util.LinkedList;
import java.util.List;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public abstract class FilterOptions extends VBox {

  private String header;
  private List<String> options;
  private List<CheckBox> checkBoxes;

  public FilterOptions(String header, List<String> options) {
    this.header = header;
    this.options = new LinkedList<>(options);
    this.checkBoxes = new LinkedList<>();

    init();

    getStyleClass().add("background-colour");
  }

  private void init() {
    Text text = new Text(header);
    text.getStyleClass().add("filter-header");
    text.getStyleClass().add(("background-colour"));

    getChildren().add(text);

    for (String option : options) {
      CheckBox checkBox = new CheckBox(option);
      checkBox.getStyleClass().add("filter-checkbox");
      checkBox.getStyleClass().add("background-colour");
      checkBox.setOnAction(actionEvent -> onUpdate(option, checkBox.isSelected()));

      checkBoxes.add(checkBox);
      getChildren().add(checkBox);
    }

    if (!checkBoxes.isEmpty()) {
      checkBoxes.get(0).getStyleClass().add("filter-checkbox-first");
      checkBoxes.get(checkBoxes.size() - 1).getStyleClass().add("filter-checkbox-last");
    }
  }

  public abstract void onUpdate(String option, boolean selected);

  public void untickAll() {
    checkBoxes.forEach(checkBox -> checkBox.setSelected(false));
  }

}