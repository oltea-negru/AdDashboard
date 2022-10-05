package ac.uk.soton.ecs.group22.addashboard;

import ac.uk.soton.ecs.group22.addashboard.controller.Campaign;
import ac.uk.soton.ecs.group22.addashboard.controller.ClickManager;
import ac.uk.soton.ecs.group22.addashboard.controller.ImpressionManager;
import ac.uk.soton.ecs.group22.addashboard.controller.ServerManager;
import ac.uk.soton.ecs.group22.addashboard.data.csv.click.ClickLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.TimeZone;

import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.Gender;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.ImpressionLoader;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ImpressionManagerTest {

    @DisplayName("Loaded in impression data contains correct number of uniques")
    @Test
    public void countUniques() throws FileNotFoundException {
        ImpressionManager impressionManager = new ImpressionManager();
        ImpressionLoader impressionLoader = impressionManager.createLoader(getSimpleImpressionTestFile());
        impressionLoader.load();
        impressionManager.setImpressions(impressionLoader);

        Assertions.assertEquals(10, impressionManager.getUniqueCount());

    }

    @DisplayName("Loaded in impression data contains correct number of impressions")
    @Test
    public void countImpressions() throws FileNotFoundException {
        ImpressionLoader impressionLoader = new ImpressionLoader(getSimpleImpressionTestFile());
        impressionLoader.load();

        Assertions.assertEquals(10, impressionLoader.getImpressions().size());

    }

    @DisplayName("Loaded in impression data contains correct number males")
    @Test
    public void countMales() throws FileNotFoundException {
        ImpressionLoader impressionLoader = new ImpressionLoader(getSimpleImpressionTestFile());
        impressionLoader.load();

        Assertions.assertEquals(2, impressionLoader.getImpressions()
                .stream()
                .filter(impressionEntry -> impressionEntry.getGender() == Gender.MALE)
                .count());

    }

    @DisplayName("Loaded in impression data contains correct number of females")
    @Test
    public void countFemales() throws FileNotFoundException {
        ImpressionLoader impressionLoader = new ImpressionLoader(getSimpleImpressionTestFile());
        impressionLoader.load();

        Assertions.assertEquals(8, impressionLoader.getImpressions()
                .stream()
                .filter(impressionEntry -> impressionEntry.getGender() == Gender.FEMALE)
                .count());
    }

    @SneakyThrows
    private File getSimpleImpressionTestFile() {
        return new File(getClass().getClassLoader().getResource("simple_impression_log.csv").toURI());
    }
}