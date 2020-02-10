package dsaa.internal.javafx;

import com.google.common.base.Preconditions;
import dsaa.internal.jre.Namings;
import java.net.URL;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum Applications {
  ;

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

  public static void start(Stage primaryStage, ApplicationInfo info) {
    log.info("准备启动程序..");
    try {
      primaryStage.setTitle(info.getTitle());
      log.info("准备加载布局..");
      Parent root = FXMLLoader.load(info.getFxml());
      if (info.getCss() != null) {
        log.info("准备添加样式..");
        root.getStylesheets().add(info.getCss().toExternalForm());
      }
      log.info("正在生成场景..");
      Scene scene = new Scene(root);
      primaryStage.setScene(scene);
      log.info("即将显示主舞台..");
      primaryStage.show();
    } catch (Exception e) {
      Alert alert = Dialogs.error(e);
      alert.setWidth(primaryStage.getWidth());
      alert.setOnCloseRequest(event -> {
        Platform.exit();
      });
      alert.show();
      log.error("启动 JavaFX 程序出错！", e);
    }
    primaryStage.setOnCloseRequest(event -> {
      Dialogs.confirm("是否关闭程序？将停止所有正在运行的任务！").ifPresent(buttonType -> Platform.exit());
      event.consume();
    });
  }
}
