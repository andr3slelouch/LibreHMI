package andrade.luis.hmiethernetip.models.canvas.input;

import andrade.luis.hmiethernetip.models.canvas.CanvasPoint;
import andrade.luis.hmiethernetip.models.canvas.GraphicalRepresentation;
import andrade.luis.hmiethernetip.models.users.HMIUser;
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
        setData(this.getGraphicalRepresentationData().getPosition().getX(), this.getGraphicalRepresentationData().getPosition().getY(), 150, 150);
    }

    private void setData(double x, double y, int width, int height) {
        this.textField = new TextField("TEXTFIELD");
        this.textField.setDisable(true);
        this.getGraphicalRepresentationData().setPosition(new CanvasPoint(x, y));
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
