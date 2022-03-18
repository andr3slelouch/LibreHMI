package andrade.luis.hmiethernetip.models.canvas;

import andrade.luis.hmiethernetip.models.Expression;
import andrade.luis.hmiethernetip.views.WriteExpressionWindow;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.util.Duration;
import org.codehaus.commons.compiler.CompileException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class CanvasText extends CanvasLabel {
    private String text;

    public Timeline getTimeline() {
        return timeline;
    }

    private Timeline timeline;

    public CanvasText() {

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public CanvasText(CanvasObjectData canvasObjectData) {
        super(canvasObjectData);
        this.getCanvasObjectData().setType("Text");
        setData();
    }

    public CanvasText(String content, CanvasPoint center) {
        super(content, center);
        this.getCanvasObjectData().setType("Text");
        this.getCanvasObjectData().setWidth(this.getWidth());
        this.getCanvasObjectData().setHeight(this.getHeight());
        setData();
    }

    private void setData(){
        this.setContextMenu();
        this.setExpression(this.getCanvasObjectData().getExpression());
        MenuItem editMI = new MenuItem("Editar");
        editMI.setId("#editMI");
        editMI.setOnAction(actionEvent -> this.setExpression());
        this.getRightClickMenu().getItems().add(editMI);
    }

    private void setExpression(){
        WriteExpressionWindow writeExpressionWindow = new WriteExpressionWindow();
        if (this.getCanvasObjectData().getExpression() != null) {
            writeExpressionWindow.setAddedTags(this.getCanvasObjectData().getExpression().getParameters());
            writeExpressionWindow.setLocalExpression(this.getCanvasObjectData().getExpression());
            writeExpressionWindow.getTextField().setText(this.getCanvasObjectData().getExpression().getExpressionToEvaluate());
            writeExpressionWindow.getFloatPrecisionTextField().setText(String.valueOf(this.getCanvasObjectData().getExpression().getFloatPrecision()));
        }
        writeExpressionWindow.showAndWait();
        Expression expression = writeExpressionWindow.getLocalExpression();
        try {
            if (expression != null) {
                expression.evaluate();
                this.setExpression(expression);
                this.getHmiApp().setWasModified(true);
            }
        } catch (Exception e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Error");
            errorAlert.setContentText("Error al agregar la expresión, reintente");
            errorAlert.showAndWait();
            setExpression();
            e.printStackTrace();
        }
    }

    public void setExpression(Expression expression) {
        if(expression!=null){
            this.getCanvasObjectData().setExpression(expression);
            this.setTimeline();
        }
    }

    public void setTimeline() {
        timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        (ActionEvent actionEvent) -> {
                            String type = this.getCanvasObjectData().getExpression().getResultType() != null ? this.getCanvasObjectData().getExpression().getResultType() : "";
                            String evaluatedValue = "";
                            try{
                                switch (type) {
                                    case "Bool":
                                        evaluatedValue = String.valueOf((boolean) this.getCanvasObjectData().getExpression().evaluate());
                                        break;
                                    case "Flotante":
                                        evaluatedValue = String.valueOf((double) this.getCanvasObjectData().getExpression().evaluate());
                                        if(this.getCanvasObjectData().getExpression().getFloatPrecision()>-1){
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
                            }catch(CompileException | InvocationTargetException | NullPointerException | SQLException | IOException e) {
                                this.errorLabel = new Label("Error en Tag de Lectura");
                                this.setTop(this.errorLabel);
                                e.printStackTrace();
                            }
                            this.text = evaluatedValue;
                            this.getLabel().setText(this.text);
                            this.getCanvasObjectData().setWidth(this.getLabel().getWidth()*2);
                            this.getCanvasObjectData().setHeight(this.getLabel().getHeight());
                        }), new KeyFrame(Duration.seconds(1)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
