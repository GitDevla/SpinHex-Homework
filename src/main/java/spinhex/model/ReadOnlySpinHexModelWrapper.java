package spinhex.model;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

public class ReadOnlySpinHexModelWrapper extends SpinHexModel {
    private ReadOnlyObjectWrapper<HexColor>[][] boardProperty;

    public ReadOnlySpinHexModelWrapper() {
        super();
        boardProperty = new ReadOnlyObjectWrapper[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                boardProperty[i][j] = new ReadOnlyObjectWrapper<>(board[i][j]);
            }
        }
    }

    public ReadOnlySpinHexModelWrapper(HexColor[][] startingBoard, HexColor[][] targetBoard) {
        super(startingBoard, targetBoard);
        boardProperty = new ReadOnlyObjectWrapper[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                boardProperty[i][j] = new ReadOnlyObjectWrapper<>(board[i][j]);
            }
        }
    }

    public ReadOnlyObjectProperty<HexColor> getHexProperty(int q, int s) {
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
