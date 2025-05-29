package spinhex.model;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

public class ReadOnlySpinHexModelWrapper extends SpinHexModel {
    private ReadOnlyIntegerWrapper[][] boardProperty;

    public ReadOnlySpinHexModelWrapper() {
        super();
        boardProperty = new ReadOnlyIntegerWrapper[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                boardProperty[i][j] = new ReadOnlyIntegerWrapper(board[i][j]);
            }
        }
    }

    public ReadOnlySpinHexModelWrapper(byte[][] startingBoard, byte[][] targetBoard) {
        super(startingBoard, targetBoard);
        boardProperty = new ReadOnlyIntegerWrapper[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                boardProperty[i][j] = new ReadOnlyIntegerWrapper(board[i][j]);
            }
        }
    }

    public ReadOnlyIntegerProperty getHexProperty(int q, int s) {
        if (!isInBounds(new AxialPosition(q, s))) {
            throw new IllegalArgumentException("Position out of bounds: " + new AxialPosition(q, s));
        }
        return boardProperty[q][s];
    }

    private void updatePropertyModel(){
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                boardProperty[i][j].set(board[i][j]);
            }
        }
    }

    @Override
    public void makeMove(TwoPhaseAction<AxialPosition, Rotation> moveAction) {
        super.makeMove(moveAction);
        updatePropertyModel();
    }
}
