package andrade.luis.librehmi.views.windows;

import andrade.luis.librehmi.util.DBConnection;
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
import java.util.Properties;

import static andrade.luis.librehmi.util.Alerts.showAlert;

/**
 * Ventana de definición de credenciales de la base de datos
 */
public class SaveDatabaseCredentialsWindow extends Stage {

    public boolean isCancelled() {
        return cancelled;
    }

    private boolean cancelled =true;

    /**
     * Constructor de la ventana
     */
    public SaveDatabaseCredentialsWindow(){
        StackPane root = new StackPane();
        setTitle("Registro de credenciales");
        Properties properties = null;
        boolean boilerFurnace=false;
        boolean conveyorBelts=false;
        boolean motorPumps=false;
        boolean others=false;
        boolean pipesValues=false;
        boolean tanks=false;
        try{
            properties = DBConnection.readPropertiesFile();
        }catch (IOException e) {
            try {
                Properties newProperties = new Properties();
                DBConnection.prepareCategoriesProperties(false, false, false, false, false, false, newProperties);
                DBConnection.writePropertiesFile("","","","",newProperties);
            } catch (IOException ex) {
                showAlert(Alert.AlertType.ERROR,"Error al leer el archivo de configuración","No se pudo leer el archivo de configuración",e.getMessage());
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
            boilerFurnace = Boolean.parseBoolean(properties.getProperty("boilerFurnace"));
            conveyorBelts = Boolean.parseBoolean(properties.getProperty("conveyorBelts"));
            motorPumps=Boolean.parseBoolean(properties.getProperty("motorPumps"));
            others=Boolean.parseBoolean(properties.getProperty("others"));
            pipesValues=Boolean.parseBoolean(properties.getProperty("pipesValues"));
            tanks=Boolean.parseBoolean(properties.getProperty("tanks"));
        }

        Button testConnectionButton = new Button("Verificar Conexión");
        testConnectionButton.setOnAction(mouseEvent -> {
            try{
                testConnection(hostnameField.getText(),portField.getText(),usernameField.getText(),passwordField.getText());
                showAlert(Alert.AlertType.INFORMATION,"Conexión exitosa","La conexión a la base de datos se realizó exitosamente","");
            }catch(SQLException e){
                showAlert(Alert.AlertType.ERROR,"Error al conectarse","La aplicación no pudo conectarse con las credenciales definidas",e.getMessage());
            }
        });
        Button cancelButton = new Button("Cancelar");
        cancelButton.setOnAction(mouseEvent -> this.close());
        Button saveButton = new Button("Guardar");
        boolean finalBoilerFurnace = boilerFurnace;
        boolean finalConveyorBelts = conveyorBelts;
        boolean finalMotorPumps = motorPumps;
        boolean finalOthers = others;
        boolean finalPipesValues = pipesValues;
        boolean finalTanks = tanks;
        saveButton.setOnAction(mouseEvent -> {
            try {
                if(showAlert(Alert.AlertType.CONFIRMATION,"Precaución","Precaución el archivo de configuración se guarda en texto plano, presione OK para terminar el proceso","")){
                    Properties newProperties = new Properties();
                    newProperties = DBConnection.prepareCategoriesProperties(finalBoilerFurnace, finalConveyorBelts, finalMotorPumps, finalOthers, finalPipesValues, finalTanks,newProperties);
                    DBConnection.writePropertiesFile(usernameField.getText(),passwordField.getText(),hostnameField.getText(),portField.getText(), newProperties);
                    this.close();
                    cancelled=false;
                }
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR,"Error al guardar","No se pudo Guardar",e.getMessage());
            }
        });

        HBox buttonsHBox = new HBox();

        buttonsHBox.getChildren().addAll(testConnectionButton,cancelButton,saveButton);

        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 30));
        vbox.getChildren().addAll(hostnameHBox,portHBox,usernameHBox,passwordHBox,buttonsHBox);

        root.getChildren().add(vbox);

        this.setScene(new Scene(root,320,180));

    }

    /**
     * Permite verificar la conexión a la base de datos con las credenciales definidas
     * @param hostname Dirección IP de la base de datos
     * @param port Puerto de la base de datos
     * @param username Nombre de usuario de la base de datos
     * @param password Contraseña del usuario de la base de datos
     * @throws SQLException
     */
    private void testConnection(String hostname, String port, String username, String password) throws SQLException {
        Connection con;
        String url = "jdbc:mysql://"+hostname+":"+port;
        con = DriverManager.getConnection(url, username, password); //attempting to connect to MySQL database
        con.close();
    }
}
