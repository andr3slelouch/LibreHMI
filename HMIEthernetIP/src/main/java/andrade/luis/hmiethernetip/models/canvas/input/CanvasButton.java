package andrade.luis.hmiethernetip.models.canvas.input;

import andrade.luis.hmiethernetip.HMIApp;
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

    public HMIApp getHmiApp() {
        return hmiApp;
    }

    public void setHmiApp(HMIApp hmiApp) {
        this.hmiApp = hmiApp;
    }

    private HMIApp hmiApp;

    public CanvasButton(CanvasPoint center) {
        super(center);
        setData(this.getCanvasObjectData().getPosition().getX(), this.getCanvasObjectData().getPosition().getY(), 150, 50);
    }

    public CanvasButton(CanvasObjectData canvasObjectData){
        super(canvasObjectData);
        logger.log(Level.INFO,canvasObjectData.getType());
        setData(this.getCanvasObjectData().getPosition().getX(), this.getCanvasObjectData().getPosition().getY(), this.getCanvasObjectData().getWidth(), this.getCanvasObjectData().getHeight());
    }

    public void setData(double x, double y, double width, double height) {
        this.getCanvasObjectData().setType("Button");
        this.button = new Button("Action");
        this.button.setDisable(true);
        this.getCanvasObjectData().setPosition(new CanvasPoint(x, y));
        this.getCanvasObjectData().setWidth(width);
        this.getCanvasObjectData().setHeight(height);
        this.button.setPrefWidth(width);
        this.button.setPrefHeight(height);
        this.setCenter(this.button);

        setNewMenuItem();
    }

    public void setNewMenuItem(){
        MenuItem attachShowHideWindowsActionMI = new MenuItem("Acción de Mostrar Ocultar Ventana");
        attachShowHideWindowsActionMI.setId("#showHideWindowsMI");
        attachShowHideWindowsActionMI.setOnAction(actionEvent -> buttonAction());
        this.getRightClickMenu().getItems().add(attachShowHideWindowsActionMI);
    }

    public void buttonAction() {
        SelectWindowsWindow selectWindowsWindow = new SelectWindowsWindow(hmiApp.getPagesTitles(), this.getCanvasObjectData().getSelectedPages());
        selectWindowsWindow.showAndWait();
        this.setSelectedPages(selectWindowsWindow.getSelectedItems());
    }

    public void setSelectedPages(ArrayList<String> selectedPages) {
        if(selectedPages != null){
            this.getCanvasObjectData().setSelectedPages(selectedPages);
            boolean pagesReadyState = true;
            for (String selectedPage : selectedPages) {
                pagesReadyState = pagesReadyState && (this.hmiApp.getIndexForScene(selectedPage) != -1);
            }
            if(pagesReadyState){
                this.button.setOnAction(mouseEvent -> this.hmiApp.generateStagesForPages(this.getCanvasObjectData().getSelectedPages()));
                this.setTop(null);
            }else{
                this.errorLabel = new Label("Error en páginas asociadas");
                this.setTop(errorLabel);
            }
        }
    }

    @Override
    public void resize(){
        super.resize();
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
