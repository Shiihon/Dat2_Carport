package app.entities;

import java.util.Objects;

public class Seller implements Account {
    int sellerId;
    String email;
    String password;
    String role;

    public Seller(int sellerId, String email, String password, String role) {
        this.sellerId = sellerId;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @Override
    public int getId() {
        return sellerId;
    }

    @Override
    public void setId(int id) {
        this.sellerId = id;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getRole() {
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Seller seller)) return false;
        return sellerId == seller.sellerId && Objects.equals(getEmail(), seller.getEmail()) && Objects.equals(getPassword(), seller.getPassword()) && Objects.equals(getRole(), seller.getRole());
    }

    @Override
    public int hashCode() {
        return Objects.hash(sellerId, getEmail(), getPassword(), getRole());
    }

    @Override
    public String toString() {
        return "Seller{" +
                "sellerId=" + sellerId +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
