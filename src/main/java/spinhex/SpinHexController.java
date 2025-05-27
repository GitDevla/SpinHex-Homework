package spinhex;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import jfxutils.JFXTwoPhaseMoveSelector;
import spinhex.model.AxialPosition;
import spinhex.model.SpinHexModel;

public class SpinHexController {

    @FXML
    private Pane gamePane;

    private final SpinHexModel model = new SpinHexModel();

    @FXML
    private void initialize() {
        for (var i = 0; i < 5; i++) {
            for (var j = 0; j < 5; j++) {
                gamePane.getChildren().add(createHex(i, j));
            }
        }
    }

    private StackPane createHex(int row, int col) {
        var width = 40;
        var height = 40;
        var xOffset = col * width + ((row % 2 == 0) ? 0 : width / 2);
        var yOffset = row * (height * 0.75);
        var square = new StackPane();
        square.setMinSize(width, height);
        square.setMaxSize(width, height);
        square.setTranslateX(xOffset);
        square.setTranslateY(yOffset);

        square.getStyleClass().add("hex-tile");
        var piece = new Circle((double) width / 2 - 5);
        square.getChildren().add(piece);
        return square;
    }
}
