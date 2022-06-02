package andrade.luis.hmiethernetip.controllers;

import andrade.luis.hmiethernetip.HMIApp;
import andrade.luis.hmiethernetip.models.HMISceneData;
import andrade.luis.hmiethernetip.models.canvas.CanvasColor;
import andrade.luis.hmiethernetip.models.canvas.CanvasObject;
import andrade.luis.hmiethernetip.models.canvas.CanvasObjectData;
import andrade.luis.hmiethernetip.models.canvas.CanvasPoint;
import andrade.luis.hmiethernetip.views.HMICanvas;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HMIScene extends Scene implements Cloneable {
    Logger logger
            = Logger.getLogger(
            HMIScene.class.getName());
    /*
     * windowImage source <a href="https://www.flaticon.com/free-icons/window" title="window icons">Window icons created by Anas Mannaa - Flaticon</a>
     * */
    private final String location = getClass().getResource("").toExternalForm();
    private final Image windowImage = new Image(getClass().getResource("window.png").toExternalForm());
    private ScrollPane backgroundScrollPane;
    private ListView<String> listViewReference;
    private ArrayList<String> itemsForList;
    private String title;
    private String sceneCommentary;
    private Color background;
    private HMICanvas hmiCanvas;
    private HMISceneData hmiSceneData = new HMISceneData();
    private String id;
    public HMISceneData getHmiSceneData() {
        return hmiSceneData;
    }

    public void setHmiSceneData(HMISceneData hmiSceneData) {
        this.hmiSceneData = hmiSceneData;
        ArrayList<CanvasObjectData> shapeArrayList = this.hmiSceneData.getShapeArrayList();
        this.hmiCanvas.setShapeArrayList(shapeArrayList);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Color getBackground() {
        return background;
    }

    public void setBackground(Color background) {
        this.background = background;
        this.backgroundScrollPane.setBackground(new Background(new BackgroundFill(background, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    public String getSceneCommentary() {
        return sceneCommentary;
    }

    public void setSceneCommentary(String sceneCommentary) {
        this.sceneCommentary = sceneCommentary;
    }

    public HMIApp getHmiApp() {
        return hmiApp;
    }

    public void setHmiApp(HMIApp hmiApp) {
        this.hmiApp = hmiApp;
        if(this.hmiCanvas != null){
            this.hmiCanvas.setHmiApp(hmiApp);
        }
    }

    private HMIApp hmiApp;

    public ArrayList<String> getItemsForList() {
        return itemsForList;
    }

    public void updateItem(int index, String value){
        this.listViewReference.getItems().set(index, value);
    }

    public void setItems(ArrayList<String> itemsForComboBox) {
        this.itemsForList = itemsForComboBox;
        ObservableList<String> elements = FXCollections.observableArrayList(itemsForComboBox);

        listViewReference.setItems(elements);
        /*setting each image to corresponding array index*/
        listViewReference.setCellFactory(param -> new ListCell<String>() {
            /*view the image class to display the image*/
            private final ImageView displayImage = new ImageView();
            @Override
            public void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    displayImage.setFitWidth(45);
                    displayImage.setFitHeight(45);
                    displayImage.setImage(windowImage);
                    setText(name);
                    setGraphic(displayImage);
                    ContextMenu contextMenu = new ContextMenu();

                    MenuItem newItem = new MenuItem();
                    newItem.setText("Nueva");
                    newItem.setOnAction(event -> hmiApp.addNewScene());
                    MenuItem exportItem = new MenuItem();
                    exportItem.setText("Exportar");
                    exportItem.setOnAction(event -> hmiApp.exportSceneData(name,false));
                    MenuItem exportEncryptedItem = new MenuItem();
                    exportEncryptedItem.setText("Exportar con contraseña");
                    exportEncryptedItem.setOnAction(event -> hmiApp.exportSceneData(name,true));
                    MenuItem deleteItem = new MenuItem();
                    deleteItem.setText("Eliminar");
                    deleteItem.setOnAction(event -> hmiApp.deleteScene(getItem()));
                    MenuItem propertiesItem = new MenuItem();
                    propertiesItem.setText("Propiedades");
                    propertiesItem.setOnAction(event -> {
                        String item = getItem();
                        hmiApp.updateScene(item);
                    });
                    contextMenu.getItems().addAll(newItem,exportItem,exportEncryptedItem,deleteItem,propertiesItem);
                    setContextMenu(contextMenu);
                    setOnMouseClicked(event -> {
                        if(event.getClickCount()==2){
                            hmiApp.changeSelectedScene(getItem());
                        }
                    });
                }
            }
        });
    }

    public ListView<String> getListViewReference() {
        return listViewReference;
    }

    public void setListViewReference(ListView<String> listViewReference) {
        this.listViewReference = listViewReference;
    }

    public ScrollPane getBackgroundScrollPane() {
        return backgroundScrollPane;
    }

    public void setBackgroundScrollPane(ScrollPane backgroundScrollPane) {
        this.backgroundScrollPane = backgroundScrollPane;
    }

    public HMICanvas getCanvas() {
        return hmiCanvas;
    }

    public void setCanvas(HMICanvas hmiCanvas) {
        this.hmiCanvas = hmiCanvas;
    }

    public HMIScene(ScrollPane scrollPane, HMICanvas hmiCanvas, String title, String sceneCommentary, double v, double v1, Paint paint) {
        super(scrollPane, v, v1, paint);
        this.backgroundScrollPane = scrollPane;
        scrollPane.setBackground(new Background(new BackgroundFill(paint, CornerRadii.EMPTY, Insets.EMPTY)));
        this.background = (Color) paint;
        this.title = title;
        this.hmiSceneData.setTitle(title);
        this.sceneCommentary = sceneCommentary;
        this.hmiSceneData.setSceneCommentary(sceneCommentary);
        this.hmiCanvas = hmiCanvas;
        this.hmiSceneData.setBackground(new CanvasColor((Color) paint));
        this.getStylesheets().add(getClass().getResource("hmiSceneStyle.css").toExternalForm());
        this.setOnMouseClicked(mouseEvent -> {
            if (this.hmiCanvas.isAddOnClickEnabled()) {
                hmiCanvas.addFigureOnCanvasClicked(new CanvasPoint(mouseEvent.getX(), mouseEvent.getY()));
            }else if(mouseEvent.getButton() == MouseButton.SECONDARY){
                hmiCanvas.onCanvasClicked(new CanvasPoint(mouseEvent.getScreenX(), mouseEvent.getScreenY()));
            }
            updateSelected();
        });
        this.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.DELETE) {
                andrade.luis.hmiethernetip.models.canvas.CanvasObject selected = getCanvas().getSelectedFigure();
                if (selected != null) {
                    selected.delete();
                }
            }
        });
        this.getAccelerators().put(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY), this::copy);
        this.getAccelerators().put(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_ANY), this::cut);
        this.getAccelerators().put(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_ANY), () -> getCanvas().paste());

    }

    public void copy(){
        CanvasObject selected = getCanvas().getSelectedFigure();
        selected.copy("Copy");
    }

    public void cut(){
        CanvasObject selected = getCanvas().getSelectedFigure();
        selected.cut();
    }

    public void updateSelected(){
        ArrayList<CanvasObject> canvasObjects = getCanvas().getShapeArrayList();
        LocalDateTime max = null;
        int index = -1;
        for (int i = 0; i < canvasObjects.size(); i++) {
            if (i == 0) {
                max = canvasObjects.get(i).getLastTimeSelected();
                index = i;
            } else {
                CanvasObject canvasObject = canvasObjects.get(i);
                if (canvasObject.getLastTimeSelected() != null && max != null) {
                    if (max.isBefore(canvasObject.getLastTimeSelected())) {
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

    public void update(String sceneTitle, String sceneCommentary, Color background) {
        setTitle(sceneTitle);
        setSceneCommentary(sceneCommentary);
        setBackground(background);
        setFill(getBackground());
    }

    @Override
    public HMIScene clone() throws CloneNotSupportedException {
        HMIScene clonedScene = (HMIScene) super.clone();
        return clonedScene;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
