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

    public HexagonalGrid(byte[][] board) {
        this(board.length);
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (isInBounds(i, j)) {
                    set(i, j, board[i][j]);
                }
            }
        }
    }

    public void set(int q, int s, byte value) {
        board[calculateIndex(q, s)] = value;
    }

    public void set(AxialPosition pos, byte value) {
        set(pos.q(), pos.s(), value);
    }

    public byte get(int q, int s) {
        if (!isInBounds(q, s))
            return 0;
        return board[calculateIndex(q, s)];

    }

    public byte get(AxialPosition pos) {
        return get(pos.q(),pos.s());
    }

    private int columnOffset(int row) {
        return Math.max(0, RADIUS - row);
    }

    private int calculateIndex(int q, int s) {
        s = convertToJaggedPosition(q, s);
        return convertToFlat(q, s);
    }

    private int convertToJaggedPosition(int q, int s) {
        return s - columnOffset(q);
    }

    private int convertToFlat(int q, int s) {
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
        return isInBounds(position.q(), position.s());
    }

    public boolean isInBounds(int q, int s) {
        if (!(q >= 0 && q < SIZE && s >= 0 && s < SIZE))
            return false;
        if (s < columnOffset(q))
            return false;
        if (s >= SIZE - columnOffset(SIZE - 1 - q))
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
