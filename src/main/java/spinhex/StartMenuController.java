package spinhex;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import spinhex.score.Score;
import spinhex.score.ScoreManager;

import java.io.IOException;
import java.nio.file.Path;

public class StartMenuController {
    @FXML
    private TextField usernameField;

    @FXML
    private TableView scoreTable;

    @FXML
    private TableColumn<Score, String> usernameCol;

    @FXML
    private TableColumn<Score, String> scoreCol;

    @FXML
    private void startGame(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/game.fxml"));
        Parent root = loader.load();

        SpinHexController controller = loader.getController();
        controller.setUsername(usernameField.getText());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("SpinHex Game");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void initialize() {
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        loadScores();
    }

    private void loadScores() {
        scoreTable.getItems().clear();
        try {
            ObservableList<Score> observableList = FXCollections.observableArrayList();
            observableList.addAll(new ScoreManager(Path.of("scores.json")).getAll());
            scoreTable.setItems(observableList);
        } catch (IOException e) {
        }
    }
}
