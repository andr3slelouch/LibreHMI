package andrade.luis.hmiethernetip.models.canvas;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.animation.Animation;

public class CanvasSystemDateTimeLabel extends CanvasLabel {
    public Timeline getTimeline() {
        return timeline;
    }

    private Timeline timeline;
    public CanvasSystemDateTimeLabel(CanvasObjectData canvasObjectData) {
        super(canvasObjectData);
        this.getCanvasObjectData().setType("SystemDateTimeLabel");
    }

    public CanvasSystemDateTimeLabel(String content, CanvasPoint center) {
        super(content, center);
        this.getCanvasObjectData().setType("SystemDateTimeLabel");
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
