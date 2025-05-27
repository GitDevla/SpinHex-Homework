package spinhex.model;

/**
 * Represents a position in an axial coordinate system for a hexagonal grid.
 * <p>
 * In axial coordinates, the position is described by two values, q and s,
 * where q represents the top-left to bottom-right diagonal and s represents
 * the top-right to bottom-left diagonal.
 * </p>
 * To learn more about axial coordinates, refer to:
 * <a href="https://www.redblobgames.com/grids/hexagons/#coordinates-axial">Red
 * Blob Games - Axial Coordinates</a>
 * 
 * @param q The top-left to bottom-right diagonal coordinate
 * @param s The top-right to bottom-left diagonal coordinate
 */
public record AxialPosition(int q, int s) {

    /**
     * Returns a string representation of this position in the format "(q,s)".
     *
     * @return A string representation of this position
     */
    @Override
    public String toString() {
        return String.format("(%d,%d)", q, s);
    }

    /**
     * Adds another axial position to this one.
     *
     * @param other The axial position to add
     * @return A new axial position representing the sum
     */
    public AxialPosition add(AxialPosition other) {
        return new AxialPosition(this.q + other.q, this.s + other.s);
    }

    /**
     * Subtracts another axial position from this one.
     *
     * @param other The axial position to subtract
     * @return A new axial position representing the difference
     */
    public AxialPosition subtract(AxialPosition other) {
        return new AxialPosition(this.q - other.q, this.s - other.s);
    }

    /**
     * Checks whether this position is equal to another position.
     *
     * @param other The position to compare with
     * @return true if the positions are equal, false otherwise
     */
    public boolean isEqual(AxialPosition other) {
        return this.q == other.q && this.s == other.s;
    }
}
