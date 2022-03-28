package andrade.luis.hmiethernetip.models.canvas.input;

import andrade.luis.hmiethernetip.models.canvas.CanvasObjectData;
import andrade.luis.hmiethernetip.models.canvas.CanvasPoint;
import andrade.luis.hmiethernetip.models.canvas.CanvasObject;
import andrade.luis.hmiethernetip.models.users.HMIUser;
import andrade.luis.hmiethernetip.views.SelectWindowsWindow;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

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
    public void properties(){
        super.properties();
        this.button.setPrefWidth(this.getCanvasObjectData().getWidth());
        this.button.setPrefHeight(this.getCanvasObjectData().getHeight());
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
