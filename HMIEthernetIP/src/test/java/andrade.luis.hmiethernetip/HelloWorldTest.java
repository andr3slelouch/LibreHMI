package andrade.luis.hmiethernetip;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloWorldTest extends ApplicationTest {
    private final HelloWorld helloWorld = new HelloWorld();
    @Override
    public void start(Stage stage) {
        stage.setTitle("Hello World");
        stage.setScene(helloWorld.getScene());
        stage.show();
        stage.toFront();
    }
    @Test
    public void hasHelloWorldButton() {
        StackPane rootNode = (StackPane) helloWorld.getScene().getRoot();
        Button button = from(rootNode).lookup(".button").query();
        assertEquals("Hello World", button.getText());
    }
    
}
