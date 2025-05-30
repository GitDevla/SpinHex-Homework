package spinhex.model;

import java.util.Arrays;

/**
 * Represents a hexagonal grid for the {@code SpinHex} puzzle game.
 * <p>
 * This class provides an efficient implementation of a hexagonal grid using a
 * flat array for internal storage.
 * It handles the mapping between axial coordinates (q,s) and
 * array indices, boundary checking, and common grid operations.
 * </p>
 */
public final class HexagonalGrid implements Cloneable {
    private byte[] board;
    private final int SIZE;
    private final int RADIUS;

    /**
     * Constructs a new hexagonal grid with the specified size.
     * <p>
     * Creates an empty grid where all cells are initialized to 0.
     * </p>
     *
     * @param size The size of the grid (number of rows/columns)
     */
    public HexagonalGrid(int size) {
        SIZE = size;
        RADIUS = (int) Math.floor((double) SIZE / 2);
        this.board = new byte[size * size - calulateSavedSize(size)];
    }

    /**
     * Constructs a new hexagonal grid from a 2D byte array.
     * <p>
     * The grid is created with the same size as the input array, and values
     * from the array are copied to the grid where they are within bounds.
     * </p>
     *
     * @param board A 2D byte array representing the initial state of the grid
     */
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

    /**
     * Sets the value of a cell at the specified axial coordinates.
     *
     * @param q     The q-coordinate of the cell
     * @param s     The s-coordinate of the cell
     * @param value The value to set for the cell
     */
    public void set(int q, int s, byte value) {
        board[calculateIndex(q, s)] = value;
    }

    /**
     * Sets the value of a cell at the specified axial position.
     *
     * @param pos   The axial position of the cell
     * @param value The value to set for the cell
     */
    public void set(AxialPosition pos, byte value) {
        set(pos.q(), pos.s(), value);
    }

    /**
     * Gets the value of a cell at the specified axial coordinates.
     * <p>
     * Returns 0 if the coordinates are outside the grid bounds.
     * </p>
     *
     * @param q The q-coordinate of the cell
     * @param s The s-coordinate of the cell
     * @return The value of the cell, or 0 if out of bounds
     */
    public byte get(int q, int s) {
        if (!isInBounds(q, s))
            return 0;
        return board[calculateIndex(q, s)];
    }

    /**
     * Gets the value of a cell at the specified axial position.
     * <p>
     * Returns 0 if the position is outside the grid bounds.
     * </p>
     *
     * @param pos The axial position of the cell
     * @return The value of the cell, or 0 if out of bounds
     */
    public byte get(AxialPosition pos) {
        return get(pos.q(), pos.s());
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

    /**
     * Checks if an axial position is within the bounds of the grid.
     *
     * @param position The axial position to check
     * @return true if the position is within bounds, false otherwise
     */
    public boolean isInBounds(AxialPosition position) {
        return isInBounds(position.q(), position.s());
    }

    /**
     * Checks if the specified axial coordinates are within the bounds of the grid.
     *
     * @param q The q-coordinate to check
     * @param s The s-coordinate to check
     * @return true if the coordinates are within bounds, false otherwise
     */
    public boolean isInBounds(int q, int s) {
        if (!(q >= 0 && q < SIZE && s >= 0 && s < SIZE))
            return false;
        if (s < columnOffset(q))
            return false;
        if (s >= SIZE - columnOffset(SIZE - 1 - q))
            return false;
        return true;
    }

    /**
     * Creates a copy of this hexagonal grid.
     *
     * @return A new hexagonal grid with the same content as this one
     */
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

    /**
     * Compares this hexagonal grid with another object for equality.
     * <p>
     * Two hexagonal grids are equal if they have the same content.
     * </p>
     *
     * @param o The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;

        HexagonalGrid that = (HexagonalGrid) o;
        return Arrays.equals(board, that.board);
    }

    /**
     * Returns a hash code for this hexagonal grid.
     *
     * @return A hash code value for this object
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(board);
    }
}
