package andrade.luis.hmiethernetip.models;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

public class CanvasDynamicRectangle extends GraphicalRepresentation{
    private double value = 0;
    public CanvasDynamicRectangle(CanvasPoint center){
        super(center);
        Rectangle rectangle= new Rectangle();
        rectangle.setX(center.getX());
        rectangle.setY(center.getY());
        rectangle.setWidth(200);
        rectangle.setHeight(200);
        rectangle.setStroke(Paint.valueOf("#000000"));
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStrokeWidth(5);
        rectangle.setStrokeMiterLimit(5);
        rectangle.setSmooth(true);
        rectangle.setStrokeLineCap(StrokeLineCap.ROUND);
        // rectangle.setStrokeLineJoin(StrokeLineJoin.ROUND);
        double length=rectangle.getWidth()*2+rectangle.getHeight()*2;

        //set stroke dash length
        rectangle.getStrokeDashArray().addAll(length);

        //display empty stroke/border
        rectangle.setStrokeDashOffset(length);

        Group group = new Group();
        group.getChildren().add(rectangle);
        super.setCenter(group);
        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        (ActionEvent actionEvent) -> {
                            value=value+50;
                            double offset=length-((length*(value/100)));
                            rectangle.setStrokeDashOffset(-offset);
                        }), new KeyFrame(Duration.seconds(1)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();


    }
}
