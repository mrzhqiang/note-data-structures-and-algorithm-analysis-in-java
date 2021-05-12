package internal.javafx;

import com.google.common.base.Preconditions;
import internal.jre.Namings;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;

@Slf4j
public enum Applications {
    ; // no instance

    @SuppressWarnings("unused")
    public static URL css(Application application) {
        Preconditions.checkNotNull(application, "application == null");
        Class<? extends Application> applicationClass = application.getClass();
        return applicationClass.getResource("./" + Namings.ofSimple(applicationClass, ".css"));
    }

    public static URL fxml(Application application) {
        Preconditions.checkNotNull(application, "application == null");
        Class<? extends Application> applicationClass = application.getClass();
        return applicationClass.getResource("./" + Namings.ofSimple(applicationClass, ".fxml"));
    }

    public static void start(Stage stage, Application application) {
        start(stage, ApplicationInfo.builder()
                .fxml(fxml(application))
                .build());
    }

    public static void start(Stage primaryStage, ApplicationInfo info) {
        log.info("prepare start application..");
        try {
            primaryStage.setTitle(info.getTitle());
            log.info("prepare load fxml..");
            Parent root = FXMLLoader.load(info.getFxml());
            if (info.getCss() != null) {
                log.info("prepare add css..");
                root.getStylesheets().add(info.getCss().toExternalForm());
            }
            log.info("generate scene now..");
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            log.info("it will show stage..");
            primaryStage.show();
        } catch (Exception e) {
            Alert alert = Dialogs.error(e);
            alert.setWidth(primaryStage.getWidth());
            alert.setOnCloseRequest(event -> {
                Platform.exit();
            });
            alert.show();
            log.error("start JavaFX application failed!", e);
        }
        primaryStage.setOnCloseRequest(event -> {
            Dialogs.confirm("Do you want to close the program? Will stop all running tasks!").ifPresent(buttonType -> Platform.exit());
            event.consume();
        });
    }

}
