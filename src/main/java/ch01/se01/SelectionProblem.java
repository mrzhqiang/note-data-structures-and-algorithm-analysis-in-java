package ch01.se01;

import com.google.common.base.CharMatcher;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import internal.javafx.ApplicationInfo;
import internal.javafx.Applications;
import internal.javafx.Console;
import internal.javafx.Dialogs;
import internal.jre.Namings;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static javafx.scene.control.ProgressIndicator.INDETERMINATE_PROGRESS;

public class SelectionProblem extends Application {

    private static final int MIN_DATA_SIZE = 10;
    private static final int MAX_DATA_SIZE = 3000_0000;
    private static final String DATA_FILE = Namings.ofCanonical(SelectionProblem.class, ".dat");

    public static void main(String[] args) {
        launch(args);
    }

    @FXML
    TextField dataSizeTextField;
    @FXML
    Label stateLabel;
    @FXML
    Button generateDataButton;
    @FXML
    Button cleanDataButton;
    @FXML
    Button saveDataButton;
    @FXML
    Button loadDataButton;
    @FXML
    TextField numberTextField;
    @FXML
    Button findBeginButton;
    @FXML
    Button findEndButton;
    @FXML
    TextArea consoleTextArea;
    @FXML
    ProgressBar taskProgressBar;
    @FXML
    ToggleGroup algorithmGroup;
    @FXML
    RadioButton bubbleRadio;
    @FXML
    RadioButton truncateRadio;
    @FXML
    Label durationLabel;

    private List<Integer> dataList;
    private Disposable durationDisposable;
    private Disposable timer;
    private String filename;

    private final ObjectProperty<State> state = new SimpleObjectProperty<>(State.NOT_EXISTS);
    private final BooleanProperty running = new SimpleBooleanProperty(false);
    private final CompositeDisposable disposable = new CompositeDisposable();

    private final Console console = new Console();

