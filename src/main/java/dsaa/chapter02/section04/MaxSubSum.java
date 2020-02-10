package dsaa.chapter02.section04;

import com.google.common.base.Stopwatch;
import dsaa.internal.javafx.Actuator;
import dsaa.internal.javafx.Applications;
import dsaa.internal.javafx.Console;
import dsaa.internal.javafx.Progress;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import static javafx.scene.control.ProgressIndicator.INDETERMINATE_PROGRESS;

public class MaxSubSum extends Application {
  private static final int[] DATA = {4, -3, 5, -2, -1, 2, 6, -2};

  @FXML Button function1Button;
  @FXML Button function2Button;
  @FXML Button function3Button;
  @FXML Button function4Button;
  @FXML TextArea consoleTextArea;
  @FXML ProgressBar taskProgressBar;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    Applications.start(primaryStage, this);
  }

  @Override public void stop() {
    disposable.clear();
    Schedulers.shutdown();
  }

  private final Actuator actuator = new Actuator();
  private final Console console = new Console();
  private final Progress progress = new Progress();

  private final CompositeDisposable disposable = new CompositeDisposable();

  @FXML void initialize() {
    disposable.add(actuator.bind()
        .doOnNext(running -> function1Button.setDisable(running))
        .doOnNext(running -> function2Button.setDisable(running))
        .doOnNext(running -> function3Button.setDisable(running))
        .doOnNext(running -> function4Button.setDisable(running))
        .subscribe(running -> progress.update(running ? INDETERMINATE_PROGRESS : 0)));
    disposable.add(console.bind().subscribe(s -> consoleTextArea.appendText(s)));
    disposable.add(progress.bind().subscribe(progress -> taskProgressBar.setProgress(progress)));
  }

  @FXML void onFunction1Clicked() {
    actuator.running();
    disposable.add(Observable.just(Stopwatch.createStarted())
        .subscribeOn(Schedulers.computation())
        .flatMap(stopwatch -> Observable.just(DATA)
            .map(this::maxSubSum1)
            .map(integer -> String.format("算法1 用时：%s, 结果：%s", stopwatch.stop(), integer)))
        .observeOn(JavaFxScheduler.platform())
        .doOnComplete(actuator::finished)
        .subscribe(console::log));
  }

  @FXML void onFunction2Clicked() {
    actuator.running();
    disposable.add(Observable.just(Stopwatch.createStarted())
        .subscribeOn(Schedulers.computation())
        .flatMap(stopwatch -> Observable.just(DATA)
            .map(this::maxSubSum2)
            .map(integer -> String.format("算法2 用时：%s, 结果：%s", stopwatch.stop(), integer)))
        .observeOn(JavaFxScheduler.platform())
        .doOnComplete(actuator::finished)
        .subscribe(console::log));
  }

  @FXML void onFunction3Clicked() {
    actuator.running();
    disposable.add(Observable.just(Stopwatch.createStarted())
        .subscribeOn(Schedulers.computation())
        .flatMap(stopwatch -> Observable.just(DATA)
            .map(this::maxSubSum3)
            .map(integer -> String.format("算法3 用时：%s, 结果：%s", stopwatch.stop(), integer)))
        .observeOn(JavaFxScheduler.platform())
        .doOnComplete(actuator::finished)
        .subscribe(console::log));
  }

  @FXML void onFunction4Clicked() {
    actuator.running();
    disposable.add(Observable.just(Stopwatch.createStarted())
        .subscribeOn(Schedulers.computation())
        .flatMap(stopwatch -> Observable.just(DATA)
            .map(this::maxSubSum4)
            .map(integer -> String.format("算法4 用时：%s, 结果：%s", stopwatch.stop(), integer)))
        .observeOn(JavaFxScheduler.platform())
        .doOnComplete(actuator::finished)
        .subscribe(console::log));
  }

  private int maxSubSum1(int[] a) {
    int maxSum = 0;

    for (int i = 0; i < a.length; i++) {
      for (int j = i; j < a.length; j++) {
        int thisSum = 0;

        for (int k = i; k <= j; k++) {
          thisSum += a[k];
        }

        if (thisSum > maxSum) {
          maxSum = thisSum;
        }
      }
    }

    return maxSum;
  }

  private int maxSubSum2(int[] a) {
    int maxSum = 0;
    for (int i = 0; i < a.length; i++) {
      int thisSum = 0;
      for (int j = i; j < a.length; j++) {
        thisSum += a[j];
        if (thisSum > maxSum) {
          maxSum = thisSum;
        }
      }
    }
    return maxSum;
  }

  private int maxSubSum3(int[] a) {
    return maxSumRec(a, 0, a.length - 1);
  }

  private int maxSumRec(int[] a, int left, int right) {
    if (left == right) {
      return Math.max(a[left], 0);
    }

    int center = (left + right) / 2;
    int maxLeftSum = maxSumRec(a, left, center);
    int maxRightSum = maxSumRec(a, center + 1, right);

    int maxLeftBorderSum = 0, leftBorderSum = 0;

    for (int i = center; i >= left; i--) {
      leftBorderSum += a[i];
      if (leftBorderSum > maxLeftBorderSum) {
        maxLeftBorderSum = leftBorderSum;
      }
    }

    int maxRightBorderSum = 0, rightBorderSum = 0;

    for (int i = center + 1; i <= right; i++) {
      rightBorderSum += a[i];
      if (rightBorderSum > maxRightBorderSum) {
        maxRightBorderSum = rightBorderSum;
      }
    }

    return max3(maxLeftSum, maxRightSum, maxLeftBorderSum + maxRightBorderSum);
  }

  private int max3(int first, int second, int third) {
    return Math.max(first, Math.max(second, third));
  }

  private int maxSubSum4(int[] a) {
    int maxSum = 0;
    int thisSum = 0;

    for (int anA : a) {
      thisSum += anA;

      if (thisSum > maxSum) {
        maxSum = thisSum;
      } else if (thisSum < 0) {
        thisSum = 0;
      }
    }

    return maxSum;
  }
}
