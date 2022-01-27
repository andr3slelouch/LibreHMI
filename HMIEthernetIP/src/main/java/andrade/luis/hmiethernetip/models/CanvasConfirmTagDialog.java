package andrade.luis.hmiethernetip.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class CanvasConfirmTagDialog extends Dialog {
    public CanvasConfirmTagDialog(GraphicalRepresentationData graphicalRepresentationData){
        DialogPane dialogPane = new DialogPane(){
            @Override
            protected Node createButtonBar(){
                ButtonBar buttonBar = (ButtonBar) super.createButtonBar();
                buttonBar.setButtonOrder(ButtonBar.BUTTON_ORDER_NONE);

                return buttonBar;
            }
        };

        dialogPane.setPrefWidth(650);
        dialogPane.setPrefHeight(650);


        final Label label = new Label("Address Book");
        label.setFont(new Font("Arial", 20));

        //CanvasTable<Tag> canvasTable = new CanvasTable<Tag>();
        TableView<Tag> table = new TableView<>();

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

        TableColumn namePLCColumn = new TableColumn("Nombre del PLC");
        //namePLCColumn.setCellFactory(new PropertyValueFactory("plcName"));
        TableColumn ipColumn = new TableColumn("IP");
        //ipColumn.setCellFactory(new PropertyValueFactory("plcAddress"));
        TableColumn groupColumn = new TableColumn("Grupo");
        //groupColumn.setCellFactory(new PropertyValueFactory("plcDeviceGroup"));
        TableColumn tagNameColumn = new TableColumn("Nombre del Tag");
        //tagNameColumn.setCellFactory(new PropertyValueFactory("tagName"));
        TableColumn typeColumn = new TableColumn("Tipo");
        //typeColumn.setCellFactory(new PropertyValueFactory("tagType"));
        TableColumn addressColumn = new TableColumn("Dirección");
        //addressColumn.setCellFactory(new PropertyValueFactory("tagAddress"));
        TableColumn actionColumn = new TableColumn("Acción");
        //actionColumn.setCellFactory(new PropertyValueFactory("tagAction"));
        TableColumn valueColumn = new TableColumn("Valor");
        //valueColumn.setCellFactory(new PropertyValueFactory("tagValue"));

        table.setItems(data);

        table.getColumns().addAll(namePLCColumn,
                ipColumn,
                groupColumn,
                tagNameColumn,
                typeColumn,
                addressColumn,
                actionColumn,
                valueColumn);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table);

        dialogPane.setContent(vbox);
        this.setDialogPane(dialogPane);
        dialogPane.getButtonTypes().addAll(ButtonType.OK);
        dialogPane.setContentText("Centered Button");

        Region spacer = new Region();
        ButtonBar.setButtonData(spacer,ButtonBar.ButtonData.BIG_GAP);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        dialogPane.applyCss();
        HBox hbox = (HBox) dialogPane.lookup(".container");
        hbox.getChildren().add(spacer);
    }
}

