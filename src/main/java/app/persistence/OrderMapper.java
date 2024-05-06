package app.persistence;

import app.entities.Invoice;
import app.entities.Order;

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

    public void createOrder(Order order, ConnectionPool connectionPool){

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
