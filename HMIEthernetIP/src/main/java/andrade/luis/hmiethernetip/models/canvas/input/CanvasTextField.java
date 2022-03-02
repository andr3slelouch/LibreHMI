package andrade.luis.hmiethernetip.models.canvas.input;

import andrade.luis.hmiethernetip.models.GraphicalRepresentationData;
import andrade.luis.hmiethernetip.models.canvas.CanvasPoint;
import andrade.luis.hmiethernetip.models.canvas.GraphicalRepresentation;
import andrade.luis.hmiethernetip.models.users.HMIUser;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class CanvasTextField extends GraphicalRepresentation {
    private TextField textField;

    public HMIUser getUser() {
        return user;
    }

    public void setUser(HMIUser user) {
        this.user = user;
    }

    private HMIUser user;

    public CanvasTextField(CanvasPoint center) {
        super(center);
        this.getGraphicalRepresentationData().setType("TextField");
        setData(this.getGraphicalRepresentationData().getPosition().getX(), this.getGraphicalRepresentationData().getPosition().getY(), 150, 150);
    }

    public CanvasTextField(GraphicalRepresentationData graphicalRepresentationData) {
        super(graphicalRepresentationData);
        this.getGraphicalRepresentationData().setType("TextField");
        setData(this.getGraphicalRepresentationData().getPosition().getX(), this.getGraphicalRepresentationData().getPosition().getY(), this.getGraphicalRepresentationData().getWidth(), this.getGraphicalRepresentationData().getHeight());
    }

    private void setData(double x, double y, double width, double height) {
        this.textField = new TextField();
        if(this.getGraphicalRepresentationData().getData()!=null){
            this.textField.setText(this.getGraphicalRepresentationData().getData());
        }
        this.textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            this.getGraphicalRepresentationData().setData(newValue);
        });
        this.textField.setDisable(true);
        this.textField.setPrefWidth(width);
        this.textField.setPrefHeight(height);
        this.getGraphicalRepresentationData().setPosition(new CanvasPoint(x, y));
        this.getGraphicalRepresentationData().setWidth(width);
        this.getGraphicalRepresentationData().setHeight(height);
        this.setCenter(this.textField);

    }

    @Override
    public void setEnable(String enabled) {
        if(user.getRole().equals("Operador")){
            enabled = "Stop";
        }
        switch (enabled) {
            case "Play":
                super.setEnable("Play");
                this.textField.setDisable(false);
                break;
            case "Stop":
                super.setEnable("Stop");
                this.textField.setDisable(true);
                break;
            default:
                super.setEnable("True");
                this.textField.setDisable(true);
                break;
        }
    }


}
