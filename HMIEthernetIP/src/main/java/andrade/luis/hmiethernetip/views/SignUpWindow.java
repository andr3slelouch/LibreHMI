package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.models.users.HMIUser;
import andrade.luis.hmiethernetip.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpWindow extends Stage {

    private StackPane root;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validateEmailAddress(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public SignUpWindow(){
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

        // Weekdays
        String userRoles[] =
                { "Administrador", "Diseñador", "Operador"};

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

        Button registerButton = new Button("Registrar Usuario");
        registerButton.setOnAction(mouseEvent -> {
            try{
                if(verifyFields(firstNameField,lastNameField,emailField,usernameField,rolesComboBox,passwordField,repeatPasswordField)){
                    HMIUser user = new HMIUser(firstNameField.getText(),lastNameField.getText(),emailField.getText(),usernameField.getText(),rolesComboBox.getSelectionModel().getSelectedItem(),passwordField.getText());
                    user.createInDatabase();
                }
            } catch (SQLException sqlException){
                sqlException.printStackTrace();
                databaseConnectionFailed(sqlException.getMessage());
            }
        });

        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, firstNameHBox,lastNameHBox,emailHBox,usernameHBox,rolesHBox,passwordHBox,repeatPasswordHBox, registerButton);

        root.getChildren().add(vbox);

        this.setScene(new Scene(root,700,500));
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

    public boolean verifyFields(TextField firstName, TextField lastName, TextField email, TextField username, ComboBox<String> role, PasswordField passwordField, PasswordField repeatPasswordField) throws SQLException {
        String message = "";
        if(firstName.getText().isEmpty() && lastName.getText().isEmpty() && email.getText().isEmpty() && username.getText().isEmpty() && passwordField.getText().isEmpty() && repeatPasswordField.getText().isEmpty() ){
            message = "Existen campos vacíos";
        }else if(!validateEmailAddress(email.getText())){
            message = "Ingrese un correo electrónico válido";
        }else if(HMIUser.existsEmail(email.getText())){
            message = "El correo electrónico ya se encuentra asociado a una cuenta";
        }else if(HMIUser.existsUsername(username.getText())){
            message = "El nombre de usuario ya existe";
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
