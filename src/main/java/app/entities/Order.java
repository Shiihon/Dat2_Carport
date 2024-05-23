package app.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class Order {

    public enum OrderStatus {
        WAITING_FOR_REVIEW,
        REVIEW_APPROVED,
        PAID
    }

    private int orderId;
    private int accountId;
    private String title;
    private String comment;
    private int carportWidth;
    private int carportLength;
    private OrderStatus status;
    private double totalPrice;
    private List<OrderBillItem> orderBill;
    private LocalDateTime timestamp;

    public Order(int orderId, int accountId, String title, String comment, int carportWidth, int carportLength, OrderStatus status, double totalPrice, List<OrderBillItem> orderBill, LocalDateTime timestamp) {
        this.orderId = orderId;
        this.accountId = accountId;
        this.title = title;
        this.comment = comment;
        this.carportWidth = carportWidth;
        this.carportLength = carportLength;
        this.status = status;
        this.totalPrice = totalPrice;
        this.orderBill = orderBill;
        this.timestamp = timestamp;
    }

    public Order(int accountId, String title, String comment, int carportWidth, int carportLength, OrderStatus status, double totalPrice, List<OrderBillItem> orderBill, LocalDateTime timestamp) {
        this.accountId = accountId;
        this.title = title;
        this.comment = comment;
        this.carportWidth = carportWidth;
        this.carportLength = carportLength;
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

    public String getComment() {
        return comment;
    }

    public int getCarportWidth() {
        return carportWidth;
    }

    public int getCarportLength() {
        return carportLength;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double newPrice) {
        totalPrice = newPrice;
    }

    public List<OrderBillItem> getOrderBill() {
        return orderBill;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;
        return getOrderId() == order.getOrderId() && getAccountId() == order.getAccountId() && getCarportWidth() == order.getCarportWidth() && getCarportLength() == order.getCarportLength() && Double.compare(getTotalPrice(), order.getTotalPrice()) == 0 && Objects.equals(getTitle(), order.getTitle()) && Objects.equals(getComment(), order.getComment()) && getStatus() == order.getStatus() && new HashSet<>(getOrderBill()).containsAll(order.getOrderBill()) && Objects.equals(getTimestamp(), order.getTimestamp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrderId(), getAccountId(), getTitle(), getComment(), getCarportWidth(), getCarportLength(), getStatus(), getTotalPrice(), getOrderBill(), getTimestamp());
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", accountId=" + accountId +
                ", title='" + title + '\'' +
                ", comment='" + comment + '\'' +
                ", carportWidth=" + carportWidth +
                ", carportLength=" + carportLength +
                ", status=" + status +
                ", totalPrice=" + totalPrice +
                ", orderBill=" + orderBill +
                ", timestamp=" + timestamp +
                '}';
    }
}