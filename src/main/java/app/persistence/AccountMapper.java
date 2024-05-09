package app.persistence;

import app.entities.Account;
import app.entities.Customer;
import app.entities.Seller;
import app.exceptions.DatabaseException;

import java.sql.*;

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
                    return getAccount(id, email, password, rs);
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

    public static void createAccount(Account account, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO accounts (account_email, account_password, account_role, account_first_name, account_last_name, account_address, account_zip, account_phone_number) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, account.getEmail());
                ps.setString(2, account.getPassword());
                ps.setString(3, account.getRole());

                if (account instanceof Customer customer) {
                    ps.setString(4, customer.getFirstName());
                    ps.setString(5, customer.getLastName());
                    ps.setString(6, customer.getAddress());
                    ps.setInt(7, customer.getZip());
                    ps.setString(8, customer.getPhoneNumber());
                } else {
                    ps.setNull(4, Types.VARCHAR);
                    ps.setNull(5, Types.VARCHAR);
                    ps.setNull(6, Types.VARCHAR);
                    ps.setNull(7, Types.INTEGER);
                    ps.setNull(8, Types.VARCHAR);
                }

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 1) {
                    ResultSet rs = ps.getGeneratedKeys();
                    rs.next();
                    int newAccountId = rs.getInt(1);

                    account.setId(newAccountId);
                } else {
                    throw new DatabaseException("Error could not insert new account");
                }
            } catch (SQLException e) {
                throw new DatabaseException("SQL Error", e.getMessage());
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to connect to the database.");
        }
    }

    public static Account getAccountById(int accountId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT account_email, account_password, account_role, account_first_name, account_last_name, account_address, account_zip, city, account_phone_number " +
                "FROM accounts LEFT JOIN postal_codes ON account_zip = zip " +
                "WHERE account_id = ?";

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, accountId);

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String email = rs.getString("account_email");
                    String password = rs.getString("account_password");

                    return getAccount(accountId, email, password, rs);
                } else {
                    throw new DatabaseException("Failed to get account with id = " + accountId);
                }
            } catch (SQLException e) {
                throw new DatabaseException("SQL Error", e.getMessage());
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to connect to the database.");
        }
    }

    private static Account getAccount(int accountId, String email, String password, ResultSet rs) throws SQLException {
        String role = rs.getString("account_role");

        if (role.equals("CUSTOMER")) {
            String firstName = rs.getString("account_first_name");
            String lastName = rs.getString("account_last_name");
            String address = rs.getString("account_address");
            int zip = rs.getInt("account_zip");
            String city = rs.getString("city");
            String phoneNumber = rs.getString("account_phone_number");

            return new Customer(accountId, email, password, role, firstName, lastName, address, zip, city, phoneNumber);
        } else {
            return new Seller(accountId, email, password, role);
        }
    }
}
