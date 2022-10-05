package ac.uk.soton.ecs.group22.addashboard.events;

import ac.uk.soton.ecs.group22.addashboard.controller.FileType;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import lombok.Getter;

/**
 * Event which is run when an invalid file is attempted to be loaded in.
 */
public class InvalidFileEvent {

  @Getter
  public static Set<Consumer<InvalidFileEvent>> listeners = new HashSet<>();

  /**
   * Notifies the listeners.
   */
  public void call() {
    listeners.forEach(listener -> listener.accept(this));
  }

  /**
   * @param fileType The type of the invalid file.
   */
  public InvalidFileEvent(FileType fileType) {
    this.fileType = fileType;
  }

  @Getter
  private final FileType fileType;

}
