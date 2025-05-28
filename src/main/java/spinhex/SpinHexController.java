package spinhex;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;
import jfxutils.JFXTwoPhaseActionSelector;
import jfxutils.TwoPhaseActionSelector;
import spinhex.model.AxialPosition;
import spinhex.model.HexColor;
import spinhex.model.Rotation;
import spinhex.model.SpinHexModel;
import org.tinylog.Logger;

import java.io.IOException;

public class SpinHexController {

    private static final int HEX_SIZE = 80;

    private ReadOnlyStringWrapper username = new ReadOnlyStringWrapper("Anonymous");

    @FXML
    private Label usernameLabel;

    @FXML
    private Label stepsLabel;

    @FXML
    private Pane gamePane;

    @FXML
    private Pane solutionPane;

    private final SpinHexModel model = new SpinHexModel(new HexColor[][] {
            { HexColor.NONE, HexColor.RED, HexColor.RED },
            { HexColor.RED, HexColor.GREEN, HexColor.RED },
            { HexColor.BLUE, HexColor.RED, HexColor.NONE }
    }, new HexColor[][] {
            { HexColor.NONE, HexColor.BLUE, HexColor.RED },
            { HexColor.RED, HexColor.GREEN, HexColor.RED },
            { HexColor.RED, HexColor.RED, HexColor.NONE }
    });
    private final JFXTwoPhaseActionSelector<AxialPosition, Rotation> selector = new JFXTwoPhaseActionSelector<>(model);

    public void setUsername(String username) {
        this.username.set(username);
        if (username == null || username.isBlank()) {
            this.username.set("Anonymous");
        }
        Logger.info("Username set to: {}", this.username.get());
    }

    @FXML
    private void initialize() {
        generateHexGridInPlain(gamePane, this::createInteractiveHex);
        generateHexGridInPlain(solutionPane, this::createMockHex);
        stepsLabel.textProperty().bind(model.getStepsProperty().asString("(%d steps taken so far)"));
        usernameLabel.textProperty().bind(username.concat("'s Board"));
        selector.phaseProperty().addListener(this::showSelectionPhaseChange);
        selector.phaseProperty().addListener(this::winConditionCheck);
    }

    private void generateHexGridInPlain(Pane pane, HexGenerator strategy) {
        var offsetStart = (double) (model.BOARD_SIZE - 1) / 4;
        for (var i = 0; i < model.BOARD_SIZE; i++) {
            for (var j = 0; j < model.BOARD_SIZE; j++) {
                var newHex = strategy.create(i, j, offsetStart);
                if (newHex == null)
                    continue;
                pane.getChildren().add(newHex);
            }
            offsetStart -= 0.5;
        }
    }

    @FunctionalInterface
    private interface HexGenerator {
        StackPane create(int row, int col, double leftOffset);
    }

    private StackPane createHexContainer(int row, int col, double leftOffset) {
        double xOffset = col * HEX_SIZE;
        xOffset -= leftOffset * HEX_SIZE;
        var yOffset = row * (HEX_SIZE * 0.75);
        var square = new StackPane();
        square.setMinSize(HEX_SIZE, HEX_SIZE);
        square.setMaxSize(HEX_SIZE, HEX_SIZE);
        square.setTranslateX(xOffset);
        square.setTranslateY(yOffset);
        square.getProperties().put("q", row);
        square.getProperties().put("s", col);
        square.getStyleClass().add("hex-tile");
        return square;
    }

    private Circle createHexCircle() {
        return new Circle(HEX_SIZE / 2.7);
    }

    private Text createHexText() {
        Text text = new Text();
        text.setFill(Color.WHITE);
        text.setBoundsType(TextBoundsType.VISUAL);
        return text;
    }

    private StackPane createMockHex(int row, int col, double leftOffset) {
        var modelHex = model.getHexProperty(row, col);
        if (modelHex.get() == HexColor.NONE) {
            return null;
        }

        var square = createHexContainer(row, col, leftOffset);
        var circle = createHexCircle();
        circle.fillProperty().set(assignHexColorToPaint(model.getSolution()[row][col]));
        var text = createHexText();
        text.textProperty().set(assignHexColorToString(model.getSolution()[row][col]));
        square.getChildren().addAll(circle, text);
        return square;
    }

    private StackPane createInteractiveHex(int row, int col, double leftOffset) {
        var modelHex = model.getHexProperty(row, col);
        if (modelHex.get() == HexColor.NONE) {
            return null;
        }

        var square = createHexContainer(row, col, leftOffset);
        var circle = createHexCircle();
        circle.fillProperty().bind(createHexBindingColor(modelHex));
        var text = createHexText();
        text.textProperty().bind(createHexBindingString(modelHex));
        square.getChildren().addAll(circle, text);
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
                return assignHexColorToPaint(hexColorProperty.get());
            }
        };
    }

    private Paint assignHexColorToPaint(HexColor hexColor) {
        return switch (hexColor) {
            case NONE -> Color.TRANSPARENT;
            case RED -> Color.RED;
            case BLUE -> Color.BLUE;
            case GREEN -> Color.GREEN;
        };
    }

    private String assignHexColorToString(HexColor hexColor) {
        return switch (hexColor) {
            case NONE -> "";
            case RED -> "P";
            case BLUE -> "K";
            case GREEN -> "Z";
        };
    }

    private ObjectBinding<String> createHexBindingString(ReadOnlyObjectProperty<HexColor> hexColorProperty) {
        return new ObjectBinding<String>() {
            {
                super.bind(hexColorProperty);
            }

            @Override
            protected String computeValue() {
                return assignHexColorToString(hexColorProperty.get());
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

    private void winConditionCheck(ObservableValue<? extends TwoPhaseActionSelector.Phase> value,
            TwoPhaseActionSelector.Phase oldPhase, TwoPhaseActionSelector.Phase newPhase) {
        if (oldPhase == TwoPhaseActionSelector.Phase.READY_TO_MOVE) {
            if (model.isSolved()) {
                Logger.info("Puzzle solved by: {}", username.get());
                showCongratulationsPopup();
                switchSceneToStartMenu();
            }
        }
    }

    private void switchSceneToStartMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/start.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) gamePane.getScene().getWindow();
            stage.setTitle("SpinHex Game");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            Logger.error("Failed to switch to start menu: {}", e.getMessage());
        }
    }

    private void showCongratulationsPopup() {
        var popup = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        popup.setTitle("Congratulations!");
        popup.setHeaderText("You solved the puzzle!");
        popup.setContentText(
                "Well done, " + username.get() + "! You solved it in " + model.getStepsProperty().get() + " steps.");
        popup.showAndWait();
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
