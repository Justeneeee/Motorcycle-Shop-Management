import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LoginControl {
    private ArrayList<User> users;
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private Label usernameStatus;
    @FXML
    private Label passwordStatus;



    @FXML
    public void signup(ActionEvent event) {
        Main.switchScene("resource/createUser.fxml");
    }

    @FXML
    public void logout(ActionEvent event) {
        Main.switchScene("resource/login.fxml");
    }

    @FXML
    private void home(ActionEvent event) {
        Main.switchScene("resource/home.fxml");
    }


    @FXML
    void login(ActionEvent event) {
        String userInput = username.getText();

        if (userInput == null) {
            setUsernameStatus();
        }

        String passwordInput = password.getText();

        if (passwordInput == null) {
            setPasswordStatus();
        }

        User user = searchUser(userInput);

        if (user == null) {
            setUsernameStatus();
            setPasswordStatus();
            //showAlert(event);
            return;
        }

        String passwordHash = user.getPassword();

        if (passwordHash == null) {
            showAlert(event);
            return;
        }

        String salt = BCrypt.gensalt(12);
        String hashedPasswordInput = BCrypt.hashpw(passwordInput, salt);

        if (BCrypt.checkpw(passwordInput, hashedPasswordInput)) {
            switchToHome();
        } else {
            showAlert(event);
        }
    }

    private void setUsernameStatus() {
        usernameStatus.setText("This field is required!");
    }

    private void setPasswordStatus() {
        passwordStatus.setText("This field is required!");
    }


    public User searchUser(String username1) {

        if (username.getText() == null) {
            setUsernameStatus();
        }

        if (password.getText() == null) {
            setPasswordStatus();
        }
        for (User checkUser : users) {
            if (checkUser.getUsername().equals(username1)) {
                return checkUser;
            }
        }

        return null;
    }

    public LoginControl() {
        users = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("data/user.csv"));
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                User user = new User(data[0], data[1]);
                users.add(user);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void switchToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resource/home.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) username.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Homepage");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resource/fail.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Invalid!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
