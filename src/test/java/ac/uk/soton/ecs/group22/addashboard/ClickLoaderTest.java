package ac.uk.soton.ecs.group22.addashboard;

import ac.uk.soton.ecs.group22.addashboard.data.csv.click.ClickLoader;
import java.io.File;
import java.io.FileNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ClickLoaderTest {

  @DisplayName("Loads in click data")
  @Test
  public void load() throws FileNotFoundException {
    ClickLoader clickLoader = new ClickLoader(getTestFile());
    Assertions.assertTrue(clickLoader.load());
  }

  @DisplayName("Throws FNF Exception on missing file")
  @Test
  public void loadFails() {
    ClickLoader clickLoader = new ClickLoader(new File("this_file_does_not_exist"));
    Assertions.assertThrows(FileNotFoundException.class, clickLoader::load);
  }

  private File getTestFile() {
    return new File(getClass().getClassLoader().getResource("click_log.csv").getFile());
  }
}

