package app.entities;

import java.util.Objects;

public class PostalCode {

    private int zip;
    private String city;

    public PostalCode(int zip, String city) {
        this.zip = zip;
        this.city = city;
    }

    public int getZip() {
        return zip;
    }

    public String getCity() {
        return city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostalCode that)) return false;
        return getZip() == that.getZip() && Objects.equals(getCity(), that.getCity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getZip(), getCity());
    }

    @Override
    public String toString() {
        return "PostalCode{" +
                "zip=" + zip +
                ", city='" + city + '\'' +
                '}';
    }
}
