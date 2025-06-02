package spinhex;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyIntegerWrapper;
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
import javafx.stage.Stage;
import jfxutils.JFXTwoPhaseActionSelector;
import jfxutils.TwoPhaseActionSelector;
import spinhex.model.*;
import spinhex.score.Score;
import spinhex.score.ScoreManager;
import spinhex.ui.RotationSelectorOverlay;
import spinhex.ui.HexTile;

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
        generateHexGridInPlain(gamePane, true);
        generateHexGridInPlain(solutionPane, false);
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

    private void generateHexGridInPlain(Pane pane, boolean interactive) {
        var offsetStart = (double) (model.getBoardSize() - 1) / 4;
        for (var row = 0; row < model.getBoardSize(); row++) {
            for (var col = 0; col < model.getBoardSize(); col++) {
                var modelHex = model.getHexProperty(row, col);
                if (modelHex.get() == HexColor.NONE) {
                    continue;
                }
                HexTile hexTile;
                if (interactive) {
                    hexTile = new HexTile(HEX_SIZE, modelHex);
                    hexTile.setOnMouseClicked(this::handleMouseClickOnHex);
                } else {
                    hexTile = new HexTile(HEX_SIZE, model.getSolution().get(row, col));
                }

                double xOffset = col * HEX_SIZE;
                xOffset -= offsetStart * HEX_SIZE;
                var yOffset = row * (HEX_SIZE * 0.75);
                hexTile.setTranslateX(xOffset);
                hexTile.setTranslateY(yOffset);
                hexTile.getProperties().put("q", row);
                hexTile.getProperties().put("s", col);
                pane.getChildren().add(hexTile);
            }
            offsetStart -= 0.5;
        }
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
        if (newPhase != TwoPhaseActionSelector.Phase.READY_TO_MOVE)
            return;
        steps.setValue(steps.getValue() + 1);
        Logger.info("Steps updated: {}", steps.get());
    }

    private void winConditionCheck(ObservableValue<? extends TwoPhaseActionSelector.Phase> value,
            TwoPhaseActionSelector.Phase oldPhase, TwoPhaseActionSelector.Phase newPhase) {
        if (oldPhase != TwoPhaseActionSelector.Phase.READY_TO_MOVE)
            return;
        if (!model.isSolved())
            return;
        Logger.info("Puzzle solved by: {}", username.get());
        saveScore();
        showCongratulationsPopup();
        switchSceneToStartMenu();
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

    private void showRotationSelectionOverlay(AxialPosition position) {
        var square = getSquare(position);
        var rotationOverlay = new RotationSelectorOverlay(square.getTranslateX(), square.getTranslateY(), HEX_SIZE);

        rotationOverlay.setOnClockwiseClick(e -> {
            selector.select(Rotation.CLOCKWISE);
            if (selector.isReadyToMove()) {
                Logger.info("Making move: {}\nRotation: {}", selector.getFrom(), selector.getTo());
                selector.makeMove();
            }
            e.consume();
        });

        rotationOverlay.setOnCounterClockwiseClick(e -> {
            selector.select(Rotation.COUNTERCLOCKWISE);
            if (selector.isReadyToMove()) {
                Logger.info("Making move: {}\nRotation: {}", selector.getFrom(), selector.getTo());
                selector.makeMove();
            }
            e.consume();
        });

        gamePane.getChildren().add(rotationOverlay);
    }
}
