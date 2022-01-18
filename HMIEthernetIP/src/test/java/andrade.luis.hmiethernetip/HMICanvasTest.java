package andrade.luis.hmiethernetip;

import andrade.luis.hmiethernetip.controllers.HMICanvasController;
import andrade.luis.hmiethernetip.models.CanvasPoint;
import andrade.luis.hmiethernetip.models.CanvasRectangle;
import andrade.luis.hmiethernetip.views.HMICanvas;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

@ExtendWith(ApplicationExtension.class)
class HMICanvasTest {
    private HMICanvas root;
    private HMICanvasController scene;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Start
    private void start(Stage stage) {
        root = new HMICanvas();
        Canvas canvas = new Canvas(300, 300);
        root.getChildren().add(canvas);
        scene = new HMICanvasController(root, 400, 400, Color.WHITESMOKE);
        Button rectangleBtn = new Button("Rectangle");
        rectangleBtn.setId("addRectangle");
        rectangleBtn.setOnMouseClicked(mouseEvent -> scene.setAddOnClickEnabled(true));
        root.getChildren().add(rectangleBtn);

        stage.setTitle("HMI");
        stage.setScene(scene);
        stage.show();
    }

    @Test
    void addGraphicalRepresentation(FxRobot robot) {
        robot.clickOn(".button");
        robot.clickOn(scene);

        Assertions.assertThat(root.getShapeArrayList().isEmpty()).isFalse();
        Assertions.assertThat(root.getShapeArrayList().get(0).getId()).isEqualTo("#createdShape0");
    }

    @Test
    void addMultipleGraphicalRepresentation(FxRobot robot) throws InterruptedException {
        robot.clickOn(".button");
        robot.clickOn(scene);

        robot.clickOn(".button");
        robot.clickOn(scene);

        robot.clickOn(".button");
        robot.clickOn(scene);

        robot.clickOn(".button");
        robot.clickOn(scene);

        robot.clickOn(".button");
        robot.clickOn(scene);


        Assertions.assertThat(root.getShapeArrayList().isEmpty()).isFalse();
        Assertions.assertThat(root.getShapeArrayList().size()).isEqualTo(5);
    }

    @Test
    void moveGraphicalRepresentation(FxRobot robot) throws InterruptedException {
        robot.clickOn(".button");
        robot.clickOn(scene);
        CanvasRectangle rectangle = new CanvasRectangle();
        for(int i=0;i<root.getChildren().size();i++){
            if(root.getChildren().get(i).getId()!=null){
                if(root.getChildren().get(i).getId().equals("#createdShape0")){
                    rectangle = (CanvasRectangle) root.getChildren().get(i);
                    System.out.println(rectangle.getX()+"***"+rectangle.getY());
                }else{
                    System.out.println(root.getChildren().get(i).getId());
                }
            }
        }

        CanvasPoint firstPosition = rectangle.getPosition();

        System.out.println("First position X:"+firstPosition.getX()+"\nFirst position Y: "+firstPosition.getY()+"\nWith ID:"+root.getShapeArrayList().get(0).getId());

        robot.drag(rectangle, MouseButton.PRIMARY);
        robot.dropBy(rectangle.getX()+10, rectangle.getY()+10);

        for(int i=0;i<root.getChildren().size();i++){
            if(root.getChildren().get(i).getId()!=null){
                if(root.getChildren().get(i).getId().equals("#createdShape0")){
                    rectangle = (CanvasRectangle) root.getChildren().get(i);
                    System.out.println(rectangle.getX()+"***"+rectangle.getY());
                }else{
                    System.out.println(root.getChildren().get(i).getId());
                }
            }
        }

        CanvasPoint newPosition = rectangle.getPosition();

        Assertions.assertThat(newPosition.getX()).isNotEqualTo(firstPosition.getX());
        Assertions.assertThat(newPosition.getY()).isNotEqualTo(firstPosition.getY());
    }
}