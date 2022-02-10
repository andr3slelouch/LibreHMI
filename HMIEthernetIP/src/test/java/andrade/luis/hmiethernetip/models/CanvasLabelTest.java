package andrade.luis.hmiethernetip.models;

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
        labelTest.setCenter(center);
        Assertions.assertThat(labelTest.getGraphicalRepresentationData().getCenter()).isEqualTo(center);
    }

    @Test
    void setCanvasLabelData(){
        CanvasLabel labelTest = new CanvasLabel();
        labelTest.setData("Test");
        Assertions.assertThat(labelTest.getLabel().getText()).isEqualTo("Test");
        Assertions.assertThat(labelTest.getGraphicalRepresentationData().getType()).isEqualTo("Label");
    }
}