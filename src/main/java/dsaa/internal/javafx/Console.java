package dsaa.internal.javafx;

import io.reactivex.Observable;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Console {
  private final StringProperty message = new SimpleStringProperty();

  public Observable<String> bind() {
    return JavaFxObservable.valuesOf(message)
        .filter(Objects::nonNull)
        .map(this::formatTimestamp)
        .observeOn(JavaFxScheduler.platform());
  }

  public void log(String content) {
    log.info(content);
    message.setValue(content);
    message.setValue(null);
  }

  private String formatTimestamp(String value) {
    String timestamp = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());
    return String.format("[%s] - %s%s", timestamp, value, System.lineSeparator());
  }
}
