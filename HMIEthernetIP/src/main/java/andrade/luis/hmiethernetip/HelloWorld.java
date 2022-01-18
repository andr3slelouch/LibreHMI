package andrade.luis.hmiethernetip;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class HelloWorld extends Parent {
    private final int SCENE_WIDTH = 500;
    private final int SCENE_HEIGHT = 500;
    private final Scene scene;
    private final StackPane rootNode = new StackPane();
    private final Button button = new Button("Hello World");
    public HelloWorld() {
        rootNode.getChildren().add(button);
        scene = new Scene(rootNode, SCENE_WIDTH, SCENE_HEIGHT);
    }
    /*public Scene getScene() {
        return scene;
    }*/

    @Override
    public Node getStyleableNode() {
        return super.getStyleableNode();
    }
}
