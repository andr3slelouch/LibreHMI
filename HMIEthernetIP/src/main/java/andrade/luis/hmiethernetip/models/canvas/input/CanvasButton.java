package andrade.luis.hmiethernetip.models.canvas.input;

import andrade.luis.hmiethernetip.HMIApp;
import andrade.luis.hmiethernetip.controllers.HMIScene;
import andrade.luis.hmiethernetip.models.canvas.CanvasPoint;
import andrade.luis.hmiethernetip.models.canvas.GraphicalRepresentation;
import andrade.luis.hmiethernetip.models.users.HMIUser;
import andrade.luis.hmiethernetip.views.SelectWindowsWindow;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;

public class CanvasButton extends GraphicalRepresentation {
    private Button button;

    public HMIUser getUser() {
        return user;
    }

    public void setUser(HMIUser user) {
        this.user = user;
    }

    private HMIUser user;
    private ArrayList<HMIScene> selectedPages = new ArrayList<>();

    public HMIApp getHmiApp() {
        return hmiApp;
    }

    public void setHmiApp(HMIApp hmiApp) {
        this.hmiApp = hmiApp;
    }

    private HMIApp hmiApp;

    public CanvasButton(CanvasPoint center) {
        super(center);
        setData(this.getGraphicalRepresentationData().getPosition().getX(), this.getGraphicalRepresentationData().getPosition().getY(), 150, 50);
    }

    public void setData(double x, double y, double width, double height) {
        this.button = new Button("Action");
        this.button.setDisable(true);
        this.getGraphicalRepresentationData().setPosition(new CanvasPoint(x, y));
        this.getGraphicalRepresentationData().setWidth(width);
        this.getGraphicalRepresentationData().setHeight(height);
        this.button.setPrefWidth(width);
        this.button.setPrefHeight(height);
        this.setCenter(this.button);

        MenuItem attachShowHideWindowsActionMI = new MenuItem("Show/Hide Windows");
        attachShowHideWindowsActionMI.setId("#showHideWindowsMI");
        attachShowHideWindowsActionMI.setOnAction(actionEvent -> selectWindows());
        this.getRightClickMenu().getItems().add(attachShowHideWindowsActionMI);
    }

    public void selectWindows() {
        SelectWindowsWindow selectWindowsWindow = new SelectWindowsWindow(hmiApp.getPages(), selectedPages);
        selectWindowsWindow.showAndWait();
        selectedPages = selectWindowsWindow.getSelectedItems();
        this.button.setOnAction(mouseEvent -> this.hmiApp.generateStagesForPages(selectedPages));
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
