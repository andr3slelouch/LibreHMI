package andrade.luis.hmiethernetip.models;

import andrade.luis.hmiethernetip.views.WriteExpressionWindow;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.codehaus.commons.compiler.CompileException;

import java.lang.reflect.InvocationTargetException;

public class CanvasRectangle extends GraphicalRepresentation {
    private MenuItem linkTag;
    private MenuItem percentFill;
    private Timeline refillRectangleTimeline;

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    private Rectangle rectangle;

    public CanvasRectangle(GraphicalRepresentationData graphicalRepresentationData) {
        super(graphicalRepresentationData);
        setData(this.getGraphicalRepresentationData().getPosition().getX(), this.getGraphicalRepresentationData().getPosition().getY(), graphicalRepresentationData.getWidth(), graphicalRepresentationData.getHeight());
        setPercentFill(graphicalRepresentationData.getRefillExpression());
        System.out.println(this.getGraphicalRepresentationData().getRefillExpression().getExpressionToEvaluate());
        System.out.println(this.refillRectangleTimeline.getStatus());

    }

    public CanvasRectangle(CanvasPoint center) {
        super(center);
        setData(this.getGraphicalRepresentationData().getPosition().getX(), this.getGraphicalRepresentationData().getPosition().getY(), 150, 150);
    }

    public CanvasRectangle() {

    }

    @Override
    public void setCenter(CanvasPoint center) {
        super.setCenter(center);
        setData(this.getPosition().getX(), this.getPosition().getY(), 150, 150);
        super.setCenter(this.rectangle);
    }

    public void setData(double x, double y, double width, double height) {
        this.rectangle = new Rectangle(x, y);
        this.rectangle.setWidth(width);
        this.rectangle.setHeight(height);
        this.setCenter(rectangle);
        this.getGraphicalRepresentationData().setType("Rectangle");
        this.linkTag = new MenuItem("Link Tag");
        this.linkTag.setId("#linkTag");
        this.linkTag.setOnAction(actionEvent -> this.getGraphicalRepresentationData().setTag(this.getCanvas().selectTag()));
        this.setContextMenu();
        this.getRightClickMenu().getItems().add(this.linkTag);
        this.percentFill = new MenuItem("Percent Fill Animation");
        this.percentFill.setId("#percentFill");
        this.percentFill.setOnAction(actionEvent -> this.setPercentFill());
        this.setContextMenu();
        this.getRightClickMenu().getItems().add(this.percentFill);
    }

    private void setPercentFill() {
        WriteExpressionWindow writeExpressionWindow = new WriteExpressionWindow();
        writeExpressionWindow.showAndWait();
        Expression expression = writeExpressionWindow.getLocalExpression();
        try {
            expression.evaluate();
            this.setPercentFill(expression);
        } catch (Exception e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Error");
            errorAlert.setContentText(e.toString());
            errorAlert.showAndWait();
            System.out.println(e.getMessage());
        }
    }

    private void setPercentFill(Expression exp){
        this.getGraphicalRepresentationData().setRefillExpression(exp);
        double width = this.rectangle.getWidth();
        DoubleProperty life = new SimpleDoubleProperty(width);
        this.rectangle.widthProperty().bind(life.multiply(width * 0.01));
        Rectangle rightRect = new Rectangle();
        rightRect.setFill(Color.GREEN);
        rightRect.setHeight(this.rectangle.getHeight());
        rightRect.xProperty().bind(this.rectangle.widthProperty());
        rightRect.widthProperty().bind(life.multiply(-width * 0.01).add(width));
        Pane solutionPane2 = new Pane(this.rectangle, rightRect);
        this.setCenter(solutionPane2);
        refillRectangleTimeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        (ActionEvent actionEvent) -> {
                            try {
                                life.set((double) this.getGraphicalRepresentationData().getRefillExpression().evaluate());
                            } catch (CompileException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }), new KeyFrame(Duration.seconds(1)));
        refillRectangleTimeline.setCycleCount(Animation.INDEFINITE);
        refillRectangleTimeline.play();

        this.setOnMouseClicked(onMyMouseClicked);
    }

    private EventHandler<MouseEvent> onMyMouseClicked = mouseEvent -> {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 1) {
            this.setSelected(true);
        } else if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
            playOrStopIfCorrespond();
        } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            showContextMenu(mouseEvent.getScreenX(), mouseEvent.getScreenY());
        }
    };

    private void playOrStopIfCorrespond() {
        if (refillRectangleTimeline.getStatus().toString().equals("RUNNING")) {
            refillRectangleTimeline.stop();
        }else if(refillRectangleTimeline.getStatus().toString().equals("STOPPED")){
            refillRectangleTimeline.play();
        }
    }
}
