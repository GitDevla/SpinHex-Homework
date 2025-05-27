package spinhex;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class StartMenuController {
    @FXML
    private TextField usernameField;

    @FXML
    private void startGame(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/game.fxml"));
        SpinHexController controller = new SpinHexController();
        controller.setUsername(usernameField.getText());

        stage.setTitle("SpinHex Game");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
