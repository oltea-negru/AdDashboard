package ac.uk.soton.ecs.group22.addashboard.controller;

import ac.uk.soton.ecs.group22.addashboard.controller.filter.DateFilter;
import ac.uk.soton.ecs.group22.addashboard.controller.filter.ImpressionFilter;
import java.util.Date;
import lombok.Getter;

/**
 * The singleton class for getting easy static access to loaded data.
 * <p>
 * A campaign consists of impressions, server logs and clicks. The campaign also keeps track of the
 * filter applied to the data.
 */
public class Campaign {

  private static Campaign instance;
  @Getter
  private final ImpressionManager impressionManager;
  @Getter
  private final ServerManager serverManager;
  @Getter
  private final ClickManager clickManager;
  @Getter
  private final ImpressionFilter impressionFilter;
  @Getter
  private final DateFilter dateFilter;

  private Campaign() {
    this.impressionManager = new ImpressionManager();
    this.serverManager = new ServerManager();
    this.clickManager = new ClickManager();
    this.impressionFilter = new ImpressionFilter();
    this.dateFilter = new DateFilter();
  }

  /**
   * @return The singleton Campaign instance. If null, it is generated.
   */
  public static Campaign getInstance() {
    if (instance != null) {
      return instance;
    }

    instance = new Campaign();

    return instance;
  }

  /**
   * @param fileType The file type.
   * @return The manager responsible for the file type.
   */
  public AbstractManager getManager(FileType fileType) {
    if (fileType == FileType.IMPRESSION) {
      return impressionManager;
    } else if (fileType == FileType.CLICK) {
      return clickManager;
    } else if (fileType == FileType.SERVER) {
      return serverManager;
    }

    throw new RuntimeException("There are more FileTypes than managers");
  }

}
