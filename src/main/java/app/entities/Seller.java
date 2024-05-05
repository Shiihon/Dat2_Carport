package app.entities;

public class Seller implements Account {
    int salespersonId;
    String email;
    String password;

    public Seller(int salespersonId, String email, String password) {
        this.salespersonId = salespersonId;
        this.email = email;
        this.password = password;
    }

    @Override
    public int getId() {
        return salespersonId;
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
                "salespersonId=" + salespersonId +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
