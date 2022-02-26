package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.models.users.HMIUser;
import andrade.luis.hmiethernetip.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpWindow extends Stage {

    private StackPane root;
    private HMIUser hmiUser;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    private boolean cancelled = true;
    private final String[] userRoles = { "Administrador", "Diseñador", "Operador"};
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validateEmailAddress(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public SignUpWindow(HMIUser hmiUser){
        root = new StackPane();
        final Label label = new Label("Registro de usuario");

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
        ComboBox<String> rolesComboBox =
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
            label.setText("Actualizar Datos de Usuario");
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
                if(verifyFields(firstNameField,lastNameField,emailField,usernameField,rolesComboBox,passwordField,repeatPasswordField)){
                    if(SignUpWindow.this.hmiUser == null){
                        SignUpWindow.this.hmiUser = new HMIUser(firstNameField.getText(),lastNameField.getText(),emailField.getText(),usernameField.getText(),rolesComboBox.getSelectionModel().getSelectedItem(),passwordField.getText());
                        SignUpWindow.this.hmiUser.createInDatabase();
                    }else{
                        SignUpWindow.this.hmiUser.setFirstName(firstNameField.getText());
                        SignUpWindow.this.hmiUser.setLastName(lastNameField.getText());
                        SignUpWindow.this.hmiUser.setEmail(emailField.getText());
                        SignUpWindow.this.hmiUser.setUsername(usernameField.getText());
                        SignUpWindow.this.hmiUser.setRole(rolesComboBox.getSelectionModel().getSelectedItem());
                        SignUpWindow.this.hmiUser.setPassword(passwordField.getText());
                        SignUpWindow.this.hmiUser.updateInDatabase();
                    }
                }
                this.cancelled = false;
                this.close();
            } catch (SQLException | IOException sqlException){
                sqlException.printStackTrace();
                databaseConnectionFailed(sqlException.getMessage());
            }
        });
        HBox buttonsHBox = new HBox();
        buttonsHBox.getChildren().addAll(cancelButton,registerButton);

        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, firstNameHBox,lastNameHBox,emailHBox,usernameHBox,rolesHBox,passwordHBox,repeatPasswordHBox, buttonsHBox);

        root.getChildren().add(vbox);

        this.setScene(new Scene(root,400,300));
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

    public boolean verifyFields(TextField firstName, TextField lastName, TextField email, TextField username, ComboBox<String> role, PasswordField passwordField, PasswordField repeatPasswordField) throws SQLException, IOException {
        String message = "";
        if(firstName.getText().isEmpty() && lastName.getText().isEmpty() && email.getText().isEmpty() && username.getText().isEmpty()){
            message = "Existen campos vacíos";
        }else if(passwordField.getText().isEmpty() && repeatPasswordField.getText().isEmpty() && this.hmiUser == null){
            message = "Existen campos vacíos";
        }else if(!validateEmailAddress(email.getText())){
            message = "Ingrese un correo electrónico válido";
        }else if(HMIUser.existsEmail(email.getText(),(this.hmiUser!=null)? this.hmiUser.getUsername() : "")){
            message = "El correo electrónico ya se encuentra asociado a una cuenta";
        }else if((this.hmiUser!=null)? !this.hmiUser.getUsername().equals(username.getText()) : false){
            if(HMIUser.existsUsername(username.getText())){
                message = "El nombre de usuario ya existe";
            }
        }else if(role.getSelectionModel().getSelectedIndex()==-1){
            message = "Debe seleccionar un rol";
        }else if(!passwordField.getText().equals(repeatPasswordField.getText())){
            message = "Las contraseñas no coindicen";
        }
        if(message.isEmpty()){
            return true;
        }else{
            showAlert(Alert.AlertType.WARNING,"Error en los campos ingresados",message);
            return false;
        }
    }
}
