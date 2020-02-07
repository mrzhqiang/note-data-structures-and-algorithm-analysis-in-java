package dsaa.chapter01.section01;

import com.google.common.base.CharMatcher;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static javafx.scene.control.ProgressIndicator.INDETERMINATE_PROGRESS;

public class SelectionProblem extends Application {
  private static final int MIN_DATA_SIZE = 10;
  private static final int MAX_DATA_SIZE = 3000_0000;
  private static final String DATA_FILE = SelectionProblem.class.getCanonicalName()
      .toLowerCase().replaceAll("\\.", "-") + ".dat";

  public static void main(String[] args) {
    launch(args);
  }

  public TextField dataSizeTextField;
  public Label stateLabel;
  public Button generateDataButton;
  public Button cleanDataButton;
  public Button saveDataButton;
  public Button loadDataButton;
  public TextField numberTextField;
  public Button findBeginButton;
  public Button findEndButton;
  public TextArea consoleTextArea;
  public ProgressBar taskProgressBar;
  public ToggleGroup algorithmGroup;
  public RadioButton bubbleRadio;
  public RadioButton truncateRadio;
  public Label durationLabel;

  private List<Integer> dataList;
  private Disposable durationDisposable;
  private String filename;

  private final ObjectProperty<State> state = new SimpleObjectProperty<>(State.NOT_EXISTS);
  private final BooleanProperty running = new SimpleBooleanProperty(false);
  private final CompositeDisposable disposable = new CompositeDisposable();

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("选择问题");
    try {
      Parent root = FXMLLoader.load(getClass().getResource("./selection-problem.fxml"));
      Scene scene = new Scene(root);
      primaryStage.setScene(scene);
      primaryStage.show();
    } catch (Exception e) {
      Alert alert = showError("启动出错：" + e.getMessage(), e);
      alert.setWidth(primaryStage.getWidth());
      alert.setOnCloseRequest(event -> Platform.exit());
      alert.show();
    }
    primaryStage.setOnCloseRequest(event -> {
      showConfirmation("是否关闭程序？将停止所有正在运行的任务！")
          .ifPresent(buttonType -> Platform.exit());
      event.consume();
    });
  }

  @Override public void stop() {
    disposable.clear();
    Schedulers.shutdown();
  }

  public void initialize() {
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
        Files.write(filePath, header.getBytes(), StandardOpenOption.CREATE);
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
        .map(s -> Paths.get(s))
        .filter(path -> Files.exists(path))
        .subscribe(exists -> state.setValue(State.SAVED)));
  }

  public void onGenerateDataClicked() {
    console("准备生成数据，请稍等..");
    String dataSizeText = dataSizeTextField.getText();
    if (Strings.isNullOrEmpty(dataSizeText) || !checkNumber(dataSizeText)) {
      showWarning("无效的数据大小！");
      dataSizeTextField.requestFocus();
      return;
    }
    int dataSize = Integer.parseInt(dataSizeText);
    if (dataSize < MIN_DATA_SIZE || dataSize > MAX_DATA_SIZE) {
      showWarning("数据大小超出范围(10--30000000)");
      dataSizeTextField.requestFocus();
      return;
    }
    if (dataList != null && !dataList.isEmpty()) {
      showConfirmation("是否重新生成？将覆盖当前已有数据！").ifPresent(buttonType -> generateData(dataSize));
      return;
    }
    generateData(dataSize);
  }

  private void generateData(int dataSize) {
    generateDataButton.setDisable(true);
    taskProgressBar.setProgress(0);
    dataList = Lists.newArrayListWithCapacity(dataSize);
    disposable.add(Observable.just(new SecureRandom())
        .subscribeOn(Schedulers.computation())
        .flatMap(random -> Observable.range(1, dataSize)
            .doOnNext(integer -> dataList.add(random.nextInt()))
            .map(integer -> (integer) * 1.0 / dataSize))
        .filter(progress -> dataSize > 1000 && progress * 1000 % 10 == 0)
        .observeOn(JavaFxScheduler.platform())
        .subscribe(progress -> taskProgressBar.setProgress(progress),
            e -> {
              console("生成数据出错：" + e.getLocalizedMessage());
              generateDataButton.setDisable(false);
              showError("生成数据出错！", e);
            }, () -> {
              console("数据生成完毕！");
              generateDataButton.setDisable(false);
              numberTextField.setText(String.valueOf(dataList.size() / 2));
              state.setValue(State.CREATED);
            }));
  }

  public void onSaveDataClicked() {
    if (dataList == null || dataList.isEmpty()) {
      showWarning("当前数据无效，保存失败！");
      return;
    }
    console("准备保存数据，请稍等..");
    saveDataButton.setDisable(true);
    taskProgressBar.setProgress(INDETERMINATE_PROGRESS);
    disposable.add(Observable.just(Paths.get(DATA_FILE))
        .subscribeOn(Schedulers.io())
        .doOnNext(Files::deleteIfExists)
        .flatMap(path -> Observable.just(dataList)
            .map(integers -> integers.stream()
                .map(String::valueOf)
                .collect(Collectors.toList()))
            .map(strings -> Files.write(path, strings, StandardOpenOption.CREATE)))
        .observeOn(JavaFxScheduler.platform())
        .subscribe(path -> {
          console("数据已保存到文件：" + path);
          taskProgressBar.setProgress(0);
        }, e -> {
          console("保存数据出错：" + e.getLocalizedMessage());
          saveDataButton.setDisable(false);
          taskProgressBar.setProgress(0);
          showError("保存数据出错！", e);
        }, () -> {
          console("数据保存完毕！");
          saveDataButton.setDisable(false);
          state.setValue(State.EXISTS);
        }));
  }

  public void onLoadDataClicked() {
    if (Files.notExists(Paths.get(DATA_FILE))) {
      showWarning("当前数据文件不存在，无法加载！");
      return;
    }
    if (dataList != null && !dataList.isEmpty()) {
      showConfirmation("是否重新加载？将覆盖当前已有数据！").ifPresent(buttonType -> loadData());
      return;
    }
    loadData();
  }

  private void loadData() {
    console("准备加载数据，请稍等..");
    loadDataButton.setDisable(true);
    taskProgressBar.setProgress(INDETERMINATE_PROGRESS);
    disposable.add(Observable.just(DATA_FILE)
        .subscribeOn(Schedulers.io())
        .map(s -> Paths.get(s))
        .map(Files::readAllLines)
        .filter(strings -> !strings.isEmpty())
        .map(strings -> strings
            .stream()
            .filter(this::checkNumber)
            .map(Integer::parseInt)
            .collect(Collectors.toList()))
        .observeOn(JavaFxScheduler.platform())
        .subscribe(integers -> {
          if (dataList == null) {
            dataList = Lists.newArrayList(integers);
          } else {
            dataList.clear();
            dataList.addAll(integers);
          }
          console("数据已加载到内存！");
          dataSizeTextField.setText(String.valueOf(dataList.size()));
          taskProgressBar.setProgress(0);
        }, e -> {
          console("加载数据出错：" + e.getLocalizedMessage());
          loadDataButton.setDisable(false);
          taskProgressBar.setProgress(0);
          e.printStackTrace();
        }, () -> {
          console("数据加载完毕！");
          loadDataButton.setDisable(false);
          numberTextField.setText(String.valueOf(dataList.size() / 2));
          state.setValue(State.EXISTS);
        }));
  }

  public void onCleanDataClicked() {
    showConfirmation("是否清理数据？将删除当前数据和文件！").ifPresent(buttonType -> {
      console("准备清理数据，请稍等..");
      cleanDataButton.setDisable(true);
      taskProgressBar.setProgress(INDETERMINATE_PROGRESS);
      if (dataList != null) {
        dataList.clear();
      }
      disposable.add(Observable.just(DATA_FILE)
          .subscribeOn(Schedulers.io())
          .map(s -> Paths.get(s))
          .filter(path -> Files.exists(path))
          .doOnNext(Files::delete)
          .observeOn(JavaFxScheduler.platform())
          .subscribe(path -> {
            console("已删除文件：" + path);
            taskProgressBar.setProgress(0);
          }, e -> {
            console("清理数据出错：" + e.getLocalizedMessage());
            cleanDataButton.setDisable(false);
            taskProgressBar.setProgress(0);
            e.printStackTrace();
          }, () -> {
            console("数据清理完毕！");
            cleanDataButton.setDisable(false);
            state.setValue(State.NOT_EXISTS);
          }));
    });
  }

  public void onFindBeginClicked() {
    int k = Integer.parseInt(numberTextField.getText());
    if (k < 0 || k > dataList.size()) {
      showWarning("无效的 K 值，请重新输入(1--" + dataList.size() + ")");
      return;
    }
    console("准备开始查找第 " + k + " 个最大值");
    running.setValue(true);
    taskProgressBar.setProgress(INDETERMINATE_PROGRESS);
    durationLabel.setText("计时：0 ms");
    if (durationDisposable != null) {
      durationDisposable.dispose();
    }
    durationDisposable = Observable.just(Stopwatch.createStarted())
        .subscribeOn(Schedulers.computation())
        .flatMap(watch -> Observable.interval(1000, 100, TimeUnit.MILLISECONDS)
            .filter(aLong -> watch.isRunning())
            .map(aLong -> watch.toString())
            .map(duration -> String.format("计时：%s", duration)))
        .observeOn(JavaFxScheduler.platform())
        .subscribe(text -> durationLabel.setText(text));
    disposable.add(Observable.just(k)
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
        .subscribe(message -> {
              console(message);
              taskProgressBar.setProgress(0);
            },
            e -> {
              console("查找出错：" + e.getLocalizedMessage());
              durationDisposable.dispose();
              taskProgressBar.setProgress(0);
              running.setValue(false);
              showError("查找出错！", e);
            }, () -> {
              console("查找完毕！");
              durationDisposable.dispose();
              running.setValue(false);
            }));
  }

  public void onFindEndClicked() {
    console("停止查找！");
    if (durationDisposable != null) {
      durationDisposable.dispose();
    }
    disposable.dispose();
    taskProgressBar.setProgress(0);
    running.setValue(false);
  }

  private void console(String message) {
    String timestamp = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());
    String content = String.format("[%s] - %s", timestamp, message);
    System.out.println(content);
    consoleTextArea.appendText(content);
    consoleTextArea.appendText(System.lineSeparator());
  }

  private boolean checkNumber(String value) {
    if (Strings.isNullOrEmpty(value)) {
      return false;
    }
    String numberText = value.trim().replaceFirst("-", "");
    return CharMatcher.inRange('0', '9').matchesAllOf(numberText);
  }

  private String bubble(int k) {
    int[] data = dataList.stream().mapToInt(value -> value).toArray();
    Stopwatch started = Stopwatch.createStarted();
    bubbleSort(data);
    long maxNumber = data[k - 1];
    return writeFile("冒泡算法", dataList.size(), k, maxNumber, started.stop().toString());
  }

  private void bubbleSort(int[] data) {
    for (int i = 0; i < data.length - 1; i++) {
      for (int j = 0; j < data.length - 1 - i; j++) {
        if (data[j] < data[j + 1]) {
          int temp = data[j];
          data[j] = data[j + 1];
          data[j + 1] = temp;
        }
      }
    }
  }

  private String writeFile(String name, int size, int k, long maxNumber, String time) {
    String result = String.format(
        "|%s|%s|%s|%s|%s|%s",
        name, size, k, maxNumber, time, System.lineSeparator());
    disposable.add(Observable.just(result)
        .subscribeOn(Schedulers.io())
        .map(String::getBytes)
        .subscribe(bytes -> Files.write(Paths.get(filename), bytes, StandardOpenOption.CREATE,
            StandardOpenOption.APPEND)));
    return String.format("找到最大值：%s，用时：%s", maxNumber, time);
  }

  private String truncate(int k) {
    Stopwatch started = Stopwatch.createStarted();
    int[] truncate = dataList.stream().limit(k).mapToInt(value -> value).toArray();
    bubbleSort(truncate);
    int[] remaining = dataList.stream().skip(k).mapToInt(value -> value).toArray();
    for (int remain : remaining) {
      if (remain < truncate[k - 1]) {
        continue;
      }
      int index = -1;
      for (int i = truncate.length - 1; i >= 0; i--) {
        if (remain < truncate[i]) {
          continue;
        }
        index = i;
      }
      if (index > -1 && remain >= truncate[index]) {
        int[] newData = new int[truncate.length];
        System.arraycopy(truncate, 0, newData, 0, index);
        newData[index] = remain;
        System.arraycopy(truncate, index, newData, index + 1, truncate.length - (index + 1));
        truncate = newData;
      }
    }
    return writeFile("截断算法", dataList.size(), k, truncate[k - 1], started.stop().toString());
  }

  private void showWarning(String message) {
    Alert alert = new Alert(Alert.AlertType.WARNING);
    alert.setTitle("警告！");
    alert.setHeaderText(message);
    alert.show();
  }

  private Alert showError(String headText, Throwable throwable) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("错误！");
    alert.setHeaderText(headText);
    if (throwable != null) {
      throwable.printStackTrace();
      StringWriter writer = new StringWriter();
      throwable.printStackTrace(new PrintWriter(writer));
      alert.setContentText(writer.toString());
    }
    return alert;
  }

  private Optional<ButtonType> showConfirmation(String message) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("请确认！");
    alert.setHeaderText(message);
    return alert.showAndWait().filter(ButtonType.OK::equals);
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

    @Override public String toString() {
      return text;
    }
  }
}
