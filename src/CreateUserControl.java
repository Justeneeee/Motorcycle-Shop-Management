import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CreateUserControl {
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private TextField confirmPassword;
    @FXML
    private Label statusLabel;

    @FXML
    public void backToLogin(ActionEvent event) {
        Main.switchScene("resource/login.fxml");
    }

    @FXML
    public void signup(ActionEvent event) {
        String username1 = username.getText();
        String password1 = password.getText();
        String confirmP = confirmPassword.getText();

        if (username1.isEmpty() || password1.isEmpty() || confirmP.isEmpty()) {
            statusLabel.setText("All fields are required!");
            return;
        }

        if (password1.equals(confirmP)) {
            try {
                saveUserToFile(username1, password1);
                Main.switchScene("resource/home.fxml");
            } catch (IOException e) {
                statusLabel.setText("Error on saving!");
            }
        } else {
            statusLabel.setText("Passwords do not match!");
        }
    }

    private void saveUserToFile(String username, String password) throws IOException {
        String userRecord = username + "," + password;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/user.csv", true))) {
            writer.newLine();
            writer.write(userRecord);
        }
    }
}
