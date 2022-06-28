package andrade.luis.librehmi.views.windows;

import andrade.luis.librehmi.models.users.HMIUser;
import andrade.luis.librehmi.models.users.HMIUserRow;
import andrade.luis.librehmi.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import static andrade.luis.librehmi.util.Alerts.showAlert;
import static javafx.scene.control.Alert.AlertType.CONFIRMATION;

/**
 * Ventana para la administración de usuarios
 */
public class ManageUsersWindow extends Stage {
    private final TableView<HMIUserRow> table;


    /**
     * Constructor de la ventana de administración de usuarios
     */
    public ManageUsersWindow(){
        StackPane root = new StackPane();

        setTitle("Seleccione el usuario a administrar");

        this.table= new TableView<>();

        TableColumn<HMIUserRow, String> firstNameColumn = new TableColumn<>("Nombre");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("first"));
        TableColumn<HMIUserRow, String> lastNameColumn = new TableColumn<>("Apellido");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("last"));
        TableColumn<HMIUserRow, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        TableColumn<HMIUserRow, String> usernameColumn = new TableColumn<>("Nombre de Usuario");
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        TableColumn<HMIUserRow, String> roleColumn = new TableColumn<>("Rol");
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        table.setItems(getUsers());

        setTableRowFactory();

        table.setPlaceholder(new Label("No existen Usuarios registrados"));

        table.getColumns().addAll(firstNameColumn,
                lastNameColumn,
                emailColumn,
                usernameColumn,
                roleColumn);

        TableView.TableViewSelectionModel<HMIUserRow> selectionModel = table.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        root.getChildren().add(table);
        Scene scene = new Scene(root,510,400);
        this.setScene(scene);
    }

    /**
     * Define la fábrica de filas de la tabla
     */
    private void setTableRowFactory(){
        table.setRowFactory(param -> new TableRow<>() {
            @Override
            public void updateItem(HMIUserRow row, boolean empty) {
                super.updateItem(row, empty);
                if (!empty) {
                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem newItem = createNewMenuItem();
                    MenuItem saveItem = createSaveMenuItem(row);
                    MenuItem deleteItem = createDeleteMenuItem(row);

                    contextMenu.getItems().addAll(newItem, saveItem, deleteItem);
                    setContextMenu(contextMenu);
                }
            }
        });
    }

    /**
     * Permite generar un menú para la creación de un nuevo usuario
     * @return MenuItem con el comportamiento de creación de nuevo usuario
     */
    private MenuItem createNewMenuItem(){
        MenuItem newItem = new MenuItem();
        newItem.setText("Nuevo");
        newItem.setOnAction(event -> {
            SignUpWindow signUpWindow = new SignUpWindow(null);
            signUpWindow.showAndWait();
            if (!signUpWindow.isCancelled()) table.setItems(getUsers());
        });
        return newItem;
    }

    /**
     * Permite generar un menú para la creación de menú de editar un usuario
     * @param row Fila de usuario con los datos de usuario a ser mostrado
     * @return MenuItem con el comportamiento de edición de usuario
     */
    private MenuItem createSaveMenuItem(HMIUserRow row){
        MenuItem saveItem = new MenuItem();
        saveItem.setText("Editar");
        saveItem.setOnAction(event -> {
            SignUpWindow signUpWindow = new SignUpWindow(row.getHmiUser());
            signUpWindow.showAndWait();
            if (!signUpWindow.isCancelled()) table.setItems(getUsers());
        });
        return saveItem;
    }

    /**
     * Permite generar un menú para eliminación de un usuario
     * @param row Fila de usuarios con los datos de usuario a ser eliminado
     * @return MenuItem con el comportamiento de eliminación de usuario
     */
    private MenuItem createDeleteMenuItem(HMIUserRow row){
        MenuItem deleteItem = new MenuItem();
        deleteItem.setText("Eliminar");
        deleteItem.setOnAction(event -> {
            try {
                if (showAlert(CONFIRMATION, "Confirmar eliminación de Usuario", "Esta seguro de eliminar el usuario '" + row.getUsername() + "'?") && row.getHmiUser().deleteFromDatabase())
                    table.setItems(getUsers());
            } catch (SQLException | IOException e) {
                Logger logger = Logger.getLogger(this.getClass().getName());
                logger.log(Level.INFO,e.getMessage());
            }
        });
        return deleteItem;
    }

    /**
     * Permite obtener los usuarios del sistema
     * @return Lista de usuarios a ser mostrada en la ventana
     */
    public ObservableList<HMIUserRow> getUsers() {
        String query = "SELECT u.first, u.last, u.email,u.username,u.role from Users u;";
        ObservableList<HMIUserRow> users = FXCollections.observableArrayList();
        try(Connection con = DBConnection.createConnectionToHMIUsers()){
            users=readUsers(con,users,query);
        } catch (SQLException | IOException e) {
            showAlert(Alert.AlertType.ERROR,"Error al conectarse a la base de datos",e.getMessage());
            log(e);
        }
        return users;
    }

    private void log(Exception e) {
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.log(Level.INFO, e.getMessage());
    }

    /**
     * Permite leer los usuarios desde la base de datos
     * @param con Conexión de base de datos
     * @param users Lista de usuarios
     * @param query Sentencia SQL a ejecutarse
     * @return Lista de usuarios
     */
    public ObservableList<HMIUserRow> readUsers(Connection con,ObservableList<HMIUserRow> users,String query) {
        try (Statement statement = con.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                users.add(new HMIUserRow(
                        new HMIUser(
                                resultSet.getString("first"),
                                resultSet.getString("last"),
                                resultSet.getString("email"),
                                resultSet.getString("username"),
                                resultSet.getString("role"),
                                ""
                        )
                ));
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error al conectarse a la base de datos", e.getMessage());
            log(e);
        }
        return users;
    }
}
