package spinhex.model;

/**
 * Represents the available colors for hexagonal tiles in the SpinHex game.
 * <p>
 * The enum provides the following color options:
 * <ul>
 *   <li>{@code NONE} - Represents an empty tile, this is logically equal to <code>null</code></li>
 *   <li>{@code RED} - Represents a red colored tile</li>
 *   <li>{@code BLUE} - Represents a blue colored tile</li>
 *   <li>{@code GREEN} - Represents a green colored tile</li>
 * </ul>
 * These colors are used to track the state of hexagonal tiles on the game board.
 */
public class HexColor {
    public static final byte NONE = 0x00;
    public static final byte RED = 0x01;
    public static final byte GREEN = 0x02;
    public static final byte BLUE = 0x03;
}
