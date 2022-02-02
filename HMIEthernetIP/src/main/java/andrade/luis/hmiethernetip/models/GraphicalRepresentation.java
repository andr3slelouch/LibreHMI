package andrade.luis.hmiethernetip.models;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.time.LocalDateTime;

public class GraphicalRepresentation extends BorderPane {
    public GraphicalRepresentation() {

    }

    public CanvasInterface getCanvas() {
        return canvas;
    }

    public void setCanvas(CanvasInterface canvas) {
        this.canvas = canvas;
    }

    public GraphicalRepresentationData getGraphicalRepresentationData() {
        return graphicalRepresentationData;
    }

    public void setGraphicalRepresentation(GraphicalRepresentationData graphicalRepresentationData) {
        this.graphicalRepresentationData = graphicalRepresentationData;
    }

    public PseudoClass getGraphicalRepresentationBorder() {
        return border;
    }

    public void setBorder(PseudoClass border) {
        this.border = border;
    }

    public boolean isBorderActive() {
        return borderActive.get();
    }

    public SimpleBooleanProperty borderActiveProperty() {
        return borderActive;
    }

    public void setBorderActive(boolean borderActive) {
        this.borderActive.set(borderActive);
    }

    public LocalDateTime getLastTimeSelected() {
        return lastTimeSelected;
    }

    public void setLastTimeSelected() {
        this.lastTimeSelected = LocalDateTime.now();
    }

    public ContextMenu getRightClickMenu() {
        return rightClickMenu;
    }

    public void setRightClickMenu(ContextMenu rightClickMenu) {
        this.rightClickMenu = rightClickMenu;
    }

    public MenuItem getCopyMenuItem() {
        return copyMenuItem;
    }

    public void setCopyMenuItem(MenuItem copyMenuItem) {
        this.copyMenuItem = copyMenuItem;
    }

    public MenuItem getCutMenuItem() {
        return cutMenuItem;
    }

    public void setCutMenuItem(MenuItem cutMenuItem) {
        this.cutMenuItem = cutMenuItem;
    }

    public MenuItem getDeleteMenuItem() {
        return deleteMenuItem;
    }

    public void setDeleteMenuItem(MenuItem deleteMenuItem) {
        this.deleteMenuItem = deleteMenuItem;
    }

    public CanvasPoint getPosition() {
        return graphicalRepresentationData.getPosition();
    }

    public CanvasPoint getStart() {
        return start;
    }

    public void setStart(CanvasPoint start) {
        this.start = start;
    }

    public CanvasPoint getEnd() {
        return end;
    }

    public void setEnd(CanvasPoint end) {
        this.end = end;
    }

    public void setPosition(CanvasPoint position, boolean force) {
        this.graphicalRepresentationData.setPosition(position);
        if(force){
            this.setLayoutX(position.getX());
            this.setLayoutY(position.getY());
        }
    }

    public void copy(String operation){
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        this.graphicalRepresentationData.setId(this.getId());
        this.graphicalRepresentationData.setOperation(operation);
        clipboard.setContents(this.graphicalRepresentationData,null);
    }

    public void cut(){
        this.copy("Cut");
        canvas.delete(this.graphicalRepresentationData);
    }

    public void delete(){
        this.graphicalRepresentationData.setId(this.getId());
        canvas.delete(this.graphicalRepresentationData);
    }

    public void hideBorder() {
        GraphicalRepresentation.this.borderActive.set(false);
        GraphicalRepresentation.this.setStyle("");
        for (int i = 0; i < GraphicalRepresentation.this.getChildren().size(); i++) {
            Node node = GraphicalRepresentation.this.getChildren().get(i);
            if (node.getId() != null && node.getId().length() > 5) {
                String substring = node.getId().substring(0, 7);
                if (substring.equals("circle#")) {
                    node.setVisible(false);
                }
            }
        }
    }

    public void showBorder() {
        GraphicalRepresentation.this.setStyle("-fx-border-color: red;-fx-border-width: 2;");
        for (int i = 0; i < GraphicalRepresentation.this.getChildren().size(); i++) {
            Node node = GraphicalRepresentation.this.getChildren().get(i);
            if (node.getId() != null && node.getId().length() > 5) {
                String substring = node.getId().substring(0, 7);
                if (substring.equals("circle#")) {
                    node.setVisible(true);
                }
            }
        }
    }

    public void showContextMenu(double screenX, double screenY) {
        rightClickMenu.show(GraphicalRepresentation.this, screenX, screenY);
        showBorder();
    }

