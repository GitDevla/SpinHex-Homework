package spinhex;

import javafx.application.Platform;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
import spinhex.model.*;
import spinhex.score.Score;
import spinhex.score.ScoreManager;
import org.tinylog.Logger;

import java.io.IOException;
import java.nio.file.Path;

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

    private ReadOnlyIntegerWrapper steps = new ReadOnlyIntegerWrapper(0);

    private final ReadOnlySpinHexModelWrapper model = new ReadOnlySpinHexModelWrapper(new byte[][] {
            { HexColor.NONE, HexColor.RED, HexColor.RED },
            { HexColor.RED, HexColor.GREEN, HexColor.RED },
            { HexColor.BLUE, HexColor.RED, HexColor.NONE }
    }, new byte[][] {
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
        stepsLabel.textProperty().bind(steps.asString("(%d steps taken so far)"));
        usernameLabel.textProperty().bind(username.concat("'s Board"));
        selector.phaseProperty().addListener(this::updateMoveCounterAfterMove);
        selector.phaseProperty().addListener(this::showSelectionPhaseChange);
        selector.phaseProperty().addListener(this::winConditionCheck);
        Platform.runLater(() -> {
            Stage stage = (Stage) gamePane.getScene().getWindow();
            stage.setTitle("SpinHex Game - " + username.get());
            stage.setWidth(2 * HEX_SIZE * model.getBoardSize() + 100);
            stage.setHeight(HEX_SIZE * model.getBoardSize() + 150);
        });
    }

    private void generateHexGridInPlain(Pane pane, HexGenerator strategy) {
        var offsetStart = (double) (model.getBoardSize() - 1) / 4;
        for (var i = 0; i < model.getBoardSize(); i++) {
            for (var j = 0; j < model.getBoardSize(); j++) {
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
        circle.fillProperty().set(assignHexColorToPaint(model.getSolution().get(row, col)));
        var text = createHexText();
        text.textProperty().set(assignHexColorToString(model.getSolution().get(row, col)));
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

    private ObjectBinding<Paint> createHexBindingColor(ReadOnlyIntegerProperty hexColorProperty) {
        return new ObjectBinding<Paint>() {
            {
                super.bind(hexColorProperty);
            }

            @Override
            protected Paint computeValue() {
                return assignHexColorToPaint((byte) hexColorProperty.get());
            }
        };
    }

    private Paint assignHexColorToPaint(Byte hexColor) {
        return switch (hexColor) {
            case HexColor.NONE -> Color.TRANSPARENT;
            case HexColor.RED -> Color.RED;
            case HexColor.BLUE -> Color.BLUE;
            case HexColor.GREEN -> Color.GREEN;
            default -> throw new IllegalStateException("Unexpected value: " + hexColor);
        };
    }

    private String assignHexColorToString(Byte hexColor) {
        return switch (hexColor) {
            case HexColor.NONE -> "";
            case HexColor.RED -> "P";
            case HexColor.BLUE -> "K";
            case HexColor.GREEN -> "Z";
            default -> throw new IllegalStateException("Unexpected value: " + hexColor);
        };
    }

    private ObjectBinding<String> createHexBindingString(ReadOnlyIntegerProperty hexColorProperty) {
        return new ObjectBinding<String>() {
            {
                super.bind(hexColorProperty);
            }

            @Override
            protected String computeValue() {
                return assignHexColorToString((byte) hexColorProperty.get());
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

    private void updateMoveCounterAfterMove(ObservableValue<? extends TwoPhaseActionSelector.Phase> value,
            TwoPhaseActionSelector.Phase oldPhase, TwoPhaseActionSelector.Phase newPhase) {
        if (newPhase == TwoPhaseActionSelector.Phase.READY_TO_MOVE) {
            steps.setValue(steps.getValue() + 1);
            Logger.info("Steps updated: {}", steps.get());
        }
    }

    private void winConditionCheck(ObservableValue<? extends TwoPhaseActionSelector.Phase> value,
            TwoPhaseActionSelector.Phase oldPhase, TwoPhaseActionSelector.Phase newPhase) {
        if (oldPhase == TwoPhaseActionSelector.Phase.READY_TO_MOVE) {
            if (model.isSolved()) {
                Logger.info("Puzzle solved by: {}", username.get());
                saveScore();
                showCongratulationsPopup();
                switchSceneToStartMenu();
            }
        }
    }

    private void saveScore() {
        try {
            ScoreManager scoreManager = new ScoreManager(Path.of("scores.json"));
            scoreManager.add(new Score(username.get(), steps.getValue()));
            Logger.info("Score saved for user: {}", username.get());
        } catch (IOException e) {
            e.printStackTrace();
            Logger.error("Failed to save score: {}", e.getMessage());
        }

    }

    private void switchSceneToStartMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/start.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) gamePane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            Logger.error("Failed to switch to start menu: {}", e.getMessage());
        }
    }

    private void showCongratulationsPopup() {
        var popup = new Alert(Alert.AlertType.INFORMATION);
        popup.setTitle("Congratulations!");
        popup.setHeaderText("You solved the puzzle!");
        popup.setContentText(
                "Well done, " + username.get() + "! You solved it in " + steps.get() + " steps.");
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

    private StackPane createRotationSelectorCircle(String Text) {
        var stackPane = new StackPane();
        var circle = new Circle(10);
        circle.setFill(Color.LIGHTGRAY);
        var text = new Text(Text);
        text.setFill(Color.BLACK);
        text.setBoundsType(TextBoundsType.VISUAL);
        stackPane.getChildren().addAll(circle, text);
        stackPane.setMinSize(30, 30);
        stackPane.setMaxSize(30, 30);
        return stackPane;
    }

    private void showRotationSelectionOverlay(AxialPosition position) {
        var square = getSquare(position);
        var rotationOverlay = new StackPane();
        rotationOverlay.getStyleClass().add("rotation-overlay");
        rotationOverlay.setMinSize(square.getMinWidth(), square.getMinHeight());
        rotationOverlay.setMaxSize(square.getMaxWidth(), square.getMaxHeight());
        rotationOverlay.setTranslateX(square.getTranslateX());
        rotationOverlay.setTranslateY(square.getTranslateY());

        var clockwise = createRotationSelectorCircle("⟳");
        clockwise.setTranslateX(15);
        clockwise.setOnMouseClicked(e -> {
            selector.select(Rotation.CLOCKWISE);
            if (selector.isReadyToMove()) {
                Logger.info("Making move: {}\nRotation: {}", selector.getFrom(), selector.getTo());
                selector.makeMove();
            }
            e.consume();
        });

        var counterClockwise = createRotationSelectorCircle("⟲");
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
