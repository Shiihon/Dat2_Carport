package app.persistence;

import app.entities.Account;
import app.entities.Customer;
import app.entities.Seller;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AccountMapperTest {

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "cupcake_development";

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @BeforeAll
    public static void setupTables() throws DatabaseException {
        try (Connection connection = connectionPool.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                // Create test schema if it does not exist
                stmt.execute("CREATE SCHEMA IF NOT EXISTS test");

                // Set the test schema to be selected
                stmt.execute("SET search_path TO test");

                // Drop existing test tables
                stmt.execute("DROP TABLE IF EXISTS test.postal_codes CASCADE");
                stmt.execute("DROP TABLE IF EXISTS test.accounts CASCADE");

                // Drop existing sequences
                stmt.execute("DROP SEQUENCE IF EXISTS test.accounts_account_id_seq CASCADE;");

                // Create tables from the public schema
                stmt.execute("CREATE TABLE test.postal_codes AS (SELECT * from public.postal_codes) WITH NO DATA");
                stmt.execute("CREATE TABLE test.accounts AS (SELECT * from public.accounts) WITH NO DATA");

                // Create table sequences
                stmt.execute("CREATE SEQUENCE test.accounts_account_id_seq");
                stmt.execute("ALTER TABLE test.accounts ALTER COLUMN account_id SET DEFAULT nextval('test.accounts_account_id_seq')");
            } catch (SQLException e) {
                throw new DatabaseException("SQL Error", e.getMessage());
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to connect to the database.");
        }
    }

    private final Map<Integer, String> expectedPostalCodes = new HashMap<>();
    private final List<Account> expectedAccounts = new ArrayList<>();

    @BeforeEach
    public void setupData() throws DatabaseException {
        expectedPostalCodes.clear();
        expectedAccounts.clear();

        // Create postal codes
        expectedPostalCodes.put(2700, "Brønshøj");
        expectedPostalCodes.put(2200, "København N");
        expectedPostalCodes.put(2450, "København SV");
        expectedPostalCodes.put(3460, "Birkerød");

        // Create accounts
        expectedAccounts.add(new Customer(
                1,
                "foo@gmail.com",
                "1234",
                "CUSTOMER",
                "Foo",
                "Bar",
                "Ruten 6",
                2700,
                "Brønshøj",
                "68 68 55 78"
        ));
        expectedAccounts.add(new Seller(
                2,
                "paul@outlook.com",
                "1234",
                "SELLER"
        ));
        expectedAccounts.add(new Customer(
                3,
                "adam@gmail.dk",
                "1234",
                "CUSTOMER",
                "Adam",
                "Sørensen",
                "Molestien 11",
                2450,
                "København SV",
                "06 25 01 79"
        ));

        try (Connection connection = connectionPool.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                // Drop existing test tables
                stmt.execute("DELETE FROM test.postal_codes CASCADE");
                stmt.execute("DELETE FROM test.accounts CASCADE");

                // Insert postal codes
                String postalCodeSql = "INSERT INTO test.postal_codes (zip, city)  VALUES" + expectedPostalCodes.entrySet().stream()
                        .map(postalCodeEntry -> String.format(
                                "(%d, '%s')",
                                postalCodeEntry.getKey(),
                                postalCodeEntry.getValue()))
                        .collect(Collectors.joining(", "));

                stmt.execute(postalCodeSql);

                // Insert accounts
                stmt.execute("SELECT setval('accounts_account_id_seq', 1)");

                String accountSql = "INSERT INTO test.accounts (account_id, account_email,account_password, account_role, account_first_name, account_last_name, account_address, account_zip, account_phone_number) VALUES" + expectedAccounts.stream()
                        .map(account -> {
                            if (account instanceof Customer customer) {
                                return String.format(
                                        "(%d, '%s', '%s', '%s', '%s', '%s', '%s', %d, '%s')",
                                        customer.getId(),
                                        customer.getEmail(),
                                        customer.getPassword(),
                                        customer.getRole(),
                                        customer.getFirstName(),
                                        customer.getLastName(),
                                        customer.getAddress(),
                                        customer.getZip(),
                                        customer.getPhoneNumber());
                            } else {
                                return String.format(
                                        "(%d, '%s', '%s', '%s', %s, %s, %s, %s, %s)",
                                        account.getId(),
                                        account.getEmail(),
                                        account.getPassword(),
                                        account.getRole(),
                                        null, null, null, null, null);
                            }
                        }).collect(Collectors.joining(", "));

                stmt.execute(accountSql);
                stmt.execute("SELECT setval('accounts_account_id_seq', COALESCE((SELECT MAX(account_id)+1 FROM accounts), 1), false)");
            } catch (SQLException e) {
                throw new DatabaseException("SQL Error", e.getMessage());
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to connect to the database.");
        }
    }

    @Test
    public void loginCustomerTest() throws DatabaseException {
        Account expectedAccountCustomer = expectedAccounts.get(0);
        Account actualAccountCustomer = AccountMapper.login(expectedAccountCustomer.getEmail(), expectedAccountCustomer.getPassword(), connectionPool);

        Assertions.assertEquals(expectedAccountCustomer, actualAccountCustomer);
    }

    @Test
    public void loginSellerTest() throws DatabaseException {
        Account expectedAccountSeller = expectedAccounts.get(1);
        Account actualAccountSeller = AccountMapper.login(expectedAccountSeller.getEmail(), expectedAccountSeller.getPassword(), connectionPool);

        Assertions.assertEquals(expectedAccountSeller, actualAccountSeller);
    }

    @Test
    public void createAccountCustomerTest() throws DatabaseException {
        Account expectedAccountCustomer = new Customer(
                expectedAccounts.size() + 1,
                "bob@gmail.com",
                "1234",
                "CUSTOMER",
                "Bob",
                "Smith",
                "Birkerød Kongevej 111",
                3460,
                "Birkerød",
                "18 05 54 95");

        AccountMapper.createAccount(expectedAccountCustomer, connectionPool);
        Account actualAccountCustomer = AccountMapper.getAccountById(expectedAccountCustomer.getId(), connectionPool);

        Assertions.assertEquals(expectedAccountCustomer, actualAccountCustomer);
    }

    @Test
    public void createAccountSellerTest() throws DatabaseException {
        Account expectedAccountCustomer = new Seller(
                expectedAccounts.size() + 1,
                "josh@gmail.com",
                "1234",
                "SELLER");

        AccountMapper.createAccount(expectedAccountCustomer, connectionPool);
        Account actualAccountCustomer = AccountMapper.getAccountById(expectedAccountCustomer.getId(), connectionPool);

        Assertions.assertEquals(expectedAccountCustomer, actualAccountCustomer);
    }
}
