package ac.uk.soton.ecs.group22.addashboard.data.csv.converter;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import java.text.SimpleDateFormat;

public class StringDateToEpochLongConverter extends AbstractBeanField {

  private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
  private static final SimpleDateFormat FORMATTER = new SimpleDateFormat(FORMAT);

  @Override
  protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
    try {
      synchronized (FORMATTER) {
        return FORMATTER.parse(s).getTime();
      }
    } catch (Exception e) {
      throw new CsvConstraintViolationException(e.getMessage());
    }
  }
}
