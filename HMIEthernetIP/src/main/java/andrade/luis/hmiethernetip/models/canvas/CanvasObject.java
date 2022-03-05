package andrade.luis.hmiethernetip.models.canvas;

import andrade.luis.hmiethernetip.models.Expression;
import andrade.luis.hmiethernetip.models.MouseOverMode;
import andrade.luis.hmiethernetip.views.SetSizeWindow;
import andrade.luis.hmiethernetip.views.SetVisibilityAnimationWindow;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import org.codehaus.commons.compiler.CompileException;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import static andrade.luis.hmiethernetip.models.MouseOverMode.DEFAULT;
import static andrade.luis.hmiethernetip.models.MouseOverMode.DRAG;

public class CanvasObject extends BorderPane {
    Logger logger = Logger.getLogger(this.getClass().getName());
    private Timeline visibilityTimeline;

    public CanvasObject() {

    }

    public CanvasObjectInterface getCanvas() {
        return canvas;
    }

    public void setCanvas(CanvasObjectInterface canvas) {
        this.canvas = canvas;
    }

    public CanvasObjectData getCanvasObjectData() {
        return canvasObjectData;
    }

    public void setGraphicalRepresentation(CanvasObjectData canvasObjectData) {
        this.canvasObjectData = canvasObjectData;
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
        return canvasObjectData.getPosition();
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
        this.canvasObjectData.setPosition(position);
        if (force) {
            this.setLayoutX(position.getX());
            this.setLayoutY(position.getY());
        }
    }

