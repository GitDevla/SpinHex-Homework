package spinhex.model;

import puzzle.State;
import puzzle.solver.BreadthFirstSearch;

import java.util.*;

public class SpinHexModel implements TwoPhaseActionState<AxialPosition, Rotation> {
    public static final int BOARD_SIZE = 5;
    private final HexColor[][] board;
    private static final AxialPosition[] DIRECTIONS = {
            new AxialPosition(-1, 0), // Up
            new AxialPosition(-1, 1), // Up-Right
            new AxialPosition(0, 1), // Right
            new AxialPosition(1, 0), // Down
            new AxialPosition(1, -1), // Down-Left
            new AxialPosition(0, -1) // Left
    };

    private static final HexColor[][] solvedBoard = {
            {HexColor.NONE, HexColor.NONE, HexColor.GREEN, HexColor.RED, HexColor.GREEN},
            {HexColor.NONE, HexColor.RED, HexColor.BLUE, HexColor.BLUE, HexColor.RED},
            {HexColor.GREEN, HexColor.BLUE, HexColor.BLUE, HexColor.BLUE, HexColor.GREEN},
            {HexColor.RED, HexColor.BLUE, HexColor.BLUE, HexColor.RED, HexColor.NONE},
            {HexColor.GREEN, HexColor.RED, HexColor.GREEN, HexColor.NONE, HexColor.NONE}
    };
    // private static final HexColor[][] solvedBoard = {
    // {HexColor.NONE, HexColor.NONE, HexColor.RED, HexColor.RED, HexColor.RED},
    // {HexColor.NONE, HexColor.GREEN, HexColor.BLUE, HexColor.RED, HexColor.RED},
    // {HexColor.BLUE, HexColor.GREEN, HexColor.BLUE, HexColor.BLUE, HexColor.BLUE},
    // {HexColor.BLUE, HexColor.BLUE, HexColor.RED, HexColor.GREEN, HexColor.NONE},
    // {HexColor.GREEN, HexColor.GREEN, HexColor.GREEN, HexColor.NONE,
    // HexColor.NONE}
    // };

    private final HashSet<TwoPhaseAction<AxialPosition, Rotation>> legalMoves;

    public SpinHexModel() {
        board = new HexColor[][]{
                {HexColor.NONE, HexColor.NONE, HexColor.RED, HexColor.RED, HexColor.RED},
                {HexColor.NONE, HexColor.RED, HexColor.RED, HexColor.RED, HexColor.BLUE},
                {HexColor.BLUE, HexColor.BLUE, HexColor.BLUE, HexColor.BLUE, HexColor.BLUE},
                {HexColor.BLUE, HexColor.GREEN, HexColor.GREEN, HexColor.GREEN, HexColor.NONE},
                {HexColor.GREEN, HexColor.GREEN, HexColor.GREEN, HexColor.NONE, HexColor.NONE}
        };
        legalMoves = new HashSet<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (isLegalToMoveFrom(new AxialPosition(i, j))) {
                    legalMoves.add(new TwoPhaseAction<>(new AxialPosition(i, j), Rotation.CLOCKWISE));
                    legalMoves.add(new TwoPhaseAction<>(new AxialPosition(i, j), Rotation.COUNTERCLOCKWISE));
                }
            }
        }
    }

    public boolean isInBounds(AxialPosition position) {
        return position.q() >= 0 && position.q() < BOARD_SIZE &&
                position.s() >= 0 && position.s() < BOARD_SIZE;
    }

    public HexColor getHex(AxialPosition position) {
        if (!isInBounds(position)) {
            throw new IllegalArgumentException("Position out of bounds: " + position);
        }
        return board[position.q()][position.s()];
    }

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

    @Override
    public boolean isLegalToMoveFrom(AxialPosition from) {
        return isInBounds(from) && getHex(from) != HexColor.NONE
                && getNeighbors(from).stream().noneMatch(color -> color == HexColor.NONE);
    }

    @Override
    public boolean isSolved() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] != solvedBoard[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public HashSet<TwoPhaseAction<AxialPosition, Rotation>> getLegalMoves() {
        return new HashSet<>(legalMoves);
    }

    @Override
    public boolean isLegalMove(TwoPhaseAction<AxialPosition, Rotation> moveAction) {
        return isLegalToMoveFrom(moveAction.from()) && moveAction.action() != null;
    }

    @Override
    public void makeMove(TwoPhaseAction<AxialPosition, Rotation> moveAction) {
        if (!isLegalMove(moveAction)) {
            throw new IllegalArgumentException("Illegal move: " + moveAction);
        }

        switch (moveAction.action()) {
            case CLOCKWISE -> rotateClockwise(moveAction.from());
            case COUNTERCLOCKWISE -> rotateCounterClockwise(moveAction.from());
            default -> throw new IllegalArgumentException("Invalid rotation action: " + moveAction.action());
        }
    }

    private void rotateCounterClockwise(AxialPosition from) {
        HexColor temp = getHex(from.add(DIRECTIONS[0]));

        for (int i = 0; i < 5; i++) {
            AxialPosition current = from.add(DIRECTIONS[i]);
            AxialPosition next = from.add(DIRECTIONS[i + 1]);
            board[current.q()][current.s()] = getHex(next);
        }

        AxialPosition lastPosition = from.add(DIRECTIONS[5]);
        board[lastPosition.q()][lastPosition.s()] = temp;
    }

    private void rotateClockwise(AxialPosition from) {
        HexColor temp = getHex(from.add(DIRECTIONS[5]));

        for (int i = 5; i > 0; i--) {
            AxialPosition current = from.add(DIRECTIONS[i]);
            AxialPosition previous = from.add(DIRECTIONS[i - 1]);
            board[current.q()][current.s()] = getHex(previous);
        }

        AxialPosition firstPosition = from.add(DIRECTIONS[0]);
        board[firstPosition.q()][firstPosition.s()] = temp;
    }

    @Override
    public State<TwoPhaseAction<AxialPosition, Rotation>> clone() {
        SpinHexModel copy = new SpinHexModel();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                copy.board[i][j] = this.board[i][j];
            }
        }
        return copy;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        for (var i = 0; i < BOARD_SIZE; i++) {
            for (var j = 0; j < BOARD_SIZE; j++) {
                sb.append(board[i][j].ordinal()).append('\t');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        SpinHexModel that = (SpinHexModel) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    public static void main(String[] args) {
        SpinHexModel model = new SpinHexModel();
        model.makeMove(new TwoPhaseAction<>(new AxialPosition(2, 2), Rotation.CLOCKWISE));
        model.makeMove(new TwoPhaseAction<>(new AxialPosition(3, 2), Rotation.CLOCKWISE));
        model.makeMove(new TwoPhaseAction<>(new AxialPosition(1, 2), Rotation.CLOCKWISE));
        model.makeMove(new TwoPhaseAction<>(new AxialPosition(2, 3), Rotation.CLOCKWISE));
        model.makeMove(new TwoPhaseAction<>(new AxialPosition(3, 2), Rotation.COUNTERCLOCKWISE));
        System.out.println(model);
        System.out.println(model.isSolved());

        new BreadthFirstSearch<TwoPhaseAction<AxialPosition, Rotation>>()
                .solveAndPrintSolution(new SpinHexModel());
    }
}
