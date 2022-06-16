package andrade.luis.hmiethernetip.views.windows;

import andrade.luis.hmiethernetip.models.users.HMIUser;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import static java.awt.Font.BOLD;
import static javafx.geometry.Pos.CENTER;

public class LogInWindow extends Stage {
    private final StackPane root;

    public HMIUser getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(HMIUser loggedUser) {
        this.loggedUser = loggedUser;
    }

    private HMIUser loggedUser;

    public LogInWindow(){
        root = new StackPane();
        final Label label = new Label("Inicio de Sesión");
        label.setFont(new Font("Arial", 30));
        HBox titleHBox = new HBox();
        titleHBox.getChildren().add(label);
        titleHBox.setAlignment(CENTER);

        Label usernameEmailLabel = new Label("Nombre de Usuario/Email:");
        TextField usernameEmailField = new TextField("");
        HBox usernameEmailHBox = new HBox(usernameEmailLabel, usernameEmailField);
        usernameEmailHBox.setSpacing(10);

        Label passwordLabel = new Label("Contraseña:");
        PasswordField passwordField = new PasswordField();

        HBox passwordHBox = new HBox(passwordLabel, passwordField);
        passwordHBox.setSpacing(100);

        HBox signInHBox = new HBox();
        signInHBox.setAlignment(CENTER);
        Button signInButton = new Button("Iniciar Sesión");
        signInButton.setOnAction(mouseEvent -> {
            try {
                HMIUser user = new HMIUser(usernameEmailField.getText(),passwordField.getText());
                if(user.isUserLoggedIn()){
                    loggedUser = user;
                    this.close();
                }else{
                    showAlert(Alert.AlertType.ERROR,"Error al Iniciar Sesión","Usuario o contraseña incorrectos");
                }
            } catch (SQLException e) {
                databaseConnectionFailed(e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        signInHBox.getChildren().add(signInButton);

        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(titleHBox, usernameEmailHBox,passwordHBox,signInHBox);
        vbox.setSpacing(10);

        root.getChildren().add(vbox);

        this.setScene(new Scene(root,400,175));
        
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
