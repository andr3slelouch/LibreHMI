package andrade.luis.hmiethernetip.models.canvas;

import andrade.luis.hmiethernetip.models.*;
import andrade.luis.hmiethernetip.views.SetPercentFillPropertiesWindow;
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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.logging.Level;

import static javafx.geometry.Pos.CENTER;

public class CanvasRectangle extends CanvasObject {
    private PercentFillOrientation perfectFillOrientation;
    private Timeline refillRectangleTimeline;
    private Label captionLabel;
    private DoubleProperty life;

    public Rectangle getRectangle() {
        return rectangle;
    }

    private Rectangle rectangle;

    public CanvasRectangle(CanvasObjectData canvasObjectData) {
        super(canvasObjectData);
        setData(this.getCanvasObjectData().getPosition().getX(), this.getCanvasObjectData().getPosition().getY(), canvasObjectData.getWidth(), canvasObjectData.getHeight());
    }

    public CanvasRectangle(CanvasPoint center) {
        super(center);
        setData(this.getCanvasObjectData().getPosition().getX(), this.getCanvasObjectData().getPosition().getY(), 150, 150);
    }

    public CanvasRectangle() {

    }

    @Override
    public void setPosition(CanvasPoint center) {
        super.setPosition(center);
        setData(this.getCanvasObjectData().getPosition().getX(), this.getCanvasObjectData().getPosition().getY(), 150, 150);
        super.setCenter(this.rectangle);
    }

    public void setData(double x, double y, double width, double height) {
        this.rectangle = new Rectangle(x, y);
        this.rectangle.setWidth(width);
        this.getCanvasObjectData().setWidth(width);
        this.rectangle.setHeight(height);
        this.getCanvasObjectData().setHeight(height);
        this.setCenter(rectangle);
        this.getCanvasObjectData().setType("Rectangle");
        this.setContextMenu();
        MenuItem percentFillMI = new MenuItem("Percent Fill Animation");
        percentFillMI.setId("#percentFillMI");
        percentFillMI.setOnAction(actionEvent -> this.setPercentFill());
        this.getRightClickMenu().getItems().add(percentFillMI);
    }

    @Override
    public void resize() {
        super.resize();
        if (refillRectangleTimeline != null) {
            if (refillRectangleTimeline.getStatus().toString().equals("RUNNING")) {
                refillRectangleTimeline.stop();
                refillRectangleTimeline = null;
                setPercentFill(this.getCanvasObjectData().getExpression(), this.getCanvasObjectData().getPrimaryColor(), this.getCanvasObjectData().getBackgroundColor(), this.getCanvasObjectData().getOrientation());
            }
        } else {
            this.rectangle.setWidth(this.getCanvasObjectData().getWidth());
            this.rectangle.setHeight(this.getCanvasObjectData().getHeight());
        }
    }

    private void setPercentFill() {
        SetPercentFillPropertiesWindow writeExpressionWindow;
        if (this.getCanvasObjectData().getExpression() != null) {
            writeExpressionWindow = new SetPercentFillPropertiesWindow(this.getCanvasObjectData().getPrimaryColor().getColor(), this.getCanvasObjectData().getBackgroundColor().getColor());
            writeExpressionWindow.setAddedTags(this.getCanvasObjectData().getExpression().getParameters());
            writeExpressionWindow.setLocalExpression(this.getCanvasObjectData().getExpression());
            writeExpressionWindow.getTextField().setText(this.getCanvasObjectData().getExpression().getExpressionToEvaluate());
            writeExpressionWindow.setSelectedOrientation(this.getCanvasObjectData().getOrientation().toString().toLowerCase());
            writeExpressionWindow.getMinValueField().setText(this.getCanvasObjectData().getMinValue() + "");
            writeExpressionWindow.getMaxValueField().setText(this.getCanvasObjectData().getMaxValue() + "");
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
                this.getHmiApp().setWasModified(true);
                this.getCanvasObjectData().setMinValue(writeExpressionWindow.getMinValue());
                this.getCanvasObjectData().setMaxValue(writeExpressionWindow.getMaxValue());
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
            this.getCanvasObjectData().setExpression(exp);
            double width = this.getCanvasObjectData().getWidth();
            double height = this.getCanvasObjectData().getHeight();
            life = new SimpleDoubleProperty(width);
            this.rectangle = new Rectangle();

            this.getCanvasObjectData().setPrimaryColor(primaryColor);

            Rectangle rightRect = new Rectangle();

            this.getCanvasObjectData().setBackgroundColor(backgroundColor);
            this.getCanvasObjectData().setOrientation(orientation);
            Pane solutionPane2;
            switch (orientation) {
                case HORIZONTAL:
                    this.rectangle.setFill(primaryColor.getColor());
                    rightRect.setFill(backgroundColor.getColor());

                    this.rectangle.widthProperty().bind(life.multiply(width * 0.01));
                    this.rectangle.setHeight(this.getCanvasObjectData().getHeight());

                    rightRect.setHeight(this.getCanvasObjectData().getHeight());
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
                    rightRect.setHeight(this.getCanvasObjectData().getHeight());

                    this.rectangle.setHeight(this.getCanvasObjectData().getHeight());
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
                    this.rectangle.setWidth(this.getCanvasObjectData().getWidth());

                    rightRect.setWidth(this.getCanvasObjectData().getWidth());

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
                    rightRect.setWidth(this.getCanvasObjectData().getWidth());

                    this.rectangle.setWidth(this.getCanvasObjectData().getWidth());
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
                                String type = this.getCanvasObjectData().getExpression().getResultType() != null ? this.getCanvasObjectData().getExpression().getResultType() : "";
                                double evaluatedValue = 0;
                                switch (type) {
                                    case "Booleano":
                                        evaluatedValue = (boolean) this.getCanvasObjectData().getExpression().evaluate() ? 100 : 0;
                                        break;
                                    case "Flotante":
                                        evaluatedValue = (double) this.getCanvasObjectData().getExpression().evaluate();
                                        break;
                                    default:
                                        break;
                                }
                                double value = calculatePercentFillValue(evaluatedValue);
                                captionLabel.setText(String.valueOf(evaluatedValue));
                                life.set(value);
                                this.setTop(null);
                            } catch (CompileException | InvocationTargetException | SQLException | IOException | NullPointerException e) {
                                this.errorLabel = new Label("Error en Tag de Relleno");
                                this.setTop(this.errorLabel);
                                e.printStackTrace();
                            }
                        }), new KeyFrame(Duration.seconds(1)));
        this.refillRectangleTimeline.setCycleCount(Animation.INDEFINITE);
    }

    private double calculatePercentFillValue(double evaluatedValue) {
        if (evaluatedValue < this.getCanvasObjectData().getMinValue()) {
            return 0;
        } else if (evaluatedValue > this.getCanvasObjectData().getMaxValue()) {
            return 100;
        }
        double multiplication = ((evaluatedValue - this.getCanvasObjectData().getMinValue()) * 100);
        double difference = (this.getCanvasObjectData().getMaxValue() - this.getCanvasObjectData().getMinValue());
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