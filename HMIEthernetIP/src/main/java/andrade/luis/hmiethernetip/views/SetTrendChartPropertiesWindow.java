package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.models.TrendChartExpressionHBox;
import andrade.luis.hmiethernetip.models.TrendChartSerieData;
import andrade.luis.hmiethernetip.models.canvas.CanvasColor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class SetTrendChartPropertiesWindow extends Stage {
    private final VBox vbox;
    private final Button finishSelectionButton;
    private final Button cancelButton;
    private final HBox buttonsHBox;
    private final Scene mainScene;

    public TextField getSamplingTimeTF() {
        return samplingTimeTF;
    }

    public void setSamplingTimeTF(TextField samplingTimeTF) {
        this.samplingTimeTF = samplingTimeTF;
    }

    private TextField samplingTimeTF;
    private ArrayList<TrendChartExpressionHBox> expressionHBoxes = new ArrayList<>();

    public ArrayList<TrendChartSerieData> getTrendChartSerieDataArrayList() {
        return trendChartSerieDataArrayList;
    }

    public void setTrendChartSerieDataArrayList(ArrayList<TrendChartSerieData> trendChartSerieDataArrayList) {
        this.trendChartSerieDataArrayList = trendChartSerieDataArrayList;
    }

    private ArrayList<TrendChartSerieData> trendChartSerieDataArrayList = new ArrayList<>();
    private StackPane root;
    private final UnaryOperator<TextFormatter.Change> numberFilter = change -> {
        String newText = change.getControlNewText();
        if (!newText.matches("^\\d+\\.\\d+$")) {
            change.setText("");
            change.setRange(change.getRangeStart(), change.getRangeStart());
        }
        return change;
    };

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    private boolean canceled=true;

    public SetTrendChartPropertiesWindow(double width, double height) {
        this.setTitle("Definir Expresiones para el Gráfico de tendencias");
        root = new StackPane();
        HBox samplingTimeHBox = new HBox();
        Label samplingTimeLbl = new Label("Tiempo de Muestreo(segundos):");
        samplingTimeTF = new TextField();
        samplingTimeTF.setPromptText("Muestreo en Segundos");
        samplingTimeTF.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 1.0, numberFilter));
        samplingTimeTF.setPrefWidth(385);
        samplingTimeHBox.getChildren().addAll(samplingTimeLbl,samplingTimeTF);
        for (int i = 0; i < 9; i++) {
            expressionHBoxes.add(new TrendChartExpressionHBox());
        }
        expressionHBoxes.get(0).getEnableChartExpression().setSelected(true);
        vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().add(samplingTimeHBox);
        vbox.getChildren().addAll(expressionHBoxes);

        finishSelectionButton = new Button("OK");
        finishSelectionButton.setAlignment(Pos.CENTER);
        finishSelectionButton.setOnAction(actionEvent -> {
            this.canceled = false;
            if(Double.parseDouble(samplingTimeTF.getText())<0.5){
                showAlert("Valor de muestreo muy bajo","El valor de muestreo debe ser mayor o igual a 0.5 segundos");
            }else{
                if(updateTrendChartSerieDataArrayList()){
                    this.close();
                }else{
                    showAlert("Existen valores vacíos","Verifique que todos los valores de las Expresiones habilitadas no estén vacíos");
                }
            }
        });
        cancelButton = new Button("Cancelar");
        cancelButton.setAlignment(Pos.BOTTOM_LEFT);
        cancelButton.setOnAction(actionEvent -> {
            this.canceled = true;
            this.close();
        });
        buttonsHBox = new HBox();
        buttonsHBox.getChildren().add(cancelButton);
        buttonsHBox.getChildren().add(finishSelectionButton);
        buttonsHBox.setAlignment(Pos.BASELINE_RIGHT);
        vbox.getChildren().add(buttonsHBox);
        root.getChildren().add(vbox);
        mainScene = new Scene(root, width, height);
        this.setScene(mainScene);
    }

    public boolean updateTrendChartSerieDataArrayList() {
        for (TrendChartExpressionHBox expressionHBox : expressionHBoxes) {
            if (expressionHBox.getEnableChartExpression().isSelected() && trendChartSerieDataArrayList!=null) {
                if(expressionHBox.getExpression()!=null && !expressionHBox.getExpressionNameTF().getText().isEmpty()){
                    trendChartSerieDataArrayList.add(new TrendChartSerieData(expressionHBox.getExpression(), new CanvasColor(expressionHBox.getExpressionColorPicker().getValue()), expressionHBox.getExpressionNameTF().getText()));
                }else{
                    return false;
                }
            }
        }
        return true;
    }

    private void showAlert(String title, String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(okButton);

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == okButton)
        {
            alert.close();
        }
    }
}
