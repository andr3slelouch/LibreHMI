package andrade.luis.hmiethernetip.example;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TwoColoredRectangle extends Application {

    @Override
    public void start(Stage primaryStage) {
        // property to change:
        DoubleProperty life = new SimpleDoubleProperty(100);

        // "animate" the property for demo:
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(life, 100)),
                new KeyFrame(Duration.seconds(2), new KeyValue(life, 0))
        );

        // button to set the animation going:
        Button startButton = new Button("Start");
        startButton.setOnAction(e -> timeline.playFromStart());

        VBox root = new VBox(5);

        // Solution 1. One rectangle on another:
        Rectangle base = new Rectangle(0, 0, 400, 20);
        base.setFill(Color.GRAY);

        Rectangle lifeRect = new Rectangle(0, 0, 400, 20);
        lifeRect.setFill(Color.GREEN);
        lifeRect.widthProperty().bind(life.multiply(4));

        Pane solutionPane1 = new Pane(base, lifeRect);
        root.getChildren().add(createSolutionPane("Dynamic rectangle on fixed rectangle", solutionPane1));

        // Solution 2: Two rectangles changing together:
        Rectangle leftRect = new Rectangle();
        leftRect.setHeight(20);
        leftRect.setFill(Color.GREEN);

        Rectangle rightRect = new Rectangle();
        rightRect.setHeight(20);
        rightRect.setFill(Color.GRAY);

        leftRect.widthProperty().bind(life.multiply(4));
        rightRect.xProperty().bind(leftRect.widthProperty());
        rightRect.widthProperty().bind(life.multiply(-4).add(400));

        Pane solutionPane2 = new Pane(leftRect, rightRect);
        root.getChildren().add(createSolutionPane("Two dynamic rectangles", solutionPane2));

        // Solution 3: Green rectangle on gray-styled pane:
        Pane basePane = new Pane();
        basePane.setMinSize(400, 20);
        basePane.setMaxSize(400, 20);
        // gray color will be defined in CSS:
        basePane.getStyleClass().add("base-pane");

        Rectangle rect = new Rectangle();
        rect.setHeight(20);
        rect.setFill(Color.GREEN);
        rect.widthProperty().bind(life.multiply(4));

        basePane.getChildren().add(rect);
        root.getChildren().add(createSolutionPane("Dynamic rectangle on pane", basePane));

        // Solution 4: Dynamically-styled pane:
        Pane dynamicPane = new Pane();
        dynamicPane.setMinSize(400, 20);
        dynamicPane.setMaxSize(400, 20);
        dynamicPane.getStyleClass().add("dynamic-pane");
        // make background insets depend on the property:
        dynamicPane.styleProperty().bind(life.multiply(4).asString("-fx-background-insets: 0, 0 0 0 %f"));

        root.getChildren().add(createSolutionPane("Dynamically styled pane", dynamicPane));

        // Solution 5: Progress bar:
        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefSize(400, 20);
        progressBar.progressProperty().bind(life.divide(100));
        root.getChildren().add(createSolutionPane("Progress bar", progressBar));


        root.getChildren().add(startButton);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("two-colored-rectangle.css");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Node createSolutionPane(String title, Node content) {
        VBox vbox = new VBox(new Label(title), content);
        vbox.getStyleClass().add("solution-pane");
        return vbox ;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

