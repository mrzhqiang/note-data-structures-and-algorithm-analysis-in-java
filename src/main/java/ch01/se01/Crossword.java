package ch01.se01;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import internal.javafx.*;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static javafx.scene.control.ProgressIndicator.INDETERMINATE_PROGRESS;

@Slf4j
public class Crossword extends Application {

    @FXML
    TableView<Letter> crosswordTable;
    @FXML
    TableColumn<Letter, String> indexColumn;
    @FXML
    TableColumn<Letter, String> oneColumn;
    @FXML
    TableColumn<Letter, String> twoColumn;
    @FXML
    TableColumn<Letter, String> threeColumn;
    @FXML
    TableColumn<Letter, String> fourColumn;
    @FXML
    TextArea consoleTextArea;
    @FXML
    ProgressBar taskProgressBar;
    @FXML
    Button findBeginButton;

    public static void main(String[] args) {
        launch(args);
    }

    private final String[] words = {
            "this", "two", "fat", "that"
    };

    private final ObservableList<Letter> letterList = FXCollections.observableArrayList();
    private final Actuator finder = new Actuator();
    private final Progress progress = new Progress();
    private final Console console = new Console();
    private final CompositeDisposable disposable = new CompositeDisposable();

    @Override
    public void start(Stage primaryStage) {
        Applications.start(primaryStage, ApplicationInfo.builder()
                .title("字谜问题")
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
        letterList.add(Letter.of("1", "t", "h", "i", "s"));
        letterList.add(Letter.of("2", "w", "a", "t", "s"));
        letterList.add(Letter.of("3", "o", "a", "h", "g"));
        letterList.add(Letter.of("4", "f", "g", "d", "t"));

        indexColumn.setCellValueFactory(param -> param.getValue().index);
        oneColumn.setCellValueFactory(param -> param.getValue().one);
        twoColumn.setCellValueFactory(param -> param.getValue().two);
        threeColumn.setCellValueFactory(param -> param.getValue().three);
        fourColumn.setCellValueFactory(param -> param.getValue().four);
        crosswordTable.setItems(letterList);

        disposable.add(progress.bind().subscribe(progress -> taskProgressBar.setProgress(progress)));
        disposable.add(console.bind().subscribe(text -> consoleTextArea.appendText(text)));
        disposable.add(finder.bind()
                .doOnNext(state -> findBeginButton.setDisable(state))
                .doOnNext(state -> progress.update(state ? INDETERMINATE_PROGRESS : 0))
                .map(running -> running ? "正在查找.." : "开始查找")
                .subscribe(text -> findBeginButton.setText(text)));
    }

    @FXML
    void onFindBeginClicked() {
        finder.running();
        disposable.add(Observable.just(Stopwatch.createStarted())
                .subscribeOn(Schedulers.computation())
                .map(stopwatch -> {
                    verify();
                    return stopwatch.stop();
                })
                .map(Stopwatch::toString)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(duration -> {
                    console.log("查找完毕，用时：" + duration);
                    finder.finished();
                }, e -> {
                    e.printStackTrace();
                    Dialogs.error("查找出现错误！", e).show();
                }));
    }

    private void verify() {
        for (int i = 0; i < letterList.size(); i++) {
            for (int j = 0; j < letterList.get(i).size(); j++) {
                for (Direction direction : Direction.values()) {
                    for (String word : words) {
                        if (direction.find(letterList, word, i, j)) {
                            console.log(String.format("在 %s,%s 位置通过 %s 方向找到匹配的 %s 字符", i, j, direction, word));
                        }
                    }
                }
            }
        }
    }

    private static class Letter {

        private final StringProperty index = new SimpleStringProperty("", "#");
        private final StringProperty one = new SimpleStringProperty("", "1");
        private final StringProperty two = new SimpleStringProperty("", "2");
        private final StringProperty three = new SimpleStringProperty("", "3");
        private final StringProperty four = new SimpleStringProperty("", "4");

        static Letter of(String index, String one, String two, String three, String four) {
            Letter letter = new Letter();
            letter.index.setValue(index);
            letter.one.setValue(one);
            letter.two.setValue(two);
            letter.three.setValue(three);
            letter.four.setValue(four);
            return letter;
        }

        String getByIndex(int index) {
            switch (index) {
                case 0:
                    return one.getValue();
                case 1:
                    return two.getValue();
                case 2:
                    return three.getValue();
                case 3:
                    return four.getValue();
                default:
                    return this.index.getValue();
            }
        }

        int size() {
            return 4;
        }
    }

    private enum Direction {
        NORTH {
            @Override
            boolean find(List<Letter> letterList, String word, int x, int y) {
                boolean valid = super.find(letterList, word, x, y);
                boolean match = y + 1 >= word.length();
                if (valid && match) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < word.length(); i++) {
                        builder.append(letterList.get(y - i).getByIndex(x));
                    }
                    return word.equals(builder.toString());
                }
                return false;
            }
        },
        NORTHEAST {
            @Override
            boolean find(List<Letter> letterList, String word, int x, int y) {
                boolean valid = super.find(letterList, word, x, y);
                boolean match = y + 1 >= word.length()
                        && letterList.get(y + 1 - word.length()).size() >= x + word.length();
                if (valid && match) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < word.length(); i++) {
                        builder.append(letterList.get(y - i).getByIndex(x + i));
                    }
                    return word.equals(builder.toString());
                }
                return false;
            }
        },
        EAST {
            @Override
            boolean find(List<Letter> letterList, String word, int x, int y) {
                boolean valid = super.find(letterList, word, x, y);
                boolean match = letterList.get(y).size() >= x + word.length();
                if (valid && match) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < word.length(); i++) {
                        builder.append(letterList.get(y).getByIndex(x + i));
                    }
                    return word.equals(builder.toString());
                }
                return false;
            }
        },
        SOUTHEAST {
            @Override
            boolean find(List<Letter> letterList, String word, int x, int y) {
                boolean valid = super.find(letterList, word, x, y);
                boolean match = letterList.size() >= y + word.length()
                        && letterList.get(y + word.length() - 1).size() >= x + word.length();
                if (valid && match) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < word.length(); i++) {
                        builder.append(letterList.get(y + i).getByIndex(x + i));
                    }
                    return word.equals(builder.toString());
                }
                return false;
            }
        },
        SOUTH {
            @Override
            boolean find(List<Letter> letterList, String word, int x, int y) {
                boolean valid = super.find(letterList, word, x, y);
                boolean match = letterList.size() >= y + word.length();
                if (valid && match) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < word.length(); i++) {
                        builder.append(letterList.get(y + i).getByIndex(x));
                    }
                    return word.equals(builder.toString());
                }
                return false;
            }
        },
        SOUTHWEST {
            @Override
            boolean find(List<Letter> letterList, String word, int x, int y) {
                boolean valid = super.find(letterList, word, x, y);
                boolean match = letterList.size() >= y + word.length()
                        && x + 1 >= word.length();
                if (valid && match) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < word.length(); i++) {
                        builder.append(letterList.get(y + i).getByIndex(x - i));
                    }
                    return word.equals(builder.toString());
                }
                return false;
            }
        },
        WEST {
            @Override
            boolean find(List<Letter> letterList, String word, int x, int y) {
                boolean valid = super.find(letterList, word, x, y);
                boolean match = x + 1 >= word.length();
                if (valid && match) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < word.length(); i++) {
                        builder.append(letterList.get(y).getByIndex(x - i));
                    }
                    return word.equals(builder.toString());
                }
                return false;
            }
        },
        NORTHWEST {
            @Override
            boolean find(List<Letter> letterList, String word, int x, int y) {
                boolean valid = super.find(letterList, word, x, y);
                boolean match = y + 1 >= word.length() && x + 1 >= word.length();
                if (valid && match) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < word.length(); i++) {
                        builder.append(letterList.get(y - i).getByIndex(x - i));
                    }
                    return word.equals(builder.toString());
                }
                return false;
            }
        },
        ;

        boolean find(List<Letter> letterList, String word, int x, int y) {
            // 约定的是 4 x 4 方阵，所以简单检查一下
            return letterList != null && !letterList.isEmpty()
                    && (y >= 0 && y < letterList.size()
                    && (x >= 0 && x < letterList.get(y).size()))
                    && !Strings.isNullOrEmpty(word);
        }
    }
}
