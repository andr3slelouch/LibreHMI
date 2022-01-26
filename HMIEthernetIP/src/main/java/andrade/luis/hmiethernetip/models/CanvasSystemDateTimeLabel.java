package andrade.luis.hmiethernetip.models;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CanvasSystemDateTimeLabel extends CanvasLabel {
    public CanvasSystemDateTimeLabel(GraphicalRepresentationData graphicalRepresentationData) {
        super(graphicalRepresentationData);
        this.getGraphicalRepresentationData().setType("SystemDateTimeLabel");
    }

    public CanvasSystemDateTimeLabel(String content, CanvasPoint center) {
        super(content, center);
        this.getGraphicalRepresentationData().setType("SystemDateTimeLabel");
    }

    public void setTimeline() {
        Timeline t1 = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        (ActionEvent actionEvent) -> {
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                            LocalDateTime now = LocalDateTime.now();
                            this.getLabel().setText(dtf.format(now));
                        }), new KeyFrame(Duration.seconds(1)));
        t1.setCycleCount(t1.INDEFINITE);
        t1.play();
    }
}
