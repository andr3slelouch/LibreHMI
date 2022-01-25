package andrade.luis.hmiethernetip;

import andrade.luis.hmiethernetip.controllers.HMICanvasController;
import andrade.luis.hmiethernetip.views.HMICanvas;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class HMIApp extends Application {
    HMICanvas root = new HMICanvas();
    @Override
    public void start(Stage stage) throws Exception {
        var canvas = new Canvas(300,300);
        root.getChildren().add(canvas);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        var scene = new HMICanvasController(root, bounds.getWidth(), bounds.getHeight(), Color.WHITESMOKE);
        Button rectangleBtn = new Button("Rectangle");
        rectangleBtn.setOnMouseClicked(mouseEvent -> {
            scene.setAddOnClickEnabled(true);
        });
        root.getChildren().add(rectangleBtn);

        stage.setTitle("HMI");
        stage.setScene(scene);
        stage.show();
    }
}
