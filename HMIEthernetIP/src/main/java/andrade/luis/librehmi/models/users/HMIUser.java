package andrade.luis.librehmi.models.users;

import andrade.luis.librehmi.util.DBConnection;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
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
    private String username="";
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
    @SerializedName("oldUsername")
    @Expose
    private String oldUsername;

    public HMIUser() {

    }

    public boolean isUserLoggedIn() {
        return userLoggedIn;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.oldUsername = this.username;
        this.username = username;
    }

    /**
     * Constructor de usuario HMI
     * @param firstName Nombre del usuario
     * @param lastName Apellido del usuario
     * @param email Correo del usuario
     * @param username Nombre de usuario
     * @param role Rol del usuario
     * @param password Contraseña de la cuenta
     */
    public HMIUser(String firstName, String lastName, String email, String username, String role, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.role = role;
        setPassword(password);
    }

    public HMIUser(String usernameOrEmail, String password) throws SQLException, IOException {
        this.userLoggedIn = verifyUserAndPassword(usernameOrEmail, password);
        if (this.username != null) {
            readFromDatabase(this.username);
        }
    }

    public void setPassword(String password) {
        this.salt = HMIPassword.createRandomSaltString();
        this.saltedHashPassword = HMIPassword.computeSaltedHashString(password, this.salt);
    }

    public void createInDatabase() throws SQLException, IOException {
        try(Connection con = DBConnection.createConnectionToHMIUsers()){
            String query = "INSERT INTO Users(first,last,email,username,role,salt,saltedHashPassword) values (?,?,?,?,?,?,?)";
            prepareAndExecuteQuery(con, query);
        }catch(SQLException e){
            throw new SQLException(e);
        }catch (IOException e){
            throw new IOException(e);
        }

    }

    private void prepareAndExecuteQuery(Connection con, String query) throws SQLException {
        try(PreparedStatement prepareStatement = con.prepareStatement(query)){
            prepareStatement.setString(1, this.firstName);
            prepareStatement.setString(2, this.lastName);
            prepareStatement.setString(3, this.email);
            prepareStatement.setString(4, this.username);
            prepareStatement.setString(5, this.role);
            prepareStatement.setString(6, this.salt);
            prepareStatement.setString(7, this.saltedHashPassword);
            prepareStatement.executeUpdate();
        }
    }

    public static boolean existsEmail(String email, String username) throws SQLException, IOException {
        try(Connection con = DBConnection.createConnectionToHMIUsers()){
            String query = "SELECT email from Users WHERE email='" + email + "'";
            if(!username.isEmpty()){
                query = query.concat(" AND username!='"+username+"'");
            }
            try(Statement statement = con.createStatement()){
                ResultSet resultSet = statement.executeQuery(query);
                boolean res = false;
                while (resultSet.next()) {
                    res = (email.equals(resultSet.getString(1)));
                }
                return res;
            }
        }catch(SQLException e){
            throw new SQLException(e);
        }catch (IOException e){
            throw new IOException(e);
        }

    }

    public static boolean existsUsername(String username) throws SQLException, IOException {
        try(Connection con = DBConnection.createConnectionToHMIUsers()){
            String query = "SELECT username from Users WHERE username='" + username + "'";
            try(Statement statement = con.createStatement()){
                ResultSet resultSet = statement.executeQuery(query);
                boolean res = false;
                while (resultSet.next()) {
                    res = (username.equals(resultSet.getString(1)));
                }
                return res;
            }
        }catch(SQLException e){
            throw new SQLException(e);
        }catch (IOException e){
            throw new IOException(e);
        }
    }

    public boolean verifyUserAndPassword(String usernameOrEmail, String password) throws SQLException, IOException {
        try(Connection con = DBConnection.createConnectionToHMIUsers()){
            String query = "SELECT username, salt, saltedHashPassword FROM Users WHERE username='" + usernameOrEmail + "' or email='" + usernameOrEmail + "'";
            try(Statement statement = con.createStatement()){
                ResultSet resultSet = statement.executeQuery(query);
                String localSalt = "";
                String localExpectedHash = "";
                while (resultSet.next()) {
                    this.username = resultSet.getString(1);
                    localSalt = resultSet.getString(2);
                    localExpectedHash = resultSet.getString(3);
                }
                return HMIPassword.verifyPassword(password, localSalt, localExpectedHash);
            }
        }catch(SQLException e){
            throw new SQLException(e);
        }catch (IOException e){
            throw new IOException(e);
        }


    }

    public void readFromDatabase(String username) throws SQLException, IOException {
        try(Connection con = DBConnection.createConnectionToHMIUsers()){
            String query = "SELECT * FROM Users WHERE username='" + username + "'";
            try(Statement statement = con.createStatement()){
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
            }
        }catch(SQLException e){
            throw new SQLException(e);
        }catch (IOException e){
            throw new IOException(e);
        }

    }
    
    

    public void updateInDatabase() throws SQLException, IOException {
        try(Connection con = DBConnection.createConnectionToHMIUsers()){
            String query = "UPDATE Users SET first=?, last=?,email=?,username=?,role=?,salt=?,saltedHashPassword=? WHERE username='"+oldUsername+"'";
            prepareAndExecuteQuery(con, query);
        }catch(SQLException e){
            throw new SQLException(e);
        }catch (IOException e){
            throw new IOException(e);
        }
    }

    public boolean deleteFromDatabase() throws SQLException, IOException {
        try(Connection con = DBConnection.createConnectionToHMIUsers()){
            String sql = "DELETE FROM Users WHERE username=?";

            try(PreparedStatement statement = con.prepareStatement(sql)){
                statement.setString(1, this.username);
                int rowsDeleted = statement.executeUpdate();
                return rowsDeleted > 0;
            }
        }catch(SQLException e){
            throw new SQLException(e);
        }catch (IOException e){
            throw new IOException(e);
        }
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

