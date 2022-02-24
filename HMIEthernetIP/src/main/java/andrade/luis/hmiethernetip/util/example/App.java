package andrade.luis.hmiethernetip.util.example;

import javafx.application.Application;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        /*TableView<SceneItem> table = new TableView<>();
        table.setEditable(true);

        TableColumn<SceneItem, Boolean> selectedCol = new TableColumn<>("Select");
        selectedCol.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());

        selectedCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectedCol));

        table.getColumns().add(selectedCol);

        TableColumn<SceneItem, String> itemCol = new TableColumn<>("Item");
        itemCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        table.getColumns().add(itemCol);

        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // observable list of items that fires updates when the selectedProperty of
        // any item in the list changes:
        ObservableList<SceneItem> selectionList = FXCollections
                .observableArrayList(sceneItem -> new Observable[] { sceneItem.selectedProperty() });

        // bind contents to items selected in the table:
        table.getSelectionModel().getSelectedItems().addListener(
                (Change<? extends SceneItem> c) -> selectionList.setAll(table.getSelectionModel().getSelectedItems()));

        // add listener so that any updates in the selection list are propagated to all
        // elements:
        selectionList.addListener(new ListChangeListener<SceneItem>() {

            private boolean processingChange = false;

            @Override
            public void onChanged(Change<? extends SceneItem> c) {
                if (!processingChange) {
                    while (c.next()) {
                        if (c.wasUpdated() && c.getTo() - c.getFrom() == 1) {
                            boolean selectedVal = c.getList().get(c.getFrom()).isSelected();
                            processingChange = true;
                            table.getSelectionModel().getSelectedItems()
                                    .forEach(sceneItem -> sceneItem.setSelected(selectedVal));
                            processingChange = false;
                        }
                    }
                }
            }

        });

        for (int i = 1; i <= 20; i++) {
            //table.getItems().add(new SceneItem("Item " + i));
        }*/
        Button button = new Button("Get");
        //BorderPane borderPane = new BorderPane(table);
        //borderPane.getChildren().add(button);
        //Scene scene = new Scene(borderPane);
        //stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

