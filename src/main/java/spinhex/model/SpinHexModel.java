package spinhex.model;

import puzzle.State;

import java.util.*;

public class SpinHexModel implements TwoPhaseActionState<AxialPosition, Rotation> {
    public static final int BOARD_SIZE = 5;
    private final HexColor[][] board;
    private static final AxialPosition[] DIRECTIONS = {
            new AxialPosition(-1, 0), // Up
            new AxialPosition(-1, 1), // Up-Right
            new AxialPosition(0, 1),  // Right
            new AxialPosition(1, 0),  // Down
            new AxialPosition(1, -1), // Down-Left
            new AxialPosition(0, -1)  // Left
    };

    private static final HexColor[][] solvedBoard = {
            {HexColor.NONE, HexColor.NONE, HexColor.GREEN, HexColor.RED, HexColor.GREEN},
            {HexColor.NONE, HexColor.RED, HexColor.BLUE, HexColor.BLUE, HexColor.RED},
            {HexColor.GREEN, HexColor.BLUE, HexColor.BLUE, HexColor.BLUE, HexColor.GREEN},
            {HexColor.RED, HexColor.BLUE, HexColor.BLUE, HexColor.RED, HexColor.NONE},
            {HexColor.GREEN, HexColor.RED, HexColor.GREEN, HexColor.NONE, HexColor.NONE}
    };

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
        return isInBounds(from) && getHex(from) != HexColor.NONE && getNeighbors(from).stream().noneMatch(color -> color == HexColor.NONE);
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
    public Set<TwoPhaseAction<AxialPosition, Rotation>> getLegalMoves() {
        return Collections.unmodifiableSet(legalMoves);
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
    public boolean isLegalMove(TwoPhaseAction<AxialPosition, Rotation> moveAction) {
        return isLegalToMoveFrom(moveAction.from()) && moveAction.action() != null;
    }

    @Override
    public void makeMove(TwoPhaseAction<AxialPosition, Rotation> axialPositionRotationTwoPhaseAction) {
        if (!isLegalMove(axialPositionRotationTwoPhaseAction)) {
            throw new IllegalArgumentException("Illegal move: " + axialPositionRotationTwoPhaseAction);
        }

        switch (axialPositionRotationTwoPhaseAction.action()) {
            case CLOCKWISE -> rotateClockwise(axialPositionRotationTwoPhaseAction.from());
            case COUNTERCLOCKWISE -> rotateCounterClockwise(axialPositionRotationTwoPhaseAction.from());
            default ->
                    throw new IllegalArgumentException("Invalid rotation action: " + axialPositionRotationTwoPhaseAction.action());
        }
    }

    private void rotateCounterClockwise(AxialPosition from) {
        var firstDqs = DIRECTIONS[0];
        var temp = getHex(from.add(firstDqs));
        for (int i = 0; i < 5; i++) {
            var current = from.add(DIRECTIONS[i]);
            var next = from.add(DIRECTIONS[i + 1]);
            board[current.q()][current.s()] = getHex(next);
        }
        var lastDqs = DIRECTIONS[5];
        board[from.add(lastDqs).q()][from.add(lastDqs).s()] = temp;
    }

    private void rotateClockwise(AxialPosition from) {
        var lastDqs = DIRECTIONS[5];
        var temp = getHex(from.add(lastDqs));
        for (int i = 5; i > 0; i--) {
            var current = from.add(DIRECTIONS[i]);
            var previous = from.add(DIRECTIONS[i - 1]);
            board[current.q()][current.s()] = getHex(previous);
        }
        var firstDqs = DIRECTIONS[0];
        board[from.add(firstDqs).q()][from.add(firstDqs).s()] = temp;
    }

    public static void main(String[] args) {
        var model = new SpinHexModel();
        System.out.println(model);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SpinHexModel that = (SpinHexModel) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
