package spinhex.model;

import puzzle.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SpinHexModel implements TwoPhaseActionState<AxialPosition, Rotation> {
    public static final int BOARD_SIZE = 5;
    private final HexColor[][] board;

    public SpinHexModel() {
        board = new HexColor[][]{
                {HexColor.NONE, HexColor.NONE, HexColor.RED, HexColor.RED, HexColor.RED},
                {HexColor.NONE, HexColor.RED, HexColor.RED, HexColor.RED, HexColor.BLUE},
                {HexColor.BLUE, HexColor.BLUE, HexColor.BLUE, HexColor.BLUE, HexColor.BLUE},
                {HexColor.BLUE, HexColor.GREEN, HexColor.GREEN, HexColor.GREEN, HexColor.NONE},
                {HexColor.GREEN, HexColor.GREEN, HexColor.GREEN, HexColor.NONE, HexColor.NONE}
        };
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
        var q = position.q();
        var s = position.s();
        var directions = new AxialPosition[]{
                new AxialPosition(q-1, s), 
                new AxialPosition(q-1, s+1), 
                new AxialPosition(q, s+1),
                new AxialPosition(q+1, s),
                new AxialPosition(q+1, s-1), 
                new AxialPosition(q, s-1)
        };
        for (var dir : directions) {
            if (isInBounds(dir)) {
                neighbors.add(getHex(dir));
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
        return false;
    }

    @Override
    public Set<TwoPhaseAction<AxialPosition, Rotation>> getLegalMoves() {
        return Set.of();
    }

    @Override
    public State<TwoPhaseAction<AxialPosition, Rotation>> clone() {
        return null;
    }

    @Override
    public boolean isLegalMove(TwoPhaseAction<AxialPosition, Rotation> moveAction) {
        return isLegalToMoveFrom(moveAction.from()) && moveAction.action() != null;
    }

    @Override
    public void makeMove(TwoPhaseAction<AxialPosition, Rotation> axialPositionRotationTwoPhaseAction) {

    }

    public static void main(String[] args) {
        var model = new SpinHexModel();
        System.out.println(model);
    }
}
