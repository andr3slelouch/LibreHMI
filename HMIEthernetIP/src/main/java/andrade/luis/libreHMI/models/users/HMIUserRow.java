package andrade.luis.libreHMI.models.users;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import javafx.beans.property.SimpleStringProperty;

public class HMIUserRow {
    public String getFirst() {
        return first.get();
    }

    public SimpleStringProperty firstProperty() {
        return first;
    }

    public void setFirst(String first) {
        this.first.set(first);
    }

    public String getLast() {
        return last.get();
    }

    public SimpleStringProperty lastProperty() {
        return last;
    }

    public void setLast(String last) {
        this.last.set(last);
    }

    public String getEmail() {
        return email.get();
    }

    public SimpleStringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getUsername() {
        return username.get();
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getRole() {
        return role.get();
    }

    public SimpleStringProperty roleProperty() {
        return role;
    }

    public void setRole(String role) {
        this.role.set(role);
    }

    public HMIUser getHmiUser() {
        return hmiUser;
    }

    public void setHmiUser(HMIUser hmiUser) {
        this.hmiUser = hmiUser;
    }

    @SerializedName("first")
    @Expose
    private final SimpleStringProperty first;
    @SerializedName("last")
    @Expose
    private final SimpleStringProperty last;
    @SerializedName("email")
    @Expose
    private final SimpleStringProperty email;
    @SerializedName("username")
    @Expose
    private final SimpleStringProperty username;
    @SerializedName("role")
    @Expose
    private final SimpleStringProperty role;

    private HMIUser hmiUser;

    public HMIUserRow(String first, String last, String email, String username, String role){
        hmiUser = new HMIUser(
                first,
                last,
                email,
                username,
                role,
                ""
        );
        this.first = new SimpleStringProperty(first);
        this.last= new SimpleStringProperty(last);
        this.email = new SimpleStringProperty(email);
        this.username = new SimpleStringProperty(username);
        this.role = new SimpleStringProperty(role);
    }

    public HMIUserRow(HMIUser hmiUser){
        this.hmiUser = hmiUser;
        this.first = new SimpleStringProperty(hmiUser.getFirstName());
        this.last= new SimpleStringProperty(hmiUser.getLastName());
        this.email = new SimpleStringProperty(hmiUser.getEmail());
        this.username = new SimpleStringProperty(hmiUser.getUsername());
        this.role = new SimpleStringProperty(hmiUser.getRole());
    }

    @Override
    public String toString() {
        return "HMIUserRow{" +
                "first=" + first +
                ", last=" + last +
                ", email=" + email +
                ", username=" + username +
                ", role=" + role +
                '}';
    }
}
