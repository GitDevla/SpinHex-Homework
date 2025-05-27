package spinhex;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextBoundsType;
import jfxutils.JFXTwoPhaseActionSelector;
import jfxutils.TwoPhaseActionSelector;
import spinhex.model.AxialPosition;
import spinhex.model.HexColor;
import spinhex.model.Rotation;
import spinhex.model.SpinHexModel;
import org.tinylog.Logger;

public class SpinHexController {

    @FXML
    private Pane gamePane;

    private final SpinHexModel model = new SpinHexModel();
    private final JFXTwoPhaseActionSelector<AxialPosition, Rotation> selector = new JFXTwoPhaseActionSelector<>(model);

    @FXML
    private void initialize() {
        for (var i = 2; i < 5; i++) {
            gamePane.getChildren().add(createHex(0, i, 1));
        }
        for (var i = 1; i < 5; i++) {
            gamePane.getChildren().add(createHex(1, i, 0.5));
        }
        for (var i = 0; i < 5; i++) {
            gamePane.getChildren().add(createHex(2, i, 0));
        }
        for (var i = 0; i < 4; i++) {
            gamePane.getChildren().add(createHex(3, i, -0.5));
        }
        for (var i = 0; i < 3; i++) {
            gamePane.getChildren().add(createHex(4, i, -1.0));
        }
        selector.phaseProperty().addListener(this::showSelectionPhaseChange);
    }

    private StackPane createHex(int row, int col, double leftOffset) {
        var width = 80;
        var height = 80;
        double xOffset = col * width;
        xOffset -= leftOffset * width;
        var yOffset = row * (height * 0.75);
        var square = new StackPane();
        square.setMinSize(width, height);
        square.setMaxSize(width, height);
        square.setTranslateX(xOffset);
        square.setTranslateY(yOffset);
        square.getProperties().put("q", row);
        square.getProperties().put("s", col);
        square.getStyleClass().add("hex-tile");

        var circle = new Circle((double) width / 2.7);
        circle.fillProperty().bind(createHexBindingColor(model.getHexProperty(row, col)));

        var text = new javafx.scene.text.Text();
        text.textProperty().bind(createHexBindingString(model.getHexProperty(row, col)));
        text.setFill(Color.WHITE);
        text.setBoundsType(TextBoundsType.VISUAL);

        square.getChildren().add(circle);
        square.getChildren().add(text);

        square.setOnMouseClicked(this::handleMouseClickOnHex);
        return square;
    }

    private ObjectBinding<Paint> createHexBindingColor(ReadOnlyObjectProperty<HexColor> hexColorProperty) {
        return new ObjectBinding<Paint>() {
            {
                super.bind(hexColorProperty);
            }

            @Override
            protected Paint computeValue() {
                return switch (hexColorProperty.get()) {
                    case NONE -> Color.TRANSPARENT;
                    case RED -> Color.RED;
                    case BLUE -> Color.BLUE;
                    case GREEN -> Color.GREEN;
                };
            }
        };
    }

    private ObjectBinding<String> createHexBindingString(ReadOnlyObjectProperty<HexColor> hexColorProperty) {
        return new ObjectBinding<String>() {
            {
                super.bind(hexColorProperty);
            }

            @Override
            protected String computeValue() {
                return switch (hexColorProperty.get()) {
                    case NONE -> "";
                    case RED -> "P";
                    case BLUE -> "K";
                    case GREEN -> "Z";
                };
            }
        };
    }

    @FXML
    private void handleMouseClickOnHex(MouseEvent event) {
        var hex = (StackPane) event.getSource();
        var q = (int) hex.getProperties().get("q");
        var s = (int) hex.getProperties().get("s");
        Logger.info("Clicked on hex at position: ({}, {})", q, s);
        selector.select(new AxialPosition(q, s));
        if (selector.isReadyToMove()) {
            selector.reset();
        }
    }

    private void showSelectionPhaseChange(ObservableValue<? extends TwoPhaseActionSelector.Phase> value,
            TwoPhaseActionSelector.Phase oldPhase, TwoPhaseActionSelector.Phase newPhase) {
        switch (newPhase) {
            case SELECT_FROM -> {
            }
            case SELECT_TO -> showRotationSelectionOverlay(selector.getFrom());
            case READY_TO_MOVE -> hideSelection();
        }
    }

    private void hideSelection() {
        gamePane.getChildren().removeIf(node -> node.getStyleClass().contains("rotation-overlay"));
    }

    private StackPane getSquare(AxialPosition position) {
        for (var child : gamePane.getChildren()) {
            var q = (int) child.getProperties().get("q");
            var s = (int) child.getProperties().get("s");
            if (q == position.q() && s == position.s()) {
                return (StackPane) child;
            }
        }
        throw new AssertionError();
    }

    private void showRotationSelectionOverlay(AxialPosition position) {
        var square = getSquare(position);
        var rotationOverlay = new StackPane();
        rotationOverlay.getStyleClass().add("rotation-overlay");
        rotationOverlay.setMinSize(square.getMinWidth(), square.getMinHeight());
        rotationOverlay.setMaxSize(square.getMaxWidth(), square.getMaxHeight());
        rotationOverlay.setTranslateX(square.getTranslateX());
        rotationOverlay.setTranslateY(square.getTranslateY());

        var clockwise = new Circle(10);
        clockwise.setCenterY(0);
        clockwise.setFill(Color.LIGHTGRAY);
        clockwise.setTranslateX(15);
        clockwise.setOnMouseClicked(e -> {
            selector.select(Rotation.CLOCKWISE);
            if (selector.isReadyToMove()) {
                Logger.info("Making move: {}\nRotation: {}", selector.getFrom(), selector.getTo());

                selector.makeMove();
            }
            e.consume();
        });

        var counterClockwise = new Circle(10);
        counterClockwise.setFill(Color.DARKGRAY);
        counterClockwise.setTranslateX(-15);
        counterClockwise.setOnMouseClicked(e -> {
            selector.select(Rotation.COUNTERCLOCKWISE);
            if (selector.isReadyToMove()) {
                Logger.info("Making move: {}\nRotation: {}", selector.getFrom(), selector.getTo());
                selector.makeMove();
            }
            e.consume();
        });

        rotationOverlay.getChildren().addAll(clockwise, counterClockwise);
        gamePane.getChildren().add(rotationOverlay);
    }
}
