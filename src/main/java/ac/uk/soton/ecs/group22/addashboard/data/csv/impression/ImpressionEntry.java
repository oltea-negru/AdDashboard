package ac.uk.soton.ecs.group22.addashboard.data.csv.impression;

import ac.uk.soton.ecs.group22.addashboard.data.csv.converter.StringDateToEpochLongConverter;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.converter.TextToAgeConverter;
import ac.uk.soton.ecs.group22.addashboard.data.csv.impression.converter.TextToContextConverter;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import lombok.Getter;

/**
 * Represents an impression on the advert, loaded in from the file.
 * <p>
 * The impression may not be unique.
 */
public class ImpressionEntry {

  @CsvCustomBindByName(converter = StringDateToEpochLongConverter.class)
  @Getter
  private long date;
  @CsvBindByName
  @Getter
  private long id;
  @CsvBindByName
  @Getter
  private Gender gender;
  @CsvCustomBindByName(converter = TextToAgeConverter.class)
  @Getter
  private AgeRange age;
  @CsvBindByName
  @Getter
  private Income income;
  @CsvCustomBindByName(converter = TextToContextConverter.class)
  @Getter
  private Context context;
  @CsvBindByName
  @Getter
  private double impressionCost;

  public ImpressionEntry() {

  }

  public ImpressionEntry(long date, long id, Gender gender, AgeRange age, Income income, Context context, double impressionCost) {
    this.date = date;
    this.id = id;
    this.gender = gender;
    this.age = age;
    this.income = income;
    this.context = context;
    this.impressionCost = impressionCost;
  }
}
