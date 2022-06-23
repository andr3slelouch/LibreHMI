package andrade.luis.libreHMI.views;

import andrade.luis.libreHMI.models.Expression;
import andrade.luis.libreHMI.models.Tag;
import andrade.luis.libreHMI.views.windows.WriteExpressionWindow;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.ArrayList;

public class TrendChartExpressionHBox extends HBox {
    public TextField getExpressionTF() {
        return expressionTF;
    }

    private final TextField expressionTF;

    public TextField getExpressionNameTF() {
        return expressionNameTF;
    }

    private final TextField expressionNameTF;

    public CheckBox getEnableChartExpression() {
        return enableChartExpression;
    }

    private final CheckBox enableChartExpression;

    public ColorPicker getExpressionColorPicker() {
        return expressionColorPicker;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    private final ColorPicker expressionColorPicker;
    private Expression expression;

    public ArrayList<Tag> getLocalTags() {
        return localTags;
    }

    public void setLocalTags(ArrayList<Tag> localTags) {
        this.localTags = localTags;
    }

    private ArrayList<Tag> localTags;
    public TrendChartExpressionHBox(ArrayList<Tag> localTags){
        this.localTags = localTags;
        enableChartExpression = new CheckBox();
        Label expressionNameLbl = new Label("Nombre de la serie:");
        expressionNameTF = new TextField();
        expressionNameTF.setPromptText("Nombre de la serie");
        Button setExpressionBtn = new Button("Definir Expresión");
        expressionTF = new TextField();
        expressionTF.setDisable(true);
        setExpressionBtn.setOnAction(actionEvent -> {
            WriteExpressionWindow writeExpressionWindow = new WriteExpressionWindow();
            writeExpressionWindow.setLocalTags(this.getLocalTags());
            writeExpressionWindow.showAndWait();
            this.expression = writeExpressionWindow.getLocalExpression();
            if(this.expression!=null){
                expressionTF.setText(this.expression.getExpressionToEvaluate());
            }
        });
        expressionColorPicker = new ColorPicker();
        HBox chartExpressionHBox = new HBox();
        chartExpressionHBox.getChildren().addAll(expressionNameLbl,expressionNameTF,setExpressionBtn,expressionTF,expressionColorPicker);
        this.getChildren().addAll(enableChartExpression,chartExpressionHBox);
        chartExpressionHBox.setDisable(true);
        enableChartExpression.setSelected(false);
        enableChartExpression.selectedProperty().addListener((observableValue, oldBoolean, newBoolean) -> chartExpressionHBox.setDisable(!newBoolean));
    }
}
