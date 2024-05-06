package app.entities;

public class OrderBillItem {
    Material material;
    String description;
    int quantity;

    public OrderBillItem(Material material, String description, int quantity) {
        this.material = material;
        this.description = description;
        this.quantity = quantity;
    }

    public Material getMaterial() {
        return material;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "OrderBillItem{" +
                "material=" + material +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
