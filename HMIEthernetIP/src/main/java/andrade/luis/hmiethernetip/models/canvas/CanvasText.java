package andrade.luis.hmiethernetip.models.canvas;

import andrade.luis.hmiethernetip.models.Expression;
import andrade.luis.hmiethernetip.models.GraphicalRepresentationData;
import andrade.luis.hmiethernetip.views.WriteExpressionWindow;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.util.Duration;
import org.codehaus.commons.compiler.CompileException;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CanvasText extends CanvasLabel {
    Logger logger = Logger.getLogger(getClass().getName());
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

    public CanvasText(GraphicalRepresentationData graphicalRepresentationData) {
        super(graphicalRepresentationData);
        this.getGraphicalRepresentationData().setType("Text");
        setData();
    }

    public CanvasText(String content, CanvasPoint center) {
        super(content, center);
        this.getGraphicalRepresentationData().setType("Text");
        this.getGraphicalRepresentationData().setWidth(this.getWidth());
        this.getGraphicalRepresentationData().setHeight(this.getHeight());
        setData();
    }

    private void setData(){
        this.setContextMenu();
        MenuItem editMI = new MenuItem("Editar");
        editMI.setId("#editMI");
        editMI.setOnAction(actionEvent -> this.setExpression());
        this.getRightClickMenu().getItems().add(editMI);
    }

    private void setExpression(){
        WriteExpressionWindow writeExpressionWindow = new WriteExpressionWindow();
        if (this.getGraphicalRepresentationData().getExpression() != null) {
            writeExpressionWindow.setAddedTags(this.getGraphicalRepresentationData().getExpression().getParameters());
            writeExpressionWindow.setLocalExpression(this.getGraphicalRepresentationData().getExpression());
            writeExpressionWindow.getTextField().setText(this.getGraphicalRepresentationData().getExpression().getExpressionToEvaluate());
        }
        writeExpressionWindow.showAndWait();
        Expression expression = writeExpressionWindow.getLocalExpression();
        try {
            if (expression != null) {
                expression.evaluate();
                this.setExpression(expression);
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
            this.getGraphicalRepresentationData().setExpression(expression);
            this.setTimeline();
        }
    }

    public void setTimeline() {
        timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        (ActionEvent actionEvent) -> {
                            String type = this.getGraphicalRepresentationData().getExpression().getResultType() != null ? this.getGraphicalRepresentationData().getExpression().getResultType() : "";
                            String evaluatedValue = "";
                            try{
                                switch (type) {
                                    case "Booleano":
                                        evaluatedValue = String.valueOf((boolean) this.getGraphicalRepresentationData().getExpression().evaluate());
                                        break;
                                    case "Flotante":
                                        evaluatedValue = String.valueOf((double) this.getGraphicalRepresentationData().getExpression().evaluate());
                                        break;
                                    case "String":
                                        evaluatedValue = (String) this.getGraphicalRepresentationData().getExpression().evaluate();
                                        break;
                                    default:
                                        break;
                                }
                            }catch(CompileException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                            this.text = evaluatedValue;
                            this.getLabel().setText(this.text);
                            this.getGraphicalRepresentationData().setWidth(this.getLabel().getWidth()*2);
                            this.getGraphicalRepresentationData().setHeight(this.getLabel().getHeight());
                        }), new KeyFrame(Duration.seconds(1)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
