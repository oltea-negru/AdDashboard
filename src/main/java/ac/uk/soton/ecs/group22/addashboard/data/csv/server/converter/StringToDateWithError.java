package ac.uk.soton.ecs.group22.addashboard.data.csv.server.converter;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringToDateWithError extends AbstractBeanField {

  private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
  private static final String ERROR_FORMAT = "n/a";

  @Override
  protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
    Date date = new Date();

    if (s.equals(ERROR_FORMAT)) {
      return date.getTime();
    } else {
      try {
        SimpleDateFormat formatter = new SimpleDateFormat(FORMAT);
        return formatter.parse(s).getTime();
      } catch (Exception e) {
        throw new CsvDataTypeMismatchException(e.getMessage());
      }
    }
  }
}
