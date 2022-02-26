package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.controllers.HMIScene;
import andrade.luis.hmiethernetip.util.example.SceneItem;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class SelectWindowsWindow extends Stage {
    private final StackPane root;
    private ArrayList<SceneItem> items = new ArrayList<>();
    private ArrayList<HMIScene> selectedItems = new ArrayList<>();
    private int height = 50;

    public SelectWindowsWindow(ArrayList<HMIScene> itemsForList, ArrayList<HMIScene> selectedItems) {
        root = new StackPane();
        final Label label = new Label("Seleccione una Ventana");

        VBox checkBoxesVBox = new VBox();
        checkBoxesVBox.setSpacing(15);

        for (HMIScene hmiScene : itemsForList) {
            CheckBox box = new CheckBox();
            Label sceneName = new Label(hmiScene.getTitle());
            HBox checkBoxHBox = new HBox();
            checkBoxHBox.setSpacing(15);
            checkBoxHBox.getChildren().addAll(box, sceneName);
            checkBoxesVBox.getChildren().addAll(checkBoxHBox);
            items.add(new SceneItem(hmiScene,box));
            height= height + 50;
        }

        for(SceneItem item : items) {
            for(HMIScene selectedScene : selectedItems) {
                if(selectedScene.getTitle().equals(item.getScene().getTitle())){
                    item.setSelected(true);
                }
            }
        }
        
        setSelectedItems();

        Button okButton = new Button("Aceptar");
        Button cancelButton = new Button("Cancelar");
        Button selectAllButton = new Button("Seleccionar Todo");
        Button clearAllButton = new Button("Limpiar Todo");

        HBox buttonHBox = new HBox();
        buttonHBox.getChildren().addAll(okButton,cancelButton,selectAllButton,clearAllButton);
        buttonHBox.setAlignment(Pos.CENTER);

        okButton.setOnAction(mouseEvent -> {
            setSelectedItems();
            this.close();
        });
        cancelButton.setOnAction(mouseEvent -> this.close());
        selectAllButton.setOnAction(mouseEvent -> selectAllPages(true));
        clearAllButton.setOnAction(mouseEvent -> selectAllPages(false));
        VBox vbox = new VBox();
        vbox.getChildren().addAll(label,checkBoxesVBox,buttonHBox);
        vbox.setSpacing(15);
        root.getChildren().add(vbox);

        this.setScene(new Scene(root,400,height));

    }

    private void selectAllPages(boolean selected) {
        for(SceneItem sceneItem : items){
            sceneItem.setSelected(selected);
        }
    }

    private void setSelectedItems() {
        for(SceneItem sceneItem : items){
            if(sceneItem.isSelected()){
                selectedItems.add(sceneItem.getScene());
            }
        }
    }

    public ArrayList<HMIScene> getSelectedItems() {
        return selectedItems;
    }
}
