package internal.javafx;

import io.reactivex.Observable;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Actuator {

    private final BooleanProperty state = new SimpleBooleanProperty(false);

    public Observable<Boolean> bind() {
        return JavaFxObservable.valuesOf(state).observeOn(JavaFxScheduler.platform());
    }

    public void running() {
        state.setValue(true);
    }

    public void finished() {
        state.setValue(false);
    }

}
