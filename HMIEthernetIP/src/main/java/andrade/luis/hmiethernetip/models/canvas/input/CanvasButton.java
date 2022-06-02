package andrade.luis.hmiethernetip.models.canvas.input;

import andrade.luis.hmiethernetip.models.canvas.CanvasColor;
import andrade.luis.hmiethernetip.models.canvas.CanvasObjectData;
import andrade.luis.hmiethernetip.models.canvas.CanvasPoint;
import andrade.luis.hmiethernetip.models.canvas.CanvasObject;
import andrade.luis.hmiethernetip.models.users.HMIUser;
import andrade.luis.hmiethernetip.views.SelectWindowsWindow;
import andrade.luis.hmiethernetip.views.SetButtonPropertiesWindow;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

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
        this.getCanvasObjectData().setSuperType("InterfaceInputObject");
        this.setSize(width,height);
        this.button.setPrefWidth(width);
        this.button.setPrefHeight(height);
        this.setCenter(this.button);
        setNewMenuItem();
        this.setRotate(this.getCanvasObjectData().getRotation());
        if(this.getCanvasObjectData().getPrimaryColor()!=null){
            this.button.setBackground(new Background(new BackgroundFill(this.getCanvasObjectData().getPrimaryColor().getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
        }
        if(this.getCanvasObjectData().getFontFamily()!=null && this.getCanvasObjectData().getFontStyle()!=null){
            this.button.setFont(
                    Font.font(
                            this.getCanvasObjectData().getFontFamily(),
                            FontWeight.valueOf(this.getCanvasObjectData().getFontStyle()),
                            this.getCanvasObjectData().getFontSize()
                    )
            );
        }
        if(this.getCanvasObjectData().getFontColor()!=null){
            this.button.setTextFill(this.getCanvasObjectData().getFontColor().getColor());
        }
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
            this.button.setOnAction(mouseEvent -> onAction());
            this.getHmiApp().setWasModified(true);
        }
    }

    public void onAction(){
        boolean pagesReadyState = true;
        for (String selectedPage : this.getCanvasObjectData().getSelectedPages()) {
            pagesReadyState = pagesReadyState && (this.getHmiApp().getIndexForScene(selectedPage) != -1);
        }
        if(pagesReadyState){
            this.getHmiApp().generateStagesForPages(this.getCanvasObjectData().getSelectedPages());
            this.setTop(null);
        }else{
            this.errorLabel = new Label("Error en páginas asociadas");
            this.setTop(errorLabel);
        }
    }

    @Override
    public void setProperties(){
        SetButtonPropertiesWindow propertiesWindow = new SetButtonPropertiesWindow(this.getCanvasObjectData().getWidth(),this.getCanvasObjectData().getHeight());
        propertiesWindow.setTitle("Propiedades del Botón");
        propertiesWindow.setHeight(375);
        propertiesWindow.getColorPickerLabel().setValue((Color) this.button.getTextFill());
        propertiesWindow.getFontStyleComboBox().getSelectionModel().select(this.button.getFont().getStyle());
        propertiesWindow.getFontFamilyComboBox().getSelectionModel().select(this.button.getFont().getFamily());
        propertiesWindow.getFontSizeField().setText(String.valueOf(this.button.getFont().getSize()));
        propertiesWindow.getRotationTextField().setText(String.valueOf(this.getCanvasObjectData().getRotation()));
        if(this.getCanvasObjectData().getPrimaryColor()!=null){
            propertiesWindow.getColorPicker().setValue(this.getCanvasObjectData().getPrimaryColor().getColor());
        }else{
            propertiesWindow.getColorPicker().setValue(Color.WHITE);
        }
        if(this.getCanvasObjectData().getPrimaryColor()!=null){
            propertiesWindow.getColorPicker().setValue(this.getCanvasObjectData().getPrimaryColor().getColor());
        }else{
            propertiesWindow.getColorPicker().setValue((Color) this.button.getBackground().getFills().get(0).getFill());
        }
        propertiesWindow.getRotationTextField().setText(String.valueOf(this.getCanvasObjectData().getRotation()));
        propertiesWindow.showAndWait();

        double rotation = Double.parseDouble(propertiesWindow.getRotationTextField().getText());
        this.getCanvasObjectData().setRotation(rotation);
        this.setRotate(rotation);
        this.getCanvasObjectData().setWidth(propertiesWindow.getSizeVBox().getWidthFromField());
        this.getCanvasObjectData().setHeight(propertiesWindow.getSizeVBox().getHeightFromField());
        this.getHmiApp().setWasModified(true);
        this.button.setPrefWidth(this.getCanvasObjectData().getWidth());
        this.button.setPrefHeight(this.getCanvasObjectData().getHeight());
        this.setSize(this.getCanvasObjectData().getWidth(),this.getCanvasObjectData().getHeight());
        this.getCanvasObjectData().setPrimaryColor(new CanvasColor(propertiesWindow.getColorPicker().getValue()));
        if(this.getCanvasObjectData().getType().equals("Button")){
            this.button.setBackground(new Background(new BackgroundFill(this.getCanvasObjectData().getPrimaryColor().getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
        }
        this.getCanvasObjectData().setFontColor(new CanvasColor(propertiesWindow.getColorPickerLabel().getValue()));
        this.getCanvasObjectData().setFontStyle(propertiesWindow.getFontStyle().name());
        this.getCanvasObjectData().setFontFamily(propertiesWindow.getFontFamilyComboBox().getValue());
        this.getCanvasObjectData().setFontSize(Double.parseDouble(propertiesWindow.getFontSizeField().getText()));
        this.button.setFont(
                Font.font(
                        propertiesWindow.getFontFamilyComboBox().getValue(),
                        propertiesWindow.getFontStyle(),
                        Double.parseDouble(propertiesWindow.getFontSizeField().getText()
                        )
                )
        );
        this.button.setTextFill(propertiesWindow.getColorPickerLabel().getValue());
        this.getHmiApp().setWasModified(true);
    }

    @Override
    public void setEnable(String mode) {
        switch (mode) {
            case "Ejecutar":
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
