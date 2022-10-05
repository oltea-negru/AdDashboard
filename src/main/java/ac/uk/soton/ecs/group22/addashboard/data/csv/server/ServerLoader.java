package ac.uk.soton.ecs.group22.addashboard.data.csv.server;

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
 * Class for loading the data from the server log and parsing
 *
 * @author Jianan
 * @version 1.0
 * @since 2022-03-07
 */
public class ServerLoader extends AbstractLoader {

  private List<ServerEntry> server;

  /**
   * Constructor for the loader
   * @param file The file to load the data from
   */
  public ServerLoader(File file) {
    super(file);
    this.server = new LinkedList<>();
  }

  /**
   * Loads the data, throwing an exception if the file could not be found
   * @return boolean whether the file was loaded or not
   * @throws FileNotFoundException
   */
  public boolean load() throws FileNotFoundException {
    server.clear();
    server.addAll(new CsvToBeanBuilder(new FileReader(file))
        .withType(ServerEntry.class)
        .withThrowExceptions(true)
        .withVerifier((BeanVerifier<ServerEntry>) o -> {
          if (o.getId() == null) {
            throw new CsvConstraintViolationException("Server entry has an invalid ID");
          }

          if (o.getEntryDate() == null) {
            throw new CsvConstraintViolationException("Entry date is invalid in server entry for user " + o.getId());
          }

          if (o.getExitDate() == null) {
            throw new CsvConstraintViolationException("Exit date is invalid in server entry for user " + o.getId());
          }

          return true;
        })
        .build()
        .parse());

    return !server.isEmpty();
  }

  /**
   * Getter for the server entries
   * @return List<ServerEntry> The server entries
   */
  public List<ac.uk.soton.ecs.group22.addashboard.data.csv.server.ServerEntry> getServer() {
    return server;
  }

}
