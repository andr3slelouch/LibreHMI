package andrade.luis.hmiethernetip.models;

import andrade.luis.hmiethernetip.views.HMICanvas;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CanvasRectangle extends BorderPane {
    Logger logger
            = Logger.getLogger(
            CanvasRectangle.class.getName());

    private CanvasInterface canvas;
    private GraphicalRepresentation graphicalRepresentation = new GraphicalRepresentation();
    private Rectangle rectangle;
    private PseudoClass rectangleBorder;
    private BorderPane rectangleWrapper;
    private SimpleBooleanProperty rectangleBorderActive;
    private LocalDateTime lastTimeSelected;
    private ContextMenu rightClickMenu;

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

    public boolean isMouseOver() {
        return graphicalRepresentation.isMouseOver();
    }

    public void setMouseOver(boolean mouseOver) {
        graphicalRepresentation.setMouseOver(mouseOver);
    }

    public ContextMenu getRightClickMenu() {
        return rightClickMenu;
    }

    public void setRightClickMenu(ContextMenu rightClickMenu) {
        this.rightClickMenu = rightClickMenu;
    }


    public LocalDateTime getLastTimeSelected() {
        return lastTimeSelected;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    private double x;
    private double y;

    public CanvasRectangle() {

    }

    public CanvasPoint getPosition() {
        return graphicalRepresentation.getPosition();
    }

    public void setPosition(CanvasPoint position) {
        this.graphicalRepresentation.setPosition(position);
        this.setLayoutX(position.getX());
        this.setLayoutY(position.getY());
    }

    public CanvasPoint getRectangleCenter() {
        return graphicalRepresentation.getCenter();
    }

    public void setCenter(CanvasPoint center) {
        this.graphicalRepresentation.setCenter(center);

        double x = center.getX() - getWidth() / 2;
        double y = center.getY() - getHeight() / 2;

        setX(x);
        setY(y);
        setPosition(new CanvasPoint(x, y));

        this.rectangle = new Rectangle(getX(), getY(), getWidth(), getHeight());
        this.rectangleBorder = PseudoClass.getPseudoClass("border");
        this.rectangleWrapper = new BorderPane(this.rectangle);
        this.rectangleBorderActive = new SimpleBooleanProperty() {
            @Override
            protected void invalidated() {
                CanvasRectangle.this.rectangleWrapper.pseudoClassStateChanged(CanvasRectangle.this.rectangleBorder, get());
            }
        };
        this.setCenter(this.rectangleWrapper);
        this.setPadding(new Insets(15));
    }

    public void setRectangleBorder(){
        Circle circleOne = new Circle();
        circleOne.setCenterX(0);
        circleOne.setCenterY(0);
        circleOne.setFill(Color.BLUE);
        circleOne.setRadius(5f);
        circleOne.setVisible(false);
        circleOne.setId("circle#1");

        Circle circleTwo = new Circle();
        circleTwo.setCenterX(0);
        circleTwo.setCenterY(CanvasRectangle.this.rectangle.getHeight() / 2);
        circleTwo.setFill(Color.BLUE);
        circleTwo.setRadius(5f);
        circleTwo.setVisible(false);
        circleTwo.setId("circle#2");
        Circle circleThree = new Circle();
        circleThree.setCenterX(0);
        circleThree.setCenterY(CanvasRectangle.this.rectangle.getHeight());
        circleThree.setFill(Color.BLUE);
        circleThree.setRadius(5f);
        circleThree.setVisible(false);
        circleThree.setId("circle#3");
        Circle circleFour = new Circle();
        circleFour.setCenterX(CanvasRectangle.this.rectangle.getWidth() / 2);
        circleFour.setCenterY(0);
        circleFour.setRadius(5f);
        circleFour.setFill(Color.BLUE);
        circleFour.setVisible(false);
        circleFour.setId("circle#4");
        Circle circleFive = new Circle();
        circleFive.setCenterX(CanvasRectangle.this.rectangle.getWidth() / 2);
        circleFive.setCenterY(CanvasRectangle.this.rectangle.getHeight());
        circleFive.setRadius(5f);
        circleFive.setFill(Color.BLUE);
        circleFive.setVisible(false);
        circleFive.setId("circle#5");
        Circle circleSix = new Circle();
        circleSix.setCenterX(CanvasRectangle.this.rectangle.getWidth());
        circleSix.setCenterY(0);
        circleSix.setRadius(5f);
        circleSix.setFill(Color.BLUE);
        circleSix.setVisible(false);
        circleSix.setId("circle#6");
        Circle circleSeven = new Circle();
        circleSeven.setCenterX(CanvasRectangle.this.rectangle.getWidth());
        circleSeven.setCenterY(CanvasRectangle.this.rectangle.getHeight() / 2);
        circleSeven.setRadius(5f);
        circleSeven.setFill(Color.BLUE);
        circleSeven.setVisible(false);
        circleSeven.setId("circle#7");
        Circle circleEight = new Circle();
        circleEight.setCenterX(CanvasRectangle.this.rectangle.getWidth());
        circleEight.setCenterY(CanvasRectangle.this.rectangle.getHeight());
        circleEight.setRadius(5f);
        circleEight.setFill(Color.BLUE);
        circleEight.setVisible(false);
        circleEight.setId("circle#8");
        CanvasRectangle.this.rectangleWrapper.getChildren().add(circleOne);
        CanvasRectangle.this.rectangleWrapper.getChildren().add(circleTwo);
        CanvasRectangle.this.rectangleWrapper.getChildren().add(circleThree);
        CanvasRectangle.this.rectangleWrapper.getChildren().add(circleFour);
        CanvasRectangle.this.rectangleWrapper.getChildren().add(circleFive);
        CanvasRectangle.this.rectangleWrapper.getChildren().add(circleSix);
        CanvasRectangle.this.rectangleWrapper.getChildren().add(circleSeven);
        CanvasRectangle.this.rectangleWrapper.getChildren().add(circleEight);
    }

    public void setContextMenu(){
        rightClickMenu = new ContextMenu();
        rightClickMenu.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                System.out.println("consuming right release button in cm filter");
                event.consume();
            }
        });
        rightClickMenu.setOnAction(event -> System.out.println("right gets consumed so this must be left on " +
                ((MenuItem) event.getTarget()).getText()));

        MenuItem copyMenuItem = new MenuItem("Copy");
        copyMenuItem.setOnAction(actionEvent -> {
            System.out.println("Copied");
            copy("Copy");
        });
        MenuItem cutMenuItem = new MenuItem("Cut");
        cutMenuItem.setOnAction(actionEvent -> {
            System.out.println("Cut");
            cut();
        });
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(actionEvent -> {
            System.out.println("Delete");
            delete();
        });

        rightClickMenu.getItems().addAll(copyMenuItem, cutMenuItem,deleteMenuItem);
    }

    public CanvasRectangle(CanvasPoint center) {
        super();

        setWidth(150);
        setHeight(150);

        this.setOnMousePressed(getOnMyMousePressed());
        this.setOnMouseDragged(getOnMyMouseDragged());
        this.setOnMouseReleased(getOnMyMouseReleased());
        this.setOnMouseClicked(getOnMyMouseDoubleClicked());

        setCenter(center);
        setSelected(true);

        setRectangleBorder();
        setContextMenu();

    }

    public CanvasRectangle(GraphicalRepresentation graphicalRepresentation){
        super();

        setWidth(150);
        setHeight(150);

        this.setOnMousePressed(getOnMyMousePressed());
        this.setOnMouseDragged(getOnMyMouseDragged());
        this.setOnMouseReleased(getOnMyMouseReleased());
        this.setOnMouseClicked(getOnMyMouseDoubleClicked());

        this.graphicalRepresentation = graphicalRepresentation;

        this.setCenter(this.graphicalRepresentation.getCenter());

        setRectangleBorder();
        setContextMenu();

    }

    private CanvasPoint start, end;

    public EventHandler<MouseEvent> getOnMyMousePressed() {
        return onMyMousePressed;
    }

    public void setOnMyMousePressed(EventHandler<MouseEvent> onMyMousePressed) {
        this.onMyMousePressed = onMyMousePressed;
    }

    public EventHandler<MouseEvent> getOnMyMouseDragged() {
        return onMyMouseDragged;
    }

    public void setOnMyMouseDragged(EventHandler<MouseEvent> onMyMouseDragged) {
        this.onMyMouseDragged = onMyMouseDragged;
    }

    public EventHandler<MouseEvent> getOnMyMouseReleased() {
        return onMyMouseReleased;
    }

    public void setOnMyMouseReleased(EventHandler<MouseEvent> onMyMouseReleased) {
        this.onMyMouseReleased = onMyMouseReleased;
    }

    private EventHandler<MouseEvent> onMyMousePressed = new EventHandler<>() {
        @Override
        public void handle(MouseEvent t) {
            start = new CanvasPoint(t.getSceneX(), t.getSceneY());
            end = new CanvasPoint(((BorderPane) (t.getSource())).getTranslateX(), ((BorderPane) (t.getSource())).getTranslateY());
            CanvasRectangle.this.setSelected(true);
            if (t.getButton() == MouseButton.SECONDARY) {
                logger.log(Level.INFO, "RIGHT PRESSED");
                showContextMenu(t.getScreenX(), t.getScreenY());
            }
        }
    };

    private boolean pointIsInSide(double side, double point) {
        return side + 10 > point && side - 10 < point;
    }

    private boolean isInTop(MouseEvent event) {
        return pointIsInSide(0, event.getY());
    }

    private EventHandler<MouseEvent> onMyMouseDragged = new EventHandler<>() {
        @Override
        public void handle(MouseEvent t) {

            double offsetX = t.getSceneX() - start.getX();
            double offsetY = t.getSceneY() - start.getY();
            double newTranslateX = end.getX() + offsetX;
            double newTranslateY = end.getY() + offsetY;

            ((BorderPane) (t.getSource())).setTranslateX(newTranslateX);
            ((BorderPane) (t.getSource())).setTranslateY(newTranslateY);


            //System.out.println(isInTop(t));

            setPosition(new CanvasPoint(CanvasRectangle.this.getLayoutX(), CanvasRectangle.this.getLayoutY()));
        }
    };

    public boolean isSelected() {
        return graphicalRepresentation.isSelected();
    }

    public void setSelected(boolean selected) {
        this.graphicalRepresentation.setSelected(selected);
        if (this.isSelected()) {
            showBorder();
            setLastTimeSelected();
            logger.log(Level.INFO,this.getId());
        } else {
            hideBorder();
        }
    }

    private EventHandler<MouseEvent> onMyMouseReleased = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            CanvasRectangle.this.setSelected(false);
            logger.log(Level.INFO, "RELEASED");
        }
    };

    public EventHandler<MouseEvent> getOnMyMouseDoubleClicked() {
        return onMyMouseDoubleClicked;
    }

    public void setOnMyMouseDoubleClicked(EventHandler<MouseEvent> onMyMouseDoubleClicked) {
        this.onMyMouseDoubleClicked = onMyMouseDoubleClicked;
    }

    private EventHandler<MouseEvent> onMyMouseDoubleClicked = mouseEvent -> {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 1) {
            logger.log(Level.INFO, "CLICKED");
            this.setSelected(true);
        } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            logger.log(Level.INFO, "RIGHT CLICKED");
            showContextMenu(mouseEvent.getScreenX(), mouseEvent.getScreenY());
        }
    };

    public void hideBorder() {
        CanvasRectangle.this.rectangleBorderActive.set(false);
        CanvasRectangle.this.rectangleWrapper.setStyle("");
        for (int i = 0; i < CanvasRectangle.this.rectangleWrapper.getChildren().size(); i++) {
            Node node = CanvasRectangle.this.rectangleWrapper.getChildren().get(i);
            if (node.getId() != null && node.getId().length() > 5) {
                String substring = node.getId().substring(0, 7);
                if (substring.equals("circle#")) {
                    node.setVisible(false);
                }
            }
        }
    }

    public void showBorder() {
        CanvasRectangle.this.rectangleWrapper.setStyle("-fx-border-color: red;-fx-border-style: segments(10, 15, 15, 15)  line-cap round;-fx-border-width: 2;");
        for (int i = 0; i < CanvasRectangle.this.rectangleWrapper.getChildren().size(); i++) {
            Node node = CanvasRectangle.this.rectangleWrapper.getChildren().get(i);
            if (node.getId() != null && node.getId().length() > 5) {
                String substring = node.getId().substring(0, 7);
                if (substring.equals("circle#")) {
                    node.setVisible(true);
                }
            }
        }
    }

    public void showContextMenu(double screenX, double screenY) {
        rightClickMenu.show(CanvasRectangle.this, screenX, screenY);
        showBorder();
    }

    public void setLastTimeSelected() {
        this.lastTimeSelected = LocalDateTime.now();
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
}
