package andrade.luis.hmiethernetip.models;

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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SelectTagWindow extends Stage {
    private final TableView.TableViewSelectionModel<Tag> selectionModel;

    public Tag getSelectedTag() {
        return selectedTag;
    }

    private Tag selectedTag;
    public SelectTagWindow() {
        StackPane root = new StackPane();


        final Label label = new Label("Seleccione el Tag a ser asociado");
        label.setFont(new Font("Arial", 20));

        TableView<Tag> table = new TableView<>();

        TableColumn<Tag, String> namePLCColumn = new TableColumn<>("Nombre del PLC");
        namePLCColumn.setCellValueFactory(new PropertyValueFactory<>("plcName"));
        TableColumn<Tag, String> ipColumn = new TableColumn<>("IP");
        ipColumn.setCellValueFactory(new PropertyValueFactory<>("plcAddress"));
        TableColumn<Tag, String> groupColumn = new TableColumn<>("Grupo");
        groupColumn.setCellValueFactory(new PropertyValueFactory<>("plcDeviceGroup"));
        TableColumn<Tag, String> tagNameColumn = new TableColumn<>("Nombre del Tag");
        tagNameColumn.setCellValueFactory(new PropertyValueFactory<>("tagName"));
        TableColumn<Tag, String> typeColumn = new TableColumn<>("Tipo");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("tagType"));
        TableColumn<Tag, String> addressColumn = new TableColumn<>("Dirección");
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("tagAddress"));
        TableColumn<Tag, String> actionColumn = new TableColumn<>("Acción");
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("tagAction"));
        TableColumn<Tag, String> valueColumn = new TableColumn<>("Valor");
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("tagValue"));

        table.setItems(getExistingTags());

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

        Button button = new Button("OK");
        button.setAlignment(Pos.CENTER);
        button.setOnAction(actionEvent -> setSelectedTag());
        HBox hbox = new HBox();
        hbox.getChildren().add(button);
        hbox.setAlignment(Pos.BASELINE_RIGHT);
        vbox.getChildren().add(hbox);
        root.getChildren().add(vbox);
        Scene scene = new Scene(root,500,400);
        this.setScene(scene);

    }

    private ObservableList<Tag> getExistingTags() {
        String query = "SELECT p.plcNombre, p.direccionIP,p.deviceGroup,t.nombreTag,t.tipoTag,t.tag,t.accion from PLCS p , TAGS t, INTERMEDIA i WHERE p.idPLCS = i.idPLCS  AND t.idTAGS = i.idTAGS ";
        ObservableList<Tag> data = FXCollections.observableArrayList();
        try (Connection con = DBConnection.createConnection()) {
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                data.add(new Tag(
                        resultSet.getString("plcNombre"),
                        resultSet.getString("direccionIP"),
                        resultSet.getString("deviceGroup"),
                        resultSet.getString("nombreTag"),
                        resultSet.getString("tipoTag"),
                        resultSet.getString("tag"),
                        resultSet.getString("accion"),
                        ""));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void setSelectedTag(){
        ObservableList<Tag> selected = selectionModel.getSelectedItems();
        Logger logger = Logger.getLogger(this.getClass().getName());

        if(!selected.isEmpty()){
            logger.log(Level.INFO,selected.get(0).getTagName());
            this.selectedTag = selected.get(0);
            this.close();
        }else{
            confirmExit();
        }
    }

    public void confirmExit(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
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
}

