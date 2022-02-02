package andrade.luis.hmiethernetip.models;

import javafx.scene.control.Label;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.logging.Level;
import java.util.logging.Logger;

@ExtendWith(ApplicationExtension.class)
class CanvasTextTest {

    @Test
    void prepareQueryAndReadFromDatabase() {
        CanvasText testCanvasText = new CanvasText();
        Tag tag = new Tag("","","","temperatura","Flotante","","","");
        testCanvasText.getGraphicalRepresentationData().setTag(tag);
        String text = "";
        text = testCanvasText.getGraphicalRepresentationData().readTagFromDatabase();
        Logger logger = Logger.getLogger(CanvasText.class.getName());
        logger.log(Level.INFO,text);
        Assertions.assertThat(text).isNotBlank();
    }

    @Test
    void setTimeline() {
        CanvasText testCanvasText = new CanvasText();
        testCanvasText.setLabel(new Label(""));
        Tag tag = new Tag("","","","temperatura","Flotante","","","");
        testCanvasText.getGraphicalRepresentationData().setTag(tag);
        testCanvasText.setTimeline();
        Assertions.assertThat(testCanvasText.getTimeline()).isNotNull();
    }
}