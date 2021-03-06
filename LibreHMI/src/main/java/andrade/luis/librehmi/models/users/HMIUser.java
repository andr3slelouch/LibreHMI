package andrade.luis.librehmi.models.users;

import andrade.luis.librehmi.util.DBConnection;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.sql.*;

/**
 * Esta clase contendrĂ¡ los datos del usuario
 */
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
    private String username = "";
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
     *
     * @param firstName Nombre del usuario
     * @param lastName  Apellido del usuario
     * @param email     Correo del usuario
     * @param username  Nombre de usuario
     * @param role      Rol del usuario
     * @param password  ContraseĂ±a de la cuenta
     */
    public HMIUser(String firstName, String lastName, String email, String username, String role, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.role = role;
        setPassword(password);
    }

    /**
     * Permite obtener los datos del usuario si la contraseĂ±a ingresada coincide
     *
     * @param usernameOrEmail Nombre de usuario o email del usuario
     * @param password        ContraseĂ±a del Usuario
     * @throws SQLException Si la clase no puede conectarse con la base de datos
     * @throws IOException  Si no se puede realizar el proceso de lectura de credenciales
     */
    public HMIUser(String usernameOrEmail, String password) throws SQLException, IOException {
        this.userLoggedIn = verifyUserAndPassword(usernameOrEmail, password);
        if (this.username != null) {
            readFromDatabase(this.username);
        }
    }

    /**
     * Permite generar el salt y su hash computado basado en una contraseĂ±a ingresada por el usuario
     *
     * @param password ContraseĂ±a ingresada por el usuario
     */
    public void setPassword(String password) {
        this.salt = HMIPassword.createRandomSaltString();
        this.saltedHashPassword = HMIPassword.computeSaltedHashString(password, this.salt);
    }

    /**
     * Permite conectarse a la base de datos y crear el usuario
     *
     * @throws SQLException Si la clase no puede conectarse con la base de datos
     * @throws IOException  Si no se puede realizar el proceso de lectura de credenciales
     */
    public void createInDatabase() throws SQLException, IOException {
        try (Connection con = DBConnection.createConnectionToHMIUsers()) {
            String query = "INSERT INTO Users(first,last,email,username,role,salt,saltedHashPassword) values (?,?,?,?,?,?,?)";
            prepareAndExecuteQuery(con, query);
        } catch (SQLException e) {
            throw new SQLException(e);
        } catch (IOException e) {
            throw new IOException(e);
        }

    }

    /**
     * MĂ©todo para preparar un statement de mysql de actualizaciĂ³n
     *
     * @param con   ConexiĂ³n hacia la base de datos mysql
     * @param query Comando SQL a ser ejecutado
     * @throws SQLException Si la clase no puede conectarse con la base de datos
     */
    private void prepareAndExecuteQuery(Connection con, String query) throws SQLException {
        try (PreparedStatement prepareStatement = con.prepareStatement(query)) {
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

    /**
     * Permite verificar si un email existe y se encuentra asociado a un usuario en la base de datos
     *
     * @param email    Correo electrĂ³nico a verificarse
     * @param username Nombre de usuario a verificarse
     * @return true si el correo se encuentra asociado a un usuario
     * @throws SQLException Si la clase no puede conectarse con la base de datos
     * @throws IOException  Si no se puede realizar el proceso de lectura de credenciales
     */
    public static boolean existsEmail(String email, String username) throws SQLException, IOException {
        try (Connection con = DBConnection.createConnectionToHMIUsers()) {
            String query = "SELECT email from Users WHERE email=?";
            boolean usernameExists = false;
            if (!username.isEmpty()) {
                query = query.concat(" AND username!=?");
                usernameExists = true;
            }
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                preparedStatement.setString(1, email);
                if (usernameExists) preparedStatement.setString(2, username);
                ResultSet resultSet = preparedStatement.executeQuery();
                boolean res = false;
                while (resultSet.next()) {
                    res = (email.equals(resultSet.getString(1)));
                }
                return res;
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * Permite verificar si un nombre de usuario existe y se encuentra asociado a un usuario en la base de datos
     *
     * @param username Nombre de usuario a verificarse
     * @return true si el nombre de usuario se encuentra en uso
     * @throws SQLException Si la clase no puede conectarse con la base de datos
     * @throws IOException  Si no se puede realizar el proceso de lectura de credenciales
     */
    public static boolean existsUsername(String username) throws SQLException, IOException {
        try (Connection con = DBConnection.createConnectionToHMIUsers()) {
            String query = "SELECT username from Users WHERE username=?";
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();
                boolean res = false;
                while (resultSet.next()) {
                    res = (username.equals(resultSet.getString(1)));
                }
                return res;
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * Permite verificar el usuario y contraseĂ±a para realizar un logueo
     *
     * @param usernameOrEmail Nombre de usuario o email del usuario
     * @param password        ContraseĂ±a a verificarse
     * @return true si el usuario y la contraseĂ±a coinciden
     * @throws SQLException Si la clase no puede conectarse con la base de datos
     * @throws IOException  Si no se puede realizar el proceso de lectura de credenciales
     */
    public boolean verifyUserAndPassword(String usernameOrEmail, String password) throws SQLException, IOException {
        try (Connection con = DBConnection.createConnectionToHMIUsers()) {
            String query = "SELECT username, salt, saltedHashPassword FROM Users WHERE username=? or email=?";
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                preparedStatement.setString(1, usernameOrEmail);
                preparedStatement.setString(2, usernameOrEmail);
                ResultSet resultSet = preparedStatement.executeQuery();
                String localSalt = "";
                String localExpectedHash = "";
                while (resultSet.next()) {
                    this.username = resultSet.getString(1);
                    localSalt = resultSet.getString(2);
                    localExpectedHash = resultSet.getString(3);
                }
                return HMIPassword.verifyPassword(password, localSalt, localExpectedHash);
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        } catch (IOException e) {
            throw new IOException(e);
        }


    }

    /**
     * Permite leer los datos del usuario desde la base de datos
     *
     * @param username Nombre de usuario a consultarse
     * @throws SQLException Si la clase no puede conectarse con la base de datos
     * @throws IOException  Si no se puede realizar el proceso de lectura de credenciales
     */
    public void readFromDatabase(String username) throws SQLException, IOException {
        try (Connection con = DBConnection.createConnectionToHMIUsers()) {
            String query = "SELECT * FROM Users WHERE username=?";
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();
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
        } catch (SQLException e) {
            throw new SQLException(e);
        } catch (IOException e) {
            throw new IOException(e);
        }

    }

    /**
     * Permite actualizar los datos del usuario en la base de datos
     *
     * @throws SQLException Si la clase no puede conectarse con la base de datos
     * @throws IOException  Si no se puede realizar el proceso de lectura de credenciales
     */
    public void updateInDatabase() throws SQLException, IOException {
        try (Connection con = DBConnection.createConnectionToHMIUsers()) {
            String query = "UPDATE Users SET first=?, last=?,email=?,username=?,role=?,salt=?,saltedHashPassword=? WHERE username='" + oldUsername + "'";
            prepareAndExecuteQuery(con, query);
        } catch (SQLException e) {
            throw new SQLException(e);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * Permite eliminar un usuario de la base de datos
     *
     * @return true si el usuario se eliminĂ³ con Ă©xito
     * @throws SQLException Si la clase no puede conectarse con la base de datos
     * @throws IOException  Si no se puede realizar el proceso de lectura de credenciales
     */
    public boolean deleteFromDatabase() throws SQLException, IOException {
        try (Connection con = DBConnection.createConnectionToHMIUsers()) {
            String sql = "DELETE FROM Users WHERE username=?";
            try (PreparedStatement statement = con.prepareStatement(sql)) {
                statement.setString(1, this.username);
                int rowsDeleted = statement.executeUpdate();
                return rowsDeleted > 0;
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        } catch (IOException e) {
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