    @Override
    public void start(Stage primaryStage) {
        Applications.start(primaryStage, ApplicationInfo.builder()
                .title("选择问题")
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
        console("初始化程序..");
        filename = String.format("%s-%s.md",
                getClass().getCanonicalName().toLowerCase().replaceAll("\\.", "-"),
                ISO_LOCAL_DATE.format(LocalDateTime.now()));
        Path filePath = Paths.get(filename);
        // 给文件加上 markdown 格式的表格头，以便于渲染出表格统计不同算法的耗时对比
        if (Files.notExists(filePath)) {
            try {
                String header = "|算法|大小|K 值|最大值|用时|" + System.lineSeparator()
                        + "|---|---|---|---|---|" + System.lineSeparator();
                Files.write(filePath, header.getBytes(), CREATE);
            } catch (IOException ignore) {
            }
        }
        disposable.add(JavaFxObservable.valuesOf(state)
                .subscribe(state -> {
                    stateLabel.setText(state.text);
                    stateLabel.setTextFill(state.color);
                }));
        disposable.add(JavaFxObservable.valuesOf(running)
                .doOnNext(disabled -> findBeginButton.setDisable(disabled))
                .subscribe(disabled -> findEndButton.setDisable(!disabled)));
        disposable.add(Observable.just(DATA_FILE)
                .map(Paths::get)
                .filter(Files::exists)
                .subscribe(exists -> state.setValue(State.SAVED)));
        disposable.add(console.bind().subscribe(s -> consoleTextArea.appendText(s)));
    }

    @FXML
    void onGenerateDataClicked() {
        console("准备生成数据，请稍等..");
        String dataSizeText = dataSizeTextField.getText();
        if (Strings.isNullOrEmpty(dataSizeText) || !checkNumber(dataSizeText)) {
            Dialogs.warn("无效的数据大小！").show();
            dataSizeTextField.requestFocus();
            return;
        }
        int dataSize = Integer.parseInt(dataSizeText);
        if (dataSize < MIN_DATA_SIZE || dataSize > MAX_DATA_SIZE) {
            Dialogs.warn("数据大小超出范围(10--30000000)").show();
            dataSizeTextField.requestFocus();
            return;
        }
        if (dataList != null && !dataList.isEmpty()) {
            Dialogs.confirm("是否重新生成？将覆盖当前已有数据！").ifPresent(buttonType -> generateData(dataSize));
            return;
        }
        generateData(dataSize);
    }

    private void generateData(int dataSize) {
        generateDataButton.setDisable(true);
        taskProgressBar.setProgress(0);
        dataList = Lists.newArrayListWithCapacity(dataSize);
        BigDecimal divisor = BigDecimal.valueOf(dataSize);
        if (durationDisposable != null) {
            durationDisposable.dispose();
        }
        durationDisposable = Observable.just(new SecureRandom())
                .subscribeOn(Schedulers.computation())
                .flatMap(random -> Observable.range(1, dataSize)
                        .doOnNext(it -> dataList.add(random.nextInt()))
                        .map(it -> BigDecimal.valueOf(it).divide(divisor, 3, BigDecimal.ROUND_HALF_EVEN)))
                .observeOn(JavaFxScheduler.platform())
                .subscribe(progress -> taskProgressBar.setProgress(progress.doubleValue()),
                        e -> {
                            console("生成数据出错：" + e.getMessage());
                            generateDataButton.setDisable(false);
                            Dialogs.error(e).show();
                        }, () -> {
                            console("数据生成完毕！");
                            generateDataButton.setDisable(false);
                            numberTextField.setText(String.valueOf(dataList.size() / 2));
                            state.setValue(State.CREATED);
                        });
    }

    @FXML
    void onSaveDataClicked() {
        if (dataList == null || dataList.isEmpty()) {
            Dialogs.warn("当前数据无效，保存失败！").show();
            return;
        }
        console("准备保存数据，请稍等..");
        saveDataButton.setDisable(true);
        taskProgressBar.setProgress(INDETERMINATE_PROGRESS);
        if (durationDisposable != null) {
            durationDisposable.dispose();
        }
        durationDisposable = Observable.just(Paths.get(DATA_FILE))
                .subscribeOn(Schedulers.io())
                .doOnNext(Files::deleteIfExists)
                .flatMap(path -> Observable.just(dataList)
                        .map(it -> it.stream().map(String::valueOf).collect(Collectors.toList()))
                        .map(it -> Files.write(path, it, CREATE)))
                .observeOn(JavaFxScheduler.platform())
                .subscribe(path -> console("数据已保存到文件：" + path), e -> {
                    console("保存数据出错：" + e.getMessage());
                    saveDataButton.setDisable(false);
                    taskProgressBar.setProgress(0);
                    Dialogs.error(e).show();
                }, () -> {
                    console("数据保存完毕！");
                    saveDataButton.setDisable(false);
                    taskProgressBar.setProgress(0);
                    state.setValue(State.EXISTS);
                });
    }

    @FXML
    void onLoadDataClicked() {
        if (Files.notExists(Paths.get(DATA_FILE))) {
            Dialogs.warn("当前数据文件不存在，无法加载！").show();
            return;
        }
        if (dataList != null && !dataList.isEmpty()) {
            Dialogs.confirm("是否重新加载？将覆盖当前已有数据！").ifPresent(buttonType -> loadData());
            return;
        }
        loadData();
    }

    private void loadData() {
        console("准备加载数据，请稍等..");
        loadDataButton.setDisable(true);
        taskProgressBar.setProgress(INDETERMINATE_PROGRESS);
        if (durationDisposable != null) {
            durationDisposable.dispose();
        }
        durationDisposable = Observable.just(DATA_FILE)
                .subscribeOn(Schedulers.io())
                .map(Paths::get)
                .map(Files::readAllLines)
                .filter(it -> !it.isEmpty())
                .map(it -> it.stream().filter(this::checkNumber).map(Integer::parseInt).collect(Collectors.toList()))
                .observeOn(JavaFxScheduler.platform())
                .subscribe(it -> {
                    if (dataList == null) {
                        dataList = Lists.newArrayList(it);
                    } else {
                        dataList.clear();
                        dataList.addAll(it);
                    }
                    console("数据已加载到内存！");
                    dataSizeTextField.setText(String.valueOf(dataList.size()));
                }, e -> {
                    console("加载数据出错：" + e.getMessage());
                    loadDataButton.setDisable(false);
                    taskProgressBar.setProgress(0);
                    Dialogs.error(e).show();
                }, () -> {
                    console("数据加载完毕！");
                    loadDataButton.setDisable(false);
                    taskProgressBar.setProgress(0);
                    numberTextField.setText(String.valueOf(dataList.size() / 2));
                    state.setValue(State.EXISTS);
                });
    }

    @FXML
    void onCleanDataClicked() {
        Dialogs.confirm("是否清理数据？将删除当前数据和文件！")
                .ifPresent(buttonType -> {
                    console("准备清理数据，请稍等..");
                    cleanDataButton.setDisable(true);
                    taskProgressBar.setProgress(INDETERMINATE_PROGRESS);
                    if (dataList != null) {
                        dataList.clear();
                    }
                    if (durationDisposable != null) {
                        durationDisposable.dispose();
                    }
                    durationDisposable = Observable.just(DATA_FILE)
                            .delay(1, TimeUnit.SECONDS)
                            .subscribeOn(Schedulers.io())
                            .map(Paths::get)
                            .filter(Files::exists)
                            .doOnNext(Files::delete)
                            .observeOn(JavaFxScheduler.platform())
                            .subscribe(path -> console("已删除文件：" + path), e -> {
                                console("清理数据出错：" + e.getMessage());
                                cleanDataButton.setDisable(false);
                                taskProgressBar.setProgress(0);
                                Dialogs.error(e).show();
                            }, () -> {
                                console("数据清理完毕！");
                                cleanDataButton.setDisable(false);
                                taskProgressBar.setProgress(0);
                                state.setValue(State.NOT_EXISTS);
                            });
                });
    }

    @FXML
    void onFindBeginClicked() {
        int k = Integer.parseInt(numberTextField.getText());
        if (k < 0 || k > dataList.size()) {
            Dialogs.warn("无效的 K 值，请重新输入(1--" + dataList.size() + ")").show();
            return;
        }
        console("准备开始查找第 " + k + " 个最大值");
        running.setValue(true);
        taskProgressBar.setProgress(INDETERMINATE_PROGRESS);
        durationLabel.setText("计时：0 ms");
        if (timer != null) {
            timer.dispose();
        }
        timer = Observable.just(Stopwatch.createStarted())
                .subscribeOn(Schedulers.computation())
                .flatMap(watch -> Observable.interval(100, 100, TimeUnit.MILLISECONDS)
                        .filter(it -> watch.isRunning())
                        .map(it -> String.format("计时：%s", watch)))
                .observeOn(JavaFxScheduler.platform())
                .subscribe(text -> durationLabel.setText(text));
        if (durationDisposable != null) {
            durationDisposable.dispose();
        }
        durationDisposable = Observable.just(k)
                .subscribeOn(Schedulers.computation())
                .map(number -> {
                    if (bubbleRadio.isSelected()) {
                        return bubble(number);
                    } else if (truncateRadio.isSelected()) {
                        return truncate(number);
                    }
                    return "";
                })
                .observeOn(JavaFxScheduler.platform())
                .subscribe(this::console,
                        e -> {
                            console("查找出错：" + e.getMessage());
                            timer.dispose();
                            running.setValue(false);
                            taskProgressBar.setProgress(0);
                            Dialogs.error(e).show();
                        }, () -> {
                            console("查找完毕！");
                            timer.dispose();
                            running.setValue(false);
                            taskProgressBar.setProgress(0);
                        });
    }

    @FXML
    void onFindEndClicked() {
        console("停止查找！");
        if (durationDisposable != null) {
            durationDisposable.dispose();
        }
        if (timer != null) {
            timer.dispose();
        }
        taskProgressBar.setProgress(0);
        running.setValue(false);
    }

    private void console(String message) {
        console.log(message);
    }

    private boolean checkNumber(String value) {
        if (Strings.isNullOrEmpty(value)) {
            return false;
        }
        String numberText = value.trim().replaceFirst("-", "");
        return CharMatcher.inRange('0', '9').matchesAllOf(numberText);
    }

    private String bubble(int k) {
        // 复制数据，不破坏原有数据内容
        List<Integer> data = Lists.newArrayList(dataList);
        Stopwatch started = Stopwatch.createStarted();
        bubbleSort(data);
        long maxNumber = data.get(k - 1);
        return writeFile("冒泡算法", dataList.size(), k, maxNumber, started.stop().toString());
    }

    private String truncate(int k) {
        Stopwatch started = Stopwatch.createStarted();
        // 需要倒序排序的数组
        List<Integer> sortList = dataList.stream().limit(k).collect(Collectors.toList());
        bubbleSort(sortList);
        // 剩下的数组
        List<Integer> remainingList = dataList.stream().skip(k).collect(Collectors.toList());
        // 逐个从剩下的数组中取新元素
        for (int newElement : remainingList) {
            // 找到第 k 个元素的下标位置
            int i = k - 1;
            // 如果新元素小于第 k 个元素，则忽略之
            if (newElement < sortList.get(i)) {
                continue;
            }
            // 找到正确的位置，即比前一个元素小（或成为第一个元素），比后一个元素大（或与之相等）
            while (i > 0 && newElement >= sortList.get(i)) {
                i--;
            }
            // 挤出最后一个元素，不会造成数组拷贝
            sortList.remove(k - 1);
            // 将新元素放在正确的位置上，因为容量不变，所以不会导致扩容，但会将 i 之后的元素都往后移动一位
            sortList.add(i, newElement);
        }
        return writeFile("截断算法", dataList.size(), k, sortList.get(k - 1), started.stop().toString());
    }

    private void bubbleSort(List<Integer> data) {
        for (int i = 0; i < data.size() - 1; i++) {
            for (int j = 0; j < data.size() - 1 - i; j++) {
                if (data.get(j) < data.get(j + 1)) {
                    // set 会返回旧元素，恰好可以放在 j 上
                    data.set(j, data.set(j + 1, data.get(j)));
                }
            }
        }
    }

    private String writeFile(String name, int size, int k, long maxNumber, String time) {
        String result = String.format("|%s|%s|%s|%s|%s|%n", name, size, k, maxNumber, time);
        Observable.just(result)
                .map(String::getBytes)
                .blockingSubscribe(bytes -> Files.write(Paths.get(filename), bytes, CREATE, APPEND));
        return String.format("找到最大值：%s，用时：%s", maxNumber, time);
    }

    private enum State {
        NOT_EXISTS("未准备", Color.RED),
        CREATED("未保存", Color.BLUE),
        SAVED("未加载", Color.BLACK),
        EXISTS("已准备", Color.GREEN);
        private final String text;
        private final Color color;

        State(String text, Color color) {
            this.text = text;
            this.color = color;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
