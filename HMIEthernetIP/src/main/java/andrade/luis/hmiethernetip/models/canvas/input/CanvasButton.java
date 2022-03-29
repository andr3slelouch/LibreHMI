package andrade.luis.hmiethernetip.models.canvas.input;

import andrade.luis.hmiethernetip.models.canvas.CanvasColor;
import andrade.luis.hmiethernetip.models.canvas.CanvasObjectData;
import andrade.luis.hmiethernetip.models.canvas.CanvasPoint;
import andrade.luis.hmiethernetip.models.canvas.CanvasObject;
import andrade.luis.hmiethernetip.models.users.HMIUser;
import andrade.luis.hmiethernetip.views.SelectWindowsWindow;
import andrade.luis.hmiethernetip.views.SetGeometricFigurePropertiesWindow;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Esta es la clase que contiene define a un CanvasButton, se utiliza para moverse entra las páginas del proyecto
 * al hacer clic en el luego de haberlo asociado a las páginas seleccionadas por el usuario.
 */
public class CanvasButton extends CanvasObject {
    Logger logger = Logger.getLogger(this.getClass().getName());
    protected Button button;

    public HMIUser getUser() {
        return user;
    }

    public void setUser(HMIUser user) {
        this.user = user;
    }

    private HMIUser user;

    /**
     * Constructor de la clase, define un botón básico de javafx con el centro en el argumento recibido.
     * @param position CanvasPoint que define la posición que tendrá el objeto
     */
    public CanvasButton(CanvasPoint position) {
        super(position);
        setData(this.getCanvasObjectData().getPosition().getX(), this.getCanvasObjectData().getPosition().getY(), 150, 50);
    }

    /**
     * Constructor de la clase, define al botón con todos los atributos correspondientes desde el argumento,
     * este método es utilizado cuando se hace un copiar/pegar o cuando se está importando desde archivo.
     * @param canvasObjectData Objeto que contiene todos los atributos relevantes para construir el objeto
     */
    public CanvasButton(CanvasObjectData canvasObjectData){
        super(canvasObjectData);
        logger.log(Level.INFO,canvasObjectData.getType());
        setData(this.getCanvasObjectData().getPosition().getX(), this.getCanvasObjectData().getPosition().getY(), this.getCanvasObjectData().getWidth(), this.getCanvasObjectData().getHeight());
    }

    /**
     * Este método permite definir los atributos que constituyen el objeto
     * @param x Posición en X
     * @param y Posición en Y
     * @param width Ancho del botón
     * @param height Altura del botón
     */
    public void setData(double x, double y, double width, double height) {
        this.getCanvasObjectData().setType("Button");
        this.button = new Button("Action");
        this.button.setDisable(true);
        this.getCanvasObjectData().setWidth(width);
        this.getCanvasObjectData().setHeight(height);
        this.button.setPrefWidth(width);
        this.button.setPrefHeight(height);
        this.setCenter(this.button);
        setNewMenuItem();
    }

    /**
     * Este método define el MenuItem que permite Editar el objeto, y lo añade al ContextMenu
     */
    public void setNewMenuItem(){
        MenuItem attachShowHideWindowsActionMI = new MenuItem("Acción de Mostrar Ocultar Ventana");
        attachShowHideWindowsActionMI.setId("#showHideWindowsMI");
        attachShowHideWindowsActionMI.setOnAction(actionEvent -> buttonAction());
        this.getRightClickMenu().getItems().set(3,attachShowHideWindowsActionMI);
    }

    /**
     * Este método es la acción ejecutada cuando el MenuItem para Editar es ejecutado
     */
    public void buttonAction() {
        SelectWindowsWindow selectWindowsWindow = new SelectWindowsWindow(this.getHmiApp().getPagesTitles(), this.getCanvasObjectData().getSelectedPages());
        selectWindowsWindow.showAndWait();
        this.setSelectedPages(selectWindowsWindow.getSelectedItems());
    }

