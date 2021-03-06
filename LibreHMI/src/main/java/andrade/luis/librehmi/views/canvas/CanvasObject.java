package andrade.luis.librehmi.views.canvas;

import andrade.luis.librehmi.HMIApp;
import andrade.luis.librehmi.models.CanvasObjectData;
import andrade.luis.librehmi.models.Expression;
import andrade.luis.librehmi.models.Tag;
import andrade.luis.librehmi.models.users.HMIUser;
import andrade.luis.librehmi.views.windows.SetSizeWindow;
import andrade.luis.librehmi.views.windows.SetVisibilityAnimationWindow;
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
import javafx.scene.control.Label;
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
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase que define el objeto CanvasObject, que será el padre de todas las figuras que se pueden agregar al canvas
 */
public class CanvasObject extends BorderPane {
    Logger logger = Logger.getLogger(this.getClass().getName());
    private Timeline visibilityTimeline;
    protected Label errorLabel = new Label();
    private CanvasObjectInterface canvas;
    private CanvasObjectData canvasObjectData = new CanvasObjectData();
    private PseudoClass border;
    private SimpleBooleanProperty borderActive;
    private LocalDateTime lastTimeSelected;
    private ContextMenu rightClickMenu;
    private CanvasPoint start;
    private CanvasPoint end;


    public HMIUser getUser() {
        return user;
    }

    public void setUser(HMIUser user) {
        this.user = user;
    }

    private HMIUser user;

    /**
     * Permite definir la posición del canvas
     * @param center Posición del objeto dentro del canvas
     * @param force Bandera para forzar que el objeto se mueva
     */
    public void setPosition(CanvasPoint center, boolean force) {
        this.canvasObjectData.setPosition(center);
        if (force) {
            this.setLayoutX(center.getX());
            this.setLayoutY(center.getY());
        }
    }

