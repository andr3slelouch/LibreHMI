package andrade.luis.librehmi.views.canvas;

import andrade.luis.librehmi.models.*;
import andrade.luis.librehmi.views.windows.SetGeometricFigurePropertiesWindow;
import andrade.luis.librehmi.views.windows.SetPercentFillPropertiesWindow;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.codehaus.commons.compiler.CompileException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;


import static javafx.geometry.Pos.CENTER;

/**
 * Clase que define el objeto CanvasRectangle, que permitirá tener la figura de rectángulo en el canvas
 */
public class CanvasRectangle extends CanvasObject {
    private CanvasOrientation perfectFillOrientation;
    private Timeline refillRectangleTimeline;
    private Label captionLabel;
    private DoubleProperty life;

    private Rectangle rectangle;

    /**
     * Constructor para pegar un CanvasRectangle copiado o regenerarlo desde el archivo
     * @param canvasObjectData CanvasObjectData conteniendo la información del objeto a generar
     */
    public CanvasRectangle(CanvasObjectData canvasObjectData) {
        super(canvasObjectData);
        setData(this.getCanvasObjectData().getPosition().getX(), this.getCanvasObjectData().getPosition().getY(), canvasObjectData.getWidth(), canvasObjectData.getHeight());
    }

    /**
     * Constructor que permite agregar un nuevo CanvasRectangle al canvas
     *
     * @param center CanvasPoint con la posición del objeto
     */
    public CanvasRectangle(CanvasPoint center) {
        super(center);
        setData(this.getCanvasObjectData().getPosition().getX(), this.getCanvasObjectData().getPosition().getY(), 150, 150);
    }

    public CanvasRectangle() {

    }

    /**
     * Método para definir las propiedades del objeto
     * @param x Posición en X del objeto
     * @param y Posición en Y del objeto
     * @param width Ancho del objeto
     * @param height Alto del objeto
     */
    public void setData(double x, double y, double width, double height) {
        this.rectangle = new Rectangle(x, y);

        this.rectangle.setWidth(width);
        this.setPrefWidth(width);
        this.getCanvasObjectData().setWidth(width);
        this.rectangle.setHeight(height);
        this.setPrefHeight(height);
        this.getCanvasObjectData().setHeight(height);

        this.setCenter(rectangle);
        this.getCanvasObjectData().setType("Rectangle");
        this.getCanvasObjectData().setDataType("Rectángulo");
        this.getCanvasObjectData().setSuperType("TagOutputObject");
        this.setContextMenu();
        MenuItem percentFillMI = new MenuItem("Animación de Relleno Porcentual");
        percentFillMI.setId("#percentFillMI");
        percentFillMI.setOnAction(actionEvent -> this.setPercentFill());
        this.getRightClickMenu().getItems().add(percentFillMI);
        this.setRotate(this.getCanvasObjectData().getRotation());
        if(this.getCanvasObjectData().getPrimaryColor()!=null){
            this.rectangle.setFill(this.getCanvasObjectData().getPrimaryColor().getColor());
        }
    }

