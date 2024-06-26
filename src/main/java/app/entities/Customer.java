package app.entities;

import java.util.Objects;

public class Customer implements Account {
    private int customerId;
    private String email;
    private String password;
    private String role;
    private String firstName;
    private String lastName;
    private String address;
    private int zip;
    private String city;
    private String phoneNumber;

    public Customer(int customerId, String email, String password, String role, String firstName, String lastName, String address, int zip, String city, String phoneNumber) {
        this.customerId = customerId;
        this.email = email;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.zip = zip;
        this.city = city;
        this.phoneNumber = phoneNumber;
    }

    public Customer(String email, String password, String role, String firstName, String lastName, String address, int zip, String city, String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.zip = zip;
        this.address = address;
        this.lastName = lastName;
        this.firstName = firstName;
        this.role = role;
        this.password = password;
        this.email = email;
    }

    @Override
    public int getId() {
        return customerId;
    }

    @Override
    public void setId(int id) {
        this.customerId = id;
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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public int getZip() {
        return zip;
    }

    public String getCity() {
        return city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer customer)) return false;
        return customerId == customer.customerId && getZip() == customer.getZip() && Objects.equals(getEmail(), customer.getEmail()) && Objects.equals(getPassword(), customer.getPassword()) && Objects.equals(getRole(), customer.getRole()) && Objects.equals(getFirstName(), customer.getFirstName()) && Objects.equals(getLastName(), customer.getLastName()) && Objects.equals(getAddress(), customer.getAddress()) && Objects.equals(getCity(), customer.getCity()) && Objects.equals(getPhoneNumber(), customer.getPhoneNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, getEmail(), getPassword(), getRole(), getFirstName(), getLastName(), getAddress(), getZip(), getCity(), getPhoneNumber());
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address='" + address + '\'' +
                ", zip=" + zip +
                ", city='" + city + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
