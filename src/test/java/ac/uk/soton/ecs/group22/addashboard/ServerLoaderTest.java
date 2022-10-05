package ac.uk.soton.ecs.group22.addashboard;

import ac.uk.soton.ecs.group22.addashboard.data.csv.server.ServerLoader;
import java.io.File;
import java.io.FileNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ServerLoaderTest {

  @DisplayName("Loading in server data")
  @Test
  public void load() throws FileNotFoundException {
    ServerLoader serverLoader = new ServerLoader(getTestFile());

    Assertions.assertTrue(serverLoader.load());
  }

  @DisplayName("Loading in server data fails if file is missing")
  @Test
  public void loadFails() {
    ServerLoader serverLoader = new ServerLoader(new File("missing_file.should_not_exist"));

    Assertions.assertThrows(FileNotFoundException.class, serverLoader::load);
  }

  private File getTestFile() {
    return new File(getClass().getClassLoader().getResource("server_log.csv").getFile());
  }

}
