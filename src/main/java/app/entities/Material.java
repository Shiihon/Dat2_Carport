package app.entities;

import java.util.Objects;

public class Material {

    public enum MaterialType {
        WOOD,
        SCREWS
    }

    int materialId;
    int materialVariantId;
    String description;
    String unit;
    int price;
    Integer length;
    MaterialType materialType;

    public Material(int materialId, int materialVariantId, String description, String unit, int price, Integer length, MaterialType materialType) {
        this.materialId = materialId;
        this.materialVariantId = materialVariantId;
        this.description = description;
        this.unit = unit;
        this.price = price;
        this.length = length;
        this.materialType = materialType;
    }

    public Material(String description, String unit, int price, Integer length, MaterialType materialType) {
        this.description = description;
        this.unit = unit;
        this.price = price;
        this.length = length;
        this.materialType = materialType;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public int getMaterialVariantId() {
        return materialVariantId;
    }

    public void setMaterialVariantId(int materialVariantId) {
        this.materialVariantId = materialVariantId;
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

    public MaterialType getMaterialType() {
        return materialType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Material material)) return false;
        return getMaterialId() == material.getMaterialId() && getMaterialVariantId() == material.getMaterialVariantId() && getPrice() == material.getPrice() && Objects.equals(getDescription(), material.getDescription()) && Objects.equals(getUnit(), material.getUnit()) && Objects.equals(getLength(), material.getLength()) && getMaterialType() == material.getMaterialType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMaterialId(), getMaterialVariantId(), getDescription(), getUnit(), getPrice(), getLength(), getMaterialType());
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
