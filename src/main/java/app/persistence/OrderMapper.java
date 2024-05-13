package app.persistence;

import app.entities.Invoice;
import app.entities.Order;
import app.entities.OrderBillItem;

import java.sql.*;
import java.util.List;

public class OrderMapper {

    public List<Order> getAllOrders(ConnectionPool connectionPool){
        return null;
    }

    public List<Order> getAllOrdersByStatus (Order.OrderStatus status, ConnectionPool connectionPool){
        return null;
    }

    public List<Order> getAllCustomerOrders(int customerId, ConnectionPool connectionPool){
        return null;
    }

    public static void createOrder(Order order, int customerId, ConnectionPool connectionPool) throws SQLException {

        String sql = "INSERT INTO orders (account_id, order_title, order_status, order_total_price, order_timestamp) VALUES (?,?,?,?,?) RETURNING order_id;";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ){
            ps.setInt(1, customerId);
            ps.setString(2, order.getTitle());
            ps.setString(3, order.getStatus().toString());
            ps.setInt(4, order.getTotalPrice());  // tjek at totalprisen er sat til at kunne være null
            ps.setTimestamp(5, Timestamp.valueOf(order.getTimestamp()));

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("failed to create order");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    order.setOrderId(generatedKeys.getInt(1));
                    createOrderBill(connection, order.getOrderId(), order.getOrderBill());
                } else {
                    throw new SQLException("failed to create order");
                }
            }
        }
    }

    private static void createOrderBill(Connection conn, int orderId, List<OrderBillItem> orderBillItems) throws SQLException {
        String sql = "INSERT INTO order_bills (order_id, material_variant_id, item_description, item_quantity) VALUES (?, ?, ?, ?);";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (OrderBillItem item : orderBillItems) {
                ps.setInt(1, orderId);
                ps.setInt(2, item.getMaterial().getMaterialId());
                ps.setString(3, item.getDescription());
                ps.setInt(4, item.getQuantity());
                ps.executeUpdate();
            }
        }
    }

    public void setOrderStatus(int orderId, Order.OrderStatus status, ConnectionPool connectionPool){

    }

    public void setOrderPrice(int orderId, int price, ConnectionPool connectionPool){

    }

    public void createOrderInvoice(int orderId, Invoice invoice, ConnectionPool connectionPool){

    }

    public Invoice getOrderInvoice(int orderId, ConnectionPool connectionPool){
        return null;
    }
}
