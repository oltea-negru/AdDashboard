package ac.uk.soton.ecs.group22.addashboard.data.csv.impression.converter;

import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.Context;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.Gender;
import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import java.util.Locale;

/**
 * OpenCSV converter to convert context in file to enums.
 */
public class TextToContextConverter extends AbstractBeanField {

  @Override
  protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
    try {
      return Context.valueOf(s.toUpperCase(Locale.ROOT).replace(" ", "_"));
    } catch (Exception e) {
      throw new CsvDataTypeMismatchException(e.getMessage());
    }
  }
}
