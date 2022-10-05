package ac.uk.soton.ecs.group22.addashboard.controller;

import ac.uk.soton.ecs.group22.addashboard.data.csv.AbstractLoader;
import java.io.File;
import lombok.Setter;

/**
 * An abstraction of a data manager.
 * <p>
 * The data manager will store loaded data from files.
 */
public abstract class AbstractManager {

  @Setter
  private boolean hasLoadedInData;

  /**
   * @return If a file has been loaded in.
   */
  public boolean hasLoadedInData() {
    return hasLoadedInData;
  }

  /**
   * This is to be overridden by a manager's loader. For example, the ImpressionManager's
   * ImpressionLoader.
   *
   * @param file The file containing the data.
   * @return A loader to load data from the file.
   */
  public abstract AbstractLoader createLoader(File file);

}
