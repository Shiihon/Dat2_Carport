package app.persistence;

import app.entities.Material;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.util.List;

public class MaterialMapper {
    public Material getMaterial(String description, ConnectionPool connectionPool) {
        return null;
    }

    public List<Material> getAllMaterials(String description, ConnectionPool connectionPool) {
        return null;
    }

    public void createMaterial(Material material, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO materials (material_id, material_description, material_unit, material_price) VALUES (?, ?, ?, ?)";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setInt(1, material.getMaterialId());
            ps.setString(2, material.getDescription());
            ps.setString(3, material.getUnit());
            ps.setInt(4, material.getPrice());
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

    public Material getMaterialById(int materialVariantId, ConnectionPool connectionPool) {
        return null;
    }

    private static void createMaterialVariant(Material material, ConnectionPool connectionPool) throws
            DatabaseException {
        String sql = "INSERT INTO material_variants (material_variant_id, material_id, length)VALUES( ?, ?, ?)";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        ) {
            ps.setInt(1, material.getMaterialVariantId());
            ps.setInt(2, material.getMaterialId());
            ps.setInt(3, material.getLength());
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 1) {
                throw new DatabaseException("Error could not insert new material variant");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error in DB connection", e.getMessage());
        }
    }
}
