package spinhex.model;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;

/**
 * A read-only wrapper for the SpinHex model that provides access to the board
 * properties without allowing modifications.
 * <p>
 * This class extends the SpinHexModel and provides a read-only view of the
 * board, allowing access to hex values at specific axial coordinates.
 * </p>
 */
public class ReadOnlySpinHexModelWrapper extends SpinHexModel {
    private ReadOnlyIntegerWrapper[][] boardProperty;

    /**
     * Constructs a read-only wrapper for a SpinHex model with the default board.
     * Initializes the board properties to reflect the current state of the board.
     */
    public ReadOnlySpinHexModelWrapper() {
        super();
        boardProperty = new ReadOnlyIntegerWrapper[getBoardSize()][getBoardSize()];
        for (int i = 0; i < getBoardSize(); i++) {
            for (int j = 0; j < getBoardSize(); j++) {
                boardProperty[i][j] = new ReadOnlyIntegerWrapper(
                        board.isInBounds(i, j) ? board.get(i, j) : HexColor.NONE);
            }
        }
    }

    /**
     * Constructs a read-only wrapper for a SpinHex model with the specified
     * starting and target boards.
     *
     * @param startingBoard The initial state of the board as a 2D byte array
     * @param targetBoard   The target state of the board as a 2D byte array
     */
    public ReadOnlySpinHexModelWrapper(byte[][] startingBoard, byte[][] targetBoard) {
        super(startingBoard, targetBoard);
        boardProperty = new ReadOnlyIntegerWrapper[getBoardSize()][getBoardSize()];
        for (int i = 0; i < getBoardSize(); i++) {
            for (int j = 0; j < getBoardSize(); j++) {
                boardProperty[i][j] = new ReadOnlyIntegerWrapper(
                        board.isInBounds(i, j) ? board.get(i, j) : HexColor.NONE);
            }
        }
    }

    /**
     * Returns a read-only property for the hex at the specified axial coordinates.
     *
     * @param q The q-coordinate of the hex
     * @param s The s-coordinate of the hex
     * @return A read-only integer property representing the hex value
     */
    public ReadOnlyIntegerProperty getHexProperty(int q, int s) {
        return boardProperty[q][s];
    }

    private void updatePropertyModelAround(int q, int s) {
        for (var d : ADJACENT_DIRECTIONS) {
            var nq = q + d.q();
            var ns = s + d.s();
            boardProperty[nq][ns].setValue(board.get(nq, ns));
        }
    }

    @Override
    public void makeMove(TwoPhaseAction<AxialPosition, Rotation> moveAction) {
        super.makeMove(moveAction);
        updatePropertyModelAround(moveAction.from().q(), moveAction.from().s());
    }
}
