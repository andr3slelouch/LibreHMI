package andrade.luis.libreHMI.views.windows;

import andrade.luis.libreHMI.models.users.HMIUser;
import andrade.luis.libreHMI.models.users.HMIUserRow;
import andrade.luis.libreHMI.util.DBConnection;
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
import java.util.Optional;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;

public class ManageUsersWindow extends Stage {
    private final TableView<HMIUserRow> table;


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

    private MenuItem createDeleteMenuItem(HMIUserRow row){
        MenuItem deleteItem = new MenuItem();
        deleteItem.setText("Eliminar");
        deleteItem.setOnAction(event -> {
            try {
                if (showAlert(CONFIRMATION, "Confirmar eliminación de Usuario", "Esta seguro de eliminar el usuario '" + row.getUsername() + "'?")) {
                    if (row.getHmiUser().deleteFromDatabase()) table.setItems(getUsers());
                }
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        });
        return deleteItem;
    }

    public ObservableList<HMIUserRow> getUsers() {
        String query = "SELECT u.first, u.last, u.email,u.username,u.role from Users u;";
        ObservableList<HMIUserRow> users = FXCollections.observableArrayList();
        try(Connection con = DBConnection.createConnectionToHMIUsers()){
            users=readUsers(con,users,query);
        } catch (SQLException | IOException e) {
            showAlert(Alert.AlertType.ERROR,"Error al conectarse a la base de datos",e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    public ObservableList<HMIUserRow> readUsers(Connection con,ObservableList<HMIUserRow> users,String query){
        try(Statement statement = con.createStatement()){
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
        }catch(Exception e){
            showAlert(Alert.AlertType.ERROR,"Error al conectarse a la base de datos",e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    private boolean showAlert(Alert.AlertType type,String title,String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(message);

        ButtonType okButton = new ButtonType("Sí",ButtonBar.ButtonData.YES);
        ButtonType cancelButton = new ButtonType("No",ButtonBar.ButtonData.NO);

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