    /**
     * Permite mostrar una ventana de definición de propiedades para actualizarlas
     */
    @Override
    public void setProperties() {
        SetGeometricFigurePropertiesWindow setGeometricFigurePropertiesWindow = new SetGeometricFigurePropertiesWindow(this.getCanvasObjectData().getWidth(),this.getCanvasObjectData().getHeight());
        setGeometricFigurePropertiesWindow.setTitle("Propiedades del Rectángulo");
        setGeometricFigurePropertiesWindow.setHeight(220);
        setGeometricFigurePropertiesWindow.setWidth(293);

        if(this.getCanvasObjectData().getPrimaryColor()!=null){
            setGeometricFigurePropertiesWindow.getColorPicker().setValue(this.getCanvasObjectData().getPrimaryColor().getColor());
        }else{
            setGeometricFigurePropertiesWindow.getColorPicker().setValue((Color) this.rectangle.getFill());
        }
        setGeometricFigurePropertiesWindow.getRotationTextField().setText(String.valueOf(this.getCanvasObjectData().getRotation()));
        setGeometricFigurePropertiesWindow.showAndWait();

        boolean isModifyingColor = setGeometricFigurePropertiesWindow.isModifyingColor();
        this.getCanvasObjectData().setModifyingColors(isModifyingColor);
        double rotation = Double.parseDouble(setGeometricFigurePropertiesWindow.getRotationTextField().getText());
        this.getCanvasObjectData().setRotation(rotation);
        this.setRotate(rotation);

        CanvasColor color = new CanvasColor(setGeometricFigurePropertiesWindow.getColorPicker().getValue());
        this.rectangle.setFill(color.getColor());
        this.getCanvasObjectData().setPrimaryColor(color);
        this.getCanvasObjectData().setWidth(setGeometricFigurePropertiesWindow.getVbox().getWidthFromField());
        this.getCanvasObjectData().setHeight(setGeometricFigurePropertiesWindow.getVbox().getHeightFromField());
        this.setSize(this.getCanvasObjectData().getWidth(), this.getCanvasObjectData().getHeight());
        this.getHmiApp().setWasModified(true);
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

    /**
     * Permite mostrar la ventana de definición de atributos de la animación de relleno porcentual
     */
    private void setPercentFill() {
        SetPercentFillPropertiesWindow writeExpressionWindow;
        if (this.getCanvasObjectData().getExpression() != null) {
            writeExpressionWindow = new SetPercentFillPropertiesWindow(this.getCanvasObjectData().getPrimaryColor().getColor(), this.getCanvasObjectData().getBackgroundColor().getColor());
            writeExpressionWindow.setLocalTags(this.getHmiApp().getLocalTags());
            writeExpressionWindow.setAddedTags(this.getCanvasObjectData().getExpression().getParameters());
            writeExpressionWindow.setLocalExpression(this.getCanvasObjectData().getExpression());
            writeExpressionWindow.getTextField().setText(this.getCanvasObjectData().getExpression().getExpressionToEvaluate());
            writeExpressionWindow.setSelectedOrientation(this.getCanvasObjectData().getOrientation().toString().toLowerCase());
            writeExpressionWindow.getMinValueField().setText(this.getCanvasObjectData().getMinValue() + "");
            writeExpressionWindow.getMaxValueField().setText(this.getCanvasObjectData().getMaxValue() + "");
            writeExpressionWindow.getFloatPrecisionTextField().setText(String.valueOf(this.getCanvasObjectData().getExpression().getFloatPrecision()));
            writeExpressionWindow.getSamplingTimeHBox().setVisible(true);
            if(this.getCanvasObjectData().getData()!=null){
                writeExpressionWindow.getShowLabelChB().setSelected(this.getCanvasObjectData().getData().equals("Show"));
            }
            writeExpressionWindow.getSamplingTimeTextField().setText(String.valueOf(this.getCanvasObjectData().getSamplingTime()<1 ? 1:this.getCanvasObjectData().getSamplingTime()));
        } else {
            writeExpressionWindow = new SetPercentFillPropertiesWindow();
            writeExpressionWindow.setLocalTags(getHmiApp().getLocalTags());
            writeExpressionWindow.getSamplingTimeHBox().setVisible(true);
        }
        writeExpressionWindow.showAndWait();
        Expression expression = writeExpressionWindow.getLocalExpression();
        try {
            if (expression != null) {
                expression.evaluate();
                this.getCanvasObjectData().setData(writeExpressionWindow.getShowLabelChB().isSelected() ? "Show":"Hide");
                this.setPercentFill(
                        expression,
                        writeExpressionWindow.getPrimaryColor(),
                        writeExpressionWindow.getBackgroundColor(),
                        writeExpressionWindow.getSelectedOrientation()
                );
                this.getHmiApp().setWasModified(true);
                this.getCanvasObjectData().setMinValue(writeExpressionWindow.getMinValue());
                this.getCanvasObjectData().setMaxValue(writeExpressionWindow.getMaxValue());
                this.getCanvasObjectData().setSamplingTime(this.getCanvasObjectData().getSamplingTime()<1 ? 1:this.getCanvasObjectData().getSamplingTime());
                this.setRefillRectangleTimeline();
                this.refillRectangleTimeline.play();
            }
        } catch (Exception e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Error");
            errorAlert.setContentText("Error al agregar la expresión, reintente");
            errorAlert.showAndWait();
            setPercentFill();
            logger.log(Level.INFO,e.getMessage());
        }
    }

    /**
     * Permite definir la animación de relleno porcentual
     * @param exp Expresión a ser evaluada
     * @param primaryColor Color primario
     * @param backgroundColor Color de fondo
     * @param orientation Orientación de la animación
     */
    public void setPercentFill(Expression exp, CanvasColor primaryColor, CanvasColor backgroundColor, CanvasOrientation orientation) {
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

                    perfectFillOrientation = CanvasOrientation.HORIZONTAL;
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

                    perfectFillOrientation = CanvasOrientation.HORIZONTAL_REVERSED;
                    break;
                case VERTICAL:
                    rightRect.setFill(primaryColor.getColor());
                    this.rectangle.setFill(backgroundColor.getColor());

                    this.rectangle.heightProperty().bind(life.multiply(height * 0.01));
                    this.rectangle.setWidth(this.getCanvasObjectData().getWidth());

                    rightRect.setWidth(this.getCanvasObjectData().getWidth());

                    rightRect.yProperty().bind(this.rectangle.heightProperty());
                    rightRect.heightProperty().bind(life.multiply(-height * 0.01).add(height));
                    solutionPane2 = new Pane(rightRect, this.rectangle);
                    this.setCenter(solutionPane2);

                    perfectFillOrientation = CanvasOrientation.VERTICAL;
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

                    perfectFillOrientation = CanvasOrientation.VERTICAL_REVERSED;
                    break;
            }
            HBox bottomHBox = new HBox();
            captionLabel = new Label("Text");
            captionLabel.setStyle("-fx-background-color:rgb(244, 244, 244);");
            bottomHBox.setAlignment(CENTER);
            bottomHBox.getChildren().add(captionLabel);
            this.setBottom(bottomHBox);
            if(this.getCanvasObjectData().getData()!=null){
                this.getBottom().setVisible(this.getCanvasObjectData().getData().equals("Show"));
            }
            this.setRefillRectangleTimeline();
            this.refillRectangleTimeline.play();
            this.setOnMouseClicked(onMyMouseClicked);
        }
    }

    /**
     * Permite definir el hilo de animación de relleno del rectángulo
     */
    private void setRefillRectangleTimeline() {
        this.refillRectangleTimeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        (ActionEvent actionEvent) -> {
                            try {
                                String type = this.getCanvasObjectData().getExpression().getResultType() != null ? this.getCanvasObjectData().getExpression().getResultType() : "";
                                double evaluatedValue = 0;
                                switch (type) {
                                    case "Bool":
                                        evaluatedValue = (boolean) this.getCanvasObjectData().getExpression().evaluate() ? 100 : 0;
                                        break;
                                    case "Flotante":
                                    case "Entero":
                                        evaluatedValue = (double) this.getCanvasObjectData().getExpression().evaluate();
                                        break;
                                    default:
                                        break;
                                }
                                String captionValue = String.valueOf(evaluatedValue);
                                if (this.getCanvasObjectData().getExpression().getFloatPrecision() > -1) {
                                    DecimalFormat decimalFormat = this.getCanvasObjectData().getExpression().generateDecimalFormat();
                                    captionValue = decimalFormat.format(evaluatedValue);
                                }
                                captionLabel.setText(captionValue);
                                double value = calculatePercentFillValue(evaluatedValue);
                                life.set(value);
                                this.setTop(null);
                            } catch (CompileException | InvocationTargetException | SQLException | IOException | NullPointerException e) {
                                this.errorLabel = new Label("Error en Tag de Relleno");
                                this.setTop(this.errorLabel);
                                logger.log(Level.INFO,e.getMessage());
                            }
                        }), new KeyFrame(Duration.seconds(this.getCanvasObjectData().getSamplingTime())));
        this.refillRectangleTimeline.setCycleCount(Animation.INDEFINITE);
    }

    /**
     * Permite calcular el valor de porcentaje de relleno
     * @param evaluatedValue Valor de porcentaje de relleno
     * @return Retorna un valor porcentual de relleno
     */
    private double calculatePercentFillValue(double evaluatedValue) {
        if (evaluatedValue < this.getCanvasObjectData().getMinValue()) {
            return 0;
        } else if (evaluatedValue > this.getCanvasObjectData().getMaxValue()) {
            return 100;
        }
        double multiplication = ((evaluatedValue - this.getCanvasObjectData().getMinValue()) * 100);
        double difference = (this.getCanvasObjectData().getMaxValue() - this.getCanvasObjectData().getMinValue());
        if (perfectFillOrientation == CanvasOrientation.HORIZONTAL || perfectFillOrientation == CanvasOrientation.VERTICAL_REVERSED) {
            return (multiplication / difference);

        } else if (perfectFillOrientation == CanvasOrientation.VERTICAL || perfectFillOrientation == CanvasOrientation.HORIZONTAL_REVERSED) {
            return 100 - (multiplication / difference);
        }
        return -1;
    }

    /**
     * Permite definir la acción cuando se da clic en la figura
     */
    private final EventHandler<MouseEvent> onMyMouseClicked = mouseEvent -> {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 1) {
            this.setSelected(true);
        } else if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
            playOrStopIfCorrespond();
        } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            showContextMenu(mouseEvent.getScreenX(), mouseEvent.getScreenY());
        }
    };

    /**
     * Permite reanudar o detener el hilo de animación porcentual según corresponda
     */
    private void playOrStopIfCorrespond() {
        if (refillRectangleTimeline.getStatus().toString().equals("RUNNING")) {
            refillRectangleTimeline.stop();
        } else if (refillRectangleTimeline.getStatus().toString().equals("STOPPED")) {
            refillRectangleTimeline.play();
        }
    }
    @Override
    public void updateTag(Tag tag){
        super.updateTag(tag);
        if(refillRectangleTimeline != null){
            ArrayList<Tag> parameters = this.getCanvasObjectData().getExpression().getParameters();
            for(int i=0;i<parameters.size();i++){
                if(parameters.get(i).compareToTag(tag)){
                    int floatPrecision = parameters.get(i).getFloatPrecision();
                    tag.setFloatPrecision(floatPrecision);
                    parameters.set(i,tag);
                }
            }
            this.getCanvasObjectData().getExpression().setParameters(parameters);
        }
    }
}