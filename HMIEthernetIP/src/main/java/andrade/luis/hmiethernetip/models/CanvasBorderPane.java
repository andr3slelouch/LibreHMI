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

public class CanvasBorderPane extends BorderPane {
    public CanvasInterface getCanvas() {
        return canvas;
    }

    public void setCanvas(CanvasInterface canvas) {
        this.canvas = canvas;
    }

    public GraphicalRepresentation getGraphicalRepresentation() {
        return graphicalRepresentation;
    }

    public void setGraphicalRepresentation(GraphicalRepresentation graphicalRepresentation) {
        this.graphicalRepresentation = graphicalRepresentation;
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
        return graphicalRepresentation.getPosition();
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
        this.graphicalRepresentation.setPosition(position);
        if(force){
            this.setLayoutX(position.getX());
            this.setLayoutY(position.getY());
        }
    }

    public void copy(String operation){
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        this.graphicalRepresentation.setId(this.getId());
        this.graphicalRepresentation.setOperation(operation);
        clipboard.setContents(this.graphicalRepresentation,null);
    }

    public void cut(){
        this.copy("Cut");
        canvas.delete(this.graphicalRepresentation);
    }

    public void delete(){
        this.graphicalRepresentation.setId(this.getId());
        canvas.delete(this.graphicalRepresentation);
    }

    public void hideBorder() {
        CanvasBorderPane.this.borderActive.set(false);
        CanvasBorderPane.this.setStyle("");
        for (int i = 0; i < CanvasBorderPane.this.getChildren().size(); i++) {
            Node node = CanvasBorderPane.this.getChildren().get(i);
            if (node.getId() != null && node.getId().length() > 5) {
                String substring = node.getId().substring(0, 7);
                if (substring.equals("circle#")) {
                    node.setVisible(false);
                }
            }
        }
    }

    public void showBorder() {
        CanvasBorderPane.this.setStyle("-fx-border-color: red;-fx-border-style: segments(10, 15, 15, 15)  line-cap round;-fx-border-width: 2;");
        for (int i = 0; i < CanvasBorderPane.this.getChildren().size(); i++) {
            Node node = CanvasBorderPane.this.getChildren().get(i);
            if (node.getId() != null && node.getId().length() > 5) {
                String substring = node.getId().substring(0, 7);
                if (substring.equals("circle#")) {
                    node.setVisible(true);
                }
            }
        }
    }

    public void showContextMenu(double screenX, double screenY) {
        rightClickMenu.show(CanvasBorderPane.this, screenX, screenY);
        showBorder();
    }

    private EventHandler<MouseEvent> onMyMousePressed = new EventHandler<>() {
        @Override
        public void handle(MouseEvent t) {
            start = new CanvasPoint(t.getSceneX(), t.getSceneY());
            end = new CanvasPoint(((BorderPane) (t.getSource())).getTranslateX(), ((BorderPane) (t.getSource())).getTranslateY());
            CanvasBorderPane.this.setSelected(true);
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

    private EventHandler<MouseEvent> onMyMouseReleased = mouseEvent -> {
        CanvasBorderPane.this.getGraphicalRepresentation().setSelected(false);
    };

    private EventHandler<MouseEvent> onMyMouseDoubleClicked = mouseEvent -> {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 1) {
            this.setSelected(true);
        } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            showContextMenu(mouseEvent.getScreenX(), mouseEvent.getScreenY());
        }
    };

    public void setSelected(boolean selected) {
        this.graphicalRepresentation.setSelected(selected);
        if (this.graphicalRepresentation.isSelected()) {
            showBorder();
            setLastTimeSelected();
        } else {
            hideBorder();
        }
    }

    public boolean isSelected(){
        return graphicalRepresentation.isSelected();
    }

    public void setCenter(CanvasPoint center){
        this.graphicalRepresentation.setCenter(center);

        double tempX = center.getX() - getWidth() / 2;
        double tempY = center.getY() - getHeight() / 2;

        this.graphicalRepresentation.setPosition(new CanvasPoint(tempX,tempY));
        setPosition(new CanvasPoint(tempX, tempY), true);

        //this.rectangle = new Rectangle(getX(), getY(), getWidth(), getHeight());
        this.border = PseudoClass.getPseudoClass("border");
        this.borderActive = new SimpleBooleanProperty() {
            @Override
            protected void invalidated() {
                CanvasBorderPane.this.pseudoClassStateChanged(CanvasBorderPane.this.border, get());
            }
        };
        //this.setCenter(this.rectangle);
    }

    public CanvasBorderPane(GraphicalRepresentation graphicalRepresentation){
        super();

        //setWidth(150);
        //setHeight(150);

        this.setOnMousePressed(onMyMousePressed);
        this.setOnMouseDragged(onMyMouseDragged);
        this.setOnMouseReleased(onMyMouseReleased);
        this.setOnMouseClicked(onMyMouseDoubleClicked);


        this.graphicalRepresentation = graphicalRepresentation;

        this.setCenter(this.graphicalRepresentation.getCenter());

        //setRectangleBorder();
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

    public CanvasBorderPane(CanvasPoint center){
        super();

        //setWidth(150);
        //setHeight(150);

        this.setOnMousePressed(onMyMousePressed);
        this.setOnMouseDragged(onMyMouseDragged);
        this.setOnMouseReleased(onMyMouseReleased);
        this.setOnMouseClicked(onMyMouseDoubleClicked);

        setCenter(center);
        setSelected(true);

        setContextMenu();
    }

    private CanvasInterface canvas;
    private GraphicalRepresentation graphicalRepresentation = new GraphicalRepresentation();
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
