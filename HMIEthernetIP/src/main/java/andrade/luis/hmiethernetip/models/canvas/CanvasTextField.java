package andrade.luis.hmiethernetip.models.canvas;

import javafx.scene.control.TextField;

public class CanvasTextField extends GraphicalRepresentation {
    private TextField textField;

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


}
