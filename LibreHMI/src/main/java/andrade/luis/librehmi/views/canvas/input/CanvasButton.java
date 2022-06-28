package andrade.luis.librehmi.views.canvas.input;

import andrade.luis.librehmi.views.canvas.CanvasColor;
import andrade.luis.librehmi.models.CanvasObjectData;
import andrade.luis.librehmi.views.canvas.CanvasPoint;
import andrade.luis.librehmi.views.canvas.CanvasObject;
import andrade.luis.librehmi.models.users.HMIUser;
import andrade.luis.librehmi.views.windows.SelectPagesWindow;
import andrade.luis.librehmi.views.windows.SetButtonPropertiesWindow;
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

/**
 * Esta es la clase que contiene define a un CanvasButton, se utiliza para moverse entra las páginas del proyecto
 * al hacer clic en el luego de haberlo asociado a las páginas seleccionadas por el usuario.
 */
public class CanvasButton extends CanvasObject {
    protected Button button;

    @Override
    public HMIUser getUser() {
        return user;
    }

    @Override
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
        setData(150, 50);
    }

    /**
     * Constructor de la clase, define al botón con todos los atributos correspondientes desde el argumento,
     * este método es utilizado cuando se hace un copiar/pegar o cuando se está importando desde archivo.
     * @param canvasObjectData Objeto que contiene todos los atributos relevantes para construir el objeto
     */
    public CanvasButton(CanvasObjectData canvasObjectData){
        super(canvasObjectData);
        setData(this.getCanvasObjectData().getWidth(), this.getCanvasObjectData().getHeight());
    }

    /**
     * Este método permite definir los atributos que constituyen el objeto
     * @param width Ancho del botón
     * @param height Altura del botón
     */
    public void setData(double width, double height) {
        this.getCanvasObjectData().setType("Button");
        this.getCanvasObjectData().setDataType("Botón");
        this.button = new Button(this.getCanvasObjectData().getData());
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
        SelectPagesWindow selectPagesWindow = new SelectPagesWindow(this.getHmiApp().getPagesTitles(), this.getCanvasObjectData().getSelectedPages());
        selectPagesWindow.showAndWait();
        this.setSelectedPages(selectPagesWindow.getSelectedItems());
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

    /**
     * Permite definir la acción del clic en el botón, buscando realizar el cambio de página
     * a la seleccionada por el usuario
     */
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

    /**
     * Permite definir las propiedades del botón, su tamaño, contenido, color
     */
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
        if(this.getCanvasObjectData().getData()!=null){
            propertiesWindow.getButtonLabelField().setText(this.getCanvasObjectData().getData());
        }
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
        this.getCanvasObjectData().setData(propertiesWindow.getButtonLabelField().getText());
        this.button.setText(this.getCanvasObjectData().getData());
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
