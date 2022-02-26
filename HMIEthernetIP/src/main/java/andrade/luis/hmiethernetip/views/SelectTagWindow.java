package andrade.luis.hmiethernetip.views;

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
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SelectTagWindow extends Stage {
    private final TableView.TableViewSelectionModel<TagRow> selectionModel;
    private TableView<TagRow> table;
    private Button finishSelectionButton;
    private Alert alert;
    public Tag getSelectedTag() {
        return selectedTagRow;
    }

    private Tag selectedTagRow;
    public SelectTagWindow(boolean testMode) {
        StackPane root = new StackPane();


        final Label label = new Label("Seleccione el Tag a ser asociado");
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

        if(getExistingTags().isEmpty() || testMode){
            setAlertIfTableIsEmpty();
        }else{
            table.setItems(getExistingTags());
        }
        table.setPlaceholder(new Label("No existen Tags definidos en la base de datos"));

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
        Scene scene = new Scene(root,500,400);
        this.setScene(scene);

    }

    public ObservableList<TagRow> getExistingTags() {
        String query = "SELECT p.plcNombre, p.direccionIP,p.deviceGroup,t.nombreTag,t.tipoTag,t.tag,t.accion from plcs p , tags t, intermedia i WHERE p.idPLCS = i.idPLCS  AND t.idTAGS = i.idTAGS ";
        ObservableList<TagRow> data = FXCollections.observableArrayList();
        try (Connection con = DBConnection.createConnectionToBDDriverEIP()) {
            Statement statement = con.createStatement();
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
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void setSelectedTag(){
        ObservableList<TagRow> selected = selectionModel.getSelectedItems();
        Logger logger = Logger.getLogger(this.getClass().getName());

        if(!selected.isEmpty()){
            logger.log(Level.INFO,selected.get(0).getTagName());
            this.selectedTagRow = new Tag(selected.get(0).getPlcName(),selected.get(0).getPlcAddress(),selected.get(0).getPlcDeviceGroup(),selected.get(0).getTagName(),selected.get(0).getTagType(),selected.get(0).getTagAddress(),selected.get(0).getTagAction(),selected.get(0).getTagValue());
            this.close();
        }else{
            confirmExit();
        }
    }

    public void confirmExit(){
        alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText("Debe seleccionar un Tag de la tabla para continuar");

        ButtonType okButton = new ButtonType("OK",ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(okButton);

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == okButton)
        {
            alert.close();
        }
    }

    public void setAlertIfTableIsEmpty(){
        alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText("No existen tags disponibles");

        ButtonType okButton = new ButtonType("OK",ButtonBar.ButtonData.OK_DONE);
        
        alert.getButtonTypes().setAll(okButton);

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == okButton)
        {
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
}

