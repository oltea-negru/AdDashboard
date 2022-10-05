package ac.uk.soton.ecs.group22.addashboard.data.csv.server;
import ac.uk.soton.ecs.group22.addashboard.data.csv.server.converter.StringToDateWithError;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvToBean;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import com.opencsv.bean.CsvToBeanBuilder;
import java.nio.file.Paths;
import java.util.Iterator;
import lombok.Getter;

/**
 * Class for objectifying the data stored in the server logs
 *
 * @author Jianan
 * @version 1.0
 * @since 2022-03-07
 */
public class ServerEntry {

  @CsvCustomBindByName(column = "Entry Date", converter = StringToDateWithError.class)
  @Getter
  private Long entryDate;

  @CsvBindByName(column = "ID")
  @Getter
  private Long id;

  @CsvCustomBindByName(column = "Exit Date", converter = StringToDateWithError.class)
  @Getter
  private Long exitDate;

  @CsvBindByName(column = "Pages Viewed")
  @Getter
  private int pagesViewed;

  @CsvBindByName(column = "Conversion")
  @Getter
  private boolean conversion;

}
