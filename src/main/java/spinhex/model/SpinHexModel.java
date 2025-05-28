package spinhex.model;

import puzzle.State;
import puzzle.solver.BreadthFirstSearch;

import java.util.*;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * A model for the SpinHex puzzle game, which implements the
 * {@code TwoPhaseActionState} interface.
 * The game is played on a hexagonal grid where each cell contains a colored
 * hex.
 * The objective is to rotate a group of hexes to match the solved
 * configuration.
 * 
 * <p>
 * The puzzle has the following key features:
 * <p>
 * <ul>
 * <li>A NxN board with colored hexagonal cells</li>
 * <li>Moves involve selecting a hex and rotating its neighbors either clockwise
 * or counterclockwise</li>
 * <li>A hex can only be selected if it is colored and all of its neighbors are
 * non-empty</li>
 * </ul>
 * 
 * 
 * The model keeps track of the current state of the board and provides methods
 * to query and manipulate the state according to the game rules.
 */
public class SpinHexModel implements TwoPhaseActionState<AxialPosition, Rotation> {
    /*
     * The size of the SpinHex board.
     */
    public final int BOARD_SIZE;

    private final ReadOnlyObjectWrapper<HexColor>[][] board;

    private final ReadOnlyIntegerWrapper steps = new ReadOnlyIntegerWrapper(0);

    private static final AxialPosition[] DIRECTIONS = {
            new AxialPosition(-1, 0), // Up
            new AxialPosition(-1, 1), // Up-Right
            new AxialPosition(0, 1), // Right
            new AxialPosition(1, 0), // Down
            new AxialPosition(1, -1), // Down-Left
            new AxialPosition(0, -1) // Left
    };

    private final HexColor[][] solvedBoard;

    private final HashSet<TwoPhaseAction<AxialPosition, Rotation>> legalMoves;

    /**
     * Constructs a new {@code SpinHexModel} with the initial board configuration.
     * The initial configuration is set up with some hexes colored and others
     * empty.
     */
    public SpinHexModel() {
        this(new HexColor[][] {
                { HexColor.NONE, HexColor.NONE, HexColor.RED, HexColor.RED, HexColor.RED },
                { HexColor.NONE, HexColor.RED, HexColor.RED, HexColor.RED, HexColor.BLUE },
                { HexColor.BLUE, HexColor.BLUE, HexColor.BLUE, HexColor.BLUE, HexColor.BLUE },
                { HexColor.BLUE, HexColor.GREEN, HexColor.GREEN, HexColor.GREEN, HexColor.NONE },
                { HexColor.GREEN, HexColor.GREEN, HexColor.GREEN, HexColor.NONE, HexColor.NONE }
        }, new HexColor[][] {
                { HexColor.NONE, HexColor.NONE, HexColor.GREEN, HexColor.RED, HexColor.GREEN },
                { HexColor.NONE, HexColor.RED, HexColor.BLUE, HexColor.BLUE, HexColor.RED },
                { HexColor.GREEN, HexColor.BLUE, HexColor.BLUE, HexColor.BLUE, HexColor.GREEN },
                { HexColor.RED, HexColor.BLUE, HexColor.BLUE, HexColor.RED, HexColor.NONE },
                { HexColor.GREEN, HexColor.RED, HexColor.GREEN, HexColor.NONE, HexColor.NONE }
        });
    }

