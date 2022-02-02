package andrade.luis.hmiethernetip.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class CanvasSystemDateTimeLabelTest {

    @Test
    void setTimeline() {
        CanvasSystemDateTimeLabel canvasSystemDateTimeLabel = new CanvasSystemDateTimeLabel("Test",new CanvasPoint(150,150));
        canvasSystemDateTimeLabel.setTimeline();
        Assertions.assertThat(canvasSystemDateTimeLabel.getTimeline()).isNotNull();
    }
}