package ac.uk.soton.ecs.group22.addashboard.data.csv.impression;

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
 * An implementation of an AbstractLoader which loads in impressions from file.
 */
public class ImpressionLoader extends AbstractLoader {

  private final List<ImpressionEntry> impressions;

  /**
   * @param file The file containing the impressions.
   */
  public ImpressionLoader(File file) {
    super(file);

    this.impressions = new LinkedList<>();
  }

  /**
   * Load in data to the loader.
   * <p>
   * This does not load in data to the manager.
   *
   * @return If loading in was successful.
   * @throws FileNotFoundException If the file does not exist.
   */
  public boolean load() throws FileNotFoundException {
    impressions.clear();

    try {
      impressions.addAll(new CsvToBeanBuilder(new FileReader(file))
          .withType(ImpressionEntry.class)
          .withThrowExceptions(true)
          .withVerifier((BeanVerifier<ImpressionEntry>) o -> {
            if (o.getGender() == null) {
              throw new CsvConstraintViolationException("Gender is invalid in impression for user " + o.getId());
            }

            if (o.getAge() == null) {
              throw new CsvConstraintViolationException("Age is invalid in impression for user " + o.getId());
            }

            if (o.getContext() == null) {
              throw new CsvConstraintViolationException("Context is invalid in impression for user " + o.getId());
            }

            if (o.getIncome() == null) {
              throw new CsvConstraintViolationException("Age is invalid in impression for user " + o.getId());
            }

            return true;
          })
          .build()
          .parse());
    } catch (FileNotFoundException e) {
      throw e;
    } catch (Exception e) {
//      e.printStackTrace();
      return false;
    }

    return !impressions.isEmpty();
  }

  /**
   * @return The list of loaded impressions, or empty list if not loaded.
   */
  public List<ImpressionEntry> getImpressions() {
    return impressions;
  }

}
