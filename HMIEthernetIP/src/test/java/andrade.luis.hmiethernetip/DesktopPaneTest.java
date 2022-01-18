package andrade.luis.hmiethernetip;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.awt.*;

import static javafx.scene.input.KeyCode.ENTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.hasChildren;

public class DesktopPaneTest extends ApplicationTest {
    @Override
    public void start(Stage stage) {
        DesktopPane desktopPane = new DesktopPane();
        Scene scene = new Scene(desktopPane, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    @BeforeAll
    public static void setUpClass() throws Exception {
        ApplicationTest.launch(ClickApplication.class);
    }


    @Test
    public void should_drag_file_into_trashcan() {
        // given:
        rightClickOn("#desktop").moveTo("New").clickOn("Text Document");
        write("myTextfile.txt").push(ENTER);

        // when:
        drag(".file").dropTo("#trash-can");

        // then:
        verifyThat("#desktop", hasChildren(0, ".file"));
    }
}

