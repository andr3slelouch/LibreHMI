package andrade.luis.hmiethernetip.models.canvas;

import javafx.scene.shape.Ellipse;

public class CanvasEllipse extends CanvasObject{
    private Ellipse ellipse;

    public CanvasEllipse(CanvasPoint center){
        super(center);
        setData(this.getCanvasObjectData().getPosition().getX(), this.getCanvasObjectData().getPosition().getY(), 75,75);
    }

    public CanvasEllipse(CanvasObjectData canvasObjectData){
        super(canvasObjectData);
        setData(this.getCanvasObjectData().getPosition().getX(), this.getCanvasObjectData().getPosition().getY(), this.getCanvasObjectData().getWidth(),this.getCanvasObjectData().getHeight());
    }

    private void setData(double x, double y, double width, double height) {
        this.ellipse = new Ellipse(x, y, width, height);
        this.getCanvasObjectData().setWidth(width);
        this.getCanvasObjectData().setHeight(height);
        this.setCenter(this.ellipse);
        this.getCanvasObjectData().setType("Ellipse");
        this.setContextMenu();
    }

    @Override
    public void properties(){
        super.properties();
        this.ellipse.setRadiusX(this.getCanvasObjectData().getWidth());
        this.ellipse.setRadiusY(this.getCanvasObjectData().getHeight());
    }
}
