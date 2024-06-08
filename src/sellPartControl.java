import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

public class sellPartControl {
    @FXML
    private TableView<Part> tableView;
    @FXML
    private TableColumn<Part, String> brandCol;
    @FXML
    private TableColumn<Part, String> typeCol;
    @FXML
    private TableColumn<Part, Integer> quantityCol;
    @FXML
    private TableColumn<Part, Double> priceCol;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Part> cartTableView;
    @FXML
    private TableColumn<Part, String> cartBrandColumn;
    @FXML
    private TableColumn<Part, Integer> cartQtyColumn;
    @FXML
    private TableColumn<Part, Double> cartPriceColumn;
    @FXML
    private TextField moneyField;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private Label changeLabel;
    @FXML
    private TextArea receiptArea;

    private ObservableList<Part> partList = FXCollections.observableArrayList();
    private ObservableList<Part> cartList = FXCollections.observableArrayList();
    @FXML
    public void home(ActionEvent event) {
        Main.switchScene("resource/home.fxml");
    }

    @FXML
    public void addPart(ActionEvent event) {
        Main.switchScene("resource/addPart.fxml");
    }

    @FXML
    public void logout(ActionEvent event) {
        Main.switchScene("resource/login.fxnl");
    }

    @FXML
    private void refresh(ActionEvent event) {
        Main.switchScene("resource/sellPart.fxml");
    }

    @FXML
    public void initialize() {
        loadGamerData();
        brandCol.setCellValueFactory(new PropertyValueFactory<>("brand"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        cartBrandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        cartQtyColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        cartPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        tableView.setItems(partList);
        cartTableView.setItems(cartList);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTable(newValue);
        });

        tableView.refresh();
    }

    private void filterTable(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            tableView.setItems(partList);
            return;
        }

        ObservableList<Part> filteredData = FXCollections.observableArrayList(
                partList.stream()
                        .filter(part -> part.getType().toLowerCase().contains(keyword.toLowerCase()) ||
                                part.getBrand().toLowerCase().contains(keyword.toLowerCase()))
                        .collect(Collectors.toList())
        );
        tableView.setItems(filteredData);
    }

    private void loadGamerData() {
        try (BufferedReader br = new BufferedReader(new FileReader("data/parts.csv"))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 4) {
                    Part part = new Part(data[0], data[1], Integer.parseInt(data[2]), Double.parseDouble(data[3]));
                    partList.add(part);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addToCart(ActionEvent event) {
        Part selectedPart = tableView.getSelectionModel().getSelectedItem();
        if (selectedPart != null && selectedPart.getQuantity() > 0) {
            boolean alreadyInCart = false;
            for (Part part : cartList) {
                if (part.equals(selectedPart)) {
                    part.setQuantity(part.getQuantity() + 1);
                    updateQuantityInCSV(selectedPart, part.getQuantity() + 1);
                    alreadyInCart = true;
                    break;
                }
            }
            if (!alreadyInCart) {
                cartList.add(new Part(selectedPart.getBrand(), selectedPart.getType(), 1, selectedPart.getPrice()));
                updateQuantityInCSV(selectedPart, selectedPart.getQuantity() - 1);
            }
            updateCartTable();
            tableView.refresh();
            cartTableView.refresh();
        }
        calculateTotal();
    }



    private void updateQuantityInCSV(Part partToUpdate, int newQuantity) {
        try (BufferedReader br = new BufferedReader(new FileReader("data/parts.csv"))) {
            String line;
            StringBuilder fileContent = new StringBuilder();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 4 && data[0].equals(partToUpdate.getBrand()) && data[1].equals(partToUpdate.getType())) {
                    data[2] = String.valueOf(newQuantity);
                    line = String.join(",", data);
                }
                fileContent.append(line).append("\n");
            }
            Files.write(Paths.get("data/parts.csv"), fileContent.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void calculateTotal() {
        int total = (int) Math.round(cartList.stream().mapToDouble(p -> p.getQuantity() * p.getPrice()).sum());
        totalPriceLabel.setText(String.valueOf(total));
    }


    @FXML
    private void clearCart() {
        cartList.clear();
        updateCartTable();
        moneyField.clear();
        totalPriceLabel.setText("0");

    }

    @FXML
    private void handleCheckout(ActionEvent event) {
        String totalText = totalPriceLabel.getText().trim();
        if (totalText.isEmpty()) {
            System.out.println("Error: total price not set!");
            return;
        }

        try {   
            double total = Double.parseDouble(totalText);

            String moneyText = moneyField.getText().trim();
            if (moneyText.isEmpty()) {
                System.out.println("Error: no money entered!");
                return;
            }

            int cash = (int) Double.parseDouble(moneyText);
            int change = (int) (cash - total);

            changeLabel.setText(String.valueOf(change));
            String receipt = generateReceipt((int) total, cash, change);
            receiptArea.setText(receipt);
        } catch (NumberFormatException e) {
            System.out.println("Error: invalid number format!");
        }
    }

    private String generateReceipt(int total, int cash, int change) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("Motorcycle Parts\n");
        receipt.append("Bacnotan, La Union\n");
        receipt.append("Telp. 092913123\n");
        receipt.append("Cashier: Ave Bonn Aquino\n");
        receipt.append("\n");
        receipt.append("CASH RECEIPT\n");
        receipt.append("*************************\n");
        for (Part g : cartList) {
            receipt.append(String.format("%s\t%d\t%d\n", g.getBrand(), g.getQuantity(), (int) (g.getQuantity() * g.getPrice())));
        }
        receipt.append("\n");
        receipt.append(String.format("Total\t%d\n", total));
        receipt.append(String.format("Cash\t%d\n", cash));
        receipt.append(String.format("Change\t%d\n", change));
        receipt.append("*************************\n");
        receipt.append("SALAMAT PO!\n");

        return receipt.toString();
    }

    private void updateCartTable() {
        cartTableView.setItems(cartList);
    }
}
