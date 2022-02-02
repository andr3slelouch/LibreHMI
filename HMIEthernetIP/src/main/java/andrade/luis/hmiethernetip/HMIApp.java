package andrade.luis.hmiethernetip;

import andrade.luis.hmiethernetip.controllers.HMICanvasController;
import andrade.luis.hmiethernetip.views.HMICanvas;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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
        HMICanvasController scene = new HMICanvasController(root, bounds.getWidth(), bounds.getHeight(), Color.WHITESMOKE);
        Button rectangleBtn = new Button("Rectangle");
        rectangleBtn.setOnMouseClicked(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("Rectangle");
        });
        Button labelBtn = new Button("Label");
        labelBtn.setOnMouseClicked(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("Label");
        });
        Button systemDateTimeLabelBtn = new Button("DateTime Label");
        systemDateTimeLabelBtn.setOnMouseClicked(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("SystemDateTimeLabel");
        });
        Button textBtn = new Button("Text");
        textBtn.setOnMouseClicked(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("Text");
        });
        Button dynamicRectBtn = new Button("DynRect");
        dynamicRectBtn.setOnMouseClicked(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("DynRect");
        });
        HBox hbox = new HBox(rectangleBtn,labelBtn,systemDateTimeLabelBtn,textBtn,dynamicRectBtn);
        root.getChildren().add(hbox);

        stage.setTitle("HMI");
        stage.setScene(scene);
        stage.show();
    }
}
