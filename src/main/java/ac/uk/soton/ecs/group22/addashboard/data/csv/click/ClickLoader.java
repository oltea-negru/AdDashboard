package ac.uk.soton.ecs.group22.addashboard.data.csv.click;

import ac.uk.soton.ecs.group22.addashboard.data.csv.AbstractLoader;
import com.opencsv.bean.BeanVerifier;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvConstraintViolationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Class for loading the data from the click log and parsing
 *
 * @author Jack
 * @version 1.0
 * @since 2022-03-05
 */
public class ClickLoader extends AbstractLoader {

  private List<ClickEntry> clicks;

  /**
   * Constructor for the loader
   * @param file The file to load the data from
   */
  public ClickLoader(File file) {
    super(file);

    this.clicks = new LinkedList<>();
  }

  /**
   * Loads the data, throwing an exception if the file could not be found
   * @return boolean whether the file was loaded or not
   * @throws FileNotFoundException
   */
  public boolean load() throws FileNotFoundException {
    clicks.clear();

    clicks.addAll(new CsvToBeanBuilder(new FileReader(file))
        .withType(ClickEntry.class)
        .withThrowExceptions(true)
        .withVerifier((BeanVerifier<ClickEntry>) o -> {
          if (o.getId() <= 0) {
            throw new CsvConstraintViolationException("Click entry has an invalid ID");
          }

          if (o.getDate() <= 0) {
            throw new CsvConstraintViolationException("Click entry has an invalid date for user " + o.getId());
          }

          if (o.getClickCost() < 0) {
            throw new CsvConstraintViolationException("Click cost has an invalid cost for user " + o.getId());
          }

          return true;
        })
        .build()
        .parse());

    return !clicks.isEmpty();
  }

  /**
   * Getter for the click entries
   * @return List<ClickEntry> The click entries
   */
  public List<ClickEntry> getClicks() {
    return clicks;
  }
}
