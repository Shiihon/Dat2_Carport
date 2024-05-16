package app.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Invoice {
    private int invoiceId;
    private int orderId;
    private int accountId;
    private LocalDate date;

    public Invoice(int invoiceId, int orderId, int accountId, LocalDate date) {
        this.invoiceId = invoiceId;
        this.orderId = orderId;
        this.accountId = accountId;
        this.date = date;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getAccountId() {
        return accountId;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceId=" + invoiceId +
                ", orderId=" + orderId +
                ", accountId=" + accountId +
                ", date=" + date +
                '}';
    }
}
