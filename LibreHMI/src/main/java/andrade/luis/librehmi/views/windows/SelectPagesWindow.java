package andrade.luis.librehmi.views.windows;

import andrade.luis.librehmi.models.SceneItem;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * Ventana de selección de ventanas
 */
public class SelectPagesWindow extends Stage {
    private final ArrayList<SceneItem> items = new ArrayList<>();
    private final ArrayList<String> selectedItems = new ArrayList<>();

    /**
     * Constructor de la ventana
     * @param itemsForList ArrayList de páginas disponibles en el proyecto
     * @param selectedItems ArrayList de páginas seleccionadas
     */
    public SelectPagesWindow(ArrayList<String> itemsForList, ArrayList<String> selectedItems) {
        StackPane root = new StackPane();
        setTitle("Seleccione una Ventana");

        VBox checkBoxesVBox = new VBox();
        checkBoxesVBox.setSpacing(15);

        int height = 50;
        for (String hmiScene : itemsForList) {
            CheckBox box = new CheckBox();
            Label sceneName = new Label(hmiScene);
            HBox checkBoxHBox = new HBox();
            checkBoxHBox.setSpacing(15);
            checkBoxHBox.getChildren().addAll(box, sceneName);
            checkBoxesVBox.getChildren().addAll(checkBoxHBox);
            items.add(new SceneItem(hmiScene,box));
            height = height + 32;
        }

        if(selectedItems!=null){
            for(SceneItem item : items) {
                for(String selectedScene : selectedItems) {
                    if(selectedScene.equals(item.getScene())){
                        item.setSelected(true);
                    }
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
        vbox.getChildren().addAll(checkBoxesVBox,buttonHBox);
        vbox.setSpacing(15);
        vbox.setPadding(new Insets(5,5,10,5));
        root.getChildren().add(vbox);

        this.setScene(new Scene(root,400, height));

    }

    /**
     * Permite seleccionar o no seleccionar todas las páginas
     * @param selected Bandera para indicar todas las páginas
     */
    private void selectAllPages(boolean selected) {
        for(SceneItem sceneItem : items){
            sceneItem.setSelected(selected);
        }
    }

    /**
     * Permite definir los items seleccionados
     */
    private void setSelectedItems() {
        selectedItems.clear();
        for(SceneItem sceneItem : items){
            if(sceneItem.isSelected()){
                selectedItems.add(sceneItem.getScene());
            }
        }
    }

    public ArrayList<String> getSelectedItems() {
        return selectedItems;
    }
}

