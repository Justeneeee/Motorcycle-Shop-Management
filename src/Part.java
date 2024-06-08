import java.util.Objects;

public class Part {
    private String brand;
    private String type;
    private int quantity;
    private double price;

    public Part(String brand, String type, int quantity, double price) {
        this.brand = brand;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Part otherPart = (Part) obj;
        return Objects.equals(brand, otherPart.brand) &&
                Objects.equals(type, otherPart.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(brand, type);
    }

}
