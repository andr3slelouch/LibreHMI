package andrade.luis.hmiethernetip;

import andrade.luis.hmiethernetip.controllers.HMIScene;
import andrade.luis.hmiethernetip.views.HMICanvas;
import andrade.luis.hmiethernetip.views.SetWindowPropertiesWindow;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HMIApp extends Application {
    private Stage mainStage;
    Logger logger
            = Logger.getLogger(
            HMIApp.class.getName());

    public ArrayList<HMIScene> getPages() {
        return pages;
    }

    private final ArrayList<HMIScene> pages = new ArrayList<>();
    private static final String HMI_TITLE = "HMI: ";

    @Override
    public void start(Stage stage) {

        mainStage = stage;
        HMIScene scene = generatePage("Página 1", "", Color.WHITESMOKE);
        pages.add(scene);
        mainStage.setTitle(HMI_TITLE + scene.getSceneTitle());
        mainStage.setScene(scene);
        mainStage.show();
    }

    private HMIScene generatePage(String sceneTitle, String sceneCommentary, Color backgroundColor) {
        var canvas = new Canvas(300, 300);
        HMICanvas root = new HMICanvas();
        root.getChildren().add(canvas);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(root);
        HMIScene scene = new HMIScene(stackPane, root, sceneTitle, sceneCommentary, bounds.getWidth(), bounds.getHeight(), backgroundColor);
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
        HBox hbox = new HBox(rectangleBtn, systemDateTimeLabelBtn, textBtn, newPageBtn);

        ArrayList<String> itemsForComboBox = new ArrayList<>(List.of(scene.getSceneTitle()));
        ListView<String> listViewReference = new ListView<>();
        scene.setListViewReference(listViewReference);
        scene.setItems(itemsForComboBox);

        VBox vbox = new VBox(hbox, scene.getListViewReference());
        root.getChildren().add(vbox);

        newPageBtn.setOnMouseClicked(mouseEvent -> addNewScene());
        scene.setHmiApp(this);

        return scene;
    }

    public void changeSelectedScene(String sceneTitle) {
        int index = findSceneIndex(sceneTitle);
        mainStage.setScene(pages.get(index));
        mainStage.setTitle(HMI_TITLE + pages.get(index).getSceneTitle());
        pages.get(index).getListViewReference().getSelectionModel().select(index);
    }

    private void updateScenesInListView(ArrayList<String> itemsForComboBox) {
        for (HMIScene page : pages) {
            page.setItems(itemsForComboBox);
        }
    }

    public void duplicateScene(String sceneTitle) {
        int index = findSceneIndex(sceneTitle);
        try {
            HMIScene duplicateScene = pages.get(index).clone();
            if (duplicateScene != null) {
                SetWindowPropertiesWindow setWindowPropertiesWindow = new SetWindowPropertiesWindow(duplicateScene.getSceneTitle() + " copy", duplicateScene.getSceneCommentary(), duplicateScene.getBackground());
                setWindowPropertiesWindow.showAndWait();
                duplicateScene.update(setWindowPropertiesWindow.getNameField().getText(), setWindowPropertiesWindow.getCommentField().getText(), setWindowPropertiesWindow.getWindowColorPicker().getValue());
                addScene(duplicateScene);
            } else {
                logger.log(Level.INFO, "duplication is null");
            }


        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        logger.log(Level.INFO, "Finished duplicatedScene method");

    }

    public void deleteScene(String sceneTitle) {
        int index = findSceneIndex(sceneTitle);
        if (confirmDelete(sceneTitle)) {
            pages.remove(index);
            ArrayList<String> pagesTitles = new ArrayList<>();
            for (HMIScene hmiScene : this.pages) {
                pagesTitles.add(hmiScene.getSceneTitle());
            }
            updateScenesInListView(pagesTitles);
            mainStage.setScene(pages.get(0));
        }
    }

    public void updateScene(String sceneTitle) {
        int index = findSceneIndex(sceneTitle);
        HMIScene scene = (index != -1 && index < pages.size()) ? pages.get(index) : null;

        if (scene != null) {
            SetWindowPropertiesWindow setWindowPropertiesWindow = new SetWindowPropertiesWindow(scene.getSceneTitle(), scene.getSceneCommentary(), scene.getBackground());
            setWindowPropertiesWindow.showAndWait();
            scene.update(setWindowPropertiesWindow.getNameField().getText(), setWindowPropertiesWindow.getCommentField().getText(), setWindowPropertiesWindow.getWindowColorPicker().getValue());
            pages.set(index, scene);
            for (HMIScene page : pages) {
                page.updateItem(index, scene.getSceneTitle());
            }
        }
    }


    public void addNewScene() {
        SetWindowPropertiesWindow setWindowPropertiesWindow = new SetWindowPropertiesWindow();
        setWindowPropertiesWindow.showAndWait();
        if (!setWindowPropertiesWindow.isCancelled()) {
            HMIScene newScene = generatePage(setWindowPropertiesWindow.getNameField().getText(), setWindowPropertiesWindow.getCommentField().getText(), setWindowPropertiesWindow.getWindowColorPicker().getValue());
            addScene(newScene);
        }
    }

    private void addScene(HMIScene newScene) {
        logger.log(Level.INFO, "In addScene");
        this.pages.add(newScene);
        ArrayList<String> pagesTitles = new ArrayList<>();
        for (HMIScene hmiScene : this.pages) {
            pagesTitles.add(hmiScene.getSceneTitle());
        }
        updateScenesInListView(pagesTitles);

        mainStage.setScene(newScene);
        mainStage.setTitle(HMI_TITLE + newScene.getSceneTitle());

        logger.log(Level.INFO, "Finishing addScene");
    }

    private int findSceneIndex(String sceneTitle) {
        for (int i = 0; i < pages.size(); i++) {
            if (pages.get(i).getSceneTitle().equals(sceneTitle)) {
                return i;
            }
        }
        return -1;
    }

    private boolean confirmDelete(String sceneTitle) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("Desea eliminar la página seleccionada \"" + sceneTitle + "\"?");

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(cancelButton, okButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == okButton) {
            alert.close();
            return true;
        } else if (result.isPresent() && result.get() == cancelButton) {
            alert.close();
            return false;
        }
        return false;
    }
}