    /**
     * Método que define la operación de copia hacia el portapapeles
     * @param operation String que define el tipo de operación
     */
    public void copy(String operation) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        this.canvasObjectData.setId(this.getId());
        this.canvasObjectData.setOperation(operation);
        clipboard.setContents(this.canvasObjectData, null);
    }

    /**
     * Método que define la operación de cortar hacia el portapapeles
     */
    public void cut() {
        this.copy("Cut");
        canvas.delete(this.canvasObjectData);
    }

    /**
     * Método que define la operación eliminación de la figura
     */
    public void delete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Confirmar eliminación del objeto seleccionado, del tipo \""+this.getCanvasObjectData().getDataType()+"\"?");
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(cancelButton,okButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == okButton) {
            alert.close();
            canvas.delete(this.canvasObjectData);
        }else if(result.isPresent() && result.get().equals(cancelButton)) {
            alert.close();
        }
    }

    /**
     * Permite actualizar el valor de un tag para el hilo de animación de visualización
     * @param tag Tag para actualizar
     */
    public void updateTag(Tag tag){
        if(visibilityTimeline!=null){
            ArrayList<Tag> parameters = this.getCanvasObjectData().getVisibilityExpression().getParameters();
            for(int i=0;i<parameters.size();i++){
                if(parameters.get(i).compareToTag(tag)){
                    parameters.set(i,tag);
                }
            }
            this.getCanvasObjectData().getVisibilityExpression().setParameters(parameters);
        }
    }

    /**
     * Permite ocultar el borde de la representación gráfica
     */
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

    /**
     * Permite mostrar el borde de la representación gráfica
     */
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

    /**
     * Permite mostrar el menú de opciones
     * @param screenX Posición en X para mostrar el menú
     * @param screenY Posición en Y para mostrar el menú
     */
    public void showContextMenu(double screenX, double screenY) {
        rightClickMenu.show(CanvasObject.this, screenX, screenY);
        showBorder();
    }

    /**
     * Permite definir la acción cuando se da doble clic en la representación gráfica
     */
    protected EventHandler<MouseEvent> onDoubleClick = t -> {
        if (t.getButton() == MouseButton.PRIMARY && t.getClickCount() == 2) {
            CanvasObject.this.hmiApp.loginUser("Ejecutar");
        }
    };

    /**
     * Permite definir la acción cuando se da clic en la representación gráfica
     */
    private final EventHandler<MouseEvent> onMyMousePressed = t->{
            start = new CanvasPoint(t.getSceneX(), t.getSceneY());
            end = new CanvasPoint(((BorderPane) (t.getSource())).getTranslateX(), ((BorderPane) (t.getSource())).getTranslateY());
            CanvasObject.this.setSelected(true);
            if (t.getButton() == MouseButton.SECONDARY) {
                showContextMenu(t.getScreenX(), t.getScreenY());
            }
    };

    /**
     * Permite definir la acción cuando se arrastra la representación gráfica
     */
    private final EventHandler<MouseEvent> onMyMouseDragged = t -> {
            double offsetX = t.getSceneX() - start.getX();
            double offsetY = t.getSceneY() - start.getY();
            double newTranslateX = end.getX() + offsetX;
            double newTranslateY = end.getY() + offsetY;

            ((BorderPane) (t.getSource())).setTranslateX(newTranslateX);
            ((BorderPane) (t.getSource())).setTranslateY(newTranslateY);

            setPosition(new CanvasPoint(((BorderPane) (t.getSource())).getBoundsInParent().getMinX(), ((BorderPane) (t.getSource())).getBoundsInParent().getMinY()), false);
            setPosition(new CanvasPoint(((BorderPane) (t.getSource())).getBoundsInParent().getMinX(), ((BorderPane) (t.getSource())).getBoundsInParent().getMinY()));
            if(CanvasObject.this.hmiApp != null){
                CanvasObject.this.hmiApp.setWasModified(true);
            }
    };

    /**
     * Permite definir la acción cuando el mouse se alza luego de dar clic en la representación gráfica
     */
    private final EventHandler<MouseEvent> onMyMouseReleased = mouseEvent -> {
        CanvasObject.this.getCanvasObjectData().setSelected(false);
        CanvasObject.this.setCursor(Cursor.DEFAULT);
    };

    /**
     * Permite definir la acción cuando se da clic en la representación gráfica
     */
    private final EventHandler<MouseEvent> onMyMouseClicked = mouseEvent -> {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 1) {
            this.setSelected(true);
        } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            showContextMenu(mouseEvent.getScreenX(), mouseEvent.getScreenY());
        }
    };

    /**
     * Permite definir la representación como seleccionada
     * @param selected Bandera que determina si la figura esta seleccionada
     */
    public void setSelected(boolean selected) {
        this.canvasObjectData.setSelected(selected);
        if (this.canvasObjectData.isSelected()) {
            showBorder();
            setLastTimeSelected();
        } else {
            if(this.borderActive!=null){
                hideBorder();
            }
        }
    }

    public boolean isSelected() {
        return canvasObjectData.isSelected();
    }

    /**
     * Permite definir la posición de la representación gráfica
     * @param center Posición de la figura dentro del canvas
     */
    public void setPosition(CanvasPoint center) {

        this.canvasObjectData.setPosition(center);

        this.border = PseudoClass.getPseudoClass("border");
        this.borderActive = new SimpleBooleanProperty() {
            @Override
            protected void invalidated() {
                CanvasObject.this.pseudoClassStateChanged(CanvasObject.this.border, get());
            }
        };

    }

    /**
     * Constructor para pegar un CanvasObject copiado o regenerarlo desde el archivo
     * @param canvasObjectData CanvasObjectData conteniendo la información del objeto a generar
     */
    public CanvasObject(CanvasObjectData canvasObjectData) {
        super();

        this.setOnMousePressed(onMyMousePressed);
        this.setOnMouseDragged(onMyMouseDragged);
        this.setOnMouseReleased(onMyMouseReleased);
        this.setOnMouseClicked(onMyMouseClicked);



        this.canvasObjectData = canvasObjectData;


        setPosition(this.canvasObjectData.getPosition(), true);


        if(this.getCanvasObjectData().getVisibilityExpression() != null){
            this.setVisibilityAnimation(this.canvasObjectData.getVisibilityExpression());
            this.visibilityTimeline.play();
        }

        setContextMenu();
    }

    /**
     * Permite eliminar los elementos del menú contextual
     */
    public void clearContextMenu() {
        rightClickMenu.getItems().clear();
    }

    /**
     * Permite definir el menú contextual con sus opciones básicas
     */
    public void setContextMenu() {
        rightClickMenu = new ContextMenu();
        rightClickMenu.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                event.consume();
            }
        });

        MenuItem copyMenuItem = new MenuItem("Copiar");
        copyMenuItem.setId("#copy");
        copyMenuItem.setOnAction(actionEvent -> copy("Copy"));
        MenuItem cutMenuItem = new MenuItem("Cortar");
        cutMenuItem.setId("#cut");
        cutMenuItem.setOnAction(actionEvent -> cut());
        MenuItem deleteMenuItem = new MenuItem("Eliminar");
        deleteMenuItem.setId("#delete");
        deleteMenuItem.setOnAction(actionEvent -> delete());
        MenuItem resizeMI = new MenuItem("Propiedades");
        resizeMI.setId("#propertiesMI");
        resizeMI.setOnAction(actionEvent -> this.setProperties());
        MenuItem visibilityAnimationMI = new MenuItem("Animación de Visibilidad");
        visibilityAnimationMI.setId("#visibilityMI");
        visibilityAnimationMI.setOnAction(actionEvent -> this.setVisibilityAnimation());
        rightClickMenu.getItems().addAll(copyMenuItem, cutMenuItem, resizeMI, deleteMenuItem,visibilityAnimationMI);
    }

    /**
     * Constructor que permite agregar un nuevo CanvasObject al canvas
     * @param positionCanvasPoint Posición del objeto en el canvas
     */
    public CanvasObject(CanvasPoint positionCanvasPoint) {
        super();

        this.setOnMousePressed(onMyMousePressed);
        this.setOnMouseDragged(onMyMouseDragged);
        this.setOnMouseReleased(onMyMouseReleased);
        this.setOnMouseClicked(onMyMouseClicked);


        setPosition(positionCanvasPoint);
        setPosition(positionCanvasPoint, true);
        setSelected(true);

        setContextMenu();
    }

    /**
     * Permite modificar el tamaño de la representación gráfica
     * @param width Ancho de la representación
     * @param height Alto de la representación
     */
    public void setSize(double width, double height) {
        setPrefWidth(width);
        setPrefHeight(height);
    }

    /**
     * Permite habilitar o deshabilitar los listeners para el clic, arrastre, soltar de la representación gráfica
     * @param enabled Bandera para habilitar los listeners de la representación gráfica
     */
    public void enableListeners(boolean enabled) {
        if (enabled) {
            this.setOnMousePressed(onMyMousePressed);
            this.setOnMouseDragged(onMyMouseDragged);
            this.setOnMouseReleased(onMyMouseReleased);
            this.setOnMouseClicked(onMyMouseClicked);
        } else {
            this.setOnMousePressed(null);
            this.setOnMouseDragged(null);
            this.setOnMouseReleased(null);
            this.setOnMouseClicked(null);
        }
    }

    /**
     * Este método permite cambiar el tamaño(propiedades) del CanvasObject.
     * Muestra una ventana para la definición de los nuevos valores de Ancho y Alto del objeto
     */
    public void setProperties() {
        SetSizeWindow setSizeWindow = new SetSizeWindow(this.getCanvasObjectData().getWidth(), this.getCanvasObjectData().getHeight());
        setSizeWindow.showAndWait();

        this.getCanvasObjectData().setWidth(setSizeWindow.getVbox().getWidthFromField());
        this.getCanvasObjectData().setHeight(setSizeWindow.getVbox().getHeightFromField());
        this.setSize(this.getCanvasObjectData().getWidth(), this.getCanvasObjectData().getHeight());
        this.getHmiApp().setWasModified(true);
    }

    /**
     * Permite definir la animación de visibilidad con base en un Tag determinado.
     * Muestra una ventana para la selección de un Tag determinado, luego consulta el valor del Tag actual,
     * y verifica si la condición se cumple o no.
     */
    protected void setVisibilityAnimation() {
        SetVisibilityAnimationWindow setVisibilityAnimationWindow = new SetVisibilityAnimationWindow();
        setVisibilityAnimationWindow.setHeight(220);
        setVisibilityAnimationWindow.setLocalTags(getHmiApp().getLocalTags());
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
        Expression expression = setVisibilityAnimationWindow.getLocalExpression();
        if (expression != null) {
            this.setVisibilityAnimation(expression);
            this.visibilityTimeline.play();
            this.getHmiApp().setWasModified(true);
        }
    }

    /**
     * Define el Timeline que verificará el valor actual de la Expression asociada cada 1 segundos.
     * @param expression Expression de tipo Bool a ser evaluada.
     */
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
                                    this.setTop(null);
                                } catch (CompileException | InvocationTargetException | SQLException | IOException | NullPointerException e) {
                                    this.errorLabel = new Label("Error de Tag de Animación");
                                    this.setTop(errorLabel);
                                    logger.log(Level.INFO,e.getMessage());
                                }
                            }), new KeyFrame(Duration.seconds(1)));
            this.visibilityTimeline.setCycleCount(Animation.INDEFINITE);
        }
    }

    /**
     * Define el ID del objeto, también lo guarda en el objeto CanvasObjectData
     * @param id ID del objeto
     */
    public void setObjectId(String id){
        this.setId(id);
        this.getCanvasObjectData().setId(id);
    }

    /**
     * Habilita el objeto si el argumento es diferente de Stop
     * @param mode Argumento para habilitar el objeto
     */
    public void setEnable(String mode) {
        if(mode.equals("Stop")){
            this.setDisable(true);
        }else{
            enableListeners(!mode.equals("Ejecutar"));
        }
    }

    public HMIApp getHmiApp() {
        return hmiApp;
    }

    public void setHmiApp(HMIApp hmiApp) {
        this.hmiApp = hmiApp;
    }

    private HMIApp hmiApp;

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

    public LocalDateTime getLastTimeSelected() {
        return lastTimeSelected;
    }

    public void setLastTimeSelected() {
        this.lastTimeSelected = LocalDateTime.now();
    }

    public ContextMenu getRightClickMenu() {
        return rightClickMenu;
    }

    /**
     * Permite actualizar el hilo con un nuevo valor del tag
     * @param linkedTag Tag enlazado
     * @param timeline Timeline a ser actualizado
     */
    public void updateTimeline(Tag linkedTag, Timeline timeline){
        if (linkedTag != null && timeline!=null) {
            linkedTag.setValue(this.getCanvasObjectData().getData());
            try {
                timeline.pause();
                this.setLastTimeSelected();
                if (!linkedTag.update()) {
                    this.errorLabel = new Label("Error en Tag de Escritura");
                    this.setTop(errorLabel);
                } else {
                    this.setTop(null);
                }
                timeline.play();
            } catch (SQLException | IOException e) {
                logger.log(Level.INFO,e.getMessage());
                this.errorLabel = new Label("Error en Tag de Escritura");
                this.setTop(errorLabel);
            }
        }
    }

    /**
     * Permite actualizar el valor de un tag y retornar dicho valor
     * @param tag Tag
     * @return Valor del tag actualizado
     */
    public String updateInputTag(Tag tag){
        int floatPrecision = this.getCanvasObjectData().getTag().getFloatPrecision();
        updateTag(tag);
        if(this.getCanvasObjectData().getTag().compareToTag(tag)){
            this.getCanvasObjectData().setTag(tag);
            this.getCanvasObjectData().getTag().setFloatPrecision(floatPrecision);
        }
        return this.getCanvasObjectData().getTag().getValue();
    }

}
