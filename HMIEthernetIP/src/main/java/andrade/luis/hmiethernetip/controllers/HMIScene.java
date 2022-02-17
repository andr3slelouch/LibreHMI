package andrade.luis.hmiethernetip.controllers;

import andrade.luis.hmiethernetip.models.GraphicalRepresentation;
import andrade.luis.hmiethernetip.models.CanvasPoint;
import andrade.luis.hmiethernetip.views.HMICanvas;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.paint.Paint;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Logger;

public class HMIScene extends Scene {
    Logger logger
            = Logger.getLogger(
            HMIScene.class.getName());
    //private final Image windowImage = new Image(getClass().getResource("images/window.png").toExternalForm());
    private final Image windowImage = new Image("https://sc01.alicdn.com/kf/HTB1gevJvuySBuNjy1zdq6xPxFXaO/201140127/HTB1gevJvuySBuNjy1zdq6xPxFXaO.jpg");
    /*
    * windowImage source <a href="https://www.flaticon.com/free-icons/window" title="window icons">Window icons created by Anas Mannaa - Flaticon</a>
    * */
    private HMICanvas hmiCanvas;

    public ArrayList<String> getItemsForComboBox() {
        return itemsForComboBox;
    }

    public void setItems(ArrayList<String> itemsForComboBox) {
        this.itemsForComboBox = itemsForComboBox;
        comboBox.setItems(FXCollections
                .observableArrayList(itemsForComboBox));
        ObservableList<String> elements = FXCollections.observableArrayList(itemsForComboBox);
        comboBox.getSelectionModel().select(0);

        listViewReference.setItems(elements);
        /*setting each image to corresponding array index*/
        listViewReference.setCellFactory(param -> new ListCell<String>() {
            /*view the image class to display the image*/
            private ImageView displayImage = new ImageView();
            @Override
            public void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    System.out.println("Updating item");
                    displayImage.setImage(windowImage);
                    setText(name);
                    setGraphic(displayImage);
                }
            }
        });
        ImageView imageView = new ImageView(windowImage);

    }

    public ComboBox getComboBox() {
        return comboBox;
    }

    public void setComboBox(ComboBox comboBox) {
        this.comboBox = comboBox;
    }

    public ListView<String> getListViewReference() {
        return listViewReference;
    }

    public void setListViewReference(ListView<String> listViewReference) {
        this.listViewReference = listViewReference;
    }

    private ListView<String> listViewReference;
    private ArrayList<String> itemsForComboBox;
    private ComboBox comboBox;

    public HMICanvas getCanvas() {
        return hmiCanvas;
    }

    public void setCanvas(HMICanvas HMICanvas) {
        this.hmiCanvas = HMICanvas;
    }

    public HMIScene(HMICanvas hmiCanvas, double v, double v1, Paint paint) {
        super(hmiCanvas, v, v1, paint);
        this.hmiCanvas = hmiCanvas;
        this.setOnMouseClicked(mouseEvent -> {
            if (this.hmiCanvas.isAddOnClickEnabled()) {
                hmiCanvas.addFigureOnCanvasClicked(new CanvasPoint(mouseEvent.getX(), mouseEvent.getY()));
                this.hmiCanvas.setAddOnClickEnabled(false);
            }else if(mouseEvent.getButton() == MouseButton.SECONDARY){
                hmiCanvas.onCanvasClicked(new CanvasPoint(mouseEvent.getScreenX(), mouseEvent.getScreenY()));
            }
            updateSelected();
        });
        this.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.DELETE) {
                GraphicalRepresentation selected = getCanvas().getSelectedFigure();
                if (selected != null) {
                    selected.delete();
                }
            }
        });
        this.getAccelerators().put(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY), () -> {
            GraphicalRepresentation selected = getCanvas().getSelectedFigure();
            selected.copy("Copy");
        });
        this.getAccelerators().put(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_ANY), () -> {
            GraphicalRepresentation selected = getCanvas().getSelectedFigure();
            selected.cut();
        });
        this.getAccelerators().put(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_ANY), () -> {
            GraphicalRepresentation selected = getCanvas().getSelectedFigure();
            getCanvas().paste();
        });

    }

    public void updateSelected(){
        ArrayList<GraphicalRepresentation> canvasObjects = getCanvas().getCurrentCanvasObjects();
        LocalDateTime max = null;
        int index = -1;
        for (int i = 0; i < canvasObjects.size(); i++) {
            if (i == 0) {
                max = canvasObjects.get(i).getLastTimeSelected();
                index = i;
            } else {
                GraphicalRepresentation rectangle = canvasObjects.get(i);
                if (rectangle.getLastTimeSelected() != null && max != null) {
                    if (max.isBefore(rectangle.getLastTimeSelected())) {
                        max = canvasObjects.get(i).getLastTimeSelected();
                        index = i;
                    }
                }
            }
        }
        if (index > -1 && max != null) {
            for (int i = 0; i < canvasObjects.size(); i++) {
                if (i != index) {
                    canvasObjects.get(i).setSelected(false);
                }
            }
        }
    }
}