    /**
     * Constructs a new {@code SpinHexModel} with a specified starting board and
     * target board.
     *
     * @param startingBoard The initial configuration of the board.
     * @param targetBoard   The solved configuration of the board.
     */
    public SpinHexModel(HexColor[][] startingBoard, HexColor[][] targetBoard) {
        BOARD_SIZE = startingBoard.length;
        board = new ReadOnlyObjectWrapper[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = new ReadOnlyObjectWrapper<>(startingBoard[i][j]);
            }
        }
        this.solvedBoard = targetBoard;
        legalMoves = generateLegalMoves();
    }

    /**
     * Gets the solved configuration of the SpinHex board.
     * 
     * @return A 2D array representing the solved configuration of the board.
     */
    public HexColor[][] getSolution() {
        return solvedBoard;
    }

    private HashSet<TwoPhaseAction<AxialPosition, Rotation>> generateLegalMoves() {
        final HashSet<TwoPhaseAction<AxialPosition, Rotation>> legalMoves;
        legalMoves = new HashSet<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (isLegalToMoveFrom(new AxialPosition(i, j))) {
                    legalMoves.add(new TwoPhaseAction<>(new AxialPosition(i, j), Rotation.CLOCKWISE));
                    legalMoves.add(new TwoPhaseAction<>(new AxialPosition(i, j), Rotation.COUNTERCLOCKWISE));
                }
            }
        }
        return legalMoves;
    }

    /**
     * Checks if the given position is within the bounds of the SpinHex board.
     * 
     * @param position The position to check.
     * @return {@code true} if the position is within bounds, {@code false}
     *         otherwise.
     */
    public boolean isInBounds(AxialPosition position) {
        return position.q() >= 0 && position.q() < BOARD_SIZE &&
                position.s() >= 0 && position.s() < BOARD_SIZE;
    }

    /**
     * Gets the color of the hex at the specified position.
     * 
     * @param position The position of the hex.
     * @return The color of the hex at the specified position.
     * @throws IllegalArgumentException if the position is out of bounds.
     */
    public HexColor getHex(AxialPosition position) {
        if (!isInBounds(position)) {
            throw new IllegalArgumentException("Position out of bounds: " + position);
        }
        return board[position.q()][position.s()].getValue();
    }

    /**
     * Gets a read-only property representing the color of the hex at the specified
     * position.
     * 
     * @param q The axial coordinate q of the hex.
     * @param s The axial coordinate s of the hex.
     * @return A read-only property containing the color of the hex.
     * @throws IllegalArgumentException if the position is out of bounds.
     */
    public ReadOnlyObjectProperty<HexColor> getHexProperty(int q, int s) {
        if (!isInBounds(new AxialPosition(q, s))) {
            throw new IllegalArgumentException("Position out of bounds: " + new AxialPosition(q, s));
        }
        return board[q][s];
    }

    /**
     * Gets the number of steps taken in the game.
     * 
     * @return The number of steps taken.
     */
    public int getSteps() {
        return steps.get();
    }

    /**
     * Gets a read-only property representing the number of steps taken.
     * 
     * @return A read-only integer property containing the number of steps.
     */
    public ReadOnlyIntegerWrapper getStepsProperty() {
        return steps;
    }

    /**
     * Gets the neighbors of the hex at the specified position.
     * 
     * @param position The position of the hex.
     * @return A list of colors of the neighboring hexes.
     */
    public List<HexColor> getNeighbors(AxialPosition position) {
        List<HexColor> neighbors = new ArrayList<>();
        for (var dir : DIRECTIONS) {
            AxialPosition neighborPos = position.add(dir);
            if (isInBounds(neighborPos)) {
                neighbors.add(getHex(neighborPos));
            } else {
                neighbors.add(HexColor.NONE);
            }
        }
        return neighbors;
    }

    /**
     * Checks if it is legal to move from the specified position.
     * A move is legal if the position is within bounds, the hex at that position
     * is not empty, and all its neighbors are non-empty.
     * 
     * @param from The position to check.
     * @return {@code true} if it is legal to move from the specified position,
     *         {@code false} otherwise.
     */
    @Override
    public boolean isLegalToMoveFrom(AxialPosition from) {
        return isInBounds(from) && getHex(from) != HexColor.NONE
                && getNeighbors(from).stream().noneMatch(color -> color == HexColor.NONE);
    }

    /**
     * Checks if the puzzle is solved.
     * The puzzle is considered solved if all hexes match the solved configuration.
     * 
     * @return {@code true} if the puzzle is solved, {@code false} otherwise.
     */
    @Override
    public boolean isSolved() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j].getValue() != solvedBoard[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Gets the set of legal moves available from the current state.
     * 
     * @return A set of legal moves.
     */
    @Override
    public HashSet<TwoPhaseAction<AxialPosition, Rotation>> getLegalMoves() {
        return new HashSet<>(legalMoves);
    }

    /**
     * Checks if the specified move action is legal.
     * A move action is legal if it is possible to move from the specified position
     * and the action is not null.
     * 
     * @param moveAction The move action to check.
     * @return {@code true} if the move action is legal, {@code false} otherwise.
     */
    @Override
    public boolean isLegalMove(TwoPhaseAction<AxialPosition, Rotation> moveAction) {
        return isLegalToMoveFrom(moveAction.from()) && moveAction.action() != null;
    }

    /**
     * Makes a move based on the specified action.
     * The action must be legal, otherwise an exception is thrown.
     * This method performs the rotation of the hexes around the specified position
     * in the direction specified by the action.
     * 
     * @param moveAction The move action to perform.
     * @throws IllegalArgumentException if the move action is illegal.
     */
    @Override
    public void makeMove(TwoPhaseAction<AxialPosition, Rotation> moveAction) {
        if (!isLegalMove(moveAction)) {
            throw new IllegalArgumentException("Illegal move: " + moveAction);
        }

        switch (moveAction.action()) {
            case CLOCKWISE -> rotateClockwise(moveAction.from());
            case COUNTERCLOCKWISE -> rotateCounterClockwise(moveAction.from());
        }
        steps.set(steps.get() + 1);
    }

    private void rotateCounterClockwise(AxialPosition from) {
        HexColor temp = getHex(from.add(DIRECTIONS[0]));

        for (int i = 0; i < 5; i++) {
            AxialPosition current = from.add(DIRECTIONS[i]);
            AxialPosition next = from.add(DIRECTIONS[i + 1]);
            board[current.q()][current.s()].set(getHex(next));
        }

        AxialPosition lastPosition = from.add(DIRECTIONS[5]);
        board[lastPosition.q()][lastPosition.s()].set(temp);
    }

    private void rotateClockwise(AxialPosition from) {
        HexColor temp = getHex(from.add(DIRECTIONS[5]));

        for (int i = 5; i > 0; i--) {
            AxialPosition current = from.add(DIRECTIONS[i]);
            AxialPosition previous = from.add(DIRECTIONS[i - 1]);
            board[current.q()][current.s()].set(getHex(previous));
        }

        AxialPosition firstPosition = from.add(DIRECTIONS[0]);
        board[firstPosition.q()][firstPosition.s()].set(temp);
    }

    /**
     * Creates a clone of the current {@code SpinHexModel} state.
     * 
     * @return A new {@code SpinHexModel} instance with the same board
     *         configuration.
     */
    @Override
    public State<TwoPhaseAction<AxialPosition, Rotation>> clone() {
        HexColor[][] board = new HexColor[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = this.board[i][j].getValue();
            }
        }

        return new SpinHexModel(board, solvedBoard);
    }

    /**
     * Returns a string representation of the {@code SpinHexModel} board.
     * Each hex is represented by its ordinal value, separated by tabs.
     * 
     * @return A string representation of the board.
     */
    @Override
    public String toString() {
        var sb = new StringBuilder();
        for (var i = 0; i < BOARD_SIZE; i++) {
            for (var j = 0; j < BOARD_SIZE; j++) {
                sb.append(board[i][j].getValue().ordinal()).append('\t');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Checks if this {@code SpinHexModel} is equal to another object.
     * Two {@code SpinHexModels} are considered equal if their boards have the same
     * configuration.
     * 
     * @param o The object to compare with.
     * @return {@code true} if the objects are equal, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SpinHexModel that = (SpinHexModel) o;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (this.board[i][j].getValue() != that.board[i][j].getValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns a hash code value for this {@code SpinHexModel}.
     * The hash code is computed based on the board configuration.
     * 
     * @return A hash code value for this {@code SpinHexModel}.
     */
    @Override
    public int hashCode() {
int result = 1;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                result = 31 * result + board[i][j].getValue().ordinal();
            }
        }
        return result;
    }

    public static void main(String[] args) {
        var smallBoardStart = new HexColor[][] {
                { HexColor.NONE, HexColor.RED, HexColor.RED },
                { HexColor.RED, HexColor.GREEN, HexColor.RED },
                { HexColor.BLUE, HexColor.RED, HexColor.NONE }
        };
        var smallBoardTarget = new HexColor[][] {
                { HexColor.NONE, HexColor.BLUE, HexColor.RED },
                { HexColor.RED, HexColor.GREEN, HexColor.RED },
                { HexColor.RED, HexColor.RED, HexColor.NONE }
        };
        SpinHexModel model = new SpinHexModel(smallBoardStart, smallBoardTarget);
        for (int i = 0; i < 4; i++) {
            model.makeMove(new TwoPhaseAction<>(new AxialPosition(1, 1), Rotation.COUNTERCLOCKWISE));
        }
        System.out.println(model);
        System.out.println(model.isSolved());

        smallBoardStart = new HexColor[][] {
                { HexColor.NONE, HexColor.RED, HexColor.RED },
                { HexColor.RED, HexColor.GREEN, HexColor.RED },
                { HexColor.BLUE, HexColor.RED, HexColor.NONE }
        };
        new BreadthFirstSearch<TwoPhaseAction<AxialPosition, Rotation>>()
                .solveAndPrintSolution(new SpinHexModel(smallBoardStart, smallBoardTarget));
    }
}