    private EventHandler<MouseEvent> onMyMousePressed = new EventHandler<>() {
        @Override
        public void handle(MouseEvent t) {
            start = new CanvasPoint(t.getSceneX(), t.getSceneY());
            end = new CanvasPoint(((BorderPane) (t.getSource())).getTranslateX(), ((BorderPane) (t.getSource())).getTranslateY());
            GraphicalRepresentation.this.setSelected(true);
            if (t.getButton() == MouseButton.SECONDARY) {
                showContextMenu(t.getScreenX(), t.getScreenY());
            }
        }
    };

    private EventHandler<MouseEvent> onMyMouseDragged = new EventHandler<>() {
        @Override
        public void handle(MouseEvent t) {
            double offsetX = t.getSceneX() - start.getX();
            double offsetY = t.getSceneY() - start.getY();
            double newTranslateX = end.getX() + offsetX;
            double newTranslateY = end.getY() + offsetY;

            ((BorderPane) (t.getSource())).setTranslateX(newTranslateX);
            ((BorderPane) (t.getSource())).setTranslateY(newTranslateY);

            setPosition(new CanvasPoint(newTranslateX, newTranslateY),false);
        }
    };

    private EventHandler<MouseEvent> onMyMouseReleased = mouseEvent -> GraphicalRepresentation.this.getGraphicalRepresentationData().setSelected(false);

    private EventHandler<MouseEvent> onMyMouseDoubleClicked = mouseEvent -> {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 1) {
            this.setSelected(true);
        } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            showContextMenu(mouseEvent.getScreenX(), mouseEvent.getScreenY());
        }
    };

    public void setSelected(boolean selected) {
        this.graphicalRepresentationData.setSelected(selected);
        if (this.graphicalRepresentationData.isSelected()) {
            showBorder();
            setLastTimeSelected();
        } else {
            hideBorder();
        }
    }

    public boolean isSelected(){
        return graphicalRepresentationData.isSelected();
    }

    public void setCenter(CanvasPoint center){
        this.graphicalRepresentationData.setCenter(center);

        double tempX = center.getX() - getWidth() / 2;
        double tempY = center.getY() - getHeight() / 2;

        this.graphicalRepresentationData.setPosition(new CanvasPoint(tempX,tempY));
        setPosition(new CanvasPoint(tempX, tempY), true);

        this.border = PseudoClass.getPseudoClass("border");
        this.borderActive = new SimpleBooleanProperty() {
            @Override
            protected void invalidated() {
                GraphicalRepresentation.this.pseudoClassStateChanged(GraphicalRepresentation.this.border, get());
            }
        };
    }

    public GraphicalRepresentation(GraphicalRepresentationData graphicalRepresentationData){
        super();

        this.setOnMousePressed(onMyMousePressed);
        this.setOnMouseDragged(onMyMouseDragged);
        this.setOnMouseReleased(onMyMouseReleased);
        this.setOnMouseClicked(onMyMouseDoubleClicked);


        this.graphicalRepresentationData = graphicalRepresentationData;

        this.setCenter(this.graphicalRepresentationData.getCenter());

        setContextMenu();
    }

    public void setContextMenu(){
        rightClickMenu = new ContextMenu();
        rightClickMenu.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                event.consume();
            }
        });

        copyMenuItem = new MenuItem("Copy");
        copyMenuItem.setId("#copy");
        copyMenuItem.setOnAction(actionEvent -> copy("Copy"));
        cutMenuItem = new MenuItem("Cut");
        cutMenuItem.setId("#cut");
        cutMenuItem.setOnAction(actionEvent -> cut());
        deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setId("#delete");
        deleteMenuItem.setOnAction(actionEvent -> delete());
        rightClickMenu.getItems().addAll(copyMenuItem, cutMenuItem,deleteMenuItem);
    }

    public GraphicalRepresentation(CanvasPoint center){
        super();

        this.setOnMousePressed(onMyMousePressed);
        this.setOnMouseDragged(onMyMouseDragged);
        this.setOnMouseReleased(onMyMouseReleased);
        this.setOnMouseClicked(onMyMouseDoubleClicked);

        setCenter(center);
        setSelected(true);

        setContextMenu();
    }

    private CanvasInterface canvas;
    private GraphicalRepresentationData graphicalRepresentationData = new GraphicalRepresentationData();
    private PseudoClass border;
    private SimpleBooleanProperty borderActive;
    private LocalDateTime lastTimeSelected;
    private ContextMenu rightClickMenu;
    private MenuItem copyMenuItem;
    private MenuItem cutMenuItem;
    private MenuItem deleteMenuItem;
    private CanvasPoint start;
    private CanvasPoint end;
}
