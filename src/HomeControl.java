import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeControl {
    @FXML
    private Button homeButton;
    @FXML
    private Button addPartButton;
    @FXML
    private Button sellPartButton;
    @FXML
    private Button logoutButton;
    @FXML
    private TableView<Part> tableView;
    @FXML
    private TableColumn<Part, String> brandColumn;
    @FXML
    private TableColumn<Part, String> typeColumn;
    @FXML
    private TableColumn<Part, Integer> qtyColumn;
    @FXML
    private TableColumn<Part, Double> priceColumn;
    @FXML
    private TextField searchField;
    private FilteredList<Part> filteredData;

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException{
        if (event.getSource() == homeButton) {
            try {
                Main.switchScene("resource/home.fxml");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (event.getSource() == addPartButton) {
            try {
                Main.switchScene("resource/addPart.fxml");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (event.getSource() == sellPartButton) {
            try {
                Main.switchScene("resource/sellPart.fxml");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (event.getSource() == logoutButton) {
           try {
               Main.switchScene("resource/login.fxml");
           } catch (Exception e) {
               e.printStackTrace();
           }
        }
    }

    private void switchScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) homeButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        qtyColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        List<Part> partsList = loadPartsFromCSV("data/parts.csv");
        filteredData = new FilteredList<>(FXCollections.observableArrayList(partsList), p -> true);
        tableView.setItems(filteredData);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(part -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (part.getBrand().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (part.getType().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
    }

    private List<Part> loadPartsFromCSV(String fileName) {
        List<Part> partsList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String brand = values[0];
                String type = values[1];
                int quantity = Integer.parseInt(values[2]);
                double price = Double.parseDouble(values[3]);
                Part part = new Part(brand, type, quantity, price);
                partsList.add(part);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return partsList;
    }
}
