package app.entities;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    int orderId;
    String title;
    orderStatus status;
    Integer totalPrice;
    List<OrderBillItem> orderBill;
    LocalDateTime timestamp;

    public enum orderStatus {
        WAITING_FOR_REVIEW,
        REVIEW_APPROVED,
        PAID
    }

    public Order(int orderId, String title, orderStatus status, Integer totalPrice, List<OrderBillItem> orderBill, LocalDateTime timestamp) {
        this.orderId = orderId;
        this.title = title;
        this.status = status;
        this.totalPrice = totalPrice;
        this.orderBill = orderBill;
        this.timestamp = timestamp;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getTitle() {
        return title;
    }

    public orderStatus getStatus() {
        return status;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public List<OrderBillItem> getOrderBill() {
        return orderBill;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", totalPrice=" + totalPrice +
                ", orderBill=" + orderBill +
                ", timestamp=" + timestamp +
                '}';
    }
}