    /**
     * Este método permite definir las páginas mostradas cuando el botón es accionado.
     * @param selectedPages ArrayList de páginas seleccionadas a ser mostradas.
     */
    public void setSelectedPages(ArrayList<String> selectedPages) {
        if(selectedPages != null){
            this.getCanvasObjectData().setSelectedPages(selectedPages);
            boolean pagesReadyState = true;
            for (String selectedPage : selectedPages) {
                pagesReadyState = pagesReadyState && (this.getHmiApp().getIndexForScene(selectedPage) != -1);
            }
            if(pagesReadyState){
                this.button.setOnAction(mouseEvent -> this.getHmiApp().generateStagesForPages(this.getCanvasObjectData().getSelectedPages()));
                this.setTop(null);
            }else{
                this.errorLabel = new Label("Error en páginas asociadas");
                this.setTop(errorLabel);
            }
            this.getHmiApp().setWasModified(true);
        }
    }

    @Override
    public void setProperties(){
        SetGeometricFigurePropertiesWindow setGeometricFigurePropertiesWindow = new SetGeometricFigurePropertiesWindow(this.getCanvasObjectData().getWidth(),this.getCanvasObjectData().getHeight());
        setGeometricFigurePropertiesWindow.setTitle("Propiedades del Botón");
        setGeometricFigurePropertiesWindow.setHeight(375);
        if(this.getCanvasObjectData().isModifyingColors()){
            setGeometricFigurePropertiesWindow.setModifyingColor(true);
            setGeometricFigurePropertiesWindow.getModColorRB().setSelected(true);
            setGeometricFigurePropertiesWindow.getBrightnessTextField().setText(String.valueOf(this.getCanvasObjectData().getBrightness()));
            setGeometricFigurePropertiesWindow.getContrastTextField().setText(String.valueOf(this.getCanvasObjectData().getContrast()));
            setGeometricFigurePropertiesWindow.getHueTextField().setText(String.valueOf(this.getCanvasObjectData().getHue()));
            setGeometricFigurePropertiesWindow.getSaturationTextField().setText(String.valueOf(this.getCanvasObjectData().getSaturation()));
        }
        if(this.getCanvasObjectData().getPrimaryColor()!=null){
            setGeometricFigurePropertiesWindow.getColorPicker().setValue(this.getCanvasObjectData().getPrimaryColor().getColor());
        }else{
            setGeometricFigurePropertiesWindow.getColorPicker().setValue((Color) this.button.getBackground().getFills().get(0).getFill());
        }
        setGeometricFigurePropertiesWindow.getRotationTextField().setText(String.valueOf(this.getCanvasObjectData().getRotation()));
        setGeometricFigurePropertiesWindow.showAndWait();

        boolean isModifyingColor = setGeometricFigurePropertiesWindow.isModifyingColor();
        this.getCanvasObjectData().setModifyingColors(isModifyingColor);
        double rotation = Double.parseDouble(setGeometricFigurePropertiesWindow.getRotationTextField().getText());
        this.getCanvasObjectData().setRotation(rotation);
        this.setRotate(rotation);
        double contrast = Double.parseDouble(setGeometricFigurePropertiesWindow.getContrastTextField().getText());
        double brightness = Double.parseDouble(setGeometricFigurePropertiesWindow.getBrightnessTextField().getText());
        double saturation = Double.parseDouble(setGeometricFigurePropertiesWindow.getSaturationTextField().getText());
        double hue = Double.parseDouble(setGeometricFigurePropertiesWindow.getHueTextField().getText());
        CanvasColor color = new CanvasColor(setGeometricFigurePropertiesWindow.getColorPicker().getValue());
        modifyColors(color,contrast,brightness,saturation,hue);
        this.getCanvasObjectData().setWidth(setGeometricFigurePropertiesWindow.getWidthFromField());
        this.getCanvasObjectData().setHeight(setGeometricFigurePropertiesWindow.getHeightFromField());
        this.setSize(this.getCanvasObjectData().getWidth(), this.getCanvasObjectData().getHeight());
        this.getHmiApp().setWasModified(true);
        this.button.setPrefWidth(this.getCanvasObjectData().getWidth());
        this.button.setPrefHeight(this.getCanvasObjectData().getHeight());
    }

    private void modifyColors(CanvasColor color, double contrast, double brightness, double saturation, double hue) {
    }

    @Override
    public void setEnable(String enabled) {
        if(user.getRole().equals("Operador")){
            enabled = "Stop";
        }
        switch (enabled) {
            case "Play":
                super.setEnable("Play");
                this.button.setDisable(false);
                break;
            case "Stop":
                super.setEnable("Stop");
                this.button.setDisable(true);
                break;
            default:
                super.setEnable("True");
                this.button.setDisable(true);
                break;
        }
    }

}
