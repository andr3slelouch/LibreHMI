package andrade.luis.librehmi.views.windows;

import andrade.luis.librehmi.models.users.HMIUser;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static andrade.luis.librehmi.util.Alerts.showAlert;

/**
 * Ventana de registro de usuario
 */
public class SignUpWindow extends Stage {

    private final ComboBox<String> rolesComboBox;
    private HMIUser hmiUser;

    public boolean isCancelled() {
        return cancelled;
    }

    private boolean cancelled = true;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z\\d._%+-]+@[A-Z\\d.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validateEmailAddress(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Constructor de la ventana
     * @param hmiUser Usuario del sistema a ser editado en caso de requerirse
     */
    public SignUpWindow(HMIUser hmiUser){
        StackPane root = new StackPane();
        setTitle("Registro de usuario");

        Label firstNameLabel = new Label("Nombre:");
        TextField firstNameField = new TextField("");
        HBox firstNameHBox = new HBox(firstNameLabel, firstNameField);
        firstNameHBox.setSpacing(96);

        Label lastNameLabel = new Label("Apellido:");
        TextField lastNameField = new TextField("");
        HBox lastNameHBox = new HBox(lastNameLabel, lastNameField);
        lastNameHBox.setSpacing(96);

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField("");
        HBox emailHBox = new HBox(emailLabel, emailField);
        emailHBox.setSpacing(112);

        Label usernameLabel = new Label("Nombre de Usuario:");
        TextField usernameField = new TextField("");
        HBox usernameHBox = new HBox(usernameLabel, usernameField);
        usernameHBox.setSpacing(25);

        // Create a combo box
        Label rolesLabel = new Label("Rol:");
        String[] userRoles = {"Administrador", "Diseñador", "Operador"};
        this.rolesComboBox =
                new ComboBox<>(FXCollections
                        .observableArrayList(userRoles));
        rolesComboBox.setPrefWidth(165);
        HBox rolesHBox = new HBox(rolesLabel, rolesComboBox);
        rolesHBox.setSpacing(125);

        Label passwordLabel = new Label("Contraseña:");
        PasswordField passwordField = new PasswordField();

        HBox passwordHBox = new HBox(passwordLabel, passwordField);
        passwordHBox.setSpacing(75);

        Label repeatPasswordLabel = new Label("Repetir contraseña:");
        PasswordField repeatPasswordField = new PasswordField();
        HBox repeatPasswordHBox = new HBox(repeatPasswordLabel, repeatPasswordField);
        repeatPasswordHBox.setSpacing(30);

        if(hmiUser !=null){
            this.hmiUser = hmiUser;
            setTitle("Actualizar Datos de Usuario");
            firstNameField.setText(this.hmiUser.getFirstName());
            lastNameField.setText(this.hmiUser.getLastName());
            emailField.setText(this.hmiUser.getEmail());
            usernameField.setText(this.hmiUser.getUsername());
            rolesComboBox.getSelectionModel().select(this.hmiUser.getRole());
            passwordField.setPromptText("Deje en blanco para no cambiar, la contraseña");
            repeatPasswordField.setPromptText("Deje en blanco para no cambiar, la contraseña");
        }

        Button cancelButton = new Button("Cancelar");
        cancelButton.setOnAction(mouseEvent -> this.close());
        Button registerButton = new Button("Guardar Usuario");
        registerButton.setOnAction(actionEvent -> {
            try{
                if(verifyFields(firstNameField,lastNameField,emailField,usernameField,passwordField,repeatPasswordField)){
                    if(SignUpWindow.this.hmiUser == null){
                        SignUpWindow.this.hmiUser = new HMIUser(firstNameField.getText(),lastNameField.getText(),emailField.getText(),usernameField.getText(),rolesComboBox.getSelectionModel().getSelectedItem(),passwordField.getText());
                        SignUpWindow.this.hmiUser.createInDatabase();
                        logger.log(Level.INFO,"Created user");
                    }else{
                        SignUpWindow.this.hmiUser.setFirstName(firstNameField.getText());
                        SignUpWindow.this.hmiUser.setLastName(lastNameField.getText());
                        SignUpWindow.this.hmiUser.setEmail(emailField.getText());
                        SignUpWindow.this.hmiUser.setUsername(usernameField.getText());
                        SignUpWindow.this.hmiUser.setRole(rolesComboBox.getSelectionModel().getSelectedItem());
                        SignUpWindow.this.hmiUser.setPassword(passwordField.getText());
                        SignUpWindow.this.hmiUser.updateInDatabase();
                    }
                    this.cancelled = false;
                    this.close();
                }
            } catch (SQLException | IOException sqlException){
                logger.log(Level.INFO,sqlException.getMessage());
                databaseConnectionFailed(sqlException.getMessage());
            }
        });
        HBox buttonsHBox = new HBox();
        buttonsHBox.getChildren().addAll(cancelButton,registerButton);
        buttonsHBox.setAlignment(Pos.BASELINE_RIGHT);

        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.getChildren().addAll(firstNameHBox,lastNameHBox,emailHBox,usernameHBox,rolesHBox,passwordHBox,repeatPasswordHBox, buttonsHBox);

        root.getChildren().add(vbox);

        this.setScene(new Scene(root,333,275));
    }

    /**
     * Permite mostrar la ventana de error al conectarse a la base de datos
     * @param message
     */
    private void databaseConnectionFailed(String message) {
        showAlert(Alert.AlertType.ERROR,"Error al conectarse a la base de datos",message,"");
    }

    /**
     * Permite verificar los campos de la ventana
     * @param firstName Nombre del usuario
     * @param lastName Apellido del usuario
     * @param email Correo electrónico del usuario
     * @param username Nombre de usuario
     * @param passwordField Contraseña del usuario
     * @param repeatPasswordField Repetición Contraseña del usuario
     * @return true si todos los campos son correctos
     * @throws SQLException
     * @throws IOException
     */
    public boolean verifyFields(TextField firstName, TextField lastName, TextField email, TextField username, PasswordField passwordField, PasswordField repeatPasswordField) throws SQLException, IOException {
        String message = "";
        if(firstName.getText().isEmpty() || lastName.getText().isEmpty() || email.getText().isEmpty() || username.getText().isEmpty()){
            message = "Existen campos vacíos";
        }else if((passwordField.getText().isEmpty() || repeatPasswordField.getText().isEmpty()) && this.hmiUser == null){
            message = "Existen campos vacíos";
        }else if(!validateEmailAddress(email.getText())){
            message = "Ingrese un correo electrónico válido";
        }
        if (this.hmiUser != null) {
            message = checkUserCredentials(email, username);
        }
        if(rolesComboBox.getSelectionModel().getSelectedItem()==null){
            message = "Debe seleccionar un rol";

        }else if(!passwordField.getText().equals(repeatPasswordField.getText())){
            message = "Las contraseñas no condicen";

        }
        if(message.isEmpty()){
            return true;
        }else{
            showAlert(Alert.AlertType.WARNING,"Error en los campos ingresados",message,"");
            return false;
        }
    }

    /**
     * Permite verificar las credenciales del usuario
     * @param email Correo electrónico del usuario
     * @param username Nombre de usuario
     * @return String conteniendo el mensaje de verificación
     * @throws SQLException
     * @throws IOException
     */
    private String checkUserCredentials(TextField email, TextField username) throws SQLException, IOException {
        String message = "";
        if(HMIUser.existsEmail(email.getText(), this.hmiUser.getUsername())){
            message = "El correo electrónico ya se encuentra asociado a una cuenta";
        }else if (!this.hmiUser.getUsername().equals(username.getText()) && HMIUser.existsUsername(username.getText())) {
            message = "El nombre de usuario ya existe";
        }
        return message;
    }
}
