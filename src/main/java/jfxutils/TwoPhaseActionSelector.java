package jfxutils;

import spinhex.model.TwoPhaseActionState;

/**
 * Utility class to determine the next action in two-phase action selection
 * puzzles.
 * It manages the state transitions between selecting a source position and
 * selecting an action to apply at that position.
 *
 * @param <T> the type representing the source position (from)
 * @param <U> the type representing the action to apply
 */
public class TwoPhaseActionSelector<T, U> {

    /**
     * Represents the current phase of the action selection process.
     */
    public static enum Phase {
        /**
         * Phase where the user selects the source position.
         */
        SELECT_FROM,

        /**
         * Phase where the user selects which action to apply at the selected position.
         */
        SELECT_ACTION,

        /**
         * Phase where both source position and action have been selected and the move
         * is ready to be executed.
         */
        READY_TO_MOVE
    }

    private final TwoPhaseActionState<T, U> state;
    /**
     * The current phase of the action selection process.
     */
    protected TwoPhaseActionSelector.Phase phase;
    private boolean invalidSelection;
    private T from;
    private U action;

    /**
     * Creates a {@code TwoPhaseActionSelector} to manage the action selection
     * process for the given state.
     *
     * @param state the state on which actions will be performed
     */
    public TwoPhaseActionSelector(TwoPhaseActionState<T, U> state) {
        this.state = state;
        this.phase = TwoPhaseActionSelector.Phase.SELECT_FROM;
        this.invalidSelection = false;
    }

    /**
     * Returns the current selection phase.
     * 
     * @return the current selection phase
     */
    public final TwoPhaseActionSelector.Phase getPhase() {
        return this.phase;
    }

    /**
     * Sets the current selection phase. The method is provided to be overridden
     * by subclasses.
     *
     * @param phase the current selection phase
     */
    protected void setPhase(TwoPhaseActionSelector.Phase phase) {
        this.phase = phase;
    }

    /**
     * Returns whether the move is ready to be made.
     * 
     * @return {@code true} if the selection is in the READY_TO_MOVE phase,
     *         {@code false} otherwise.
     */
    public final boolean isReadyToMove() {
        return this.phase == TwoPhaseActionSelector.Phase.READY_TO_MOVE;
    }

    /**
     * Selects a source position from which an action will be performed.
     * If the position is valid for making a move, the selection process advances
     * to the action selection phase.
     * 
     * @param from the position to select as the source
     */
    public void selectFrom(T from) {
        if (this.state.isLegalToMoveFrom(from)) {
            this.from = from;
            this.setPhase(TwoPhaseActionSelector.Phase.SELECT_ACTION);
            this.invalidSelection = false;
        } else {
            this.invalidSelection = true;
        }
    }

    /**
     * Selects an action to be performed at the previously selected source position.
     * If the action is valid for the selected source, the selection process
     * advances to the ready-to-move phase.
     * 
     * @param action the action to perform at the selected source
     */
    public void selectAction(U action) {
        if (this.state.isLegalMove(new TwoPhaseActionState.TwoPhaseAction<>(this.from, action))) {
            this.action = action;
            this.setPhase(TwoPhaseActionSelector.Phase.READY_TO_MOVE);
            this.invalidSelection = false;
        } else {
            this.invalidSelection = true;
        }
    }

    /**
     * Returns the source position selected.
     * 
     * @return the source position selected
     * @throws IllegalStateException if the selection is still in the
     *                               SELECT_FROM phase, meaning no source has been
     *                               selected yet.
     */
    public final T getFrom() {
        if (this.phase == TwoPhaseActionSelector.Phase.SELECT_FROM) {
            throw new IllegalStateException();
        } else {
            return this.from;
        }
    }

    /**
     * Returns the action selected.
     * 
     * @return the action selected
     * @throws IllegalStateException if the selection is not in the
     *                               READY_TO_MOVE phase.
     */
    public final U getAction() {
        if (this.phase != TwoPhaseActionSelector.Phase.SELECT_FROM
                && this.phase != TwoPhaseActionSelector.Phase.SELECT_ACTION) {
            return this.action;
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Returns whether the last selection (i.e, for the source or the target
     * respectively) was invalid.
     * 
     * @return {@code true} if the last selection was invalid, {@code false}
     *         otherwise.
     */
    public final boolean isInvalidSelection() {
        return this.invalidSelection;
    }

    /**
     * Makes the move selected. If the move is not yet ready to be made, then an
     * {@link IllegalStateException} is thrown.
     */
    public final void makeMove() {
        if (this.phase != TwoPhaseActionSelector.Phase.READY_TO_MOVE) {
            throw new IllegalStateException();
        } else {
            this.state.makeMove(new TwoPhaseActionState.TwoPhaseAction<>(this.from, this.action));
            this.reset();
        }
    }

    /**
     * Resets the selection state, clearing both the source position and action
     * selections.
     */
    public final void reset() {
        this.from = null;
        this.action = null;
        this.setPhase(TwoPhaseActionSelector.Phase.SELECT_FROM);
        this.invalidSelection = false;
    }

}
