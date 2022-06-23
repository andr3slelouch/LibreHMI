package andrade.luis.libreHMI.views.windows;

import andrade.luis.libreHMI.models.*;
import andrade.luis.libreHMI.views.RotationHBox;
import andrade.luis.libreHMI.views.SizeVBox;
import andrade.luis.libreHMI.views.TrendChartExpressionHBox;
import andrade.luis.libreHMI.views.canvas.CanvasColor;
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

    public SizeVBox getSizeVBox() {
        return sizeVBox;
    }

    private final SizeVBox sizeVBox;

    public RotationHBox getRotationHBox() {
        return rotationHBox;
    }

    private final RotationHBox rotationHBox;

    private String rotationValue;

    public TextField getSamplingTimeTF() {
        return samplingTimeTF;
    }

    private final TextField samplingTimeTF;

    public ArrayList<TrendChartExpressionHBox> getExpressionHBoxes() {
        return expressionHBoxes;
    }

    private final ArrayList<TrendChartExpressionHBox> expressionHBoxes = new ArrayList<>();

    public ArrayList<TrendChartSerieData> getTrendChartSerieDataArrayList() {
        return trendChartSerieDataArrayList;
    }

    private final ArrayList<TrendChartSerieData> trendChartSerieDataArrayList = new ArrayList<>();

    public boolean isCanceled() {
        return canceled;
    }

    private boolean canceled=true;

    public ArrayList<Tag> getLocalTags() {
        return localTags;
    }

    public void setLocalTags(ArrayList<Tag> localTags) {
        this.localTags = localTags;
    }

    private ArrayList<Tag> localTags;


    public SetTrendChartPropertiesWindow(double width, double height,ArrayList<Tag> localTags) {
        this.setTitle("Propiedades para el Gráfico de tendencias");
        this.localTags = localTags;
        StackPane root = new StackPane();
        HBox samplingTimeHBox = new HBox();
        Label samplingTimeLbl = new Label("Tiempo de Muestreo(segundos):");
        samplingTimeTF = new TextField();
        samplingTimeTF.setPromptText("Muestreo en Segundos");
        UnaryOperator<TextFormatter.Change> numberFilter = change -> {
            String newText = change.getControlNewText();
            if (!newText.matches("^\\d+\\.\\d+$")) {
                change.setText("");
                change.setRange(change.getRangeStart(), change.getRangeStart());
            }
            return change;
        };
        samplingTimeTF.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 1.0, numberFilter));
        samplingTimeTF.setPrefWidth(385);
        samplingTimeHBox.getChildren().addAll(samplingTimeLbl,samplingTimeTF);
        for (int i = 0; i < 9; i++) {
            expressionHBoxes.add(new TrendChartExpressionHBox(localTags));
        }
        expressionHBoxes.get(0).getEnableChartExpression().setSelected(true);
        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().add(samplingTimeHBox);

        sizeVBox = new SizeVBox(700,500,482,482);
        sizeVBox.getHeightValueHBox().setSpacing(19);
        sizeVBox.getWidthValueHBox().setSpacing(5);
        sizeVBox.setSpacing(5);
        vbox.getChildren().add(sizeVBox);
        rotationHBox = new RotationHBox(this.rotationValue);
        rotationHBox.setSpacing(66);
        rotationHBox.getRotationTextField().setPrefWidth(435);
        vbox.getChildren().add(rotationHBox);
        vbox.getChildren().addAll(expressionHBoxes);


        Button finishSelectionButton = new Button("OK");
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
        Button cancelButton = new Button("Cancelar");
        cancelButton.setAlignment(Pos.BOTTOM_LEFT);
        cancelButton.setOnAction(actionEvent -> {
            this.canceled = true;
            this.close();
        });
        HBox buttonsHBox = new HBox();
        buttonsHBox.getChildren().add(cancelButton);
        buttonsHBox.getChildren().add(finishSelectionButton);
        buttonsHBox.setAlignment(Pos.BASELINE_RIGHT);
        vbox.getChildren().add(buttonsHBox);
        root.getChildren().add(vbox);
        Scene mainScene = new Scene(root, width, height);
        this.setScene(mainScene);
    }

    public boolean updateTrendChartSerieDataArrayList() {
        for (TrendChartExpressionHBox expressionHBox : expressionHBoxes) {
            if (expressionHBox.getEnableChartExpression().isSelected()) {
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
