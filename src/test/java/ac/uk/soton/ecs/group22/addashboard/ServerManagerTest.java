package ac.uk.soton.ecs.group22.addashboard;
import ac.uk.soton.ecs.group22.addashboard.controller.ServerManager;
import ac.uk.soton.ecs.group22.addashboard.data.csv.server.ServerLoader;
import java.io.File;
import java.io.FileNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ServerManagerTest {

  @DisplayName("Users who do not meet the bounce are properly filtered")
  @Test
  public void correctBounceFilter() throws FileNotFoundException {
    ServerManager serverManager = new ServerManager();
    ServerLoader serverLoader = serverManager.createLoader(getSimpleTestFile());
    serverLoader.load();
    serverManager.setServer(serverLoader);
    Assertions.assertEquals(0, serverManager.fetchBounces().size());
  }

  @DisplayName("Number of conversions is correct")
  @Test
  public void correctConversions() throws FileNotFoundException {
    ServerManager serverManager = new ServerManager();
    ServerLoader serverLoader = serverManager.createLoader(getSimpleTestFile());
    serverLoader.load();
    serverManager.setServer(serverLoader);
    Assertions.assertEquals(2, serverManager.fetchConversions(false).size());
  }



  private File getSimpleTestFile() {
    return new File(getClass().getClassLoader().getResource("simple_server_log.csv").getFile());
  }

}


