package ac.uk.soton.ecs.group22.addashboard.controller;

import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

/**
 * Class for storing the settings
 *
 * @since 2022-03-23
 * @version 1.0
 */
public class Settings {

  private static Settings instance;

  @Getter
  @Setter
  private Integer pagesForBounce = 1; //defaults
  @Getter
  @Setter
  private Long timeForBounce = ((long) 60 * 60 * 1000); //default 1 hour
  @Getter
  @Setter
  private Integer fontForSize = 10; //defaults
  @Getter
  @Setter
  private String fontForFamily = "Arial";
  @Getter
  private double[] sizeFamilyXY = new double[4];
  @Getter
  private double[] pagesLabel12XY = new double[4];
  @Getter
  private double[] windowXY = new double[2];
  @Getter
  @Setter
  private String imageFilePath = null;
  @Getter
  @Setter
  private WritableImage compareImage = null;
  @Getter
  @Setter
  private WritableImage metricImage = null;
  @Getter
  @Setter
  private Color fontColor = Color.BLACK;

  @Getter
  @Setter
  private Color backgroundColor = Color.WHITE;
  @Getter
  @Setter
  private Color primaryColor = Color.rgb(201, 201, 201);
  @Getter
  @Setter
  private Color secondaryColor = Color.DARKGRAY;

  private Settings() {
    //the width and height of size Label in 1pt
    sizeFamilyXY[0] = 6;
    sizeFamilyXY[1] = 1.6;
    //the width and height of family Label in 1pt
    sizeFamilyXY[2] = 7.5;
    sizeFamilyXY[3] = 1.5;

    pagesLabel12XY[0] = 16.2;
    pagesLabel12XY[1] = 1.5;

    windowXY[0] = 800;
    windowXY[1] = 550;

  }

  /**
   * Static method for getting the settings instance
   * @return Settings The instance
   */
  public static Settings getInstance() {
    return instance == null ? instance = new Settings() : instance;
  }

  public static String colorToHex(Color color) {
    return "#" + color.toString().substring(2, 8);
  }

}
