package app.persistence;

import app.entities.Invoice;
import app.entities.Material;
import app.entities.Order;
import app.entities.OrderBillItem;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderMapper {

    public List<Order> getAllOrders(ConnectionPool connectionPool) {
        return null;
    }

    public static List<Order> getAllOrdersByStatus(Order.OrderStatus status, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT order_id, account_id, order_title, carport_width, carport_length, order_total_price, order_timestamp FROM orders " +
                "WHERE order_status = ?";
        List<Order> orders = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, status.name());

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int orderId = rs.getInt("order_id");
                    int accountId = rs.getInt("account_id");
                    String orderTitle = rs.getString("order_title");
                    int carportWidth = rs.getInt("carport_width");
                    int carportLength = rs.getInt("carport_length");
                    int orderTotalPrice = rs.getInt("order_total_price");
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

    public List<Order> getAllCustomerOrders(int customerId, ConnectionPool connectionPool) {
        return null;
    }

    public static void createOrder(Order order, int customerId, ConnectionPool connectionPool) throws DatabaseException {

        String sql = "INSERT INTO orders (account_id, order_title, order_status, order_total_price, order_timestamp) VALUES (?,?,?,?,?) RETURNING order_id;";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            ps.setInt(1, customerId);
            ps.setString(2, order.getTitle());
            ps.setString(3, order.getStatus().toString());
            ps.setInt(4, order.getTotalPrice());  //tjek at totalprisen er sat til at kunne være null
            ps.setTimestamp(5, Timestamp.valueOf(order.getTimestamp()));

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("failed to create order");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    order.setOrderId(generatedKeys.getInt(1));
                    createOrderBill(connectionPool, order.getOrderId(), order.getOrderBill());
                } else {
                    throw new DatabaseException("failed to create order");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    private static void createOrderBill(ConnectionPool connectionPool, int orderId, List<OrderBillItem> orderBillItems) throws DatabaseException {
        String sql = "INSERT INTO order_bills (order_id, material_variant_id, item_description, item_quantity) VALUES (?, ?, ?, ?);";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            for (OrderBillItem item : orderBillItems) {
                ps.setInt(1, orderId);
                ps.setInt(2, item.getMaterial().getMaterialId());
                ps.setString(3, item.getDescription());
                ps.setInt(4, item.getQuantity());
                ps.executeUpdate();
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

    public void setOrderPrice(int orderId, int price, ConnectionPool connectionPool) {

    }

    public void createOrderInvoice(int orderId, Invoice invoice, ConnectionPool connectionPool) {

    }

    public Invoice getOrderInvoice(int orderId, ConnectionPool connectionPool) {
        return null;
    }
}
