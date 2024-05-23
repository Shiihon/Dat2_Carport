package app.persistence;

import app.entities.PostalCode;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostalCodeMapper {

    public static PostalCode getPostalCodeByZip(int zip, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT zip, city FROM postal_codes WHERE zip = ?";

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, zip);

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String city = rs.getString("city");

                    return new PostalCode(zip, city);
                } else {
                    throw new DatabaseException("Failed to get postal code with zip = " + zip);
                }
            } catch (SQLException e) {
                throw new DatabaseException("Failed to get postal code by zip", e.getMessage());
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to connect to the database.");
        }
    }
}
