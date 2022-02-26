package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.models.Tag;
import andrade.luis.hmiethernetip.models.users.HMIUser;
import andrade.luis.hmiethernetip.models.users.HMIUserRow;
import andrade.luis.hmiethernetip.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;

public class ManageUsersWindow extends Stage {
    private final TableView.TableViewSelectionModel<HMIUserRow> selectionModel;
    private HMIUser adminUser;
    private HMIUser selectedHMIUserRow;
    public ManageUsersWindow(HMIUser adminUser){
        StackPane root = new StackPane();

        final Label label = new Label("Seleccione el Tag a ser asociado");
        label.setFont(new Font("Arial", 20));

        TableView<HMIUserRow> table= new TableView<>();;

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

        table.setRowFactory(param -> new TableRow<HMIUserRow>(){
            public void updateItem(HMIUserRow row, boolean empty){
                super.updateItem(row, empty);
                if(!empty){
                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem newItem = new MenuItem();
                    newItem.setText("Nuevo");
                    newItem.setOnAction(event -> {
                        SignUpWindow signUpWindow = new SignUpWindow(null);
                        signUpWindow.showAndWait();
                        if(!signUpWindow.isCancelled()) table.setItems(getUsers());
                    });
                    MenuItem saveItem = new MenuItem();
                    saveItem.setText("Editar");
                    saveItem.setOnAction(event -> {
                        SignUpWindow signUpWindow = new SignUpWindow(row.getHmiUser());
                        signUpWindow.showAndWait();
                        if(!signUpWindow.isCancelled()) table.setItems(getUsers());
                    });
                    MenuItem deleteItem = new MenuItem();
                    deleteItem.setText("Eliminar");
                    deleteItem.setOnAction(event -> {
                        try {
                            if(showAlert(CONFIRMATION,"Confirmar eliminación de Usuario","Esta seguro de eliminar el usuario '"+row.getUsername()+"'?")){
                                if(row.getHmiUser().deleteFromDatabase()) table.setItems(getUsers());
                            }
                        } catch (SQLException | IOException e) {
                            e.printStackTrace();
                        }
                    });

                    contextMenu.getItems().addAll(newItem,saveItem,deleteItem);
                    setContextMenu(contextMenu);
                }
            }
        });

        table.setPlaceholder(new Label("No existen Usuarios registrados"));

        table.getColumns().addAll(firstNameColumn,
                lastNameColumn,
                emailColumn,
                usernameColumn,
                roleColumn);

        selectionModel = table.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table);

        root.getChildren().add(vbox);
        Scene scene = new Scene(root,500,400);
        this.setScene(scene);
    }

    public ObservableList<HMIUserRow> getUsers() {
        String query = "SELECT u.first, u.last, u.email,u.username,u.role from Users u;";
        ObservableList<HMIUserRow> users = FXCollections.observableArrayList();
        try(Connection con = DBConnection.createConnectionToHMIUsers()){
            Statement statement = con.createStatement();
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
        } catch (SQLException | IOException e) {
            showAlert(Alert.AlertType.ERROR,"Error al conectarse a la base de datos",e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    public void setSelectedUser(){
        ObservableList<HMIUserRow> selected = selectionModel.getSelectedItems();
        Logger logger = Logger.getLogger(this.getClass().getName());

        if(!selected.isEmpty()){
            this.selectedHMIUserRow = new HMIUser(selected.get(0).getFirst(),selected.get(0).getLast(),selected.get(0).getEmail(),selected.get(0).getUsername(),selected.get(0).getRole(),"");
            this.close();
        }else{
            showAlert(Alert.AlertType.WARNING,"Advertencia","Debe seleccionar un usuario de la tabla para continuar");
        }
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
