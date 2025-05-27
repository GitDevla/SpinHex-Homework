package spinhex.model;

import puzzle.State;

public interface TwoPhaseActionState<T,U> extends State<TwoPhaseActionState.TwoPhaseAction<T,U>> {

    boolean isLegalToMoveFrom(T from);

    record TwoPhaseAction<T,U>(T from, U action) {}

}