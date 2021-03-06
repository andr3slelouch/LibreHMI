package andrade.luis.librehmi.views.windows;

import andrade.luis.librehmi.models.Tag;
import andrade.luis.librehmi.models.TagRow;
import andrade.luis.librehmi.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
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


import static andrade.luis.librehmi.util.Alerts.showAlert;
import static javafx.scene.control.Alert.AlertType.CONFIRMATION;

/**
 * Ventana de selección de tag
 */
public class SelectTagWindow extends Stage {
    private final TableView.TableViewSelectionModel<TagRow> selectionModel;
    private final TableView<TagRow> table;
    private final Button finishSelectionButton;

    public ArrayList<Tag> getLocalTags() {
        return localTags;
    }

    public void setLocalTags(ArrayList<Tag> localTags) {
        this.localTags = localTags;
    }

    private ArrayList<Tag> localTags;
    private Alert alert;

    public boolean isCancelled() {
        return cancelled;
    }

    private boolean cancelled = true;

    public Tag getSelectedTag() {
        return selectedTagRow;
    }

    private Tag selectedTagRow;

    /**
     * Constructor de la ventana
     * @param inputMode Permite filtrar los tags a aquellos que solo sean de escritura
     * @param filter Permite aplica un filtro de tags basado en su tipo
     * @param testMode Bandera para mostrar la ventana en modo test
     * @param localTags ArrayList de tags locales
     */
    public SelectTagWindow(boolean inputMode, String filter, boolean testMode, ArrayList<Tag> localTags) {
        StackPane root = new StackPane();

        this.localTags = localTags;

        table = new TableView<>();

        TableColumn<TagRow, String> namePLCColumn = new TableColumn<>("Nombre del PLC");
        namePLCColumn.setPrefWidth(120);
        namePLCColumn.setCellValueFactory(new PropertyValueFactory<>("plcName"));
        TableColumn<TagRow, String> ipColumn = new TableColumn<>("IP");
        ipColumn.setCellValueFactory(new PropertyValueFactory<>("plcAddress"));
        TableColumn<TagRow, String> groupColumn = new TableColumn<>("Grupo");
        groupColumn.setCellValueFactory(new PropertyValueFactory<>("plcDeviceGroup"));
        TableColumn<TagRow, String> tagNameColumn = new TableColumn<>("Nombre del Tag");
        tagNameColumn.setPrefWidth(120);
        tagNameColumn.setCellValueFactory(new PropertyValueFactory<>("tagName"));
        TableColumn<TagRow, String> typeColumn = new TableColumn<>("Tipo");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("tagType"));
        TableColumn<TagRow, String> addressColumn = new TableColumn<>("Dirección");
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("tagAddress"));
        TableColumn<TagRow, String> actionColumn = new TableColumn<>("Acción");
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("tagAction"));
        TableColumn<TagRow, String> valueColumn = new TableColumn<>("Valor");
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("tagValue"));
        this.finishSelectionButton = new Button("OK");
        ObservableList<TagRow> existingTagsObsList = initExistingTags(inputMode, filter);

        if (localTags != null) {
            for (Tag localTag : localTags) {
                TagRow row = new TagRow(
                        localTag.getPlcName(),
                        localTag.getPlcAddress(),
                        localTag.getPlcDeviceGroup(),
                        localTag.getName(),
                        localTag.getType(),
                        localTag.getAddress(),
                        localTag.getAction()
                );
                existingTagsObsList.add(row);
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
        vbox.getChildren().addAll(table);


        finishSelectionButton.setAlignment(Pos.CENTER);
        finishSelectionButton.setOnAction(actionEvent -> {
            try {
                setSelectedTag();
            } catch (SQLException | IOException e) {
                log(e.getMessage());
            }
        });
        HBox hbox = new HBox();
        hbox.getChildren().add(finishSelectionButton);
        hbox.setAlignment(Pos.BASELINE_RIGHT);
        vbox.getChildren().add(hbox);
        vbox.setPadding(new Insets(5, 5, 5, 5));
        root.getChildren().add(vbox);
        Scene scene = new Scene(root, 650, 400);
        this.setScene(scene);

    }

    /**
     * Permite inicializar los tags que existen desde la base de datos
     * @param inputMode Bandera para filtrar solamente los tags de escritura
     * @param filter Filtro para filtrar por tipo
     * @return Lista de Tags existentes
     */
    private ObservableList<TagRow> initExistingTags(boolean inputMode, String filter) {
        ObservableList<TagRow> existingTagsObsList = FXCollections.observableArrayList();
        if (filter.equals("LocalTags")) {
            setTitle("Seleccione un Tag Local a modificar");
            table.setPlaceholder(new Label("No existen Tags Locales definidos en el proyecto"));
            table.setRowFactory(param -> new TableRow<>() {
                @Override
                public void updateItem(TagRow row, boolean empty) {
                    super.updateItem(row, empty);
                    if (!empty) {
                        ContextMenu contextMenu = new ContextMenu();
                        MenuItem newItem = createNewMenuItem();
                        MenuItem saveItem = createSaveMenuItem(row, super.getIndex());
                        MenuItem deleteItem = createDeleteMenuItem(row, super.getIndex());
                        contextMenu.getItems().addAll(newItem, saveItem, deleteItem);
                        setContextMenu(contextMenu);
                    }
                }
            });
            finishSelectionButton.setVisible(false);
        } else {
            existingTagsObsList = getExistingTags(inputMode, filter);
            setTitle("Seleccione el Tag a ser asociado");
            table.setPlaceholder(new Label("No existen Tags definidos en la base de datos"));
        }
        return existingTagsObsList;
    }

    /**
     * Permite crear el menú de creación de nuevo tag local
     * @return MenuItem con el comportamiento para la creación de un tag local
     */
    private MenuItem createNewMenuItem() {
        MenuItem newItem = new MenuItem();
        newItem.setText("Nuevo");
        newItem.setOnAction(event -> {

            try {
                ManageLocalTagWindow manageLocalTagWindow = new ManageLocalTagWindow(null);
                manageLocalTagWindow.showAndWait();
                if (manageLocalTagWindow.getTag() != null) {
                    localTags.add(manageLocalTagWindow.getTag());
                }
            } catch (SQLException | IOException e) {
                log(e.getMessage());
            }

        });
        return newItem;
    }

    /**
     * Permite crear el menu de edición de tag local
     * @param row Fila del tag a ser editado
     * @param index Índice del tag a ser editado
     * @return MenuItem con el comportamiento de edición de tag local
     */
    private MenuItem createSaveMenuItem(TagRow row, int index) {
        MenuItem saveItem = new MenuItem();
        saveItem.setText("Editar");
        saveItem.setOnAction(event -> {

            try {
                ManageLocalTagWindow manageLocalTagWindow = new ManageLocalTagWindow(row.generateTag());
                manageLocalTagWindow.showAndWait();
                if (manageLocalTagWindow.getTag() != null) {
                    localTags.set(index, manageLocalTagWindow.getTag());
                }
            } catch (SQLException | IOException e) {
                log(e.getMessage());
            }

        });
        return saveItem;
    }

    /**
     * Permite crear un menu de eliminación de tag local
     * @param row Fila del tag a ser eliminado
     * @param index Índice del tag a ser eliminado
     * @return MenuItem con el comportamiento de eliminación de tag local
     */
    private MenuItem createDeleteMenuItem(TagRow row, int index) {
        MenuItem deleteItem = new MenuItem();
        deleteItem.setText("Eliminar");
        deleteItem.setOnAction(event -> {
            if (showAlert(CONFIRMATION, "Confirmar eliminación de Tag Local", "Esta seguro de eliminar el Tag '" + row.getTagName() + "'? Al ser eliminado se limitará que este Tag pueda ser enlazado\na futuras figuras,sin embargo en aquellas que ya fue enlazado previamente conservará su último\nestado actual, se recomienda actualizar las figuras afectadas a otro Tag disponible")) {
                localTags.remove(index);
            }
        });
        return deleteItem;
    }

    /**
     * Permite obtener una lista de filas de tag con los tags existentes
     * @param inputMode Bandera de definición de filtro de tags de escritura
     * @param filter Filtro para aplicar en cuanto al tipo
     * @return Lista de filas para la tabla
     */
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
            log(e.getMessage());
        }
        return data;
    }

    /**
     * Permite leer los tags desde la base de datos
     * @param con Conexión a la base de datos
     * @param data Lista de filas de tags
     * @param query Sentencia SQL
     */
    private void readTags(Connection con, ObservableList<TagRow> data, String query) {
        log(query);
        try (Statement statement = con.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                log(resultSet.getString("nombreTag"));
                TagRow row = new TagRow(
                        resultSet.getString("plcNombre"),
                        resultSet.getString("direccionIP"),
                        resultSet.getString("deviceGroup"),
                        resultSet.getString("nombreTag"),
                        resultSet.getString("tipoTag"),
                        resultSet.getString("tag"),
                        resultSet.getString("accion"));
                data.add(row);
                log("Size:"+ data.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log("Error cacthed"+e.getMessage());
        }
    }

    private void log(String e) {
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.log(Level.INFO, e);
    }

    /**
     * Permite definir el tag seleccionado
     */
    public void setSelectedTag() throws SQLException, IOException {
        ObservableList<TagRow> selected = selectionModel.getSelectedItems();
        if (!selected.isEmpty()) {
            this.selectedTagRow = selected.get(0).generateTag();
            this.cancelled = false;
            this.close();
        } else {
            confirmExit();
        }
    }

    /**
     * Permite mostrar una ventana de confirmación de selección de tag
     */
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

    /**
     * Permite mostrar una ventana de advertencia
     */
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

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }


}

