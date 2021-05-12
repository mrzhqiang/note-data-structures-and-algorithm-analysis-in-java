package internal.javafx;

import com.google.common.base.Preconditions;
import io.reactivex.Observable;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Progress {

    private final DoubleProperty progress = new SimpleDoubleProperty(0);

    public Observable<Double> bind() {
        return JavaFxObservable.valuesOf(progress)
                .map(Number::doubleValue)
                .map(min -> Math.min(1, min))
                .map(max -> Math.max(-1, max));
    }

    public void update(double value) {
        progress.setValue(value);
    }

    public void compute(double current, double target) {
        Preconditions.checkArgument(current >= 0, "current: %s >= 0");
        Preconditions.checkArgument(target > 0, "target: %s > 0");
        progress.setValue(current / target);
    }

    public void compute(int start, int count) {
        Preconditions.checkArgument(start >= 0, "start: %s >= 0", start);
        Preconditions.checkArgument(count > 0, "count: %s > 0", count);
        progress.setValue(start * 1.0 / (start + count));
    }

}
