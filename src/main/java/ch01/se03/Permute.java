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
import java.util.Locale;

public class Permute extends Application {

    public TextField textTextField;
    public Button randomButton;
    public TextArea consoleTextArea;
    public Button startButton;

    public static void main(String[] args) {
        launch(args);
    }

    private final Actuator starting = new Actuator();
    private final Console console = new Console();
    private final CompositeDisposable disposable = new CompositeDisposable();

    @Override
    public void start(Stage primaryStage) {
        Applications.start(primaryStage, ApplicationInfo.builder()
                .title("回文串")
                .fxml(Applications.fxml(this))
                .build());
    }

    @Override
    public void stop() throws Exception {
        disposable.clear();
        Schedulers.shutdown();
    }

    @FXML
    void initialize() {
        disposable.add(console.bind().subscribe(text -> consoleTextArea.appendText(text)));
        disposable.add(starting.bind()
                .doOnNext(state -> randomButton.setDisable(state))
                .doOnNext(state -> startButton.setDisable(state))
                .map(running -> running ? "正在打印.." : "开始打印")
                .subscribe(text -> startButton.setText(text)));
        textTextField.setText("abc");
    }

    @FXML
    void onRandom() {
        textTextField.clear();
        String lowerLetter = "abcdefghijklmnopqrstuvwxyz";
        String world = lowerLetter + lowerLetter.toUpperCase(Locale.ROOT);
        SecureRandom random = new SecureRandom();
        disposable.add(Observable.range(1, 4)
                .subscribeOn(Schedulers.computation())
                .map(it -> world.charAt(random.nextInt(world.length())))
                .map(Object::toString)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(it -> textTextField.appendText(it)));
    }

    @FXML
    void onStart() {
        starting.running();
        String text = textTextField.getText();
        if (Strings.isNullOrEmpty(text)) {
            Dialogs.warn("请输入字符串").show();
            textTextField.requestFocus();
            return;
        }

        permute(text);

        starting.finished();
    }

    private void permute(String str) {
        char[] chars = str.toCharArray();
        permute(chars, 0, chars.length);
    }

    private void permute(char[] str, int low, int high) {
        // 1. base case
        if (low == high) {
            console.log(String.valueOf(str));
            return;
        }

        // 2. making progress
        for (int i = low; i < high; i++) {
            // 3. design rule
            permute(swap(str, low, i), low + 1, high);
        }
    }

    // 4. compound interest rule
    private char[] swap(char[] str, int i1, int i2) {
        char[] dest = new char[str.length];
        System.arraycopy(str, 0, dest, 0, str.length);
        if (i1 != i2) {
            char temp = dest[i1];
            dest[i1] = dest[i2];
            dest[i2] = temp;
        }
        return dest;
    }
}
