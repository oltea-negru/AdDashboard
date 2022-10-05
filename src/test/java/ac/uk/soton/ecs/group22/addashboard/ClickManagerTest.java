package ac.uk.soton.ecs.group22.addashboard;

import ac.uk.soton.ecs.group22.addashboard.controller.Campaign;
import ac.uk.soton.ecs.group22.addashboard.controller.ClickManager;
import ac.uk.soton.ecs.group22.addashboard.controller.ImpressionManager;
import ac.uk.soton.ecs.group22.addashboard.controller.ServerManager;
import ac.uk.soton.ecs.group22.addashboard.data.csv.click.ClickLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;

import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.ImpressionLoader;
import ac.uk.soton.ecs.group22.addashboard.data.csv.server.ServerLoader;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ClickManagerTest {

  private ClickManager clickManager;
  private ImpressionManager impressionManager;
  private ServerManager serverManager;
  private Campaign campaign;
  BigDecimal smallDifference;

  @BeforeEach
  void before() throws FileNotFoundException {

    campaign=Campaign.getInstance();

    clickManager = campaign.getClickManager();
    ClickLoader clickLoader = clickManager.createLoader(getSimpleClickTestFile());
    clickLoader.load();
    clickManager.setClicks(clickLoader);

    impressionManager = campaign.getImpressionManager();
    ImpressionLoader impressionLoader = impressionManager.createLoader(getSimpleImpressionTestFile());
    impressionLoader.load();
    impressionManager.setImpressions(impressionLoader);

    serverManager =campaign.getServerManager();
    ServerLoader serverLoader =serverManager.createLoader(getSimpleServerTestFile());
    serverLoader.load();
    serverManager.setServer(serverLoader);

    smallDifference=new BigDecimal(0.0000001);

  }

  @DisplayName("Correct total sum of clicks")
  @Test
  public void correctSumTotal(){
    Assertions.assertEquals(42.681081999999996, clickManager.getTotalCost(false));
  }

  @DisplayName("Correct CTR")
  @Test
  public void correctCTR(){
    System.out.println(campaign.getClickManager().getCTR(false));
    Assertions.assertTrue(campaign.getClickManager().getCTR(false).subtract(new BigDecimal(0.60)).compareTo(smallDifference)<=0);
  }

  @DisplayName("Correct CPC")
  @Test
  public void correctCPC(){
    System.out.println(campaign.getClickManager().getCPC(false));
    Assertions.assertTrue(campaign.getClickManager().getCPC(false).subtract(new BigDecimal(0.071)).compareTo(smallDifference)<=0);
  }

  @DisplayName("Correct CPM")
  @Test
  public void correctCPM(){
    System.out.println(campaign.getClickManager().getCPM(false));
    Assertions.assertTrue(campaign.getClickManager().getCPM(false).subtract(new BigDecimal(42.681)).compareTo(smallDifference) <=0);
  }

  @DisplayName("Correct CPA")
  @Test
  public void correctCPA(){
    System.out.println(campaign.getClickManager().getCPA(false));
    Assertions.assertTrue(campaign.getClickManager().getCPA(false).subtract(new BigDecimal(0.213)).compareTo(smallDifference) <=0);
  }

  @SneakyThrows
  private File getSimpleClickTestFile() {
    return new File(getClass().getClassLoader().getResource("simple_click_log.csv").toURI());
  }

  @SneakyThrows
  private File getSimpleServerTestFile() {
    return new File(getClass().getClassLoader().getResource("simple_server_log.csv").toURI());
  }

  @SneakyThrows
  private File getSimpleImpressionTestFile() {
    return new File(getClass().getClassLoader().getResource("simple_impression_log.csv").toURI());
  }
}


