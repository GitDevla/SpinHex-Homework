package jfxutils;

import spinhex.model.TwoPhaseActionState;

public class TwoPhaseActionSelector<T, U> {
    private final TwoPhaseActionState<T, U> state;
    protected TwoPhaseActionSelector.Phase phase;
    private boolean invalidSelection;
    private T from;
    private U to;

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

    public final void select(Object action) {
        switch (this.phase.ordinal()) {
            case 0 -> this.selectFrom((T) action);
            case 1 -> this.selectTo((U) action);
            case 2 -> throw new IllegalStateException();
        }
    }

    protected void selectFrom(T from) {
        if (this.state.isLegalToMoveFrom(from)) {
            this.from = from;
            this.setPhase(TwoPhaseActionSelector.Phase.SELECT_TO);
            this.invalidSelection = false;
        } else {
            this.invalidSelection = true;
        }

    }

    protected void selectTo(U to) {
        if (this.state.isLegalMove(new TwoPhaseActionState.TwoPhaseAction(this.from, to))) {
            this.to = to;
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

    public final U getTo() {
        if (this.phase != TwoPhaseActionSelector.Phase.SELECT_FROM
                && this.phase != TwoPhaseActionSelector.Phase.SELECT_TO) {
            return this.to;
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
            this.state.makeMove(new TwoPhaseActionState.TwoPhaseAction(this.from, this.to));
            this.reset();
        }
    }

    public final void reset() {
        this.from = null;
        this.to = null;
        this.setPhase(TwoPhaseActionSelector.Phase.SELECT_FROM);
        this.invalidSelection = false;
    }

    public static enum Phase {
        SELECT_FROM,
        SELECT_TO,
        READY_TO_MOVE;

        private Phase() {
        }
    }
}
