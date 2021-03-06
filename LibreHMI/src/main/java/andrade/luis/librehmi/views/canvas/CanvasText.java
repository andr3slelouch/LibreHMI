package andrade.luis.librehmi.views.canvas;

import andrade.luis.librehmi.models.CanvasObjectData;
import andrade.luis.librehmi.models.Expression;
import andrade.luis.librehmi.models.Tag;
import andrade.luis.librehmi.views.windows.SetTextPropertiesWindow;
import andrade.luis.librehmi.views.windows.WriteExpressionWindow;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import org.codehaus.commons.compiler.CompileException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Clase que define el objeto CanvasText, que permitirá mostrar el valor de un tag o de un valor definido por el usuario
 */
public class CanvasText extends CanvasLabel {
    private String text;

    private Timeline timeline;

    public CanvasText() {

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * Constructor para pegar un CanvasText copiado o regenerarlo desde el archivo
     * @param canvasObjectData CanvasObjectData conteniendo la información del objeto a generar
     */
    public CanvasText(CanvasObjectData canvasObjectData) {
        super(canvasObjectData);
        this.getCanvasObjectData().setType("Text");
        this.getCanvasObjectData().setDataType("Texto");
        this.getCanvasObjectData().setSuperType("TagOutputObject");
        setData();
    }

    /**
     * Constructor que permite agregar un nuevo CanvasText al canvas
     * @param content Texto del label
     * @param center Posición del objeto
     */
    public CanvasText(String content, CanvasPoint center) {
        super(content, center);
        this.getCanvasObjectData().setType("Text");
        this.getCanvasObjectData().setSuperType("TagOutputObject");
        this.getCanvasObjectData().setWidth(this.getWidth());
        this.getCanvasObjectData().setHeight(this.getHeight());
        setData();
    }

    /**
     * Permite definir los datos del objeto
     */
    private void setData() {
        this.setContextMenu();
        this.setExpression(this.getCanvasObjectData().getExpression());
        MenuItem editMI = new MenuItem("Editar");
        editMI.setId("#editMI");
        editMI.setOnAction(actionEvent -> this.setExpression());
        this.getRightClickMenu().getItems().add(2,editMI);
        if(this.getCanvasObjectData().getFontFamily()!=null && this.getCanvasObjectData().getFontStyle()!=null){
            this.getLabel().setFont(
                    Font.font(
                            this.getCanvasObjectData().getFontFamily(),
                            FontWeight.valueOf(this.getCanvasObjectData().getFontStyle()),
                            this.getCanvasObjectData().getFontSize()
                    )
            );
        }
        if(this.getCanvasObjectData().getFontColor()!=null){
            this.getLabel().setTextFill(this.getCanvasObjectData().getFontColor().getColor());
        }
        this.setRotate(this.getCanvasObjectData().getRotation());
    }

    /**
     * Permite mostrar la ventana de definición de propiedades del texto y modificar la representación
     * con los nuevos datos definidos
     */
    @Override
    public void setProperties() {
        SetTextPropertiesWindow propertiesWindow = new SetTextPropertiesWindow();
        propertiesWindow.setTitle("Propiedades de Texto");
        propertiesWindow.setWidth(340);
        propertiesWindow.setHeight(265);
        propertiesWindow.getFontStyleComboBox().getSelectionModel().select(this.getLabel().getFont().getStyle());
        propertiesWindow.getFontFamilyComboBox().getSelectionModel().select(this.getLabel().getFont().getFamily());
        propertiesWindow.getFontSizeField().setText(String.valueOf(this.getLabel().getFont().getSize()));
        propertiesWindow.getRotationTextField().setText(String.valueOf(this.getCanvasObjectData().getRotation()));
        propertiesWindow.getColorPicker().setValue((Color) this.getLabel().getTextFill());
        propertiesWindow.showAndWait();
        double rotation = Double.parseDouble(propertiesWindow.getRotationTextField().getText());
        double fontSize = Double.parseDouble(propertiesWindow.getFontSizeField().getText());
        this.setProperties(rotation,new CanvasColor(propertiesWindow.getColorPicker().getValue()),propertiesWindow.getFontStyle(),propertiesWindow.getFontFamilyComboBox().getValue(),fontSize);
        this.getHmiApp().setWasModified(true);
    }

    /**
     * Permite mostrar la ventana de definición de expresión asociada a la rerpesentación
     */
    private void setExpression() {
        WriteExpressionWindow writeExpressionWindow = new WriteExpressionWindow();
        writeExpressionWindow.setTitle("Propiedades de valor del Texto");
        writeExpressionWindow.setHeight(200);
        writeExpressionWindow.setLocalTags(getHmiApp().getLocalTags());
        if (this.getCanvasObjectData().getExpression() != null) {
            writeExpressionWindow.setAddedTags(this.getCanvasObjectData().getExpression().getParameters());
            writeExpressionWindow.setLocalExpression(this.getCanvasObjectData().getExpression());
            writeExpressionWindow.getTextField().setText(this.getCanvasObjectData().getExpression().getExpressionToEvaluate());
            writeExpressionWindow.getFloatPrecisionTextField().setText(String.valueOf(this.getCanvasObjectData().getExpression().getFloatPrecision()));
            writeExpressionWindow.getSamplingTimeTextField().setText(String.valueOf(this.getCanvasObjectData().getSamplingTime()<1 ? 1:this.getCanvasObjectData().getSamplingTime()));
            writeExpressionWindow.getSamplingTimeHBox().setVisible(true);
            writeExpressionWindow.getSamplingTimeTextField().setText(String.valueOf(this.getCanvasObjectData().getSamplingTime()<1 ? 1:this.getCanvasObjectData().getSamplingTime()));
        }
        writeExpressionWindow.showAndWait();
        if (writeExpressionWindow.isDone()) {
            Expression expression = writeExpressionWindow.getLocalExpression();
            try {
                if (expression != null) {
                    expression.evaluate();
                    this.setExpression(expression);
                    this.getHmiApp().setWasModified(true);
                    this.getCanvasObjectData().setSamplingTime(this.getCanvasObjectData().getSamplingTime()<1 ? 1:this.getCanvasObjectData().getSamplingTime());
                }
            } catch (Exception e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Error");
                errorAlert.setContentText("Error al agregar la expresión, reintente");
                errorAlert.showAndWait();
                setExpression();
                logger.log(Level.INFO,e.getMessage());
            }
        }
    }

    /**
     * Permite asociar la expresión definida desde la ventana al objeto
     * @param expression Expresión a ser asociada al objeto
     */
    public void setExpression(Expression expression) {
        if (expression != null) {
            this.getCanvasObjectData().setExpression(expression);
            this.setTimeline();
        }
    }

    /**
     * Permite definir el hilo de actualización del valor de la expresión asociada al objeto
     */
    public void setTimeline() {
        timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        (ActionEvent actionEvent) -> {
                            String type = this.getCanvasObjectData().getExpression().getResultType() != null ? this.getCanvasObjectData().getExpression().getResultType() : "";
                            String evaluatedValue = "";
                            try {
                                switch (type) {
                                    case "Bool":
                                        evaluatedValue = String.valueOf((boolean) this.getCanvasObjectData().getExpression().evaluate());
                                        break;
                                    case "Flotante":
                                        evaluatedValue = String.valueOf((double) this.getCanvasObjectData().getExpression().evaluate());
                                        if (this.getCanvasObjectData().getExpression().getFloatPrecision() > -1) {
                                            DecimalFormat decimalFormat = this.getCanvasObjectData().getExpression().generateDecimalFormat();
                                            evaluatedValue = decimalFormat.format(Double.parseDouble(evaluatedValue));
                                        }
                                        break;
                                    case "String":
                                        evaluatedValue = (String) this.getCanvasObjectData().getExpression().evaluate();
                                        break;
                                    default:
                                        break;
                                }
                                this.setTop(null);
                            } catch (CompileException | InvocationTargetException | NullPointerException | SQLException | IOException e) {
                                this.errorLabel = new Label("Error en Tag de Lectura");
                                this.setTop(this.errorLabel);
                                logger.log(Level.INFO,e.getMessage());
                            }
                            this.text = evaluatedValue;
                            this.getLabel().setText(this.text);
                            this.getCanvasObjectData().setWidth(this.getLabel().getWidth() * 2);
                            this.getCanvasObjectData().setHeight(this.getLabel().getHeight());
                        }), new KeyFrame(Duration.seconds(this.getCanvasObjectData().getSamplingTime())));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    @Override
    public void updateTag(Tag tag){
        super.updateTag(tag);
        if(timeline != null){
            ArrayList<Tag> parameters = this.getCanvasObjectData().getExpression().getParameters();
            for(int i=0;i<parameters.size();i++){
                if(parameters.get(i).compareToTag(tag)){
                    parameters.set(i,tag);
                }
            }
            this.getCanvasObjectData().getExpression().setParameters(parameters);
        }
    }
}
