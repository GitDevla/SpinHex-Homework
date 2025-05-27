package jfxutils;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import spinhex.model.TwoPhaseActionState;

public class JFXTwoPhaseActionSelector<T,U> extends TwoPhaseActionSelector<T,U> {
    private final ReadOnlyObjectWrapper<Phase> phaseProperty;

    public JFXTwoPhaseActionSelector(TwoPhaseActionState<T,U> state) {
        super(state);
        this.phaseProperty = new ReadOnlyObjectWrapper(this.phase);
    }

    public ReadOnlyObjectProperty<Phase> phaseProperty() {
        return this.phaseProperty.getReadOnlyProperty();
    }

    protected void setPhase(TwoPhaseActionSelector.Phase phase) {
        super.setPhase(phase);
        this.phaseProperty.set(phase);
    }
}
