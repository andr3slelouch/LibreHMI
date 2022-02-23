package andrade.luis.hmiethernetip.models.canvas;

import andrade.luis.hmiethernetip.models.GraphicalRepresentationData;
import andrade.luis.hmiethernetip.models.canvas.CanvasLabel;
import andrade.luis.hmiethernetip.models.canvas.CanvasPoint;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.util.Duration;

public class CanvasText extends CanvasLabel {
    private String text;

    public Timeline getTimeline() {
        return timeline;
    }

    private Timeline timeline;

    public CanvasText() {

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public CanvasText(GraphicalRepresentationData graphicalRepresentationData) {
        super(graphicalRepresentationData);
        this.getGraphicalRepresentationData().setType("Text");
    }

    public CanvasText(String content, CanvasPoint center) {
        super(content, center);
        this.getGraphicalRepresentationData().setType("Text");
    }

    public void setTimeline() {
        timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        (ActionEvent actionEvent) -> {
                            this.text = this.getGraphicalRepresentationData().readTagFromDatabase();
                            this.getLabel().setText(this.text);
                        }), new KeyFrame(Duration.seconds(1)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
