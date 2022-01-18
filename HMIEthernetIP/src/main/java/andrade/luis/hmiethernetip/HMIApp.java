package andrade.luis.hmiethernetip;

import andrade.luis.hmiethernetip.controllers.HMICanvasController;
import andrade.luis.hmiethernetip.views.HMICanvas;
import javafx.application.Application;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class HMIApp extends Application {
    HMICanvas root = new HMICanvas();
    @Override
    public void start(Stage stage) throws Exception {
        var canvas = new Canvas(300,300);
        root.getChildren().add(canvas);
        var scene = new HMICanvasController(root,400,400, Color.WHITESMOKE);
        String workingDirectory = System.getProperty("user.dir");
        System.out.println(workingDirectory);
        //scene.getStylesheets().add(getClass().getResource("border.css").toExternalForm());
        Button rectangleBtn = new Button("Rectangle");
        rectangleBtn.setOnMouseClicked(mouseEvent -> scene.setAddOnClickEnabled(true));
        root.getChildren().add(rectangleBtn);

        stage.setTitle("HMI");
        stage.setScene(scene);
        stage.show();
    }
}
