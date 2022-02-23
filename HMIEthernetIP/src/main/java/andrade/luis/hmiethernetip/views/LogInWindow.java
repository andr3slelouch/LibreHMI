package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.models.users.HMIUser;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Optional;

public class LogInWindow extends Stage {
    private final StackPane root;

    public LogInWindow(){
        root = new StackPane();
        final Label label = new Label("Iniciar Sesión");

        Label usernameEmailLabel = new Label("Nombre de Usuario/Email:");
        TextField usernameEmailField = new TextField("");
        HBox usernameEmailHBox = new HBox(usernameEmailLabel, usernameEmailField);

        Label passwordLabel = new Label("Contraseña:");
        PasswordField passwordField = new PasswordField();

        HBox passwordHBox = new HBox(passwordLabel, passwordField);
        passwordHBox.setSpacing(90);

        Button signInButton = new Button("Iniciar Sesión");
        signInButton.setOnAction(mouseEvent -> {
            try {
                HMIUser user = new HMIUser(usernameEmailField.getText(),passwordField.getText());
                System.out.println(user.isUserLoggedIn());
            } catch (SQLException e) {
                databaseConnectionFailed(e.getMessage());
                e.printStackTrace();
            }
        });

        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, usernameEmailHBox,passwordHBox,signInButton);

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
}
