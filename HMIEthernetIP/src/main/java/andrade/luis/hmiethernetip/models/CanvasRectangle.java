package andrade.luis.hmiethernetip.models;

import javafx.scene.shape.Rectangle;

public class CanvasRectangle extends GraphicalRepresentation {
    private Rectangle rectangle;
    public CanvasRectangle(GraphicalRepresentationData graphicalRepresentationData) {
        super(graphicalRepresentationData);
        this.rectangle = new Rectangle(this.getGraphicalRepresentationData().getPosition().getX(),this.getGraphicalRepresentationData().getPosition().getY());
        this.rectangle.setWidth(graphicalRepresentationData.getWidth());
        this.rectangle.setHeight(graphicalRepresentationData.getHeight());
        this.setCenter(rectangle);
        this.getGraphicalRepresentationData().setType("Rectangle");
    }

    public CanvasRectangle(CanvasPoint center) {
        super(center);
        this.rectangle = new Rectangle(this.getGraphicalRepresentationData().getPosition().getX(),this.getGraphicalRepresentationData().getPosition().getY());
        this.rectangle.setWidth(150);
        this.rectangle.setHeight(150);
        this.setCenter(rectangle);
        this.getGraphicalRepresentationData().setType("Rectangle");
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
