package andrade.luis.libreHMI.views.windows;

import andrade.luis.libreHMI.models.users.HMIUser;
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
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                sqlException.printStackTrace();
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

    private void databaseConnectionFailed(String message) {
        showAlert(Alert.AlertType.ERROR,"Error al conectarse a la base de datos",message);
    }

    public void showAlert(Alert.AlertType type, String title,String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(message);

        ButtonType okButton = new ButtonType("OK",ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(okButton);

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == okButton)
        {
            alert.close();
        }
    }

    public boolean verifyFields(TextField firstName, TextField lastName, TextField email, TextField username, PasswordField passwordField, PasswordField repeatPasswordField) throws SQLException, IOException {
        String message = "";
        if(firstName.getText().isEmpty() || lastName.getText().isEmpty() || email.getText().isEmpty() || username.getText().isEmpty()){
            message = "Existen campos vacíos";
            logger.log(Level.INFO,"EMPTY VALUEs");
        }else if((passwordField.getText().isEmpty() || repeatPasswordField.getText().isEmpty()) && this.hmiUser == null){
            message = "Existen campos vacíos";
            logger.log(Level.INFO,"EMPTY VALUES PASSWORD");
        }else if(!validateEmailAddress(email.getText())){
            message = "Ingrese un correo electrónico válido";
            logger.log(Level.INFO,"EMAIL CONDITION");
        }
        if (this.hmiUser != null) {
            if(HMIUser.existsEmail(email.getText(), this.hmiUser.getUsername())){
                message = "El correo electrónico ya se encuentra asociado a una cuenta";
                logger.log(Level.INFO,"REPEATED EMAIL CONDITION");
            }else if(!this.hmiUser.getUsername().equals(username.getText())){
                if(HMIUser.existsUsername(username.getText())){
                    message = "El nombre de usuario ya existe";
                    logger.log(Level.INFO,"USER EXISTS");
                }
            }
        }
        if(rolesComboBox.getSelectionModel().getSelectedItem()==null){
            message = "Debe seleccionar un rol";
            logger.log(Level.INFO,"ROLES CONDITION");
        }else if(!passwordField.getText().equals(repeatPasswordField.getText())){
            message = "Las contraseñas no condicen";
            logger.log(Level.INFO,"PASSWORD NOT EQUAL");
        }
        logger.log(Level.INFO,firstName.getText());
        logger.log(Level.INFO,lastName.getText());
        logger.log(Level.INFO,email.getText());
        logger.log(Level.INFO,username.getText());
        logger.log(Level.INFO, String.valueOf(rolesComboBox.getSelectionModel().getSelectedIndex()));
        logger.log(Level.INFO, String.valueOf(rolesComboBox.getSelectionModel().getSelectedIndex() == -1));
        logger.log(Level.INFO,rolesComboBox.getSelectionModel().getSelectedItem());
        logger.log(Level.INFO,passwordField.getText());
        logger.log(Level.INFO,repeatPasswordField.getText());
        logger.log(Level.INFO,message);
        if(message.isEmpty()){
            return true;
        }else{
            showAlert(Alert.AlertType.WARNING,"Error en los campos ingresados",message);
            return false;
        }
    }
}
