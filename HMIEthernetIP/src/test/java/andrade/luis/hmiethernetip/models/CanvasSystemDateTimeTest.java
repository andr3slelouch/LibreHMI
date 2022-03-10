package andrade.luis.hmiethernetip.models;

import andrade.luis.hmiethernetip.models.canvas.CanvasPoint;
import andrade.luis.hmiethernetip.models.canvas.CanvasSystemDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;

@ExtendWith(ApplicationExtension.class)
class CanvasSystemDateTimeTest {

    @Test
    void setTimeline() {
        CanvasSystemDateTime canvasSystemDateTime = new CanvasSystemDateTime("Test",new CanvasPoint(150,150));
        canvasSystemDateTime.setTimeline();
        Assertions.assertThat(canvasSystemDateTime.getTimeline()).isNotNull();
    }
}