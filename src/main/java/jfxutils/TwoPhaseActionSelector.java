package jfxutils;

import spinhex.model.TwoPhaseActionState;

public class TwoPhaseActionSelector<T, U> {
    private final TwoPhaseActionState<T, U> state;
    protected TwoPhaseActionSelector.Phase phase;
    private boolean invalidSelection;
    private T from;
    private U action;

    public TwoPhaseActionSelector(TwoPhaseActionState<T, U> state) {
        this.state = state;
        this.phase = TwoPhaseActionSelector.Phase.SELECT_FROM;
        this.invalidSelection = false;
    }

    public final TwoPhaseActionSelector.Phase getPhase() {
        return this.phase;
    }

    protected void setPhase(TwoPhaseActionSelector.Phase phase) {
        this.phase = phase;
    }

    public final boolean isReadyToMove() {
        return this.phase == TwoPhaseActionSelector.Phase.READY_TO_MOVE;
    }

    public void selectFrom(T from) {
        if (this.state.isLegalToMoveFrom(from)) {
            this.from = from;
            this.setPhase(TwoPhaseActionSelector.Phase.SELECT_ACTION);
            this.invalidSelection = false;
        } else {
            this.invalidSelection = true;
        }

    }

    public void selectAction(U action) {
        if (this.state.isLegalMove(new TwoPhaseActionState.TwoPhaseAction(this.from, action))) {
            this.action = action;
            this.setPhase(TwoPhaseActionSelector.Phase.READY_TO_MOVE);
            this.invalidSelection = false;
        } else {
            this.invalidSelection = true;
        }

    }

    public final T getFrom() {
        if (this.phase == TwoPhaseActionSelector.Phase.SELECT_FROM) {
            throw new IllegalStateException();
        } else {
            return this.from;
        }
    }

    public final U getAction() {
        if (this.phase != TwoPhaseActionSelector.Phase.SELECT_FROM
                && this.phase != TwoPhaseActionSelector.Phase.SELECT_ACTION) {
            return this.action;
        } else {
            throw new IllegalStateException();
        }
    }

    public final boolean isInvalidSelection() {
        return this.invalidSelection;
    }

    public final void makeMove() {
        if (this.phase != TwoPhaseActionSelector.Phase.READY_TO_MOVE) {
            throw new IllegalStateException();
        } else {
            this.state.makeMove(new TwoPhaseActionState.TwoPhaseAction(this.from, this.action));
            this.reset();
        }
    }

    public final void reset() {
        this.from = null;
        this.action = null;
        this.setPhase(TwoPhaseActionSelector.Phase.SELECT_FROM);
        this.invalidSelection = false;
    }

    public static enum Phase {
        SELECT_FROM,
        SELECT_ACTION,
        READY_TO_MOVE;

        private Phase() {
        }
    }
}
