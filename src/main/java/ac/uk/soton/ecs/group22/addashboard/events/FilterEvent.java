package ac.uk.soton.ecs.group22.addashboard.events;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import lombok.Getter;

/**
 * Event which is run when the filter is updated.
 */
public class FilterEvent {

  @Getter
  public static Set<Consumer<FilterEvent>> listeners = new HashSet<>();

  /**
   * Notifies the listeners.
   */
  public void call() {
    listeners.forEach(listener -> listener.accept(this));
  }

}
