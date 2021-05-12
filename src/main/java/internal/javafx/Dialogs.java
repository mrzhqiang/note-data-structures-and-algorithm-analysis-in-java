package internal.javafx;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import internal.jre.StackTraces;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.Optional;

@Slf4j
public enum Dialogs {
    ; // no instance

    private static final String DEFAULT_ERROR_MESSAGE = "Sorry! The program has an unknown error..";

    @SuppressWarnings("unused")
    public static Alert alert(String message) {
        return alert(message, null);
    }

    public static Alert alert(String message, @Nullable String content) {
        Preconditions.checkNotNull(message, "message == null");
        log.info("information={}, content={}", message, content);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("INFORMATION");
        alert.setHeaderText(message);
        if (content != null) {
            alert.setContentText(content);
        }
        return alert;
    }

    public static Alert warn(String message) {
        return warn(message, null);
    }

    public static Alert warn(String message, @Nullable String content) {
        Preconditions.checkNotNull(message, "message == null");
        log.info("warning={}, content={}", message, content);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("WARNING");
        alert.setHeaderText(message);
        if (content != null) {
            alert.setContentText(content);
        }
        return alert;
    }

    public static Alert error(Throwable error) {
        return error(error.toString(), error);
    }

    public static Alert error(@Nullable String message, Throwable cause) {
        Preconditions.checkNotNull(cause, "cause == null");
        String errorMsg = Strings.isNullOrEmpty(message) ? DEFAULT_ERROR_MESSAGE : message;
        log.info("error={}, content={}", errorMsg, cause);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText(errorMsg);
        alert.setContentText(StackTraces.of(cause));
        return alert;
    }

    public static Optional<ButtonType> confirm(String message) {
        return confirm(message, null);
    }

    public static Optional<ButtonType> confirm(String message, @Nullable String content) {
        Preconditions.checkNotNull(message, "message == null");
        log.info("confirmation={}, content={}", message, content);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("CONFIRM");
        alert.setHeaderText(message);
        if (content != null) {
            alert.setContentText(content);
        }
        return alert.showAndWait().filter(ButtonType.OK::equals);
    }

}
