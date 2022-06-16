package andrade.luis.hmiethernetip.views.windows;

import andrade.luis.hmiethernetip.models.Tag;
import andrade.luis.hmiethernetip.models.TagRow;
import andrade.luis.hmiethernetip.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;

public class SelectTagWindow extends Stage {
    private final TableView.TableViewSelectionModel<TagRow> selectionModel;
    private TableView<TagRow> table;

    public ArrayList<Tag> getLocalTags() {
        return localTags;
    }

    public void setLocalTags(ArrayList<Tag> localTags) {
        this.localTags = localTags;
    }

    private ArrayList<Tag> localTags;
    private Button finishSelectionButton;
    private Alert alert;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    private boolean cancelled = true;

    public Tag getSelectedTag() {
        return selectedTagRow;
    }

    private Tag selectedTagRow;

    public SelectTagWindow(boolean inputMode, String filter, boolean testMode, ArrayList<Tag> localTags) {
        StackPane root = new StackPane();

        this.localTags = localTags;
        Label label = new Label("");
        label.setFont(new Font("Arial", 20));

        table = new TableView<>();

        TableColumn<TagRow, String> namePLCColumn = new TableColumn<>("Nombre del PLC");
        namePLCColumn.setCellValueFactory(new PropertyValueFactory<>("plcName"));
        TableColumn<TagRow, String> ipColumn = new TableColumn<>("IP");
        ipColumn.setCellValueFactory(new PropertyValueFactory<>("plcAddress"));
        TableColumn<TagRow, String> groupColumn = new TableColumn<>("Grupo");
        groupColumn.setCellValueFactory(new PropertyValueFactory<>("plcDeviceGroup"));
        TableColumn<TagRow, String> tagNameColumn = new TableColumn<>("Nombre del Tag");
        tagNameColumn.setCellValueFactory(new PropertyValueFactory<>("tagName"));
        TableColumn<TagRow, String> typeColumn = new TableColumn<>("Tipo");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("tagType"));
        TableColumn<TagRow, String> addressColumn = new TableColumn<>("Dirección");
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("tagAddress"));
        TableColumn<TagRow, String> actionColumn = new TableColumn<>("Acción");
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("tagAction"));
        TableColumn<TagRow, String> valueColumn = new TableColumn<>("Valor");
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("tagValue"));

        ObservableList<TagRow> existingTagsObsList = FXCollections.observableArrayList();
        if (filter.equals("LocalTags")) {
            label = new Label("Seleccione un Tag Local a modificar");
            table.setPlaceholder(new Label("No existen Tags Locales definidos en el proyecto"));
            table.setRowFactory(param -> new TableRow<>() {
                @Override
                public void updateItem(TagRow row, boolean empty) {
                    super.updateItem(row, empty);
                    if (!empty) {
                        ContextMenu contextMenu = new ContextMenu();
                        MenuItem newItem = new MenuItem();
                        newItem.setText("Nuevo");
                        newItem.setOnAction(event -> {
                            ManageLocalTagWindow manageLocalTagWindow = new ManageLocalTagWindow(null);
                            manageLocalTagWindow.showAndWait();
                            if (manageLocalTagWindow.getTag() != null) {
                                localTags.add(manageLocalTagWindow.getTag());
                            }
                        });
                        MenuItem saveItem = new MenuItem();
                        saveItem.setText("Editar");
                        saveItem.setOnAction(event -> {
                            ManageLocalTagWindow manageLocalTagWindow = new ManageLocalTagWindow(row.generateTag());
                            manageLocalTagWindow.showAndWait();
                            if (manageLocalTagWindow.getTag() != null) {
                                localTags.set(super.getIndex(), manageLocalTagWindow.getTag());
                            }
                        });
                        MenuItem deleteItem = new MenuItem();
                        deleteItem.setText("Eliminar");
                        deleteItem.setOnAction(event -> {
                            if (showAlert(CONFIRMATION, "Confirmar eliminación de Tag Local", "Esta seguro de eliminar el Tag '" + row.getTagName() + "'? Al ser eliminado se limitará que este Tag pueda ser enlazado\na futuras figuras,sin embargo en aquellas que ya fue enlazado previamente conservará su último\nestado actual, se recomienda actualizar las figuras afectadas a otro Tag disponible")) {
                                localTags.remove(super.getIndex());
                            }
                        });

                        contextMenu.getItems().addAll(newItem, saveItem, deleteItem);
                        setContextMenu(contextMenu);
                    }
                }
            });
        } else {
            existingTagsObsList = getExistingTags(inputMode, filter);
            label = new Label("Seleccione el Tag a ser asociado");
            table.setPlaceholder(new Label("No existen Tags definidos en la base de datos"));
        }
        if (localTags != null) {
            for (Tag localTag : localTags) {
                existingTagsObsList.add(new TagRow(
                        localTag.getPlcName(),
                        localTag.getPlcAddress(),
                        localTag.getPlcDeviceGroup(),
                        localTag.getName(),
                        localTag.getType(),
                        localTag.getAddress(),
                        localTag.getAction(),
                        localTag.getValue()
                ));
            }
        }
        if (existingTagsObsList.isEmpty() || testMode) {
            setAlertIfTableIsEmpty();
        } else {
            table.setItems(existingTagsObsList);
        }

        table.getColumns().addAll(namePLCColumn,
                ipColumn,
                groupColumn,
                tagNameColumn,
                typeColumn,
                addressColumn,
                actionColumn);

        selectionModel = table.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table);

        finishSelectionButton = new Button("OK");
        finishSelectionButton.setAlignment(Pos.CENTER);
        finishSelectionButton.setOnAction(actionEvent -> setSelectedTag());
        HBox hbox = new HBox();
        hbox.getChildren().add(finishSelectionButton);
        hbox.setAlignment(Pos.BASELINE_RIGHT);
        vbox.getChildren().add(hbox);
        root.getChildren().add(vbox);
        Scene scene = new Scene(root, 500, 400);
        this.setScene(scene);

    }

    public ObservableList<TagRow> getExistingTags(boolean inputMode, String filter) {
        String query = "SELECT p.plcNombre, p.direccionIP,p.deviceGroup,t.nombreTag,t.tipoTag,t.tag,t.accion from plcs p , tags t, intermedia i WHERE p.idPLCS = i.idPLCS  AND t.idTAGS = i.idTAGS ";
        if (inputMode) {
            query = query + "AND t.accion = 'Escritura' ";
        }

        switch (filter) {
            case "bool":
                query = query + "AND t.tipoTag = 'Bool' ";
                break;
            case "numbers":
                query = query + "AND t.tipoTag != 'Bool' ";
                break;
            default:
                break;
        }
        ObservableList<TagRow> data = FXCollections.observableArrayList();
        try (Connection con = DBConnection.createConnectionToBDDriverEIP()) {
            readTags(con, data, query);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private ObservableList<TagRow> readTags(Connection con, ObservableList<TagRow> data, String query) {
        try (Statement statement = con.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                data.add(new TagRow(
                        resultSet.getString("plcNombre"),
                        resultSet.getString("direccionIP"),
                        resultSet.getString("deviceGroup"),
                        resultSet.getString("nombreTag"),
                        resultSet.getString("tipoTag"),
                        resultSet.getString("tag"),
                        resultSet.getString("accion"),
                        ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public void setSelectedTag() {
        ObservableList<TagRow> selected = selectionModel.getSelectedItems();
        Logger logger = Logger.getLogger(this.getClass().getName());

        if (!selected.isEmpty()) {
            logger.log(Level.INFO, selected.get(0).getTagName());
            //this.selectedTagRow = new Tag(selected.get(0).getPlcName(),selected.get(0).getPlcAddress(),selected.get(0).getPlcDeviceGroup(),selected.get(0).getTagName(),selected.get(0).getTagType(),selected.get(0).getTagAddress(),selected.get(0).getTagAction(),selected.get(0).getTagValue(),0);
            this.selectedTagRow = selected.get(0).generateTag();
            this.cancelled = false;
            this.close();
        } else {
            confirmExit();
        }
    }

    public void confirmExit() {
        alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText("Debe seleccionar un Tag de la tabla para continuar");

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(okButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == okButton) {
            alert.close();
        }
    }

    public void setAlertIfTableIsEmpty() {
        alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText("No existen tags disponibles");

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(okButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == okButton) {
            alert.close();
            this.close();
        }
    }

    public TableView<TagRow> getTable() {
        return table;
    }

    public void setTable(TableView<TagRow> table) {
        this.table = table;
    }

    public Button getFinishSelectionButton() {
        return finishSelectionButton;
    }

    public void setFinishSelectionButton(Button finishSelectionButton) {
        this.finishSelectionButton = finishSelectionButton;
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

    private boolean showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(message);

        ButtonType okButton = new ButtonType("Sí", ButtonBar.ButtonData.YES);
        ButtonType cancelButton = new ButtonType("No", ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(cancelButton, okButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == okButton) {
            alert.close();
            return true;
        } else if (result.isPresent() && result.get() == cancelButton) {
            alert.close();
            return false;
        }
        return false;
    }
}

