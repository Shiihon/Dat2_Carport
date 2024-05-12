package app.persistence;

import app.entities.Material;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaterialMapper {

    public static Material getMaterial(String description, int length, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT material_id, material_description, material_unit, material_price, material_type, material_variant_id, length " +
                "FROM materials INNER JOIN material_variants USING (material_id) " +
                "WHERE material_description = ? AND length = ?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        ) {
            ps.setString(1, description);
            ps.setInt(2, length);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return getMaterialFromResultSet(rs);
            } else {
                throw new DatabaseException(String.format("Error: Could not find any materials with the description '%s'", description));
            }
        } catch (SQLException e) {
            throw new DatabaseException("DB Error: " + e.getMessage()); // for connectionpool.
        }
    }

    public static List<Material> getAllMaterials(String description, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT material_id, material_description, material_unit, material_price, material_type, material_variant_id, length " +
                "FROM materials INNER JOIN material_variants USING (material_id) " +
                "WHERE material_description = ?";
        List<Material> materials = new ArrayList<>();

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        ) {
            ps.setString(1, description);

            getAllMaterialsFromResultSet(materials, ps);
        } catch (SQLException e) {
            throw new DatabaseException("DB Error: " + e.getMessage()); // for connectionpool.
        }

        return materials;
    }

    public static List<Material> getAllMaterials(ConnectionPool connectionPool) throws DatabaseException {

        List<Material> materialList = new ArrayList<>();
        String sql = "SELECT material_id, material_description, material_unit, material_price, material_type, length, material_variant_id " +
                "FROM materials " +
                "INNER JOIN material_variants " +
                "USING (material_id)";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        ) {
            getAllMaterialsFromResultSet(materialList, ps);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to connect to the database.");
        }
        return materialList;
    }

    public static void createMaterial(Material material, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO materials (material_description, material_unit, material_price, material_type) VALUES (?, ?, ?, ?)";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, material.getDescription());
            ps.setString(2, material.getUnit());
            ps.setInt(3, material.getPrice());
            ps.setString(4, material.getMaterialType().toString());
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

            if (rowsAffected == 0) {
                throw new DatabaseException("Error material variant could not be inserted");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error in DB connection", e.getMessage());
        }
    }

    private static void getAllMaterialsFromResultSet(List<Material> materialList, PreparedStatement ps) throws SQLException {
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            materialList.add(getMaterialFromResultSet(rs));
        }
    }

    private static Material getMaterialFromResultSet(ResultSet rs) throws SQLException {
        int materialId = rs.getInt("material_id");
        int materialVariantId = rs.getInt("material_variant_id");
        String materialDescription = rs.getString("material_description");
        String unit = rs.getString("material_unit");
        int price = rs.getInt("material_price");
        int materialVariantLength = rs.getInt("length");
        String materialType = rs.getString("material_type");

        return new Material(materialId, materialVariantId, materialDescription, unit, price, materialVariantLength, Material.MaterialType.valueOf(materialType));
    }
}
