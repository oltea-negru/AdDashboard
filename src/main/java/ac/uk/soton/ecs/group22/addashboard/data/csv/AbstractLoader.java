package ac.uk.soton.ecs.group22.addashboard.data.csv;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * An abstraction of loading in data from a file.
 */
public abstract class AbstractLoader {

  protected final File file;

  /**
   * @param file The file containing the data.
   */
  public AbstractLoader(File file) {
    this.file = file;
  }

  /**
   * Loads in data from file.
   *
   * @return Whether loading was successful.
   * @throws FileNotFoundException File was not found.
   */
  public abstract boolean load() throws FileNotFoundException;

}
