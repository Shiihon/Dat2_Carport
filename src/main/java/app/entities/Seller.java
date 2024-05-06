package app.entities;

public class Seller implements Account {
    int sellerId;
    String email;
    String password;

    public Seller(int sellerId, String email, String password) {
        this.sellerId = sellerId;
        this.email = email;
        this.password = password;
    }

    @Override
    public int getId() {
        return sellerId;
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
    public String toString() {
        return "Seller{" +
                "sellerId=" + sellerId +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
