package app.entities;

public class Material {

    int materialId;
    int materialVariantId;
    String description;
    String unit;
    int price;
    Integer length;
    Type materialType; // spørg victor!

    public enum Type {
        WOOD,
        SCREWS
    }

    public Material(int materialId, int materialVariantId, String description, String unit, int price, Integer length, Type materialType) {
        this.materialId = materialId;
        this.materialVariantId = materialVariantId;
        this.description = description;
        this.unit = unit;
        this.price = price;
        this.length = length;
        this.materialType = materialType;
    }

    public int getMaterialId() {
        return materialId;
    }

    public int getMaterialVariantId() {
        return materialVariantId;
    }

    public String getDescription() {
        return description;
    }

    public String getUnit() {
        return unit;
    }

    public int getPrice() {
        return price;
    }

    public Integer getLength() {
        return length;
    }

    public Type getMaterialType() {
        return materialType;
    }

    @Override
    public String toString() {
        return "Material{" +
                "materialId=" + materialId +
                ", materialVariantId=" + materialVariantId +
                ", description='" + description + '\'' +
                ", unit='" + unit + '\'' +
                ", price=" + price +
                ", length=" + length +
                ", materialType=" + materialType +
                '}';
    }
}
