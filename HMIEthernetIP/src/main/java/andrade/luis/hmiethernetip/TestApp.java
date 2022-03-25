package andrade.luis.hmiethernetip;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class TestApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Button someButton = new Button("Sample content");

        ScrollPane stackPane = new ScrollPane(someButton);
        stackPane.setPrefSize(500, 500);
        stackPane.setStyle("-fx-background-color: blue;");

        VBox vBox = new VBox();
        vBox.getChildren().add(new Button("Test"));
        vBox.setPrefWidth(200);
        vBox.setStyle("-fx-background-color: red; -fx-border-color: orange; -fx-border-width: 5;");


        Button rectangleBtn = new Button("Rectangle");

        Button systemDateTimeLabelBtn = new Button("DateTime Label");

        Button textBtn = new Button("Text");

        Button buttonBtn = new Button("Button");

        Button sliderBtn = new Button("Slider");

        Button textFieldBtn = new Button("TextField");

        Button alarmDisplayBtn = new Button("Alarm Display");

        Button manageUsersBtn = new Button("Manage Users");
        Button registerUserBtn = new Button("Register");
        Button logIntUserBtn = new Button("Log In");
        Button propertiesBtn = new Button("Guardar Propiedades");
        Button symbolBtn = new Button("Symbol");
        Button imageBtn = new Button("Image");
        Button pushbuttonBtn = new Button("Pushbutton");
        Button playBtn = new Button("Play");
        Button stopBtn = new Button("Stop");
        Button defaultBtn = new Button("Default");
        Button saveBtn = new Button("Guardar");
        Button saveAsBtn = new Button("Guardar Como");
        Button loadBtn = new Button("Cargar");
        Button newBtn = new Button("Nuevo");
        Button alarmBtn = new Button("Alarma");
        Button manageAlarmBtn = new Button("Administrar Alarmas");
        HBox hbox = new HBox(rectangleBtn, systemDateTimeLabelBtn, textBtn, buttonBtn, alarmDisplayBtn);
        HBox secondHBox = new HBox(sliderBtn, textFieldBtn, manageUsersBtn, registerUserBtn);
        HBox thirdHBox = new HBox(saveBtn, saveAsBtn, loadBtn, newBtn);
        HBox fourthHBox = new HBox(logIntUserBtn, propertiesBtn, imageBtn, symbolBtn, pushbuttonBtn);
        HBox fifthHBox = new HBox(playBtn, stopBtn, defaultBtn, alarmBtn, manageAlarmBtn);

        ListView<String> listViewReference = new ListView<>();


        vBox.getChildren().addAll(hbox, secondHBox, thirdHBox, fourthHBox, fifthHBox);


        Button expandButton = new Button(">");

        HBox slider = new HBox(vBox, expandButton);
        slider.setAlignment(Pos.CENTER);
        slider.setPrefWidth(VBox.USE_COMPUTED_SIZE);
        slider.setMaxWidth(VBox.USE_PREF_SIZE);

        // start out of view
        slider.setTranslateX(-vBox.getPrefWidth());
        StackPane.setAlignment(slider, Pos.CENTER_LEFT);

        // animation for moving the slider
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(slider.translateXProperty(), -vBox.getPrefWidth())),
                new KeyFrame(Duration.millis(500), new KeyValue(slider.translateXProperty(), 0d))
        );

        expandButton.setOnAction(evt -> {
            // adjust the direction of play and start playing, if not already done
            String text = expandButton.getText();
            boolean playing = timeline.getStatus() == Animation.Status.RUNNING;
            if (">".equals(text)) {
                timeline.setRate(1);
                if (!playing) {
                    timeline.playFromStart();
                }
                expandButton.setText("<");
            } else {
                timeline.setRate(-1);
                if (!playing) {
                    timeline.playFrom("end");
                }
                expandButton.setText(">");
            }
        });

        stackPane.setContent(slider);

        final Scene scene = new Scene(stackPane);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
