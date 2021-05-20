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

import java.math.BigDecimal;
import java.security.SecureRandom;

public class PrintDouble extends Application {

    @FXML
    TextField numberTextField;
    @FXML
    Button randomButton;
    @FXML
    TextArea consoleTextArea;
    @FXML
    Button startButton;

    public static void main(String[] args) {
        launch(args);
    }

    private final Actuator generating = new Actuator();
    private final Actuator starting = new Actuator();
    private final Console console = new Console();
    private final CompositeDisposable disposable = new CompositeDisposable();

    @Override
    public void start(Stage primaryStage) {
        Applications.start(primaryStage, ApplicationInfo.builder()
                .title("Double Print 练习")
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
        disposable.add(generating.bind()
                .doOnNext(state -> randomButton.setDisable(state))
                .map(running -> running ? "正在生成.." : "随机生成")
                .subscribe(text -> randomButton.setText(text)));
        disposable.add(starting.bind()
                .doOnNext(state -> startButton.setDisable(state))
                .map(running -> running ? "正在打印.." : "开始打印")
                .subscribe(text -> startButton.setText(text)));
    }

    @FXML
    void onRandom() {
        generating.running();
        disposable.add(Observable.just(new SecureRandom())
                .subscribeOn(Schedulers.computation())
                .map(secureRandom -> secureRandom.nextInt() + secureRandom.nextDouble())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(number -> numberTextField.setText(number.toString()),
                        error -> {
                            console.log("生成 double 数字出错：" + error.getMessage());
                            generating.finished();
                            Dialogs.error(error).show();
                        }, () -> {
                            console.log("随机 double 数字生成完毕！");
                            generating.finished();
                        }));
    }

    @FXML
    void onStart() {
        String numberText = numberTextField.getText();
        if (Strings.isNullOrEmpty(numberText)) {
            Dialogs.warn("double 数字不能为空！").show();
            numberTextField.requestFocus();
            return;
        }
        double number;
        try {
            number = Double.parseDouble(numberText);
        } catch (Exception e) {
            Dialogs.error("无效的 double 数字", e).show();
            numberTextField.requestFocus();
            return;
        }

        starting.running();
        printDouble(number);
        starting.finished();
    }

    private void printDouble(double number) {
        // 允许负数
        if (number < 0) {
            printDigit("-");
            number = Math.abs(number);
        }

        // 整数范围
        int n = (int) number;
        if (number >= 10) {
            printOut(n);
        } else {
            printDigit(String.valueOf(n));
        }

        // 小数点
        printDigit(".");

        // 小数范围，需要注意精度问题：double - int 的精度以及小数位的精度
        printDecimal(BigDecimal.valueOf(number).subtract(BigDecimal.valueOf(n)).doubleValue(), 15);
    }

    private void printOut(int n) {
        if (n >= 10) {
            printOut(n / 10);
        }
        printDigit(String.valueOf(n % 10));
    }

    private void printDecimal(double d, int precision) {
        d = d * 10;
        int n = (int) d;
        printOut(n);
        d = BigDecimal.valueOf(d).subtract(BigDecimal.valueOf(n)).doubleValue();
        if (d > 0 && precision > 0) {
            printDecimal(d, precision - 1);
        }
    }

    private void printDigit(String value) {
        console.log("输出：" + value);
    }

}
