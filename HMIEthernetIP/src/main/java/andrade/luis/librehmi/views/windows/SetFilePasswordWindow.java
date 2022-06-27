package andrade.luis.librehmi.views.windows;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static andrade.luis.librehmi.util.Alerts.showAlert;
import static javafx.geometry.Pos.BASELINE_RIGHT;
import static javafx.geometry.Pos.CENTER;

public class SetFilePasswordWindow extends Stage {
    private final StackPane root;

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    private boolean canceled=true;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;


    public SetFilePasswordWindow(boolean registration){
        root = new StackPane();
        this.setTitle("Definir Contraseña Para Proyecto");

        Label passwordLabel = new Label("Contraseña:");
        PasswordField passwordField = new PasswordField();

        HBox passwordHBox = new HBox(passwordLabel, passwordField);
        passwordHBox.setSpacing(50);

        Label repeatPasswordLabel = new Label("Repetir Contraseña:");
        PasswordField repeatPasswordField = new PasswordField();

        HBox repeatPasswordHBox = new HBox(repeatPasswordLabel, repeatPasswordField);
        repeatPasswordHBox.setSpacing(5);

        HBox buttonsHBox = new HBox();
        buttonsHBox.setAlignment(CENTER);
        Button okButton = new Button("OK");
        Button cancelButton = new Button("Cancelar");
        cancelButton.setOnAction(actionEvent -> {
            canceled = true;
            this.close();
        });
        okButton.setOnAction(mouseEvent -> {
            if(registration){
                if(passwordField.getText().equals(repeatPasswordField.getText())){
                    password = passwordField.getText();
                    canceled = false;
                    this.close();
                }else{
                    showAlert(Alert.AlertType.ERROR,"Las contraseñas no coinciden","Verifique que las contraseñas ingresadas sean las mismas","");
                }
            }else{
                canceled=false;
                password = passwordField.getText();
                this.close();
            }

        });
        buttonsHBox.getChildren().addAll(cancelButton,okButton);
        buttonsHBox.setAlignment(BASELINE_RIGHT);

        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().add(passwordHBox);
        if(registration){
            vbox.getChildren().add(repeatPasswordHBox);
        }
        vbox.getChildren().add(buttonsHBox);
        vbox.setSpacing(10);

        root.getChildren().add(vbox);
        double height = registration ? 150 : 125;
        this.setScene(new Scene(root,400,height));

    }
}
