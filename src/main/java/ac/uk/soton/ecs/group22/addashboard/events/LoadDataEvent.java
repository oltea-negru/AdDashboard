package ac.uk.soton.ecs.group22.addashboard.events;

import ac.uk.soton.ecs.group22.addashboard.controller.FileType;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import lombok.Getter;

/**
 * Event which is run when data is successfully loaded in.
 */
public class LoadDataEvent {

  @Getter
  public static Set<Consumer<LoadDataEvent>> listeners = new HashSet<>();

  /**
   * Notifies the listeners.
   */
  public void call() {
    listeners.forEach(listener -> listener.accept(this));
  }

  /**
   * @param fileType The file type of the data loaded in.
   */
  public LoadDataEvent(FileType fileType) {
    this.fileType = fileType;
  }

  @Getter
  private final FileType fileType;

}
