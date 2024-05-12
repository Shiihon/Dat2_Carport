package app.entities;

import java.time.LocalDateTime;
import java.util.List;

public class Order {

    public enum OrderStatus {
        WAITING_FOR_REVIEW,
        REVIEW_APPROVED,
        PAID
    }

    int orderId;
    int accountId;
    String title;
    OrderStatus status;
    Integer totalPrice;
    List<OrderBillItem> orderBill;
    LocalDateTime timestamp;

    public Order(int orderId, int accountId, String title, OrderStatus status, Integer totalPrice, List<OrderBillItem> orderBill, LocalDateTime timestamp) {
        this.orderId = orderId;
        this.accountId = accountId;
        this.title = title;
        this.status = status;
        this.totalPrice = totalPrice;
        this.orderBill = orderBill;
        this.timestamp = timestamp;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getTitle() {
        return title;
    }

    public OrderStatus getStatus() {
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
                ", accountId=" + accountId +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", totalPrice=" + totalPrice +
                ", orderBill=" + orderBill +
                ", timestamp=" + timestamp +
                '}';
    }
}
