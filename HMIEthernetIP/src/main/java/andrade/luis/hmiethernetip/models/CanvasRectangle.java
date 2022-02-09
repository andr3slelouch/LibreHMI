package andrade.luis.hmiethernetip.models;

import andrade.luis.hmiethernetip.views.SetPercentFillPropertiesWindow;
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
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.codehaus.commons.compiler.CompileException;

import java.lang.reflect.InvocationTargetException;

public class CanvasRectangle extends GraphicalRepresentation {
    private Timeline refillRectangleTimeline;
    private DoubleProperty life;

    public Rectangle getRectangle() {
        return rectangle;
    }

    private Rectangle rectangle;

    public CanvasRectangle(GraphicalRepresentationData graphicalRepresentationData) {
        super(graphicalRepresentationData);
        setData(this.getGraphicalRepresentationData().getPosition().getX(), this.getGraphicalRepresentationData().getPosition().getY(), graphicalRepresentationData.getWidth(), graphicalRepresentationData.getHeight());
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
        this.getGraphicalRepresentationData().setPosition(new CanvasPoint(x,y));
        this.rectangle.setWidth(width);
        this.getGraphicalRepresentationData().setWidth(width);
        this.rectangle.setHeight(height);
        this.getGraphicalRepresentationData().setHeight(height);
        this.setCenter(rectangle);
        this.getGraphicalRepresentationData().setType("Rectangle");
        MenuItem linkTag = new MenuItem("Link Tag");
        linkTag.setId("#linkTag");
        linkTag.setOnAction(actionEvent -> this.getGraphicalRepresentationData().setTag(this.getCanvas().selectTag()));
        this.setContextMenu();
        this.getRightClickMenu().getItems().add(linkTag);
        MenuItem percentFill = new MenuItem("Percent Fill Animation");
        percentFill.setId("#percentFill");
        percentFill.setOnAction(actionEvent -> this.setPercentFill());
        this.setContextMenu();
        this.getRightClickMenu().getItems().add(percentFill);
    }

    private void setPercentFill() {
        SetPercentFillPropertiesWindow writeExpressionWindow;
        if(this.getGraphicalRepresentationData().getRefillExpression()!=null){
            writeExpressionWindow = new SetPercentFillPropertiesWindow(this.getGraphicalRepresentationData().getPrimaryColor().getColor(),this.getGraphicalRepresentationData().getBackgroundColor().getColor());
            writeExpressionWindow.setAddedTags(this.getGraphicalRepresentationData().getRefillExpression().getParameters());
            writeExpressionWindow.setLocalExpression(this.getGraphicalRepresentationData().getRefillExpression());
            writeExpressionWindow.getTextField().setText(this.getGraphicalRepresentationData().getRefillExpression().getExpressionToEvaluate());
        }else{
            writeExpressionWindow = new SetPercentFillPropertiesWindow();
        }
        writeExpressionWindow.showAndWait();
        Expression expression = writeExpressionWindow.getLocalExpression();
        try {
            if(expression !=null) {
                expression.evaluate();
                this.setPercentFill(expression, writeExpressionWindow.getPrimaryColor(),writeExpressionWindow.getBackgroundColor());
            }
        } catch (Exception e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Error");
            errorAlert.setContentText(e.toString());
            errorAlert.showAndWait();
            e.printStackTrace();
        }
    }

    public void setPercentFill(Expression exp, CanvasColor primaryColor, CanvasColor backgroundColor) {
        if(exp != null){
            this.getGraphicalRepresentationData().setRefillExpression(exp);
            double width = this.getGraphicalRepresentationData().getWidth();
            life = new SimpleDoubleProperty(width);
            this.rectangle = new Rectangle();
            this.rectangle.widthProperty().bind(life.multiply(width * 0.01));
            this.rectangle.setFill(primaryColor.getColor());
            this.rectangle.setHeight(this.getGraphicalRepresentationData().getHeight());
            this.getGraphicalRepresentationData().setPrimaryColor(primaryColor);

            Rectangle rightRect = new Rectangle();
            rightRect.setFill(backgroundColor.getColor());
            this.getGraphicalRepresentationData().setBackgroundColor(backgroundColor);
            rightRect.setHeight(this.getGraphicalRepresentationData().getHeight());
            rightRect.xProperty().bind(this.rectangle.widthProperty());
            rightRect.widthProperty().bind(life.multiply(-width * 0.01).add(width));
            Pane solutionPane2 = new Pane(this.rectangle, rightRect);
            this.setCenter(solutionPane2);
            this.setRefillRectangleTimeline();
            this.refillRectangleTimeline.play();
            this.setOnMouseClicked(onMyMouseClicked);
        }
    }

    private void setRefillRectangleTimeline(){
        this.refillRectangleTimeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        (ActionEvent actionEvent) -> {
                            try {
                                life.set((double) this.getGraphicalRepresentationData().getRefillExpression().evaluate());
                            } catch (CompileException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }), new KeyFrame(Duration.seconds(1)));
        this.refillRectangleTimeline.setCycleCount(Animation.INDEFINITE);
    }

    private final EventHandler<MouseEvent> onMyMouseClicked = mouseEvent -> {
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
