import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AddPartControl {

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
    @FXML
    private TextField brandField;
    @FXML
    private TextField typeField;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField priceField;
    @FXML
    private Button addButton;
    @FXML
    private Button addQuantityButton;
    @FXML
    private Button removeQuantityButton;

    private ObservableList<Part> partData = FXCollections.observableArrayList();

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {
        if (event.getSource() == homeButton) {
            switchScene("resource/home.fxml");
        } else if (event.getSource() == addPartButton) {
            switchScene("resource/addPart.fxml");
        } else if (event.getSource() == sellPartButton) {
            switchScene("resource/sellPart.fxml");
        } else if (event.getSource() == logoutButton) {
            switchScene("resource/logout.fxml");
        }
    }

    @FXML
    private void handleAddButtonAction(ActionEvent event) {
        String brand = brandField.getText();
        String type = typeField.getText();

        if (!isInteger(quantityField.getText())) {
            showAlert(event);
            return;
        }

        if (!isDouble(priceField.getText())) {
            showAlert(event);
            return;
        }

        int quantity = Integer.parseInt(quantityField.getText());
        double price = Double.parseDouble(priceField.getText());

        Part newPart = new Part(brand, type, quantity, price);
        partData.add(newPart);
        tableView.setItems(partData);

        clearFields();
    }

    private void showAlert(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resource/fail1.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Invalid!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isDouble(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @FXML
    private void handleAddQuantity(ActionEvent event) {
        Part selectedPart = tableView.getSelectionModel().getSelectedItem();
        if (selectedPart != null) {
            selectedPart.setQuantity(selectedPart.getQuantity() + 1);
            updateCSV();
            tableView.refresh();
        }
    }

    @FXML
    private void handleRemoveQuantity(ActionEvent event) {
        Part selectedPart = tableView.getSelectionModel().getSelectedItem();
        if (selectedPart != null && selectedPart.getQuantity() > 0) {
            selectedPart.setQuantity(selectedPart.getQuantity() - 1);
            updateCSV();
            tableView.refresh();
        }
    }

    private void updateCSV() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("data/parts.csv"))) {
            writer.println("Brand,Type,Quantity,Price");
            for (Part part : partData) {
                writer.println(part.getBrand() + "," + part.getType() + "," + part.getQuantity() + "," + part.getPrice());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void clearFields() {
        brandField.clear();
        typeField.clear();
        quantityField.clear();
        priceField.clear();
    }

    @FXML
    public void initialize() {
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        qtyColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        List<Part> partsList = loadPartsFromCSV("data/parts.csv");
        partData = FXCollections.observableArrayList(partsList);
        FilteredList<Part> filteredData = new FilteredList<>(partData, p -> true);
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

    private void switchScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) homeButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("New Scene");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
