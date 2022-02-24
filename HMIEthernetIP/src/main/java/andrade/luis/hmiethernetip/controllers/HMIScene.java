package andrade.luis.hmiethernetip.controllers;

import andrade.luis.hmiethernetip.HMIApp;
import andrade.luis.hmiethernetip.models.canvas.GraphicalRepresentation;
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
    private ScrollPane backgroundStackPane;
    private ListView<String> listViewReference;
    private ArrayList<String> itemsForList;
    private String title;
    private String sceneCommentary;
    private Color background;
    private HMICanvas hmiCanvas;

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
        this.backgroundStackPane.setBackground(new Background(new BackgroundFill(background, CornerRadii.EMPTY, Insets.EMPTY)));
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
            private ImageView displayImage = new ImageView();
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
                    newItem.setText("New...");
                    newItem.setOnAction(event -> {
                        hmiApp.addNewScene();
                    });
                    MenuItem saveItem = new MenuItem();
                    saveItem.setText("Save");
                    saveItem.setOnAction(event -> {
                        String item = getItem();
                        // code to edit item...
                    });
                    MenuItem duplicateItem = new MenuItem();
                    duplicateItem.setText("Duplicate");
                    duplicateItem.setOnAction(event -> hmiApp.duplicateScene(getItem()));
                    MenuItem deleteItem = new MenuItem();
                    deleteItem.setText("Delete");
                    deleteItem.setOnAction(event -> hmiApp.deleteScene(getItem()));
                    MenuItem propertiesItem = new MenuItem();
                    propertiesItem.setText("Properties...");
                    propertiesItem.setOnAction(event -> {
                        String item = getItem();
                        hmiApp.updateScene(item);
                    });
                    contextMenu.getItems().addAll(newItem,saveItem,duplicateItem,deleteItem,propertiesItem);
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

    public ScrollPane getBackgroundStackPane() {
        return backgroundStackPane;
    }

    public void setBackgroundStackPane(ScrollPane backgroundStackPane) {
        this.backgroundStackPane = backgroundStackPane;
    }

    public HMICanvas getCanvas() {
        return hmiCanvas;
    }

    public void setCanvas(HMICanvas hmiCanvas) {
        this.hmiCanvas = hmiCanvas;
    }

    public HMIScene(ScrollPane stackPane, HMICanvas hmiCanvas, String title, String sceneCommentary, double v, double v1, Paint paint) {
        super(stackPane, v, v1, paint);
        this.backgroundStackPane = stackPane;
        stackPane.setBackground(new Background(new BackgroundFill(paint, CornerRadii.EMPTY, Insets.EMPTY)));
        this.background = (Color) paint;
        this.title = title;
        this.sceneCommentary = sceneCommentary;
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
}
