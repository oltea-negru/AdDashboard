package ac.uk.soton.ecs.group22.addashboard.data.csv.click;

import ac.uk.soton.ecs.group22.addashboard.data.csv.converter.StringDateToEpochLongConverter;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import lombok.Getter;

/**
 * Class for objectifying the data stored in the click logs
 *
 * @author Jack
 * @version 1.0
 * @since 2022-03-05
 */
public class ClickEntry {

  @CsvCustomBindByName(converter = StringDateToEpochLongConverter.class)
  @Getter
  private long date;

  @CsvBindByName
  @Getter
  private long id;

  @CsvBindByName(column = "Click Cost")
  @Getter
  private double clickCost;

  @Override
  public String toString() {
    return "Click from: "
        + id
        + " with cost: "
        + clickCost
        + " at: "
        + date;
  }

}
