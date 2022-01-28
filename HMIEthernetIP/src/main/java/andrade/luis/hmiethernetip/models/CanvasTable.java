package andrade.luis.hmiethernetip.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class CanvasTable<T> extends TableView {
    public CanvasTable() {

        TableColumn<Tag, String> namePLCColumn = new TableColumn<>("Nombre del PLC");
        namePLCColumn.setCellFactory(new PropertyValueFactory("plcName"));
        TableColumn<Tag, String> ipColumn = new TableColumn("IP");
        ipColumn.setCellFactory(new PropertyValueFactory("plcAddress"));
        TableColumn<Tag, String> groupColumn = new TableColumn("Grupo");
        groupColumn.setCellFactory(new PropertyValueFactory("plcDeviceGroup"));
        TableColumn<Tag, String> tagNameColumn = new TableColumn("Nombre del Tag");
        tagNameColumn.setCellFactory(new PropertyValueFactory("tagName"));
        TableColumn<Tag, String> typeColumn = new TableColumn("Tipo");
        typeColumn.setCellFactory(new PropertyValueFactory("tagType"));
        TableColumn<Tag, String> addressColumn = new TableColumn("Dirección");
        addressColumn.setCellFactory(new PropertyValueFactory("tagAddress"));
        TableColumn<Tag, String> actionColumn = new TableColumn("Acción");
        actionColumn.setCellFactory(new PropertyValueFactory("tagAction"));
        TableColumn<Tag, String> valueColumn = new TableColumn("Valor");
        valueColumn.setCellFactory(new PropertyValueFactory("tagValue"));

        this.getColumns().addAll(namePLCColumn,
                ipColumn,
                groupColumn,
                tagNameColumn,
                typeColumn,
                addressColumn,
                actionColumn,
                valueColumn);

        ObservableList<Tag> data =
                FXCollections.observableArrayList(
                        new Tag("test",
                                "192.168.1.2",
                                "laboratorio",
                                "temperatura",
                                "Flotante",
                                "F3:0",
                                "Lectura",
                                "23.5"),
                        new Tag("test",
                                "192.168.1.3",
                                "pruebas",
                                "temperatura",
                                "Flotante",
                                "F5:1",
                                "Lectura",
                                "23.5")
                );
        this.setItems(data);
    }
}
