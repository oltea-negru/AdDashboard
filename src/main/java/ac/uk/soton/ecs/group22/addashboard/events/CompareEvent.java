package ac.uk.soton.ecs.group22.addashboard.events;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import lombok.Getter;

/**
 * Event that is called for changes made in the compare screen
 */
public class CompareEvent {
  @Getter
  public static Set<Consumer<CompareEvent>> listeners = new HashSet<>();

  /**
   * Notifies the listeners.
   */
  public void call() {
    listeners.forEach(listener -> listener.accept(this));
  }

}
