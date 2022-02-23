package andrade.luis.hmiethernetip.models.canvas;

import javafx.scene.control.Button;

public class CanvasButton extends GraphicalRepresentation {
    private Button button;
    public CanvasButton(CanvasPoint center){
        super(center);
        setData(this.getGraphicalRepresentationData().getPosition().getX(), this.getGraphicalRepresentationData().getPosition().getY(), 150, 150);
    }
    public void setData(double x, double y, double width, double height) {
        this.button = new Button("Action");
        this.button.setDisable(false);
        this.getGraphicalRepresentationData().setPosition(new CanvasPoint(x, y));
        //this.button.setPrefWidth(width);
        //this.button.setPrefHeight(height);
        this.setCenter(this.button);
    }
}
