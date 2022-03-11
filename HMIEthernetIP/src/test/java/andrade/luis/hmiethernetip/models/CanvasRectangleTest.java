package andrade.luis.hmiethernetip.models;

import andrade.luis.hmiethernetip.models.canvas.CanvasPoint;
import andrade.luis.hmiethernetip.models.canvas.CanvasRectangle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;

@ExtendWith(ApplicationExtension.class)
class CanvasRectangleTest {

    @Test
    void setCenter() {
        CanvasRectangle rectangleTest = new CanvasRectangle();
        CanvasPoint center = new CanvasPoint(150,150);
        rectangleTest.setPosition(center);
        Assertions.assertThat(rectangleTest.getCanvasObjectData().getPosition()).isEqualTo(center);
        Assertions.assertThat(rectangleTest.getRectangle()).isNotNull();
        Assertions.assertThat(rectangleTest.getCenter()).isEqualTo(rectangleTest.getRectangle());
        Assertions.assertThat(rectangleTest.getCanvasObjectData().getType()).isEqualTo("Rectangle");
    }
}