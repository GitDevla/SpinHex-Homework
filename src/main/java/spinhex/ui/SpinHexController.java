package spinhex.ui;

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
import javafx.scene.layout.StackPane;
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
    private HexGrid gamePane;

    @FXML
    private HexGrid solutionPane;

    private ReadOnlyIntegerWrapper steps = new ReadOnlyIntegerWrapper(0);

    private final ReadOnlySpinHexModelWrapper model = new ReadOnlySpinHexModelWrapper();
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
        gamePane.populateFromGrid(model.getBoard());
        solutionPane.setHexSize((int) (HEX_SIZE / 1.5));
        solutionPane.populateFromGrid(model.getSolution());

        for (HexTile tile : gamePane.getHexTiles()) {
            var modelHex = model.getHexProperty(tile.getQ(), tile.getS());
            tile.bind(modelHex);
            tile.setOnMouseClicked(this::handleMouseClickOnHex);
        }

        stepsLabel.textProperty().bind(steps.asString("(%d steps taken so far)"));
        usernameLabel.textProperty().bind(username.concat("'s Board"));
        selector.phaseProperty().addListener(this::updateMoveCounterAfterMove);
        selector.phaseProperty().addListener(this::showSelectionPhaseChange);
        selector.phaseProperty().addListener(this::makeMoveIfAllowed);
        selector.phaseProperty().addListener(this::winConditionCheck);
        Platform.runLater(() -> {
            Stage stage = (Stage) gamePane.getScene().getWindow();
            stage.setTitle("SpinHex Game - " + username.get());
        });
    }

    @FXML
    private void handleMouseClickOnHex(MouseEvent event) {
        var hex = (HexTile) event.getSource();
        var q = hex.getQ();
        var s = hex.getS();
        Logger.info("Clicked on hex at position: ({}, {})", q, s);
        selector.reset();
        selector.selectFrom(new AxialPosition(q, s));
    }

    private void makeMoveIfAllowed(ObservableValue<? extends TwoPhaseActionSelector.Phase> value,
            TwoPhaseActionSelector.Phase oldPhase, TwoPhaseActionSelector.Phase newPhase) {
        if (newPhase != TwoPhaseActionSelector.Phase.READY_TO_MOVE)
            return;
        Logger.info("Making move: {}\nRotation: {}", selector.getFrom(), selector.getAction());
        selector.makeMove();
    }

    private void showSelectionPhaseChange(ObservableValue<? extends TwoPhaseActionSelector.Phase> value,
            TwoPhaseActionSelector.Phase oldPhase, TwoPhaseActionSelector.Phase newPhase) {
        switch (newPhase) {
            case SELECT_FROM -> hideSelection();
            case SELECT_ACTION -> showRotationSelectionOverlay(selector.getFrom());
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
        for (HexTile child : gamePane.getHexTiles()) {
            if (child.getQ() == position.q() && child.getS() == position.s()) {
                return (StackPane) child;
            }
        }
        throw new AssertionError();
    }

    private void showRotationSelectionOverlay(AxialPosition position) {
        var square = getSquare(position);
        var rotationOverlay = new RotationSelectorOverlay(square.getTranslateX(), square.getTranslateY(), HEX_SIZE);

        rotationOverlay.setOnClockwiseClick(e -> {
            selector.selectAction(Rotation.CLOCKWISE);
        });

        rotationOverlay.setOnCounterClockwiseClick(e -> {
            selector.selectAction(Rotation.COUNTERCLOCKWISE);
        });

        gamePane.getChildren().add(rotationOverlay);
    }
}