    public void copy(String operation) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        this.canvasObjectData.setId(this.getId());
        this.canvasObjectData.setOperation(operation);
        clipboard.setContents(this.canvasObjectData, null);
    }

    public void cut() {
        this.copy("Cut");
        canvas.delete(this.canvasObjectData);
    }

    public void delete() {
        this.canvasObjectData.setId(this.getId());
        canvas.delete(this.canvasObjectData);
    }

    public void hideBorder() {
        CanvasObject.this.borderActive.set(false);
        CanvasObject.this.setStyle("");
        for (int i = 0; i < CanvasObject.this.getChildren().size(); i++) {
            Node node = CanvasObject.this.getChildren().get(i);
            if (node.getId() != null && node.getId().length() > 5) {
                String substring = node.getId().substring(0, 7);
                if (substring.equals("circle#")) {
                    node.setVisible(false);
                }
            }
        }
    }

    public void showBorder() {
        CanvasObject.this.setStyle("-fx-border-color: red;-fx-border-width: 2;");
        for (int i = 0; i < CanvasObject.this.getChildren().size(); i++) {
            Node node = CanvasObject.this.getChildren().get(i);
            if (node.getId() != null && node.getId().length() > 5) {
                String substring = node.getId().substring(0, 7);
                if (substring.equals("circle#")) {
                    node.setVisible(true);
                }
            }
        }
    }

    public void showContextMenu(double screenX, double screenY) {
        rightClickMenu.show(CanvasObject.this, screenX, screenY);
        showBorder();
    }

    private EventHandler<MouseEvent> onMyMousePressed = new EventHandler<>() {
        @Override
        public void handle(MouseEvent t) {
            /*if(isInResizingMode(t)){
                mouseOverMode = currentMouseOverMode(t);
            }
            startWidth = GraphicalRepresentation.this.getBoundsInLocal().getWidth();
            startHeight= GraphicalRepresentation.this.getBoundsInLocal().getHeight();*/
            start = new CanvasPoint(t.getSceneX(), t.getSceneY());
            end = new CanvasPoint(((BorderPane) (t.getSource())).getTranslateX(), ((BorderPane) (t.getSource())).getTranslateY());
            CanvasObject.this.setSelected(true);
            if (t.getButton() == MouseButton.SECONDARY) {
                showContextMenu(t.getScreenX(), t.getScreenY());
            }
        }
    };

    private EventHandler<MouseEvent> onMyMouseDragged = new EventHandler<>() {
        @Override
        public void handle(MouseEvent t) {
            //if(mouseOverMode == DRAG){
            double offsetX = t.getSceneX() - start.getX();
            double offsetY = t.getSceneY() - start.getY();
            double newTranslateX = end.getX() + offsetX;
            double newTranslateY = end.getY() + offsetY;

            ((BorderPane) (t.getSource())).setTranslateX(newTranslateX);
            ((BorderPane) (t.getSource())).setTranslateY(newTranslateY);

            setPosition(new CanvasPoint(newTranslateX, newTranslateY), false);
            /*}else if(mouseOverMode != DEFAULT){
                double newTranslateX = start.getX();
                double newTranslateY = start.getY();
                double newH = startHeight;
                double newW = startWidth;

                if(mouseOverMode == MouseOverMode.RIGHT || mouseOverMode == MouseOverMode.SUPERIOR_RIGHT || mouseOverMode == MouseOverMode.INFERIOR_RIGHT){
                    newW = GraphicalRepresentation.this.getBoundsInLocal().getMinX() + t.getX() - start.getX();
                }

                if(mouseOverMode == MouseOverMode.LEFT || mouseOverMode == MouseOverMode.SUPERIOR_LEFT || mouseOverMode == MouseOverMode.INFERIOR_LEFT){
                    newTranslateX = GraphicalRepresentation.this.getBoundsInLocal().getMinX() + t.getX();
                    newW = startWidth + start.getX() - newTranslateX;
                }

                if(mouseOverMode == MouseOverMode.INFERIOR || mouseOverMode == MouseOverMode.INFERIOR_LEFT || mouseOverMode == MouseOverMode.INFERIOR_RIGHT){
                    newH = GraphicalRepresentation.this.getBoundsInLocal().getMinY() + t.getY() - start.getY();
                }

                if(mouseOverMode == MouseOverMode.SUPERIOR || mouseOverMode == MouseOverMode.SUPERIOR_LEFT || mouseOverMode == MouseOverMode.SUPERIOR_RIGHT){
                    newTranslateY = GraphicalRepresentation.this.getBoundsInLocal().getMinY() + t.getY();
                    newH = startHeight + start.getY() - newTranslateY;
                }

                GraphicalRepresentation.this.setLayoutX(newTranslateX);
                GraphicalRepresentation.this.setLayoutY(newTranslateY);
                GraphicalRepresentation.this.setWidth(newW);
                GraphicalRepresentation.this.setHeight(newH);
            }*/

        }
    };

    private EventHandler<MouseEvent> onMyMouseReleased = mouseEvent -> {
        CanvasObject.this.getCanvasObjectData().setSelected(false);
        CanvasObject.this.setCursor(Cursor.DEFAULT);
        mouseOverMode = MouseOverMode.DEFAULT;
    };

    private EventHandler<MouseEvent> onMyMouseClicked = mouseEvent -> {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 1) {
            this.setSelected(true);
        } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            showContextMenu(mouseEvent.getScreenX(), mouseEvent.getScreenY());
        }
    };

    private EventHandler<MouseEvent> onMyMouseMoved = mouseEvent -> {
        MouseOverMode mode = currentMouseOverMode(mouseEvent);
        Cursor cursor = getCursorForMode(mode);
        this.setCursor(cursor);
    };

    private Cursor getCursorForMode(MouseOverMode mode) {
        switch (mode) {
            case SUPERIOR_LEFT:
                return Cursor.NW_RESIZE;
            case INFERIOR_LEFT:
                return Cursor.SW_RESIZE;
            case SUPERIOR_RIGHT:
                return Cursor.NE_RESIZE;
            case INFERIOR_RIGHT:
                return Cursor.SE_RESIZE;
            case LEFT:
                return Cursor.W_RESIZE;
            case RIGHT:
                return Cursor.E_RESIZE;
            case SUPERIOR:
                return Cursor.N_RESIZE;
            case INFERIOR:
                return Cursor.S_RESIZE;
            default:
                return Cursor.DEFAULT;
        }

    }

    private MouseOverMode currentMouseOverMode(MouseEvent mouseEvent) {
        MouseOverMode mode;
        boolean isLeftOverMode = intersectOperation(0, mouseEvent.getX());
        boolean isRightOverMode = intersectOperation(this.getBoundsInParent().getWidth(), mouseEvent.getX());
        boolean isTopOverMode = intersectOperation(0, mouseEvent.getY());
        boolean isBottomOverMode = intersectOperation(this.getBoundsInParent().getHeight(), mouseEvent.getY());

        if (isLeftOverMode && isTopOverMode) {
            mode = MouseOverMode.SUPERIOR_LEFT;
        } else if (isLeftOverMode && isBottomOverMode) {
            mode = MouseOverMode.INFERIOR_LEFT;
        } else if (isRightOverMode && isTopOverMode) {
            mode = MouseOverMode.SUPERIOR_RIGHT;
        } else if (isRightOverMode && isBottomOverMode) {
            mode = MouseOverMode.INFERIOR_RIGHT;
        } else if (isRightOverMode) {
            mode = MouseOverMode.RIGHT;
        } else if (isLeftOverMode) {
            mode = MouseOverMode.LEFT;
        } else if (isTopOverMode) {
            mode = MouseOverMode.SUPERIOR;
        } else if (isBottomOverMode) {
            mode = MouseOverMode.INFERIOR;
        } else {
            mode = DRAG;
        }
        return mode;
    }

    private boolean isInResizingMode(MouseEvent event) {
        return currentMouseOverMode(event) != DRAG && currentMouseOverMode(event) != DEFAULT;
    }

    private boolean intersectOperation(double side, double point) {
        return side + borderMargin > point && side - borderMargin < point;
    }


    public void setSelected(boolean selected) {
        this.canvasObjectData.setSelected(selected);
        if (this.canvasObjectData.isSelected()) {
            showBorder();
            setLastTimeSelected();
        } else {
            hideBorder();
        }
    }

    public boolean isSelected() {
        return canvasObjectData.isSelected();
    }

    public void setCenter(CanvasPoint center) {
        this.canvasObjectData.setCenter(center);

        double tempX = center.getX() - getWidth() / 2;
        double tempY = center.getY() - getHeight() / 2;

        this.canvasObjectData.setPosition(new CanvasPoint(tempX, tempY));
        setPosition(new CanvasPoint(tempX, tempY), true);

        this.border = PseudoClass.getPseudoClass("border");
        this.borderActive = new SimpleBooleanProperty() {
            @Override
            protected void invalidated() {
                CanvasObject.this.pseudoClassStateChanged(CanvasObject.this.border, get());
            }
        };
    }

    public CanvasObject(CanvasObjectData canvasObjectData) {
        super();

        this.setOnMousePressed(onMyMousePressed);
        this.setOnMouseDragged(onMyMouseDragged);
        this.setOnMouseReleased(onMyMouseReleased);
        this.setOnMouseClicked(onMyMouseClicked);
        //this.setOnMouseMoved(onMyMouseMoved);


        this.canvasObjectData = canvasObjectData;

        this.setCenter(this.canvasObjectData.getCenter());

        setContextMenu();
    }

    public void clearContextMenu() {
        rightClickMenu.getItems().clear();
    }

    public void setContextMenu() {
        rightClickMenu = new ContextMenu();
        rightClickMenu.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                event.consume();
            }
        });

        copyMenuItem = new MenuItem("Copiar");
        copyMenuItem.setId("#copy");
        copyMenuItem.setOnAction(actionEvent -> copy("Copy"));
        cutMenuItem = new MenuItem("Cortar");
        cutMenuItem.setId("#cut");
        cutMenuItem.setOnAction(actionEvent -> cut());
        deleteMenuItem = new MenuItem("Eliminar");
        deleteMenuItem.setId("#delete");
        deleteMenuItem.setOnAction(actionEvent -> delete());
        MenuItem resizeMI = new MenuItem("Redimensionar");
        resizeMI.setId("#resizeMI");
        resizeMI.setOnAction(actionEvent -> this.resize());
        MenuItem visibilityAnimationMI = new MenuItem("Animación de Visibilidad");
        visibilityAnimationMI.setId("#visibilityMI");
        visibilityAnimationMI.setOnAction(actionEvent -> {
            try {
                this.setVisibilityAnimation();
            } catch (SQLException | CompileException | IOException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        rightClickMenu.getItems().addAll(copyMenuItem, cutMenuItem, deleteMenuItem, resizeMI, visibilityAnimationMI);
    }

    public CanvasObject(CanvasPoint center) {
        super();

        this.setOnMousePressed(onMyMousePressed);
        this.setOnMouseDragged(onMyMouseDragged);
        this.setOnMouseReleased(onMyMouseReleased);
        this.setOnMouseClicked(onMyMouseClicked);
        //this.setOnMouseMoved(onMyMouseMoved);

        setCenter(center);
        setSelected(true);

        setContextMenu();
    }

    public void setSize(double width, double height) {
        setPrefWidth(width);
        setPrefHeight(height);
    }

    public void resize() {
        SetSizeWindow setSizeWindow = new SetSizeWindow(this.getCanvasObjectData().getWidth(), this.getCanvasObjectData().getHeight());
        setSizeWindow.showAndWait();
        this.getCanvasObjectData().setWidth(setSizeWindow.getWidthFromField());
        this.getCanvasObjectData().setHeight(setSizeWindow.getHeightFromField());
        this.setSize(this.getCanvasObjectData().getWidth(), this.getCanvasObjectData().getHeight());
    }

    protected void setVisibilityAnimation() throws SQLException, CompileException, IOException, InvocationTargetException {
        SetVisibilityAnimationWindow setVisibilityAnimationWindow = new SetVisibilityAnimationWindow();
        if (this.canvasObjectData.getVisibilityExpression() != null) {
            setVisibilityAnimationWindow.setAddedTags(this.canvasObjectData.getVisibilityExpression().getParameters());
            setVisibilityAnimationWindow.setLocalExpression(this.canvasObjectData.getVisibilityExpression());
            setVisibilityAnimationWindow.getTextField().setText(this.canvasObjectData.getVisibilityExpression().getExpressionToEvaluate());
            if(this.canvasObjectData.isVisible()){
                setVisibilityAnimationWindow.getTrueRadioButton().setSelected(true);
            }else{
                setVisibilityAnimationWindow.getFalseRadioButton().setSelected(true);
            }
        }
        setVisibilityAnimationWindow.showAndWait();
        this.canvasObjectData.setVisible(setVisibilityAnimationWindow.getTrueRadioButton().isSelected());
        logger.log(Level.INFO,setVisibilityAnimationWindow.getLocalExpression().getExpressionToEvaluate());
        Expression expression = setVisibilityAnimationWindow.getLocalExpression();
        if (expression != null) {
            expression.evaluate();
            this.setVisibilityAnimation(expression);
            this.visibilityTimeline.play();
        }
    }

    protected void setVisibilityAnimation(Expression expression) {
        if (expression != null) {
            this.getCanvasObjectData().setVisibilityExpression(expression);
            this.visibilityTimeline = new Timeline(
                    new KeyFrame(
                            Duration.seconds(0),
                            (ActionEvent actionEvent) -> {
                                try {
                                    boolean evaluatedValue = (boolean) this.getCanvasObjectData().getVisibilityExpression().evaluate();
                                    this.setVisible(evaluatedValue == this.getCanvasObjectData().isVisible());
                                } catch (CompileException | InvocationTargetException | SQLException | IOException e) {
                                    e.printStackTrace();
                                }
                            }), new KeyFrame(Duration.seconds(1)));
            this.visibilityTimeline.setCycleCount(Animation.INDEFINITE);
        }
    }

    public void setEnable(String enabled) {
        this.setDisable(enabled.equals("Stop"));
    }

    private CanvasObjectInterface canvas;
    private CanvasObjectData canvasObjectData = new CanvasObjectData();
    private PseudoClass border;
    private SimpleBooleanProperty borderActive;
    private LocalDateTime lastTimeSelected;
    private ContextMenu rightClickMenu;
    private MenuItem copyMenuItem;
    private MenuItem cutMenuItem;
    private MenuItem deleteMenuItem;
    private CanvasPoint start;
    private CanvasPoint end;
    private static final int borderMargin = 10;
    private MouseOverMode mouseOverMode;
    private double startWidth;
    private double startHeight;
    private double endWidth;
    private double endHeight;
}
