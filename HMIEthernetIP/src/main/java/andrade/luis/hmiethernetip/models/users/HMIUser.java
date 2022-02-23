package andrade.luis.hmiethernetip.models.users;

import andrade.luis.hmiethernetip.util.DBConnection;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.sql.*;

public class HMIUser {
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("salt")
    @Expose
    private String salt;
    @SerializedName("saltedHashPassword")
    @Expose
    private String saltedHashPassword;
    @SerializedName("userLoggedIn")
    @Expose
    private boolean userLoggedIn = false;

    public boolean isUserLoggedIn() {
        return userLoggedIn;
    }

    public void setUserLoggedIn(boolean userLoggedIn) {
        this.userLoggedIn = userLoggedIn;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getSaltedHashPassword() {
        return saltedHashPassword;
    }

    public void setSaltedHashPassword(String saltedHashPassword) {
        this.saltedHashPassword = saltedHashPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public HMIUser(String firstName, String lastName, String email, String username, String role, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.role = role;
        setPassword(password);
    }

    public HMIUser(String usernameOrEmail, String password) throws SQLException {
        this.userLoggedIn = verifyUserAndPassword(usernameOrEmail, password);
        if (this.username != null) {
            readFromDatabase(this.username);
        }
    }

    public void setPassword(String password) {
        this.salt = HMIPassword.createRandomSaltString();
        this.saltedHashPassword = HMIPassword.computeSaltedHashString(password, this.salt);
    }

    public boolean createInDatabase() throws SQLException {
        Connection con = DBConnection.createConnectionToHMIUsers();
        String query = "INSERT INTO Users(first,last,email,username,role,salt,saltedHashPassword) values (?,?,?,?,?,?,?)";
        PreparedStatement prepareStatement = con.prepareStatement(query);
        prepareStatement.setString(1, this.firstName);
        prepareStatement.setString(2, this.lastName);
        prepareStatement.setString(3, this.email);
        prepareStatement.setString(4, this.username);
        prepareStatement.setString(5, this.role);
        prepareStatement.setString(6, this.salt);
        prepareStatement.setString(7, this.saltedHashPassword);
        int insertRowResult = prepareStatement.executeUpdate();
        con.close();
        return insertRowResult > 0;
    }

    public static boolean existsEmail(String email) throws SQLException {
        Connection con = DBConnection.createConnectionToHMIUsers();
        String query = "SELECT email from Users WHERE email='" + email + "'";
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        boolean res = false;
        while (resultSet.next()) {
            res = (email.equals(resultSet.getString(1)));
        }
        con.close();
        return res;
    }

    public static boolean existsUsername(String username) throws SQLException {
        Connection con = DBConnection.createConnectionToHMIUsers();
        String query = "SELECT username from Users WHERE username='" + username + "'";
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        boolean res = false;
        while (resultSet.next()) {
            res = (username.equals(resultSet.getString(1)));
        }
        con.close();
        return res;
    }

    public boolean verifyUserAndPassword(String usernameOrEmail, String password) throws SQLException {
        Connection con = DBConnection.createConnectionToHMIUsers();
        String query = "SELECT username, salt, saltedHashPassword FROM Users WHERE username='" + usernameOrEmail + "' or email='" + usernameOrEmail + "'";
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        String salt = "";
        String expectedHash = "";
        while (resultSet.next()) {
            this.username = resultSet.getString(1);
            salt = resultSet.getString(2);
            expectedHash = resultSet.getString(3);
        }
        return HMIPassword.verifyPassword(password, salt, expectedHash);

    }

    public void readFromDatabase(String username) throws SQLException {
        Connection con = DBConnection.createConnectionToHMIUsers();
        String query = "SELECT * FROM Users WHERE username='" + username + "'";
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            this.firstName = resultSet.getString(2);
            this.lastName = resultSet.getString(3);
            this.email = resultSet.getString(4);
            this.username = resultSet.getString(5);
            this.role = resultSet.getString(6);
            this.salt = resultSet.getString(7);
            this.saltedHashPassword = resultSet.getString(8);
        }
        con.close();
    }

    public boolean updateInDatabase() throws SQLException {
        Connection con = DBConnection.createConnectionToHMIUsers();
        String query = "UPDATE Users SET first=?, last=?,email=?,username=?,role=?,salt=?,saltedHashPassword=?";
        PreparedStatement prepareStatement = con.prepareStatement(query);
        prepareStatement.setString(1, this.firstName);
        prepareStatement.setString(2, this.lastName);
        prepareStatement.setString(3, this.email);
        prepareStatement.setString(4, this.username);
        prepareStatement.setString(5, this.role);
        prepareStatement.setString(6, this.salt);
        prepareStatement.setString(7, this.saltedHashPassword);
        int insertRowResult = prepareStatement.executeUpdate();
        con.close();
        return insertRowResult > 0;
    }

    public boolean deleteFromDatabase(String username) throws SQLException {
        Connection con = DBConnection.createConnectionToHMIUsers();
        String sql = "DELETE FROM Users WHERE username=?";

        PreparedStatement statement = con.prepareStatement(sql);
        statement.setString(1, username);

        int rowsDeleted = statement.executeUpdate();
        return rowsDeleted > 0;
    }

    @Override
    public String toString() {
        return "HMIUser{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", salt='" + salt + '\'' +
                ", saltedHashPassword='" + saltedHashPassword + '\'' +
                '}';
    }
}

