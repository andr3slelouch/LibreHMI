package andrade.luis.hmiethernetip.models;

import andrade.luis.hmiethernetip.views.SetPercentFillPropertiesWindow;
import andrade.luis.hmiethernetip.views.SetSizeWindow;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.codehaus.commons.compiler.CompileException;

import java.lang.reflect.InvocationTargetException;

import static javafx.geometry.Pos.CENTER;

public class CanvasRectangle extends GraphicalRepresentation {
    private PercentFillOrientation perfectFillOrientation;
    private Timeline refillRectangleTimeline;
    private Label captionLabel;
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
        this.getGraphicalRepresentationData().setPosition(new CanvasPoint(x, y));
        this.rectangle.setWidth(width);
        this.getGraphicalRepresentationData().setWidth(width);
        this.rectangle.setHeight(height);
        this.getGraphicalRepresentationData().setHeight(height);
        this.setCenter(rectangle);
        this.getGraphicalRepresentationData().setType("Rectangle");
        this.setContextMenu();
        MenuItem linkTagMI = new MenuItem("Link Tag");
        linkTagMI.setId("#linkTagMI");
        linkTagMI.setOnAction(actionEvent -> this.getGraphicalRepresentationData().setTag(this.getCanvas().selectTag()));
        this.setContextMenu();
        this.getRightClickMenu().getItems().add(linkTagMI);
        MenuItem percentFillMI = new MenuItem("Percent Fill Animation");
        percentFillMI.setId("#percentFillMI");
        percentFillMI.setOnAction(actionEvent -> this.setPercentFill());
        this.getRightClickMenu().getItems().add(percentFillMI);
        MenuItem resizeMI = new MenuItem("Resize");
        resizeMI.setId("#resizeMI");
        resizeMI.setOnAction(actionEvent -> this.setResize());
        this.getRightClickMenu().getItems().add(resizeMI);

    }

    private void setResize() {
        SetSizeWindow setSizeWindow = new SetSizeWindow(this.getGraphicalRepresentationData().getWidth(), this.getGraphicalRepresentationData().getHeight());
        setSizeWindow.showAndWait();
        this.getGraphicalRepresentationData().setWidth(setSizeWindow.getWidthFromField());
        this.getGraphicalRepresentationData().setHeight(setSizeWindow.getHeightFromField());
        this.setSize(this.getGraphicalRepresentationData().getWidth(), this.getGraphicalRepresentationData().getHeight());
        if (refillRectangleTimeline != null) {
            if (refillRectangleTimeline.getStatus().toString().equals("RUNNING")) {
                refillRectangleTimeline.stop();
                refillRectangleTimeline = null;
                setPercentFill(this.getGraphicalRepresentationData().getRefillExpression(), this.getGraphicalRepresentationData().getPrimaryColor(), this.getGraphicalRepresentationData().getBackgroundColor(), this.getGraphicalRepresentationData().getOrientation());
            }
        } else {
            this.rectangle.setWidth(this.getGraphicalRepresentationData().getWidth());
            this.rectangle.setHeight(this.getGraphicalRepresentationData().getHeight());
        }
    }

    private void setPercentFill() {
        SetPercentFillPropertiesWindow writeExpressionWindow;
        if (this.getGraphicalRepresentationData().getRefillExpression() != null) {
            writeExpressionWindow = new SetPercentFillPropertiesWindow(this.getGraphicalRepresentationData().getPrimaryColor().getColor(), this.getGraphicalRepresentationData().getBackgroundColor().getColor());
            writeExpressionWindow.setAddedTags(this.getGraphicalRepresentationData().getRefillExpression().getParameters());
            writeExpressionWindow.setLocalExpression(this.getGraphicalRepresentationData().getRefillExpression());
            writeExpressionWindow.getTextField().setText(this.getGraphicalRepresentationData().getRefillExpression().getExpressionToEvaluate());
            writeExpressionWindow.setSelectedOrientation(this.getGraphicalRepresentationData().getOrientation().toString().toLowerCase());
            writeExpressionWindow.getMinValueField().setText(this.getGraphicalRepresentationData().getMinValue() + "");
            writeExpressionWindow.getMaxValueField().setText(this.getGraphicalRepresentationData().getMaxValue() + "");
        } else {
            writeExpressionWindow = new SetPercentFillPropertiesWindow();
        }
        writeExpressionWindow.showAndWait();
        Expression expression = writeExpressionWindow.getLocalExpression();
        try {
            if (expression != null) {
                expression.evaluate();
                this.setPercentFill(
                        expression,
                        writeExpressionWindow.getPrimaryColor(),
                        writeExpressionWindow.getBackgroundColor(),
                        writeExpressionWindow.getSelectedOrientation()
                );
                this.getGraphicalRepresentationData().setMinValue(writeExpressionWindow.getMinValue());
                this.getGraphicalRepresentationData().setMaxValue(writeExpressionWindow.getMaxValue());
            }
        } catch (Exception e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Error");
            errorAlert.setContentText("Error al agregar la expresión, reintente");
            errorAlert.showAndWait();
            setPercentFill();
            e.printStackTrace();
        }
    }

    public void setPercentFill(Expression exp, CanvasColor primaryColor, CanvasColor backgroundColor, PercentFillOrientation orientation) {
        if (exp != null) {
            this.getGraphicalRepresentationData().setRefillExpression(exp);
            double width = this.getGraphicalRepresentationData().getWidth();
            double height = this.getGraphicalRepresentationData().getHeight();
            life = new SimpleDoubleProperty(width);
            this.rectangle = new Rectangle();

            this.getGraphicalRepresentationData().setPrimaryColor(primaryColor);

            Rectangle rightRect = new Rectangle();

            this.getGraphicalRepresentationData().setBackgroundColor(backgroundColor);
            this.getGraphicalRepresentationData().setOrientation(orientation);
            Pane solutionPane2;
            switch (orientation) {
                case HORIZONTAL:
                    this.rectangle.setFill(primaryColor.getColor());
                    rightRect.setFill(backgroundColor.getColor());

                    this.rectangle.widthProperty().bind(life.multiply(width * 0.01));
                    this.rectangle.setHeight(this.getGraphicalRepresentationData().getHeight());

                    rightRect.setHeight(this.getGraphicalRepresentationData().getHeight());
                    rightRect.xProperty().bind(this.rectangle.widthProperty());
                    rightRect.widthProperty().bind(life.multiply(-width * 0.01).add(width));
                    solutionPane2 = new Pane(this.rectangle, rightRect);
                    this.setCenter(solutionPane2);

                    perfectFillOrientation = PercentFillOrientation.HORIZONTAL;
                    break;
                case HORIZONTAL_REVERSED:
                    rightRect.setFill(backgroundColor.getColor());
                    this.rectangle.setFill(primaryColor.getColor());

                    rightRect.widthProperty().bind(life.multiply(width * 0.01));
                    rightRect.setHeight(this.getGraphicalRepresentationData().getHeight());

                    this.rectangle.setHeight(this.getGraphicalRepresentationData().getHeight());
                    this.rectangle.xProperty().bind(rightRect.widthProperty());
                    this.rectangle.widthProperty().bind(life.multiply(-width * 0.01).add(width));
                    solutionPane2 = new Pane(this.rectangle, rightRect);
                    this.setCenter(solutionPane2);

                    perfectFillOrientation = PercentFillOrientation.HORIZONTAL_REVERSED;
                    break;
                case VERTICAL:
                    rightRect.setFill(primaryColor.getColor());
                    this.rectangle.setFill(backgroundColor.getColor());

                    this.rectangle.heightProperty().bind(life.multiply(height * 0.01));
                    this.rectangle.setWidth(this.getGraphicalRepresentationData().getWidth());

                    rightRect.setWidth(this.getGraphicalRepresentationData().getWidth());

                    rightRect.yProperty().bind(this.rectangle.heightProperty());
                    rightRect.heightProperty().bind(life.multiply(-height * 0.01).add(height));
                    solutionPane2 = new Pane(rightRect,this.rectangle);
                    this.setCenter(solutionPane2);

                    perfectFillOrientation = PercentFillOrientation.VERTICAL;
                    break;
                case VERTICAL_REVERSED:
                    rightRect.setFill(primaryColor.getColor());
                    this.rectangle.setFill(backgroundColor.getColor());

                    rightRect.heightProperty().bind(life.multiply(height * 0.01));
                    rightRect.setWidth(this.getGraphicalRepresentationData().getWidth());

                    this.rectangle.setWidth(this.getGraphicalRepresentationData().getWidth());
                    this.rectangle.yProperty().bind(rightRect.heightProperty());
                    this.rectangle.heightProperty().bind(life.multiply(-height * 0.01).add(height));
                    solutionPane2 = new Pane(rightRect, this.rectangle);
                    this.setCenter(solutionPane2);

                    perfectFillOrientation = PercentFillOrientation.VERTICAL_REVERSED;
                    break;
            }
            HBox bottomHBox = new HBox();
            captionLabel = new Label("Text");
            captionLabel.setStyle("-fx-background-color:rgb(244, 244, 244);");
            bottomHBox.setAlignment(CENTER);
            bottomHBox.getChildren().add(captionLabel);
            this.setBottom(bottomHBox);

            this.setRefillRectangleTimeline();
            this.refillRectangleTimeline.play();
            this.setOnMouseClicked(onMyMouseClicked);
        }
    }

    private void setRefillRectangleTimeline() {
        this.refillRectangleTimeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        (ActionEvent actionEvent) -> {
                            try {
                                String type = this.getGraphicalRepresentationData().getRefillExpression().getResultType() != null ? this.getGraphicalRepresentationData().getRefillExpression().getResultType() : "";
                                double evaluatedValue = 0;
                                if (type.equals("Booleano")) {
                                    evaluatedValue = (boolean) this.getGraphicalRepresentationData().getRefillExpression().evaluate() ? 100 : 0;
                                } else if (type.equals("Flotante")) {
                                    evaluatedValue = (double) this.getGraphicalRepresentationData().getRefillExpression().evaluate();
                                }

                                double value = calculatePercentFillValue(evaluatedValue);
                                captionLabel.setText(String.valueOf(evaluatedValue));
                                life.set(value);
                            } catch (CompileException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }), new KeyFrame(Duration.seconds(1)));
        this.refillRectangleTimeline.setCycleCount(Animation.INDEFINITE);
    }

    private double calculatePercentFillValue(double evaluatedValue) {
        if (evaluatedValue < this.getGraphicalRepresentationData().getMinValue()) {
            return 0;
        } else if (evaluatedValue > this.getGraphicalRepresentationData().getMaxValue()) {
            return 100;
        }
        double multiplication = ((evaluatedValue - this.getGraphicalRepresentationData().getMinValue()) * 100);
        double difference = (this.getGraphicalRepresentationData().getMaxValue() - this.getGraphicalRepresentationData().getMinValue());
        if (perfectFillOrientation == PercentFillOrientation.HORIZONTAL || perfectFillOrientation == PercentFillOrientation.VERTICAL_REVERSED) {
            return (multiplication / difference);

        } else if (perfectFillOrientation == PercentFillOrientation.VERTICAL || perfectFillOrientation == PercentFillOrientation.HORIZONTAL_REVERSED) {
            return 100-(multiplication / difference);
        }
        return -1;
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
        } else if (refillRectangleTimeline.getStatus().toString().equals("STOPPED")) {
            refillRectangleTimeline.play();
        }
    }
}