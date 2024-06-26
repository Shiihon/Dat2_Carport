package app.entities;

import java.util.Objects;

public class OrderBillItem {
    private Material material;
    private String description;
    private int quantity;

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

    public double getItemPrice() {
        if (material.getMaterialType() == Material.MaterialType.WOOD) {
            double lengthInMeters = material.getLength() / 100D;
            return lengthInMeters * material.getPrice();
        } else {
            return material.getPrice();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderBillItem that)) return false;
        return getQuantity() == that.getQuantity() && Objects.equals(getMaterial(), that.getMaterial()) && Objects.equals(getDescription(), that.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMaterial(), getDescription(), getQuantity());
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
