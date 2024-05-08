package app.persistence;

import app.entities.Material;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaterialMapper {

    public static Material getMaterial(String description, ConnectionPool connectionPool) {
        return null;
    }

    public static List<Material> getAllMaterials(String description, ConnectionPool connectionPool) {
        return null;
    }

    public static List<Material> getAllMaterials(ConnectionPool connectionPool) {

        List<Material> materialList = new ArrayList<>();
        String sql = "SELECT material_id, material_description, material_unit, material_price, material_type, length, material_variant_id " +
                "FROM materials " +
                "INNER JOIN material_variants " +
                "USING (material_id)";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        ) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int materialId = rs.getInt("material_id");
                int materialVariantId = rs.getInt("material_variant_id");
                String description = rs.getString("material_description");
                String unit = rs.getString("material_unit");
                int price = rs.getInt("material_price");
                int length = rs.getInt("length");
                String materialType = rs.getString("material_type");
                materialList.add(new Material(materialId, materialVariantId, description, unit, price, length, Material.MaterialType.valueOf(materialType)));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e); // for connectionpool.
        }
        return materialList;
    }

    public static void createMaterial(Material material, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO materials (material_description, material_unit, material_price) VALUES (?, ?, ?)";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, material.getDescription());
            ps.setString(2, material.getUnit());
            ps.setInt(3, material.getPrice());
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 1) {
                ResultSet rs = ps.getGeneratedKeys();
                rs.next();
                int newMaterialId = rs.getInt(1);

                material.setMaterialId(newMaterialId);
                createMaterialVariant(material, connectionPool);

            } else {
                throw new DatabaseException("Error could not insert new material");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error in DB connection", e.getMessage());
        }
    }

    public static Material getMaterialById(int materialVariantId, ConnectionPool connectionPool) {
        return null;
    }

    private static void createMaterialVariant(Material material, ConnectionPool connectionPool) throws
            DatabaseException {
        String sql = "INSERT INTO material_variants (material_id, length)VALUES( ?, ?)";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        ) {
            ps.setInt(1, material.getMaterialId());
            ps.setInt(2, material.getLength());
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 1) {
                throw new DatabaseException("Error could not insert new material variant");
            } else {
                throw new DatabaseException("Error material variant could not be inserted");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error in DB connection", e.getMessage());
        }
    }
}
