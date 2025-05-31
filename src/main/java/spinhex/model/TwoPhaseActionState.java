package spinhex.model;

import puzzle.State;

/**
 * Represents a state in a two-phase action system where actions are performed
 * in two distinct phases: first selecting a source element, then specifying
 * the action to perform from that source.
 *
 * <p>
 * This interface extends the State interface with a specialized action type
 * that encapsulates both the source element and the action to be performed.
 * </p>
 *
 * @param <T> the type of the source element (from where an action originates)
 * @param <U> the type of the action to be performed
 * 
 * @see State
 */
public interface TwoPhaseActionState<T, U> extends State<TwoPhaseActionState.TwoPhaseAction<T, U>> {

    /**
     * Determines whether it is legal to initiate an action from the specified
     * source.
     *
     * @param from the source element to check
     * @return {@code true} if an action can be initiated from the specified source,
     *         {@code false} otherwise
     */
    boolean isLegalToMoveFrom(T from);

    /**
     * Represents a two-phase action consisting of a source element and the action
     * to be performed.
     *
     * @param <T>    the type of the source element
     * @param <U>    the type of the action
     * @param from   the source element from which the action originates
     * @param action the action to be performed from the source
     */
    record TwoPhaseAction<T, U>(T from, U action) {
    }
}