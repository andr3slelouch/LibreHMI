package andrade.luis.hmiethernetip;

import andrade.luis.hmiethernetip.controllers.HMIScene;
import andrade.luis.hmiethernetip.views.HMICanvas;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class HMIApp extends Application {
    private Stage mainStage;
    private ArrayList<HMIScene> pagesScene = new ArrayList<>();

    @Override
    public void start(Stage stage) throws Exception {

        mainStage = stage;
        HMIScene scene = generatePage();
        pagesScene.add(scene);
        mainStage.setTitle("HMI");
        mainStage.setScene(scene);
        mainStage.show();
    }

    private HMIScene generatePage(){
        var canvas = new Canvas(300,300);
        HMICanvas root = new HMICanvas();
        root.getChildren().add(canvas);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        HMIScene scene = new HMIScene(root, bounds.getWidth(), bounds.getHeight(), Color.WHITESMOKE);
        Button rectangleBtn = new Button("Rectangle");
        rectangleBtn.setOnMouseClicked(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("Rectangle");
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
        Button newPageBtn = new Button("Add new Page");
        HBox hbox = new HBox(rectangleBtn,systemDateTimeLabelBtn,textBtn,newPageBtn);

        Label selectStagesLabel = new Label("Seleccione la ventana");
        ArrayList<String> itemsForComboBox = new ArrayList<>(List.of("Page "+pagesScene.size()));
        ComboBox<String> comboBox = new ComboBox<>();
        ListView<String> listViewReference = new ListView<>();
        scene.setComboBox(comboBox);
        scene.setListViewReference(listViewReference);
        scene.setItems(itemsForComboBox);
        Button changeButton = new Button("Cambiar");
        changeButton.setOnAction(actionEvent -> {
            int index = scene.getComboBox().getSelectionModel().getSelectedIndex();
            mainStage.setScene(pagesScene.get(index));
        });

        HBox changeStagesBox = new HBox(selectStagesLabel,scene.getComboBox(),changeButton);

        Image windowImage = null;
        try {
            windowImage = new Image("https://sc01.alicdn.com/kf/HTB1gevJvuySBuNjy1zdq6xPxFXaO/201140127/HTB1gevJvuySBuNjy1zdq6xPxFXaO.jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ImageView imageView = new ImageView(windowImage);

        VBox vbox = new VBox(hbox,changeStagesBox,scene.getListViewReference(),imageView);
        root.getChildren().add(vbox);

        newPageBtn.setOnMouseClicked(mouseEvent -> {
            HMIScene newScene = generatePage();
            ArrayList<String> pages = new ArrayList<>();
            pagesScene.add(newScene);
            for(int i = 0; i < pagesScene.size(); i++){
                pages.add("Page "+i);
            }
            updateScenesComboBox(pages);
            mainStage.setScene(newScene);
        });

        return scene;
    }

    private void updateScenesComboBox(ArrayList<String> itemsForComboBox){
        for(int i = 0; i < pagesScene.size(); i++){
            pagesScene.get(i).setItems(itemsForComboBox);
        }
    }
}
