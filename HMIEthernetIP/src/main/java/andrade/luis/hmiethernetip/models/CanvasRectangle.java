package andrade.luis.hmiethernetip.models;

import javafx.scene.shape.Rectangle;

public class CanvasRectangle extends CanvasBorderPane{
    private Rectangle rectangle;
    public CanvasRectangle(GraphicalRepresentation graphicalRepresentation) {
        super(graphicalRepresentation);
        this.rectangle = new Rectangle(this.getGraphicalRepresentation().getPosition().getX(),this.getGraphicalRepresentation().getPosition().getY());
        this.rectangle.setWidth(graphicalRepresentation.getWidth());
        this.rectangle.setHeight(graphicalRepresentation.getHeight());
        this.setCenter(rectangle);
    }

    public CanvasRectangle(CanvasPoint center) {
        super(center);
        this.rectangle = new Rectangle(this.getGraphicalRepresentation().getPosition().getX(),this.getGraphicalRepresentation().getPosition().getY());
        this.rectangle.setWidth(150);
        this.rectangle.setHeight(150);
        this.setCenter(rectangle);
    }

    @Override
    public void setCenter(CanvasPoint center){
        super.setCenter(center);
        this.rectangle = new Rectangle(this.getPosition().getX(),this.getPosition().getY());
        this.rectangle.setWidth(150);
        this.rectangle.setHeight(150);
        super.setCenter(this.rectangle);
    }
}
