package ch01.se03;

import com.google.common.base.Strings;
import internal.javafx.*;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class Include extends Application {

    @FXML
    TextField filenameTextField;
    @FXML
    Button loadFilenameButton;
    @FXML
    TextArea consoleTextArea;
    @FXML
    Button startButton;

    public static void main(String[] args) {
        launch(args);
    }

    private final Actuator starting = new Actuator();
    private final Console console = new Console();
    private final CompositeDisposable disposable = new CompositeDisposable();

    private final Set<String> filenames = new HashSet<>();

    @Override
    public void start(Stage primaryStage) {
        Applications.start(primaryStage, ApplicationInfo.builder()
                .title("Include 练习")
                .fxml(Applications.fxml(this))
                .build());
    }

    @Override
    public void stop() {
        filenames.clear();
        disposable.clear();
        Schedulers.shutdown();
    }

    @FXML
    void initialize() {
        disposable.add(console.bind().subscribe(text -> consoleTextArea.appendText(text)));
        disposable.add(starting.bind()
                .doOnNext(state -> startButton.setDisable(state))
                .map(running -> running ? "正在解析.." : "开始解析")
                .subscribe(text -> startButton.setText(text)));
    }

    @FXML
    void onLoadFilename() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("请选择你要加载的文件");
        chooser.setInitialDirectory(new File("./docs/data/"));
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            filenameTextField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    void onStart() {
        starting.running();
        // 清理上一次加载过的文件地址
        filenames.clear();

        String text = filenameTextField.getText();
        if (Strings.isNullOrEmpty(text)) {
            Dialogs.warn("请输入文件地址").show();
            filenameTextField.requestFocus();
            return;
        }

        Path path = Paths.get(text);
        if (!Files.exists(path)) {
            Dialogs.warn("无效的文件地址").show();
            filenameTextField.requestFocus();
            return;
        }

        try {
            processFile(path.toRealPath().toString());
        } catch (IOException e) {
            Dialogs.error("加载文件出错", e).show();
        }

        starting.finished();
    }

    private void processFile(String filename) {
        if (filenames.contains(filename)) {
            console.log("已加载过：" + filename);
            // 已加载过，忽略
            return;
        }

        console.log("准备加载：" + filename);
        // 记录文件绝对地址到列表
        filenames.add(filename);
        try {
            Files.readAllLines(Paths.get(filename)).stream()
                    .peek(it -> {
                        // 打印正常内容
                        if (!it.contains("#include")) {
                            console.log(it);
                        }
                    })
                    // 确认内容包含加载指令
                    .filter(it -> it.contains("#include"))
                    // 取得需要加载的文件地址
                    .map(it -> it.replaceAll("#include", "").trim())
                    .map(Paths::get)
                    .filter(Files::exists)
                    // 不能读取一个目录
                    .filter(it -> !Files.isDirectory(it))
                    // 递归处理需要加载的文件地址
                    .forEach(it -> {
                        try {
                            processFile(it.toRealPath().toString());
                        } catch (IOException ignore) {
                        }
                    });
        } catch (IOException e) {
            Dialogs.error("读取文件出错", e).show();
        }
    }
}
