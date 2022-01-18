package andrade.luis.hmiethernetip;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private final HelloWorld helloWorld = new HelloWorld();
    @Override
    public void start(Stage stage) throws IOException {
        /*FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();*/
        stage.setTitle("Hello World");
        stage.setScene(helloWorld.getScene());
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}