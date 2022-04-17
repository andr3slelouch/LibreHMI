package andrade.luis.hmiethernetip.models;

import andrade.luis.hmiethernetip.views.WriteExpressionWindow;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

public class TrendChartExpressionHBox extends HBox {
    public TextField getExpressionNameTF() {
        return expressionNameTF;
    }

    public void setExpressionNameTF(TextField expressionNameTF) {
        this.expressionNameTF = expressionNameTF;
    }

    private TextField expressionNameTF;

    public CheckBox getEnableChartExpression() {
        return enableChartExpression;
    }

    public void setEnableChartExpression(CheckBox enableChartExpression) {
        this.enableChartExpression = enableChartExpression;
    }

    private CheckBox enableChartExpression;

    public ColorPicker getExpressionColorPicker() {
        return expressionColorPicker;
    }

    public void setExpressionColorPicker(ColorPicker expressionColorPicker) {
        this.expressionColorPicker = expressionColorPicker;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    private ColorPicker expressionColorPicker;
    private Expression expression;
    public TrendChartExpressionHBox(){
        enableChartExpression = new CheckBox();
        Label expressionNameLbl = new Label("Nombre de la serie:");
        expressionNameTF = new TextField();
        expressionNameTF.setPromptText("Nombre de la serie");
        Button setExpressionBtn = new Button("Definir Expresión");
        TextField expressionTF = new TextField();
        expressionTF.setDisable(true);
        setExpressionBtn.setOnAction(actionEvent -> {
            WriteExpressionWindow writeExpressionWindow = new WriteExpressionWindow();
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
        enableChartExpression.selectedProperty().addListener((observableValue, oldBoolean, newBoolean) -> {
            chartExpressionHBox.setDisable(!newBoolean);
        });
    }
}
