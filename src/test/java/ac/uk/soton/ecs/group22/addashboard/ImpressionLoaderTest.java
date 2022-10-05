package ac.uk.soton.ecs.group22.addashboard;

import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.Gender;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.ImpressionLoader;
import java.io.File;
import java.io.FileNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ImpressionLoaderTest {

  @DisplayName("Loading in impression data")
  @Test
  public void load() throws FileNotFoundException {
    ImpressionLoader impressionLoader = new ImpressionLoader(getTestFile());

    Assertions.assertTrue(impressionLoader.load());
  }

  @DisplayName("Loading in impression data fails if file is missing")
  @Test
  public void loadFails() {
    ImpressionLoader impressionLoader = new ImpressionLoader(new File("missing_file.should_not_exist"));

    Assertions.assertThrows(FileNotFoundException.class, impressionLoader::load);
  }

  @DisplayName("Loaded in impression data contains correct number of females and males")
  @Test
  public void countGenders() throws FileNotFoundException {
    ImpressionLoader impressionLoader = new ImpressionLoader(getTestFile());
    impressionLoader.load();

    Assertions.assertEquals(161469, impressionLoader.getImpressions()
        .stream()
        .filter(impressionEntry -> impressionEntry.getGender() == Gender.MALE)
        .count());

    Assertions.assertEquals(324635, impressionLoader.getImpressions()
        .stream()
        .filter(impressionEntry -> impressionEntry.getGender() == Gender.FEMALE)
        .count());
  }

  @DisplayName("Invalid file format throws error")
  @Test
  public void invalidFileFormat() throws FileNotFoundException {
    ImpressionLoader impressionLoader = new ImpressionLoader(getInvalidTestFile());

    Assertions.assertFalse(impressionLoader.load());
  }

  private File getTestFile() {
    return new File(getClass().getClassLoader().getResource("impression_log.csv").getFile());
  }

  private File getInvalidTestFile() {
    return new File(getClass().getClassLoader().getResource("click_log.csv").getFile());
  }

}
