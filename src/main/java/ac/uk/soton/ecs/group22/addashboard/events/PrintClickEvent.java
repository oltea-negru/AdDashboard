package ac.uk.soton.ecs.group22.addashboard.events;

import ac.uk.soton.ecs.group22.addashboard.MainController;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import lombok.Getter;

/**
 * Event which is run when PrintImage have been clicked.
 */
public class PrintClickEvent {

  @Getter
  public static Set<Consumer<PrintClickEvent>> listeners0 = new HashSet<>();
  @Getter
  public static Set<Consumer<PrintClickEvent>> listeners1 = new HashSet<>();
  @Getter
  public static Set<Consumer<PrintClickEvent>> listeners2 = new HashSet<>();

  /**
   * Notifies the listeners.
   */
  public void PDF0(){
    listeners0.forEach(listener -> listener.accept(this));
    MainController.savePDF(0,"All");
  }
  public void PDF1(){
    listeners1.forEach(listener -> listener.accept(this));
    MainController.savePDF(1,"All");
  }
  public void metricPDF() {
    listeners2.forEach(listener -> listener.accept(this));
    MainController.savePDF(2,"Metric");
  }
  public void comparePDF() {
    listeners2.forEach(listener -> listener.accept(this));
    MainController.savePDF(2,"Compare");
  }




}
