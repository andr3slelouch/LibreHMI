package andrade.luis.librehmi.controllers;

import andrade.luis.librehmi.HMIApp;
import andrade.luis.librehmi.models.CanvasObjectData;
import andrade.luis.librehmi.models.HMISceneData;
import andrade.luis.librehmi.views.HMICanvas;
import andrade.luis.librehmi.views.canvas.CanvasColor;
import andrade.luis.librehmi.views.canvas.CanvasObject;
import andrade.luis.librehmi.views.canvas.CanvasPoint;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Esta clase se encargará de contener el canvas, y de administrar ciertas propiedades
 */
public class HMIScene extends Scene {
    /*
     * windowImage source <a href="https://www.flaticon.com/free-icons/window" title="window icons">Window icons created by Anas Mannaa - Flaticon</a>
     * */
    private final Image windowImage = new Image(getClass().getResource("window.png").toExternalForm());
    private final ScrollPane backgroundScrollPane;
    private ListView<String> listViewReference;
    private String title;
    private String sceneCommentary;
    private Color background;
    private HMICanvas hmiCanvas;
    private HMISceneData hmiSceneData = new HMISceneData();
    private String id;
    private LocalDateTime max;
    private ArrayList<CanvasObject> canvasObjects;

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

    public void updateItem(int index, String value){
        this.listViewReference.getItems().set(index, value);
    }

    /**
     * Este método sirve para crear los elementos de la lista de páginas del menú lateral
     * @param itemsForComboBox Lista de páginas
     */
    public void setItems(ArrayList<String> itemsForComboBox) {
        ObservableList<String> elements = FXCollections.observableArrayList(itemsForComboBox);

        listViewReference.setItems(elements);
        /*setting each image to corresponding array index*/
        listViewReference.setCellFactory(param -> new ListCell<>() {
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
                    exportItem.setOnAction(event -> hmiApp.exportSceneData(name, false));
                    MenuItem exportEncryptedItem = new MenuItem();
                    exportEncryptedItem.setText("Exportar con contraseña");
                    exportEncryptedItem.setOnAction(event -> hmiApp.exportSceneData(name, true));
                    MenuItem deleteItem = new MenuItem();
                    deleteItem.setText("Eliminar");
                    deleteItem.setOnAction(event -> hmiApp.deleteScene(getItem()));
                    MenuItem propertiesItem = new MenuItem();
                    propertiesItem.setText("Propiedades");
                    propertiesItem.setOnAction(event -> {
                        String item = getItem();
                        hmiApp.updateScene(item);
                    });
                    contextMenu.getItems().addAll(newItem, exportItem, exportEncryptedItem, deleteItem, propertiesItem);
                    setContextMenu(contextMenu);
                    setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2) {
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

    public HMICanvas getCanvas() {
        return hmiCanvas;
    }

    public void setCanvas(HMICanvas hmiCanvas) {
        this.hmiCanvas = hmiCanvas;
    }

    /**
     * Constructor de la clase, aquí se definirán el color de fondo de la página, su título, además que se habilita la
     * lógica para agregar y pegar figuras
     * @param scrollPane Panel de scroll que permitirá que la página tenga esta propiedad
     * @param hmiCanvas Canvas donde se pueden agregar las representaciones gráficas
     * @param title Título de la página
     * @param sceneCommentary Comentario o descripción de la página
     * @param width Ancho de la página
     * @param height Alto de la página
     * @param paint Color de fondo de la página
     */
    public HMIScene(ScrollPane scrollPane, HMICanvas hmiCanvas, String title, String sceneCommentary, double width, double height, Paint paint) {
        super(scrollPane, width, height, paint);
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
                CanvasObject selected = getCanvas().getSelectedFigure();
                if (selected != null) {
                    selected.delete();
                }
            }
        });
    }

    /**
     * Permite llamar al método de copiar de la figura seleccionada
     */
    public void copy(){
        CanvasObject selected = getCanvas().getSelectedFigure();
        selected.copy("Copy");
    }

    /**
     * Permite llamar al método de cortar de la figura seleccionada
     */
    public void cut(){
        CanvasObject selected = getCanvas().getSelectedFigure();
        selected.cut();
    }

    /**
     * Permite actualizar la figura que se encuentra seleccionada
     */
    public void updateSelected(){
        canvasObjects = getCanvas().getShapeArrayList();
        max = null;
        int index = getLastSelectedCanvasObjectIndex();
        if (index > -1 && max != null) {
            for (int i = 0; i < canvasObjects.size(); i++) {
                if (i != index) {
                    canvasObjects.get(i).setSelected(false);
                }
            }
        }

    }

    /**
     * Permite obtener el índice de la figura seleccionada dentro del arraylist de figuras
     * @return Valor del índice de la última figura seleccionada
     */
    private int getLastSelectedCanvasObjectIndex(){
        int index = -1;
        for (int i = 0; i < canvasObjects.size(); i++) {
            if (i == 0) {
                max = canvasObjects.get(i).getLastTimeSelected();
                index = i;
            } else {
                CanvasObject canvasObject = canvasObjects.get(i);
                if (canvasObject.getLastTimeSelected() != null && max != null && max.isBefore(canvasObject.getLastTimeSelected())) {
                    max = canvasObjects.get(i).getLastTimeSelected();
                    index = i;
                }
            }
        }
        return index;
    }

    /**
     * Permite actualizar los parámetros de la página
     * @param sceneTitle Título de la página
     * @param sceneCommentary Comentario de la página
     * @param background Color de fondo de la página
     */
    public void update(String sceneTitle, String sceneCommentary, Color background) {
        setTitle(sceneTitle);
        setSceneCommentary(sceneCommentary);
        setBackground(background);
        setFill(getBackground());
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
