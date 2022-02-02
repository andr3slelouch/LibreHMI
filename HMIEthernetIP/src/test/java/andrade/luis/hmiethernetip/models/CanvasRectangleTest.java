package andrade.luis.hmiethernetip.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class CanvasRectangleTest {

    @Test
    void setCenter() {
        CanvasRectangle rectangleTest = new CanvasRectangle();
        CanvasPoint center = new CanvasPoint(150,150);
        rectangleTest.setCenter(center);
        Assertions.assertThat(rectangleTest.getGraphicalRepresentationData().getCenter()).isEqualTo(center);
        Assertions.assertThat(rectangleTest.getRectangle()).isNotNull();
        Assertions.assertThat(rectangleTest.getCenter()).isEqualTo(rectangleTest.getRectangle());
        Assertions.assertThat(rectangleTest.getGraphicalRepresentationData().getType()).isEqualTo("Rectangle");
    }
}