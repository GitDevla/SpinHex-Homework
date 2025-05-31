package spinhex.model;

/**
 * Represents the available colors for hexagonal tiles in the SpinHex game.
 * <p>
 * For performance and memory efficiency, the colors are represented as
 * single-byte values.
 * </p>
 */
public final class HexColor {
    /**
     * Represents an empty tile, logically equivalent to <code>null</code>.
     * This is used to indicate that a hexagonal tile is not occupied by any
     * color.
     */
    public static final byte NONE = 0x00;

    /**
     * Represents a red colored tile.
     */
    public static final byte RED = 0x01;

    /**
     * Represents a green colored tile.
     */
    public static final byte GREEN = 0x02;

    /**
     * Represents a blue colored tile.
     */
    public static final byte BLUE = 0x03;
}
