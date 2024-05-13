package app.persistence;

import app.entities.Invoice;
import app.entities.Material;
import app.entities.Order;
import app.entities.OrderBillItem;
import app.exceptions.DatabaseException;

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
        String sql = "SELECT order_id, account_id, order_title, order_total_price, order_timestamp FROM orders " +
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
                    int orderTotalPrice = rs.getInt("order_total_price");
                    LocalDateTime orderTimestamp = rs.getTimestamp("order_timestamp").toLocalDateTime();

                    List<OrderBillItem> orderBillItems = getOrderBillItems(orderId, connection);

                    orders.add(new Order(orderId, accountId, orderTitle, status, orderTotalPrice, orderBillItems, orderTimestamp));
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

    public static Order getOrderById(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT account_id, order_title, order_status, order_total_price, order_timestamp  FROM orders WHERE account_id=?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        ) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int accountId = rs.getInt("account_id");
                String orderTitle = rs.getString("order_title");
                Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(rs.getString("order_status"));
                int orderTotalPrice = rs.getInt("order_total_price");
                LocalDateTime orderTimestamp = rs.getTimestamp("order_timestamp").toLocalDateTime();

                return new Order(orderId, accountId, orderTitle, orderStatus, orderTotalPrice, getOrderBillItems(orderId, connection), orderTimestamp);
            } else {
                throw new DatabaseException("Can't find the specific order by its id");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Failed to connect to db");
        }
    }

    public void createOrder(Order order, ConnectionPool connectionPool) {

    }

    public void setOrderStatus(int orderId, Order.OrderStatus status, ConnectionPool connectionPool) {

    }

    public void setOrderPrice(int orderId, int price, ConnectionPool connectionPool) {

    }

    public void createOrderInvoice(int orderId, Invoice invoice, ConnectionPool connectionPool) {

    }

    public Invoice getOrderInvoice(int orderId, ConnectionPool connectionPool) {
        return null;
    }
}
