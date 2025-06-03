package jfxutils;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import spinhex.model.TwoPhaseActionState;

/**
 * A subclass of {@link TwoPhaseActionSelector} that provides a
 * property to observe the selection phase.
 *
 * @param <T> represents the moves that can be selected
 * @param <U> represents the actions that can be applied to the moves
 */
public class JFXTwoPhaseActionSelector<T, U> extends TwoPhaseActionSelector<T, U> {
    private final ReadOnlyObjectWrapper<Phase> phaseProperty;

    /**
     * Creates a {@code JFXTwoPhaseActionSelector} object to determine the next
     * move in the state specified.
     * 
     * @param state the state in which the next move is to be made
     */
    public JFXTwoPhaseActionSelector(TwoPhaseActionState<T, U> state) {
        super(state);
        this.phaseProperty = new ReadOnlyObjectWrapper(this.phase);
    }

    /**
     * Represents the current selection phase.
     */
    public ReadOnlyObjectProperty<Phase> phaseProperty() {
        return this.phaseProperty.getReadOnlyProperty();
    }

    protected void setPhase(TwoPhaseActionSelector.Phase phase) {
        super.setPhase(phase);
        this.phaseProperty.set(phase);
    }
}
