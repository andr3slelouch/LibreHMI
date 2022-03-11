package andrade.luis.hmiethernetip.models;

import andrade.luis.hmiethernetip.models.canvas.CanvasLabel;
import andrade.luis.hmiethernetip.models.canvas.CanvasPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;

@ExtendWith(ApplicationExtension.class)
class CanvasLabelTest {

    @Test
    void setCenter() {
        CanvasLabel labelTest = new CanvasLabel();
        CanvasPoint center = new CanvasPoint(150,150);
        labelTest.setPosition(center);
        Assertions.assertThat(labelTest.getCanvasObjectData().getPosition()).isEqualTo(center);
    }

    @Test
    void setCanvasLabelData(){
        CanvasLabel labelTest = new CanvasLabel();
        labelTest.setData("Test");
        Assertions.assertThat(labelTest.getLabel().getText()).isEqualTo("Test");
        Assertions.assertThat(labelTest.getCanvasObjectData().getType()).isEqualTo("Label");
    }
}