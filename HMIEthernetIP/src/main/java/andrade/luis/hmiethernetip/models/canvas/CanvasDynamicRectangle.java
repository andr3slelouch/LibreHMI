package andrade.luis.hmiethernetip.models.canvas;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class CanvasDynamicRectangle extends GraphicalRepresentation {
    private double value = 0;
    public CanvasDynamicRectangle(CanvasPoint center){
        super(center);

        // Solution 2: Two rectangles changing together:
        Rectangle leftRect = new Rectangle();
        leftRect.setHeight(20);
        leftRect.setFill(Color.GREEN);

        Rectangle rightRect = new Rectangle();
        rightRect.setHeight(20);
        rightRect.setFill(Color.GRAY);

        DoubleProperty life = new SimpleDoubleProperty(100);
        leftRect.widthProperty().bind(life.multiply(4));
        rightRect.xProperty().bind(leftRect.widthProperty());
        rightRect.widthProperty().bind(life.multiply(-4).add(400));

        Pane solutionPane2 = new Pane(leftRect, rightRect);
        this.setCenter(solutionPane2);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(life, 100)),
                new KeyFrame(Duration.seconds(2), new KeyValue(life, 0))
        );

        //timeline.playFromStart();

    }
}
