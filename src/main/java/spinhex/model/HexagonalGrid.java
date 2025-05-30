package spinhex.model;

import java.util.Arrays;

public final class HexagonalGrid implements Cloneable {
    private byte[] board;
    private final int SIZE;
    private final int RADIUS;

    public HexagonalGrid(int size) {
        SIZE = size;
        RADIUS = (int) Math.floor((double) SIZE / 2);
        this.board = new byte[size * size - calulateSavedSize(size)];
    }

    public void set(int q, int s, byte value) {
        board[convertToFlat(new AxialPosition(q, s))] = value;
    }

    public void set(AxialPosition pos, byte value) {
        board[convertToFlat(pos)] = value;
    }

    public byte get(int q, int s) {
        return get(new AxialPosition(q, s));
    }

    public byte get(AxialPosition pos) {
        if (!isInBounds(pos))
            return 0;
        return board[convertToFlat(pos)];
    }

    private int columnOffset(int row) {
        return Math.max(0, RADIUS - row);
    }

    private AxialPosition convertToJaggedPosition(AxialPosition position) {
        int q = position.q();
        int s = position.s();

        int newCol = s - columnOffset(q);
        return new AxialPosition(q, newCol);
    }

    private int convertToFlat(AxialPosition position) {
        position = convertToJaggedPosition(position);
        int q = position.q();
        int s = position.s();

        int leftOffset = 0;
        for (int i = 0; i < q; i++) {
            leftOffset += columnOffset(i);
        }
        int rightOffset = 0;
        for (int i = RADIUS; i < q; i++) {
            rightOffset += RADIUS - i;
        }
        return (q * SIZE) + (s - leftOffset + rightOffset);
    }

    private int calulateSavedSize(int size) {
        int savedSize = 0;
        for (int i = 0; i < size; i++) {
            savedSize += Math.abs(RADIUS - i);
        }
        return savedSize;
    }

    public boolean isInBounds(AxialPosition position) {
        if (!(position.q() >= 0 && position.q() < SIZE &&
                position.s() >= 0 && position.s() < SIZE))
            return false;
        if (position.s() < columnOffset(position.q()))
            return false;
        if (position.s() >= SIZE - columnOffset(SIZE - 1 - position.q()))
            return false;
        return true;
    }

    @Override
    public HexagonalGrid clone() {
        try {
            HexagonalGrid cloned = (HexagonalGrid) super.clone();
            cloned.board = this.board.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported", e);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;

        HexagonalGrid that = (HexagonalGrid) o;
        return Arrays.equals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(board);
    }
}
