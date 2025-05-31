package spinhex.model;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

public class ReadOnlySpinHexModelWrapper extends SpinHexModel {
    private ReadOnlyIntegerWrapper[][] boardProperty;

    public ReadOnlySpinHexModelWrapper() {
        super();
        boardProperty = new ReadOnlyIntegerWrapper[getBoardSize()][getBoardSize()];
        for (int i = 0; i < getBoardSize(); i++) {
            for (int j = 0; j < getBoardSize(); j++) {
                boardProperty[i][j] = new ReadOnlyIntegerWrapper(board.isInBounds(i, j) ? board.get(i, j) : HexColor.NONE);
            }
        }
    }

    public ReadOnlySpinHexModelWrapper(byte[][] startingBoard, byte[][] targetBoard) {
        super(startingBoard, targetBoard);
        boardProperty = new ReadOnlyIntegerWrapper[getBoardSize()][getBoardSize()];
        for (int i = 0; i < getBoardSize(); i++) {
            for (int j = 0; j < getBoardSize(); j++) {
                boardProperty[i][j] = new ReadOnlyIntegerWrapper(board.isInBounds(i, j) ? board.get(i, j) : HexColor.NONE);
            }
        }
    }

    public ReadOnlyIntegerProperty getHexProperty(int q, int s) {
        return boardProperty[q][s];
    }

    private void updatePropertyModelAround(int q, int s) {
        for (var d : DIRECTIONS) {
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
