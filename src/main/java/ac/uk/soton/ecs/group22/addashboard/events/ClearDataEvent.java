package ac.uk.soton.ecs.group22.addashboard.events;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import lombok.Getter;

/**
 * Event which is run when "clear data" button is clicked.
 */
public class ClearDataEvent {

  @Getter
  public static Set<Consumer<ClearDataEvent>> listeners = new HashSet<>();

  /**
   * Notifies the listeners.
   */
  public void call() {
    listeners.forEach(listener -> listener.accept(this));
  }

  public ClearDataEvent() {

  }

}
