package andrade.luis.hmiethernetip;

import andrade.luis.hmiethernetip.controllers.HMIScene;
import andrade.luis.hmiethernetip.models.canvas.CanvasPoint;
import andrade.luis.hmiethernetip.models.canvas.CanvasText;
import andrade.luis.hmiethernetip.views.HMICanvas;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.util.ArrayList;

@ExtendWith(ApplicationExtension.class)
class HMICanvasTest {
    private HMICanvas root;
    private HMIScene scene;
    private Button rectangleBtn;
    private Button systemDateTimeLabelBtn;
    private Button textBtn;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Start
    private void start(Stage stage) {
        root = new HMICanvas();
        root.setMode("Test");
        Canvas canvas = new Canvas(300, 300);
        root.getChildren().add(canvas);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        javafx.scene.control.ScrollPane stackPane = new ScrollPane();
        stackPane.setContent(root);
        scene = new HMIScene(stackPane,root, "Page","",bounds.getWidth(), bounds.getHeight(), Color.WHITESMOKE);
        rectangleBtn = new Button("Rectangle");
        rectangleBtn.setId("addRectangle");
        rectangleBtn.setOnMouseClicked(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("Rectangle");
        });
        systemDateTimeLabelBtn = new Button("DateTime Label");
        systemDateTimeLabelBtn.setOnMouseClicked(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("SystemDateTimeLabel");
        });
        textBtn = new Button("Text");
        textBtn.setOnMouseClicked(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("Text");
        });
        HBox hbox = new HBox(rectangleBtn,systemDateTimeLabelBtn,textBtn);
        root.getChildren().add(hbox);


        stage.setTitle("HMI");
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }

    void addGraphicalRepresentationSequence(FxRobot robot){
        robot.clickOn(".button");
        robot.clickOn(scene);
    }

    @Test
    void addGraphicalRepresentationTest(FxRobot robot) {
        addGraphicalRepresentationSequence(robot);

        Assertions.assertThat(root.getShapeArrayList().isEmpty()).isFalse();
        Assertions.assertThat(root.getShapeArrayList().get(0).getId()).isEqualTo("#createdShape0");
    }

    @Test
    void addMultipleGraphicalRepresentation(FxRobot robot) {
        addGraphicalRepresentationSequence(robot);
        addGraphicalRepresentationSequence(robot);
        addGraphicalRepresentationSequence(robot);
        addGraphicalRepresentationSequence(robot);
        addGraphicalRepresentationSequence(robot);

        Assertions.assertThat(root.getShapeArrayList().isEmpty()).isFalse();
        Assertions.assertThat(root.getShapeArrayList().size()).isEqualTo(5);
    }

    andrade.luis.hmiethernetip.models.canvas.CanvasObject getAddedGraphicalRepresentation(String id){
        andrade.luis.hmiethernetip.models.canvas.CanvasObject rectangle = null;
        for (int i = 0; i < root.getChildren().size(); i++) {
            if (root.getChildren().get(i).getId() != null) {
                if (root.getChildren().get(i).getId().equals(id)) {
                    rectangle = (andrade.luis.hmiethernetip.models.canvas.CanvasObject) root.getChildren().get(i);
                }
            }
        }
        return rectangle;
    }

    @Test
    void moveGraphicalRepresentation(FxRobot robot) {
        addGraphicalRepresentationSequence(robot);
        andrade.luis.hmiethernetip.models.canvas.CanvasObject rectangle = getAddedGraphicalRepresentation("#createdShape0");
        Assertions.assertThat(rectangle).isNotNull();

        CanvasPoint firstPosition = rectangle.getPosition();

        robot.drag(rectangle, MouseButton.PRIMARY);
        robot.dropBy(rectangle.getPosition().getX() + 10, rectangle.getPosition().getY() + 10);

        rectangle = getAddedGraphicalRepresentation("#createdShape0");

        CanvasPoint newPosition = rectangle.getPosition();

        Assertions.assertThat(newPosition.getX()).isNotEqualTo(firstPosition.getX());
        Assertions.assertThat(newPosition.getY()).isNotEqualTo(firstPosition.getY());
    }

    andrade.luis.hmiethernetip.models.canvas.CanvasObject reduceMenuItemsInGraphicalRepresentation(andrade.luis.hmiethernetip.models.canvas.CanvasObject rectangle, String id){
        MenuItem tempCopyMenuItem = null;
        ArrayList<MenuItem> tempItems = new ArrayList<>();
        for (int i = 0; i < rectangle.getRightClickMenu().getItems().size(); i++) {
            if (rectangle.getRightClickMenu().getItems().get(i).getId().equals(id)) {
                tempCopyMenuItem = rectangle.getRightClickMenu().getItems().get(i);
            } else {
                tempItems.add(rectangle.getRightClickMenu().getItems().get(i));
            }
        }
        Assertions.assertThat(tempCopyMenuItem).isNotNull();
        rectangle.getRightClickMenu().getItems().removeAll(tempItems);

        return rectangle;
    }

    void rightClickOnRightClickMenu(FxRobot robot, andrade.luis.hmiethernetip.models.canvas.CanvasObject rectangle){
        robot.rightClickOn(rectangle);
        robot.clickOn(rectangle.getRightClickMenu());
    }

    void checkForClipboardReadyToPaste(){
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            DataFlavor flavor = new DataFlavor("application/x-java-serialized-object;class=andrade.luis.hmiethernetip.models.GraphicalRepresentationData");
            Assertions.assertThat(clipboard.isDataFlavorAvailable(flavor)).isTrue();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    String copyGraphicalRepresentationToClipboardByRightClickOnContextMenu(FxRobot robot) {
        addGraphicalRepresentationSequence(robot);

        Assertions.assertThat(root.getShapeArrayList().isEmpty()).isFalse();
        Assertions.assertThat(root.getShapeArrayList().get(0).getId()).isEqualTo("#createdShape0");

        andrade.luis.hmiethernetip.models.canvas.CanvasObject rectangle = getAddedGraphicalRepresentation("#createdShape0");

        rectangle = reduceMenuItemsInGraphicalRepresentation(rectangle,"#copy");

        rightClickOnRightClickMenu(robot,rectangle);

        checkForClipboardReadyToPaste();

        return rectangle.getId();
    }

    @Test
    void copyGraphicalRepresentationToClipboardByRightClickOnContextMenuTest(FxRobot robot) {
        copyGraphicalRepresentationToClipboardByRightClickOnContextMenu(robot);
    }

    @Test
    void copyGraphicalRepresentationToClipboardByRightClickOnContextMenuAndPasteOnKeyCombinationTest(FxRobot robot) {
        copyGraphicalRepresentationToClipboardByRightClickOnContextMenuAndPasteOnKeyCombination(robot);
    }

    void copyGraphicalRepresentationToClipboardByRightClickOnContextMenuAndPasteOnKeyCombination(FxRobot robot) {
        String oldId = copyGraphicalRepresentationToClipboardByRightClickOnContextMenu(robot);
        robot.press(KeyCode.CONTROL, KeyCode.V);
        robot.release(KeyCode.CONTROL, KeyCode.V);
        boolean idIsACopyId = checkIfIdIsCopyId(oldId);
        Assertions.assertThat(idIsACopyId).isTrue();
        Assertions.assertThat(root.getShapeArrayList().size()).isEqualTo(2);
    }

    void copyGraphicalRepresentationToClipboardByRightClickOnContextMenuAndPasteOnRightClick(FxRobot robot) {
        String oldId = copyGraphicalRepresentationToClipboardByRightClickOnContextMenu(robot);
        robot.moveTo(100,100);
        robot.rightClickOn(100,100);
        robot.clickOn(scene.getCanvas().getRightClickMenu());
        boolean idIsACopyId = checkIfIdIsCopyId(oldId);
        Assertions.assertThat(idIsACopyId).isTrue();
        Assertions.assertThat(root.getShapeArrayList().size()).isEqualTo(2);
    }

    @Test
    void copyGraphicalRepresentationToClipboardByRightClickOnContextMenuAndPasteOnRightClickTest(FxRobot robot) {
        copyGraphicalRepresentationToClipboardByRightClickOnContextMenuAndPasteOnRightClick(robot);
    }

    @Test
    void copyGraphicalRepresentationToClipboardByKeyCombinationTest(FxRobot robot) {
        copyGraphicalRepresentationToClipboardByKeyCombination(robot);
    }

    String copyGraphicalRepresentationToClipboardByKeyCombination(FxRobot robot) {
        addGraphicalRepresentationSequence(robot);

        Assertions.assertThat(root.getShapeArrayList().isEmpty()).isFalse();
        Assertions.assertThat(root.getShapeArrayList().get(0).getId()).isEqualTo("#createdShape0");

        robot.press(KeyCode.CONTROL, KeyCode.C);
        robot.release(KeyCode.CONTROL, KeyCode.C);

        checkForClipboardReadyToPaste();

        return getAddedGraphicalRepresentation("#createdShape0").getId();
    }

    boolean checkIfIdIsCopyId(String oldId){
        boolean idIsACopyId = false;
        for (int i = 0; i < root.getChildren().size(); i++) {
            if (root.getChildren().get(i).getId() != null) {
                if (root.getChildren().get(i).getId().contains(oldId) && !root.getChildren().get(i).getId().equals(oldId)) {
                    idIsACopyId = true;
                }
            }
        }
        return idIsACopyId;
    }

    boolean checkIfIdIsCutId(String oldId){
        boolean idIsACutId = false;
        for (int i = 0; i < root.getChildren().size(); i++) {
            if (root.getChildren().get(i).getId() != null) {
                if (root.getChildren().get(i).getId().equals(oldId)) {
                    idIsACutId = true;
                }
            }
        }
        return idIsACutId;
    }

    @Test
    void copyGraphicalRepresentationToClipboardByKeyCombinationAndPasteOnKeyCombinationTest(FxRobot robot) {
        copyGraphicalRepresentationToClipboardByKeyCombinationAndPasteOnKeyCombination(robot);
    }

    void copyGraphicalRepresentationToClipboardByKeyCombinationAndPasteOnKeyCombination(FxRobot robot) {
        String oldId = copyGraphicalRepresentationToClipboardByKeyCombination(robot);
        robot.press(KeyCode.CONTROL, KeyCode.V);
        robot.release(KeyCode.CONTROL, KeyCode.V);
        boolean idIsACopyId = checkIfIdIsCopyId(oldId);
        Assertions.assertThat(idIsACopyId).isTrue();
        Assertions.assertThat(root.getShapeArrayList().size()).isEqualTo(2);
    }

    @Test
    void copyGraphicalRepresentationToClipboardByKeyCombinationAndPasteOnOnRightClickTest(FxRobot robot) {
        copyGraphicalRepresentationToClipboardByKeyCombinationAndPasteOnRightClick(robot);
    }

    void copyGraphicalRepresentationToClipboardByKeyCombinationAndPasteOnRightClick(FxRobot robot) {
        String oldId = copyGraphicalRepresentationToClipboardByKeyCombination(robot);
        robot.moveTo(100,100);
        robot.rightClickOn(100,100);
        robot.clickOn(scene.getCanvas().getRightClickMenu());
        boolean idIsACopyId = checkIfIdIsCopyId(oldId);
        Assertions.assertThat(idIsACopyId).isTrue();
        Assertions.assertThat(root.getShapeArrayList().size()).isEqualTo(2);
    }

    @Test
    void cutGraphicalRepresentationToClipboardByKeyCombinationTest(FxRobot robot) {
        cutGraphicalRepresentationToClipboardByKeyCombination(robot);
    }

    String cutGraphicalRepresentationToClipboardByKeyCombination(FxRobot robot) {
        addGraphicalRepresentationSequence(robot);

        Assertions.assertThat(root.getShapeArrayList().isEmpty()).isFalse();
        Assertions.assertThat(root.getShapeArrayList().get(0).getId()).isEqualTo("#createdShape0");

        String oldId = getAddedGraphicalRepresentation("#createdShape0").getId();

        robot.press(KeyCode.CONTROL, KeyCode.X);
        robot.release(KeyCode.CONTROL, KeyCode.X);
        checkForClipboardReadyToPaste();

        return oldId;
    }

    @Test
    void cutGraphicalRepresentationToClipboardByKeyCombinationAndPasteOnRightClickTest(FxRobot robot) {
        cutGraphicalRepresentationToClipboardByKeyCombinationAndPasteOnRightClick(robot);
    }

    void cutGraphicalRepresentationToClipboardByKeyCombinationAndPasteOnRightClick(FxRobot robot) {
        String oldId = cutGraphicalRepresentationToClipboardByKeyCombination(robot);
        robot.rightClickOn(100,100);
        robot.clickOn(scene.getCanvas().getRightClickMenu());
        boolean idIsACutId = checkIfIdIsCutId(oldId);
        Assertions.assertThat(idIsACutId).isTrue();
        Assertions.assertThat(root.getShapeArrayList().size()).isEqualTo(1);
    }

    @Test
    void cutGraphicalRepresentationToClipboardByKeyCombinationAndPasteOnKeyCombinationTest(FxRobot robot) {
        cutGraphicalRepresentationToClipboardByKeyCombinationAndPasteOnKeyCombination(robot);
    }

    void cutGraphicalRepresentationToClipboardByKeyCombinationAndPasteOnKeyCombination(FxRobot robot) {
        String oldId = cutGraphicalRepresentationToClipboardByKeyCombination(robot);
        robot.press(KeyCode.CONTROL, KeyCode.V);
        robot.release(KeyCode.CONTROL, KeyCode.V);
        boolean idIsACutId = checkIfIdIsCutId(oldId);
        Assertions.assertThat(idIsACutId).isTrue();
        Assertions.assertThat(root.getShapeArrayList().size()).isEqualTo(1);
    }

    @Test
    void cutGraphicalRepresentationToClipboardByRightClickOnContextMenuTest(FxRobot robot) {
        cutGraphicalRepresentationToClipboardByRightClickOnContextMenu(robot);
    }

    String cutGraphicalRepresentationToClipboardByRightClickOnContextMenu(FxRobot robot) {
        addGraphicalRepresentationSequence(robot);

        Assertions.assertThat(root.getShapeArrayList().isEmpty()).isFalse();
        Assertions.assertThat(root.getShapeArrayList().get(0).getId()).isEqualTo("#createdShape0");

        andrade.luis.hmiethernetip.models.canvas.CanvasObject rectangle = getAddedGraphicalRepresentation("#createdShape0");
        Assertions.assertThat(rectangle).isNotNull();

        rectangle = reduceMenuItemsInGraphicalRepresentation(rectangle,"#cut");
        String oldId = rectangle.getId();

        robot.rightClickOn(rectangle);
        robot.clickOn(rectangle.getRightClickMenu());

        rectangle = getAddedGraphicalRepresentation("#createdShape0");
        Assertions.assertThat(rectangle).isNull();

        checkForClipboardReadyToPaste();

        return oldId;

    }

    @Test
    void cutGraphicalRepresentationToClipboardByRightClickOnContextMenuAndPasteOnRightClickTest(FxRobot robot) {
        cutGraphicalRepresentationToClipboardByRightClickOnContextMenuAndPasteOnRightClick(robot);
    }

    void cutGraphicalRepresentationToClipboardByRightClickOnContextMenuAndPasteOnRightClick(FxRobot robot) {
        String oldId = cutGraphicalRepresentationToClipboardByRightClickOnContextMenu(robot);
        robot.moveTo(100,100);
        robot.rightClickOn(100,100);
        robot.clickOn(scene.getCanvas().getRightClickMenu());
        boolean idIsACutId = checkIfIdIsCutId(oldId);
        Assertions.assertThat(idIsACutId).isTrue();
        Assertions.assertThat(root.getShapeArrayList().size()).isEqualTo(1);
    }

    @Test
    void cutGraphicalRepresentationToClipboardByRightClickOnContextMenuAndPasteOnKeyCombinationTest(FxRobot robot) {
        cutGraphicalRepresentationToClipboardByRightClickOnContextMenuAndPasteOnKeyCombination(robot);
    }

    void cutGraphicalRepresentationToClipboardByRightClickOnContextMenuAndPasteOnKeyCombination(FxRobot robot) {
        String oldId = cutGraphicalRepresentationToClipboardByRightClickOnContextMenu(robot);
        robot.press(KeyCode.CONTROL, KeyCode.V);
        robot.release(KeyCode.CONTROL, KeyCode.V);
        boolean idIsACutId = checkIfIdIsCutId(oldId);
        Assertions.assertThat(idIsACutId).isTrue();
        Assertions.assertThat(root.getShapeArrayList().size()).isEqualTo(1);
    }

    @Test
    void deleteGraphicalRepresentationByRightClickOnContextMenuTest(FxRobot robot){
        addGraphicalRepresentationSequence(robot);
        Assertions.assertThat(root.getShapeArrayList().isEmpty()).isFalse();
        Assertions.assertThat(root.getShapeArrayList().get(0).getId()).isEqualTo("#createdShape0");

        andrade.luis.hmiethernetip.models.canvas.CanvasObject rectangle = getAddedGraphicalRepresentation("#createdShape0");
        Assertions.assertThat(rectangle).isNotNull();

        rectangle = reduceMenuItemsInGraphicalRepresentation(rectangle,"#delete");

        robot.rightClickOn(rectangle);
        robot.clickOn(rectangle.getRightClickMenu());

        Assertions.assertThat(root.getShapeArrayList().size()).isZero();

    }

    @Test
    void deleteGraphicalRepresentationByKeyPressTest(FxRobot robot){
        addGraphicalRepresentationSequence(robot);
        Assertions.assertThat(root.getShapeArrayList().isEmpty()).isFalse();
        Assertions.assertThat(root.getShapeArrayList().get(0).getId()).isEqualTo("#createdShape0");
        robot.press(KeyCode.DELETE);
        robot.release(KeyCode.DELETE);
        Assertions.assertThat(root.getShapeArrayList().size()).isZero();
    }

    @Test
    void addSystemDateTimeLabel(FxRobot robot){
        robot.clickOn(systemDateTimeLabelBtn);
        robot.clickOn(scene);

        andrade.luis.hmiethernetip.models.canvas.CanvasObject canvasSystemDateTimeLabel = getAddedGraphicalRepresentation("#createdShape0");
        Assertions.assertThat(canvasSystemDateTimeLabel).isNotNull();
    }
    @Test
    void addTextLabel(FxRobot robot){
        robot.clickOn(textBtn);
        robot.clickOn(scene);

        CanvasText canvasSystemDateTimeLabel = (CanvasText) getAddedGraphicalRepresentation("#createdShape0");
        Assertions.assertThat(canvasSystemDateTimeLabel).isNotNull();
        Assertions.assertThat(canvasSystemDateTimeLabel.getCanvasObjectData().getTag()).isNotNull();

    }
}