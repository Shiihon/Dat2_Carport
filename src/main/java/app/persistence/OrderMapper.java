package app.persistence;

import app.entities.Invoice;
import app.entities.Material;
import app.entities.Order;
import app.entities.OrderBillItem;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    public List<Order> getAllOrders(ConnectionPool connectionPool) {
        return null;
    }

    public static List<Order> getAllOrdersByStatus(Order.OrderStatus status, ConnectionPool connectionPool) throws DatabaseException {
        return getAllOrdersByStatus(List.of(status), connectionPool);
    }

    public static List<Order> getAllOrdersByStatus(List<Order.OrderStatus> statusList, ConnectionPool connectionPool) throws DatabaseException {
        StringBuilder sql = new StringBuilder("SELECT order_id, account_id, order_title, carport_width, carport_length, order_status, order_total_price, order_timestamp FROM orders");

        sql.append(" WHERE order_status IN (");
        sql.append(statusList.stream().map(status -> String.format("'%s'", status.toString())).collect(Collectors.joining(", ")));
        sql.append(") ORDER BY order_timestamp");
        List<Order> orders = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int orderId = rs.getInt("order_id");
                    int accountId = rs.getInt("account_id");
                    String orderTitle = rs.getString("order_title");
                    int carportWidth = rs.getInt("carport_width");
                    int carportLength = rs.getInt("carport_length");
                    Order.OrderStatus status = Order.OrderStatus.valueOf(rs.getString("order_status"));
                    double orderTotalPrice = rs.getDouble("order_total_price");
                    LocalDateTime orderTimestamp = rs.getTimestamp("order_timestamp").toLocalDateTime();

                    List<OrderBillItem> orderBillItems = getOrderBillItems(orderId, connection);

                    orders.add(new Order(orderId, accountId, orderTitle, carportWidth, carportLength, status, orderTotalPrice, orderBillItems, orderTimestamp));
                }
            } catch (SQLException e) {
                throw new DatabaseException("SQL Error", e.getMessage());
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to connect to the database.");
        }

        return orders;
    }

    private static List<OrderBillItem> getOrderBillItems(int orderId, Connection connection) throws DatabaseException {
        String sql = "SELECT material_variant_id, item_description, item_quantity, length, material_id, material_description, material_unit, material_price, material_type FROM order_bills " +
                "INNER JOIN material_variants USING (material_variant_id) " +
                "INNER JOIN materials USING (material_id) " +
                "WHERE order_id = ?";
        List<OrderBillItem> items = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String itemDescription = rs.getString("item_description");
                int itemQuantity = rs.getInt("item_quantity");

                int materialVariantId = rs.getInt("material_variant_id");
                int length = rs.getInt("length");

                int materialId = rs.getInt("material_id");
                String materialDescription = rs.getString("material_description");
                String materialUnit = rs.getString("material_unit");
                int materialPrice = rs.getInt("material_price");
                Material.MaterialType materialType = Material.MaterialType.valueOf(rs.getString("material_type"));

                Material material = new Material(materialId, materialVariantId, materialDescription, materialUnit, materialPrice, length, materialType);
                items.add(new OrderBillItem(material, itemDescription, itemQuantity));
            }
        } catch (SQLException e) {
            throw new DatabaseException("SQL Error", e.getMessage());
        }

        return items;
    }

    public static List<Order> getAllCustomerOrders(int customerId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT order_id, order_title, carport_width, carport_length, order_status, order_total_price, order_timestamp  FROM orders WHERE account_id=?";
        List<Order> myorders = new ArrayList<>();

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        ) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                String orderTitle = rs.getString("order_title");
                int carportWidth = rs.getInt("carport_width");
                int carportLength = rs.getInt("carport_length");
                Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(rs.getString("order_status"));
                double orderTotalPrice = rs.getDouble("order_total_price");
                LocalDateTime orderTimestamp = rs.getTimestamp("order_timestamp").toLocalDateTime();

                List<OrderBillItem> orderBillItems = getOrderBillItems(orderId, connection);

                myorders.add(new Order(orderId, customerId, orderTitle, carportWidth, carportLength, orderStatus, orderTotalPrice, orderBillItems, orderTimestamp));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to connect to db");
        }

        return myorders;
    }

    public static void createOrder(Order order, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO orders (account_id, order_title, carport_width, carport_length, order_status, order_total_price, order_timestamp) VALUES (?,?,?,?,?,?,?) RETURNING order_id;";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            ps.setInt(1, order.getAccountId());
            ps.setString(2, order.getTitle());
            ps.setInt(3, order.getCarportWidth());
            ps.setInt(4, order.getCarportLength());
            ps.setString(5, order.getStatus().toString());
            ps.setDouble(6, order.getTotalPrice());  //tjek at totalprisen er sat til at kunne være null
            ps.setTimestamp(7, Timestamp.valueOf(order.getTimestamp()));

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("failed to create order");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    order.setOrderId(generatedKeys.getInt(1));
                    createOrderBill(order.getOrderId(), order.getOrderBill(), connectionPool);
                } else {
                    throw new DatabaseException("failed to create order");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    private static void createOrderBill(int orderId, List<OrderBillItem> orderBillItems, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO order_bills (order_id, material_variant_id, item_description, item_quantity) VALUES (?, ?, ?, ?);";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            for (OrderBillItem item : orderBillItems) {
                ps.setInt(1, orderId);
                ps.setInt(2, item.getMaterial().getMaterialVariantId());
                ps.setString(3, item.getDescription());
                ps.setInt(4, item.getQuantity());

                int rowsAffected = ps.executeUpdate();

                if (rowsAffected == 0) {
                    throw new DatabaseException("Failed insert order bill for order = " + orderId);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public static void setOrderStatus(int orderId, Order.OrderStatus status, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE orders SET order_status = ? WHERE order_id = ?";

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, status.name());
                ps.setInt(2, orderId);

                int rowsAffected = ps.executeUpdate();

                if (rowsAffected == 0) {
                    throw new DatabaseException("Failed to set order status of order = " + orderId);
                }
            } catch (SQLException e) {
                throw new DatabaseException("SQL Error", e.getMessage());
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to connect to the database.");
        }
    }

    public static void setOrderPrice(int orderId, double newPrice, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE orders SET order_total_price = ? WHERE order_id = ?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        ) {
            ps.setDouble(1, newPrice);
            ps.setInt(2, orderId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Failed to update new price for order ID: " + orderId);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error updating new price", e.getMessage());
        }
    }

    public static Order getOrderById(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT account_id, order_title, carport_width, carport_length, order_status, order_total_price, order_timestamp  FROM orders WHERE order_id=?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        ) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int accountId = rs.getInt("account_id");
                String orderTitle = rs.getString("order_title");
                int carportWidth = rs.getInt("carport_width");
                int carportLength = rs.getInt("carport_length");
                Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(rs.getString("order_status"));
                double orderTotalPrice = rs.getDouble("order_total_price");
                LocalDateTime orderTimestamp = rs.getTimestamp("order_timestamp").toLocalDateTime();

                return new Order(orderId, accountId, orderTitle, carportWidth, carportLength, orderStatus, orderTotalPrice, getOrderBillItems(orderId, connection), orderTimestamp);
            } else {
                throw new DatabaseException("Can't find the specific order by its id");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Failed to connect to db");
        }
    }

    public static void createOrderInvoice(Invoice invoice, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO invoices (order_id, account_id, invoice_date) VALUES (?, ?, ?)";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        ) {
            ps.setInt(1, invoice.getOrderId());
            ps.setInt(2, invoice.getAccountId());
            ps.setTimestamp(3, Timestamp.valueOf(invoice.getDate().atStartOfDay()));
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                throw new DatabaseException("Error Invoice could not be created");
            }

        } catch (SQLException e) {
            throw new DatabaseException("SQL Error: " + e.getMessage());
        }
    }

    public static Invoice getOrderInvoice(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT invoice_id, account_id, invoice_date FROM invoices WHERE order_id=?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        ) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int invoiceId = rs.getInt("invoice_id");
                int accountId = rs.getInt("account_id");
                LocalDate invoiceDate = rs.getTimestamp("invoice_date").toLocalDateTime().toLocalDate();

                return new Invoice(invoiceId, orderId, accountId, invoiceDate);
            } else {
                throw new DatabaseException("Can't find the specific invoice by its id");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Failed to connect to db");
        }
    }

    public static void removeOrder(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "DELETE FROM orders WHERE order_id = ?";

        removeOrderBills(orderId, connectionPool);

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setInt(1, orderId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Error trying to removing order");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error in removing order", e.getMessage());
        }
    }

    public static void removeOrderBills(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "DELETE FROM order_bills WHERE order_id = ?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setInt(1, orderId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Error trying to removing order");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error in removing order", e.getMessage());
        }
    }
}
