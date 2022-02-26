package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.util.DBConnection;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;

public class SaveDatabaseCredentialsWindow extends Stage {

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    private boolean cancelled =true;

    public SaveDatabaseCredentialsWindow(){
        StackPane root = new StackPane();
        final Label label = new Label("Registro de credenciales");
        Properties properties = null;

        try{
            properties = DBConnection.readPropertiesFile();
        }catch (IOException e) {
            try {
                DBConnection.writePropertiesFile("","","","");
            } catch (IOException ex) {
                showAlert(Alert.AlertType.ERROR,"Error al leer el archivo de configuración","No se pudo leer el archivo de configuración, Error:'"+e.getMessage()+"'");
            }
        }


        Label hostnameLabel = new Label("Nombre de Host:");
        TextField hostnameField = new TextField("");
        HBox hostnameHBox = new HBox(hostnameLabel, hostnameField);

        Label portLabel = new Label("Puerto:");
        TextField portField = new TextField("");
        HBox portHBox = new HBox(portLabel, portField);
        portHBox.setSpacing(62);

        Label usernameLabel = new Label("Usuario:");
        TextField usernameField = new TextField("");
        HBox usernameHBox = new HBox(usernameLabel, usernameField);
        usernameHBox.setSpacing(55);

        Label passwordLabel = new Label("Contraseña:");
        PasswordField passwordField = new PasswordField();
        HBox passwordHBox = new HBox(passwordLabel, passwordField);
        passwordHBox.setSpacing(32);

        if(properties!= null){
            hostnameField.setText(properties.getProperty("hostname",""));
            portField.setText(properties.getProperty("port"));
            usernameField.setText(properties.getProperty("username"));
            passwordField.setText(properties.getProperty("password"));
        }

        Button testConnectionButton = new Button("Verificar Conexión");
        testConnectionButton.setOnAction(mouseEvent -> {
            try{
                testConnection(hostnameField.getText(),portField.getText(),usernameField.getText(),passwordField.getText());
                showAlert(Alert.AlertType.INFORMATION,"Conexión exitosa","La conexión a la base de datos se realizó exitosamente");
            }catch(SQLException e){
                showAlert(Alert.AlertType.ERROR,"Error al conectarse",e.getMessage());
                e.printStackTrace();
            }
        });
        Button cancelButton = new Button("Cancelar");
        cancelButton.setOnAction(mouseEvent -> this.close());
        Button saveButton = new Button("Guardar");
        saveButton.setOnAction(mouseEvent ->{
            try {
                if(showAlert(Alert.AlertType.CONFIRMATION,"Precaución","Precaución el archivo de configuración se guarda en texto plano, presione OK para terminar el proceso")){
                    DBConnection.writePropertiesFile(usernameField.getText(),passwordField.getText(),hostnameField.getText(),portField.getText());
                    this.close();
                    cancelled=false;
                }
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR,"Error al guardar","No se pudo Guardar, Error:'"+e.getMessage()+"'");
            }
        });

        HBox buttonsHBox = new HBox();

        buttonsHBox.getChildren().addAll(testConnectionButton,cancelButton,saveButton);

        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, hostnameHBox,portHBox,usernameHBox,passwordHBox,buttonsHBox);

        root.getChildren().add(vbox);

        this.setScene(new Scene(root,300,200));

    }

    private void testConnection(String hostname, String port, String username, String password) throws SQLException {
        Connection con;
        String url = "jdbc:mysql://"+hostname+":"+port;
        con = DriverManager.getConnection(url, username, password); //attempting to connect to MySQL database
        con.close();
    }

    public boolean showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(message);

        ButtonType cancelButton = new ButtonType("Cancelar",ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType okButton = new ButtonType("OK",ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(cancelButton,okButton);

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == okButton)
        {
            alert.close();
            return true;
        }else if(result.isPresent() && result.get() == cancelButton){
            alert.close();
            return false;
        }
        return false;
    }
}
