package ch01.se03;

import com.google.common.base.Strings;
import internal.javafx.*;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.security.SecureRandom;
import java.util.Random;

public class BinaryOne extends Application {

    public Button computeButton;
    public TextArea consoleTextArea;
    public Button randomButton;
    public TextField binaryTextField;

    public static void main(String[] args) {
        launch(args);
    }

    private final Actuator starting = new Actuator();
    private final Console console = new Console();
    private final CompositeDisposable disposable = new CompositeDisposable();

    @Override
    public void start(Stage primaryStage) {
        Applications.start(primaryStage, ApplicationInfo.builder()
                .title("二进制中 1 的个数")
                .fxml(Applications.fxml(this))
                .build());
    }

    @Override
    public void stop() {
        disposable.clear();
        Schedulers.shutdown();
    }

    @FXML
    void initialize() {
        disposable.add(console.bind().subscribe(text -> consoleTextArea.appendText(text)));
        disposable.add(starting.bind()
                .doOnNext(state -> randomButton.setDisable(state))
                .doOnNext(state -> computeButton.setDisable(state))
                .map(running -> running ? "正在计算.." : "开始计算")
                .subscribe(text -> computeButton.setText(text)));
    }

    @FXML
    void onRandom() {
        disposable.add(Observable.just(new SecureRandom())
                .subscribeOn(Schedulers.computation())
                .map(Random::nextInt)
                .doOnNext(it -> console.log("生成整数：" + it))
                .map(Integer::toBinaryString)
                .doOnNext(it -> console.log("二进制：" + it))
                .observeOn(JavaFxScheduler.platform())
                .subscribe(it -> binaryTextField.setText(it)));
    }

    @FXML
    void onCompute() {
        starting.running();
        String text = binaryTextField.getText();
        if (Strings.isNullOrEmpty(text)) {
            Dialogs.warn("请输入二进制数").show();
            binaryTextField.requestFocus();
            return;
        }

        try {
            int n = Integer.parseUnsignedInt(text, 2);
            int ones = ones(n);
            console.log("经过计算 [" + n + "] 共有 [" + ones + "] 个数字 1");
        } catch (Exception e) {
            Dialogs.error("无效的二进制数", e).show();
        }

        starting.finished();
    }

    private int ones(int n) {
        int i = 0;
        if (n < 0) {
            n = n + 1;
            n = Math.abs(n);
            i = 32;
        }
        int ones = n < 2 ? n : n % 2 + ones(n / 2);
        return i > 0 ? i - ones : ones;
    }
}
