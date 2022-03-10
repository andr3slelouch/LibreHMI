package andrade.luis.hmiethernetip.models.canvas;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.animation.Animation;

public class CanvasSystemDateTime extends CanvasLabel {
    public Timeline getTimeline() {
        return timeline;
    }

    private Timeline timeline;
    public CanvasSystemDateTime(CanvasObjectData canvasObjectData) {
        super(canvasObjectData);
        this.getCanvasObjectData().setType("SystemDateTime");
    }

    public CanvasSystemDateTime(String content, CanvasPoint center) {
        super(content, center);
        this.getCanvasObjectData().setType("SystemDateTime");
    }

    public void setTimeline() {
        timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        (ActionEvent actionEvent) -> {
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                            LocalDateTime now = LocalDateTime.now();
                            this.getLabel().setText(dtf.format(now));
                        }), new KeyFrame(Duration.seconds(1)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
