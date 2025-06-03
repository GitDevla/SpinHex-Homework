package spinhex.model;

import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import puzzle.State;
import puzzle.solver.BreadthFirstSearch;

import java.util.*;

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
    /**
     * The hexagonal grid representing the current state of the SpinHex board.
     * It contains the colors of the hexes in the game.
     */
    protected HexagonalGrid board;

    /**
     * The relative positions of the six adjacent hexes in a hexagonal grid.
     * These positions are used to determine the neighbors of a hex and to
     * perform rotations.
     */
    protected static final AxialPosition[] ADJACENT_DIRECTIONS = {
            new AxialPosition(-1, 0), // Up
            new AxialPosition(-1, 1), // Up-Right
            new AxialPosition(0, 1), // Right
            new AxialPosition(1, 0), // Down
            new AxialPosition(1, -1), // Down-Left
            new AxialPosition(0, -1) // Left
    };

    private final HexagonalGrid solvedBoard;

    private static final HashMap<Integer, UnifiedSet<TwoPhaseAction<AxialPosition, Rotation>>> legalMovesMemo = new HashMap<>();

    /**
     * Constructs a new {@code SpinHexModel} with the initial board configuration.
     * The initial configuration is set up with some hexes colored and others
     * empty.
     */
    public SpinHexModel() {
        this(new byte[][] {
                { HexColor.NONE, HexColor.NONE, HexColor.RED, HexColor.RED, HexColor.RED },
                { HexColor.NONE, HexColor.RED, HexColor.RED, HexColor.RED, HexColor.BLUE },
                { HexColor.BLUE, HexColor.BLUE, HexColor.BLUE, HexColor.BLUE, HexColor.BLUE },
                { HexColor.BLUE, HexColor.GREEN, HexColor.GREEN, HexColor.GREEN, HexColor.NONE },
                { HexColor.GREEN, HexColor.GREEN, HexColor.GREEN, HexColor.NONE, HexColor.NONE }
        }, new byte[][] {
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
    public SpinHexModel(byte[][] startingBoard, byte[][] targetBoard) {
        board = new HexagonalGrid(startingBoard);
        solvedBoard = new HexagonalGrid(targetBoard);
    }

    /**
     * Gets the solved configuration of the SpinHex board.
     *
     * @return A deep copy of the HexagonalGrid representing the solved board
     *         configuration.
     */
    public HexagonalGrid getSolution() {
        return solvedBoard.clone();
    }

    /**
     * Gets the current state of the SpinHex board.
     *
     * @return A deep copy of the HexagonalGrid representing the current board
     *         state.
     */
    public HexagonalGrid getBoard() {
        return board.clone();
    }

    private UnifiedSet<TwoPhaseAction<AxialPosition, Rotation>> generateLegalMoves() {
        final var legalMoves = new ArrayList<TwoPhaseAction<AxialPosition, Rotation>>();

        for (int i = 0; i < getBoardSize(); i++) {
            for (int j = 0; j < getBoardSize(); j++) {
                if (isLegalToMoveFrom(new AxialPosition(i, j))) {
                    legalMoves.add(new TwoPhaseAction<>(new AxialPosition(i, j), Rotation.CLOCKWISE));
                    legalMoves.add(new TwoPhaseAction<>(new AxialPosition(i, j), Rotation.COUNTERCLOCKWISE));
                }
            }
        }
        final var legalMovesSet = new UnifiedSet<TwoPhaseAction<AxialPosition, Rotation>>(legalMoves.size(), 1);
        legalMovesSet.addAll(legalMoves);
        return legalMovesSet;
    }

    /**
     * Gets the size of the SpinHex board.
     *
     * @return The size of the board (N for an NxN board).
     */
    public int getBoardSize() {
        return board.getSize();
    }

    /**
     * Gets the color of the hex at the specified position.
     *
     * @param position The position of the hex.
     * @return The color of the hex at the specified position.
     * @throws IllegalArgumentException if the position is out of bounds.
     */
    public Byte getHex(AxialPosition position) {
        if (!board.isInBounds(position)) {
            throw new IllegalArgumentException("Position out of bounds: " + position);
        }
        return board.get(position);
    }

    /**
     * Gets the neighbors of the hex at the specified position.
     *
     * @param position The position of the hex.
     * @return A list of colors of the neighboring hexes.
     */
    public List<Byte> getNeighbors(AxialPosition position) {
        List<Byte> neighbors = new ArrayList<>(6);
        for (var dir : ADJACENT_DIRECTIONS) {
            AxialPosition neighborPos = position.add(dir);
            if (board.isInBounds(neighborPos)) {
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
        return board.isInBounds(from) && getNeighbors(from).stream().noneMatch(color -> color == HexColor.NONE);
    }

    /**
     * Checks if the puzzle is solved.
     * The puzzle is considered solved if all hexes match the solved configuration.
     *
     * @return {@code true} if the puzzle is solved, {@code false} otherwise.
     */
    @Override
    public boolean isSolved() {
        return board.equals(solvedBoard);
    }

    /**
     * Gets the set of legal moves available from the current state.
     *
     * @return A set of legal moves.
     */
    @Override
    public Set<TwoPhaseAction<AxialPosition, Rotation>> getLegalMoves() {
        return legalMovesMemo.computeIfAbsent(getBoardSize(), (_) -> generateLegalMoves()).clone();
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
        switch (moveAction.action()) {
            case CLOCKWISE -> rotateClockwise(moveAction.from());
            case COUNTERCLOCKWISE -> rotateCounterClockwise(moveAction.from());
        }
    }

    private void rotateCounterClockwise(AxialPosition from) {
        Byte temp = getHex(from.add(ADJACENT_DIRECTIONS[0]));

        for (int i = 0; i < ADJACENT_DIRECTIONS.length - 1; i++) {
            AxialPosition current = from.add(ADJACENT_DIRECTIONS[i]);
            AxialPosition next = from.add(ADJACENT_DIRECTIONS[i + 1]);
            board.set(current, getHex(next));
        }

        AxialPosition lastPosition = from.add(ADJACENT_DIRECTIONS[5]);
        board.set(lastPosition, temp);
    }

    private void rotateClockwise(AxialPosition from) {
        Byte temp = getHex(from.add(ADJACENT_DIRECTIONS[5]));

        for (int i = ADJACENT_DIRECTIONS.length - 1; i > 0; i--) {
            AxialPosition current = from.add(ADJACENT_DIRECTIONS[i]);
            AxialPosition previous = from.add(ADJACENT_DIRECTIONS[i - 1]);
            board.set(current, getHex(previous));
        }

        AxialPosition firstPosition = from.add(ADJACENT_DIRECTIONS[0]);
        board.set(firstPosition, temp);
    }

    /**
     * Creates a clone of the current {@code SpinHexModel} state.
     *
     * @return A new {@code SpinHexModel} instance with the same board
     *         configuration.
     */
    @Override
    public State<TwoPhaseAction<AxialPosition, Rotation>> clone() {
        SpinHexModel copy;
        try {
            copy = (SpinHexModel) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
        copy.board = board.clone();
        return copy;
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
        for (var i = 0; i < getBoardSize(); i++) {
            for (var j = 0; j < getBoardSize(); j++) {
                if (!board.isInBounds(new AxialPosition(i, j)))
                    continue;
                sb.append(board.get(i, j)).append('\t');
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
        return this.board.equals(that.board);
    }

    /**
     * Returns a hash code value for this {@code SpinHexModel}.
     * The hash code is computed based on the board configuration.
     *
     * @return A hash code value for this {@code SpinHexModel}.
     */
    @Override
    public int hashCode() {
        return board.hashCode();
    }

    public static void main(String[] args) {
        var smallBoardStart = new byte[][] {
                { HexColor.NONE, HexColor.RED, HexColor.RED },
                { HexColor.RED, HexColor.GREEN, HexColor.RED },
                { HexColor.BLUE, HexColor.RED, HexColor.NONE }
        };
        var smallBoardTarget = new byte[][] {
                { HexColor.NONE, HexColor.BLUE, HexColor.RED },
                { HexColor.RED, HexColor.GREEN, HexColor.RED },
                { HexColor.RED, HexColor.RED, HexColor.NONE }
        };
        SpinHexModel model = new SpinHexModel(smallBoardStart, smallBoardTarget);
        for (int i = 0; i < 4; i++) {
            model.makeMove(new TwoPhaseAction<>(new AxialPosition(1, 1),
                    Rotation.COUNTERCLOCKWISE));
        }
        System.out.println(model);
        System.out.println(model.isSolved());

        smallBoardStart = new byte[][] {
                { HexColor.NONE, HexColor.RED, HexColor.RED },
                { HexColor.RED, HexColor.GREEN, HexColor.RED },
                { HexColor.BLUE, HexColor.RED, HexColor.NONE }
        };
        new BreadthFirstSearch<TwoPhaseAction<AxialPosition, Rotation>>()
                .solveAndPrintSolution(new SpinHexModel(smallBoardStart, smallBoardTarget));
        new BreadthFirstSearch<TwoPhaseAction<AxialPosition, Rotation>>()
                .solveAndPrintSolution(new SpinHexModel());
    }
}
