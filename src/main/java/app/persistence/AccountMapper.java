package app.persistence;

import app.entities.Account;
import app.entities.Customer;
import app.entities.Seller;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountMapper {

    public static Account login(String email, String password, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT account_id, account_role, account_first_name, account_last_name, account_address, account_zip, city, account_phone_number " +
                "FROM accounts LEFT JOIN postal_codes ON account_zip = zip " +
                "WHERE account_email = ? AND account_password = ?";

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, email);
                ps.setString(2, password);

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int id = rs.getInt("account_id");
                    String role = rs.getString("account_role");
                    String firstName = rs.getString("account_first_name");
                    String lastName = rs.getString("account_last_name");
                    String address = rs.getString("account_address");
                    int zip = rs.getInt("account_zip");
                    String city = rs.getString("city");
                    String phoneNumber = rs.getString("account_phone_number");

                    if (role.equals("SELLER")) {
                        return new Seller(id, email, password, role);
                    } else {
                        return new Customer(id, email, password, role, firstName, lastName, address, zip, city, phoneNumber);
                    }
                } else {
                    throw new DatabaseException("Login failed. Try again.");
                }
            } catch (SQLException e) {
                throw new DatabaseException("SQL Error", e.getMessage());
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to connect to the database.");
        }
    }

    public void createAccount(String email, String password) {

    }
}
