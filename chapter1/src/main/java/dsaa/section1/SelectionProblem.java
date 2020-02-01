package dsaa.section1;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import dsaa.TotalTimeHelper;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
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

import static javafx.scene.control.ProgressIndicator.INDETERMINATE_PROGRESS;

public class SelectionProblem extends Application {
  private static final int MAX_DATA_SIZE = 3000_0000;

  public Label stateLabel;
  public Button generateDataButton;
  public Button cleanDataButton;
  public TextField numberTextField;
  public Button findBeginButton;
  public Button findEndButton;
  public TextArea consoleTextArea;
  public ProgressBar taskProgressBar;
  public ToggleGroup algorithmGroup;
  public RadioButton bubbleRadio;
  public RadioButton truncateRadio;

  private List<Long> dataList;

  private final CompositeDisposable disposable = new CompositeDisposable();

  private enum State {
    NOT_EXISTS("不存在"),
    EXISTS("已生成"),
    ;
    private final String text;

    State(String text) {
      this.text = text;
    }

    @Override public String toString() {
      return text;
    }
  }

  private final ObjectProperty<State> state = new SimpleObjectProperty<>();

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("选择问题");
    try {
      Parent root = FXMLLoader.load(getClass().getResource("./selection-problem.fxml"));
      Scene scene = new Scene(root);
      primaryStage.setScene(scene);
      primaryStage.show();
    } catch (Exception e) {
      e.printStackTrace();
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("启动出错！");
      alert.setHeaderText(e.getLocalizedMessage());
      StringWriter writer = new StringWriter();
      PrintWriter printWriter = new PrintWriter(writer);
      e.printStackTrace(printWriter);
      alert.setContentText(writer.toString());
      // Platform.exit 在此处不触发 stop 方法；只有当 start 方法完成之后，此方法被调用才会触发 stop 方法。
      alert.showAndWait().ifPresent(buttonType -> Platform.exit());
    }
    primaryStage.setOnCloseRequest(event -> {
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("确认是否关闭程序？");
      alert.setHeaderText("将停止所有正在运行的任务！");
      alert.showAndWait().filter(ButtonType.OK::equals).ifPresent(buttonType -> Platform.exit());
    });
  }

  @Override public void stop() {
    disposable.dispose();
  }

  public void initialize() {
    showMessage("初始化程序..");
    state.addListener((observable, oldValue, newValue) -> {
      boolean exists = State.EXISTS.equals(newValue);
      if (exists) {
        stateLabel.setTextFill(Color.BLUE);
      } else {
        stateLabel.setTextFill(Color.RED);
      }
      stateLabel.setText(newValue.text);
      generateDataButton.setDisable(exists);
      cleanDataButton.setDisable(!exists);
      numberTextField.setDisable(!exists);
      findBeginButton.setDisable(!exists || isNotNumber(numberTextField.getText()));
    });
    state.setValue(State.NOT_EXISTS);
    numberTextField.textProperty().addListener((observable, oldValue, newValue) ->
        findBeginButton.setDisable(isNotNumber(newValue)));
    findEndButton.setDisable(true);
    consoleTextArea.setEditable(false);
  }

  public void onGenerateDataClicked() {
    showMessage("准备生成数据，请稍等..");
    generateDataButton.setDisable(true);
    taskProgressBar.setProgress(0);
    if (dataList == null) {
      dataList = Lists.newArrayListWithCapacity(MAX_DATA_SIZE);
    }
    dataList.clear();

    disposable.add(Observable.create((ObservableOnSubscribe<Double>) emitter -> {
      Random random = new SecureRandom();
      for (int i = 0; i < MAX_DATA_SIZE; i++) {
        double progress = (i + 1) * 1.0 / MAX_DATA_SIZE;
        long value = random.nextLong();
        dataList.add(value);
        emitter.onNext(progress);
      }
      emitter.onComplete();
    }).subscribeOn(Schedulers.io())
        .observeOn(JavaFxScheduler.platform())
        .subscribe(
            aDouble -> taskProgressBar.setProgress(aDouble),
            e -> {
              e.printStackTrace();
              showMessage("生成数据文件出错：" + e.getLocalizedMessage());
              generateDataButton.setDisable(false);
              Alert alert = new Alert(Alert.AlertType.ERROR);
              alert.setTitle("生成数据文件出错！");
              alert.setHeaderText(e.getLocalizedMessage());
              StringWriter writer = new StringWriter();
              PrintWriter printWriter = new PrintWriter(writer);
              e.printStackTrace(printWriter);
              alert.setContentText(writer.toString());
              alert.show();
            }, () -> {
              showMessage("数据生成完毕！");
              generateDataButton.setDisable(false);
              state.setValue(State.EXISTS);
            }));
  }

  public void onCleanDataClicked() {
    showMessage("准备清理数据，请稍等..");
    cleanDataButton.setDisable(true);
    taskProgressBar.setProgress(INDETERMINATE_PROGRESS);
    if (dataList != null) {
      dataList.clear();
    }
    taskProgressBar.setProgress(0);
    cleanDataButton.setDisable(false);
    state.setValue(State.NOT_EXISTS);
  }

  public void onFindBeginClicked() {
    int k = Integer.parseInt(numberTextField.getText());
    showMessage("准备开始查找第 " + k + " 个最大值");
    findBeginButton.setDisable(true);
    findEndButton.setDisable(false);
    cleanDataButton.setDisable(true);
    taskProgressBar.setProgress(INDETERMINATE_PROGRESS);
    disposable.add(Observable.just(k)
        .subscribeOn(Schedulers.io())
        .map(number -> {
          TotalTimeHelper timeHelper = null;
          if (bubbleRadio.isSelected()) {
            timeHelper = TotalTimeHelper.of("冒泡算法");
            bubble(number);
          } else if (truncateRadio.isSelected()) {
            timeHelper = TotalTimeHelper.of("截断算法");
            truncate(number);
          }
          return timeHelper;
        })
        .observeOn(JavaFxScheduler.platform())
        .subscribe(timeHelper -> {
          if (timeHelper != null) {
            showMessage("查找完毕 >>> " + timeHelper.total());
          }
        }, e -> {
          e.printStackTrace();
          showMessage("查找出错：" + e.getLocalizedMessage());
          taskProgressBar.setProgress(0);
          findBeginButton.setDisable(false);
          findEndButton.setDisable(true);
          cleanDataButton.setDisable(false);
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("查找出错！");
          alert.setHeaderText(e.getLocalizedMessage());
          StringWriter writer = new StringWriter();
          PrintWriter printWriter = new PrintWriter(writer);
          e.printStackTrace(printWriter);
          alert.setContentText(writer.toString());
          alert.show();
        }, () -> {
          taskProgressBar.setProgress(0);
          findBeginButton.setDisable(false);
          findEndButton.setDisable(true);
          cleanDataButton.setDisable(false);
        }));
  }

  public void onFindEndClicked() {
    findEndButton.setDisable(true);
    disposable.dispose();
  }

  private void showMessage(String message) {
    String timestamp = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());
    String content = String.format("[%s] - %s", timestamp, message);
    System.out.println(content);
    consoleTextArea.appendText(content);
    consoleTextArea.appendText(System.lineSeparator());
  }

  private boolean isNotNumber(String value) {
    if (Strings.isNullOrEmpty(value)) {
      return true;
    }
    String numberText = value.trim();
    for (char c : numberText.toCharArray()) {
      if (!CharMatcher.digit().matches(c)) {
        return true;
      }
    }
    return false;
  }

  private void bubble(int k) {
    List<Long> bubbleList = Lists.newArrayList(dataList);
    bubbleSort(bubbleList);
    long maxNumber = bubbleList.get(k);
    showMessage("找到第 " + k + " 个最大数为: " + maxNumber);
  }

  private void bubbleSort(List<Long> sourceList) {
    // 每遍历一次，产生一个最小数放到末尾
    for (int i = 0; i < sourceList.size() - 1; i++) {
      // 两两比较，留出空余；已产生的最小数，不参与比较
      for (int j = 0; j < sourceList.size() - 1 - i; j++) {
        // 当前位置的数值小于后面位置的数值，则进行交换
        if (sourceList.get(j) < sourceList.get(j + 1)) {
          long temp = sourceList.get(j);
          sourceList.set(j, sourceList.get(j + 1));
          sourceList.set(j + 1, temp);
        }
      }
    }
  }

  private void truncate(int k) {
    List<Long> truncateDataList = dataList.stream().limit(k).collect(Collectors.toList());
    bubbleSort(truncateDataList);
    List<Long> remainingList = dataList.stream().skip(k).collect(Collectors.toList());
    for (Long remaining : remainingList) {
      // 冒泡排序后，数据末尾是最小的数，因此如果不能“达标”，就直接放弃
      if (remaining < truncateDataList.get(truncateDataList.size() - 1)) {
        continue;
      }
      int index = -1;
      // 按从大到小遍历，如果比当前数字还大，就记录这个下标，然后终止此次循环
      for (int i = 0; i < truncateDataList.size(); i++) {
        if (remaining > truncateDataList.get(i)) {
          index = i;
          break;
        }
      }
      // 如果存在合适的位置，那就直接插入到此位置，然后所有元素后移一位，并删除最后一个元素
      if (index > -1) {
        truncateDataList.add(index, remaining);
        truncateDataList.remove(truncateDataList.size() - 1);
      }
    }
    showMessage("找到第 " + k + " 个最大数为: " + truncateDataList.get(k - 1));
  }
}
