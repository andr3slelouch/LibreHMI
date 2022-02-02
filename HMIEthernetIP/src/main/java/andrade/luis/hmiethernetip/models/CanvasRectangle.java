package andrade.luis.hmiethernetip.models;

import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CanvasRectangle extends GraphicalRepresentation {
    private MenuItem linkTag;
    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    private Rectangle rectangle;
    public CanvasRectangle(GraphicalRepresentationData graphicalRepresentationData) {
        super(graphicalRepresentationData);
        setData(this.getGraphicalRepresentationData().getPosition().getX(),this.getGraphicalRepresentationData().getPosition().getY(),graphicalRepresentationData.getWidth(),graphicalRepresentationData.getHeight());
        Logger logger = Logger.getLogger(CanvasRectangle.this.getClass().getName());
        logger.log(Level.INFO,this.getGraphicalRepresentationData().getTag().getTagName());
    }

    public CanvasRectangle(CanvasPoint center) {
        super(center);
        setData(this.getGraphicalRepresentationData().getPosition().getX(),this.getGraphicalRepresentationData().getPosition().getY(),150,150);
    }

    public CanvasRectangle() {

    }

    @Override
    public void setCenter(CanvasPoint center){
        super.setCenter(center);
        setData(this.getPosition().getX(),this.getPosition().getY(),150,150);
        super.setCenter(this.rectangle);
    }

    public void setData(double x, double y,double width,double height){
        this.rectangle = new Rectangle(x,y);
        this.rectangle.setWidth(width);
        this.rectangle.setHeight(height);
        Stop[] stop = {new Stop(0, Color.RED),
                new Stop(1, Color.BLUE)};

        // create a Linear gradient object
        LinearGradient linear_gradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stop);
        this.rectangle.setFill(linear_gradient);
        this.setCenter(rectangle);
        this.getGraphicalRepresentationData().setType("Rectangle");
        this.linkTag = new MenuItem("Link Tag");
        this.linkTag.setId("#linkTag");
        this.linkTag.setOnAction(actionEvent -> this.getGraphicalRepresentationData().setTag(this.getCanvas().selectTag()));
        this.setContextMenu();
        this.getRightClickMenu().getItems().add(this.linkTag);
    }
}
