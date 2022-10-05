package ac.uk.soton.ecs.group22.addashboard.data.csv.impression;

import lombok.Getter;

public enum Context {
  NEWS("News"),
  SHOPPING("Shopping"),
  SOCIAL_MEDIA("Social Media"),
  BLOG("Blog"),
  HOBBIES("Hobbies"),
  TRAVEL("Travel");

  @Getter
  private final String formatted;

  Context(String formatted) {
    this.formatted = formatted;
  }

}
