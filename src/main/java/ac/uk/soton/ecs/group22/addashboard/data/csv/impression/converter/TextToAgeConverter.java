package ac.uk.soton.ecs.group22.addashboard.data.csv.impression.converter;

import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.AgeRange;
import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

/**
 * OpenCSV converter to convert age in file to enums.
 */
public class TextToAgeConverter extends AbstractBeanField {

  @Override
  protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
    return switch (s) {
      case "<25" -> AgeRange.LESS_THAN_TWENTY_FIVE;
      case "25-34" -> AgeRange.TWENTY_FIVE_TO_THIRTY_FOUR;
      case "35-44" -> AgeRange.THIRTY_FIVE_TO_FOURTY_FOUR;
      case "45-54" -> AgeRange.FOURTY_FIVE_TO_FIFTY_FOUR;
      case ">54" -> AgeRange.MORE_THAN_FIFTY_FOUR;
      default -> throw new CsvDataTypeMismatchException(s + " is not a valid age range");
    };
  }
}
