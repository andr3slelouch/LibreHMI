package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.models.TrendChartExpressionHBox;
import andrade.luis.hmiethernetip.models.TrendChartSerieData;
import andrade.luis.hmiethernetip.models.canvas.CanvasColor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class SetTrendChartPropertiesWindow extends Stage {
    private final VBox vbox;
    private final Button finishSelectionButton;
    private final Button cancelButton;
    private final HBox buttonsHBox;
    private final Scene mainScene;
    private ArrayList<TrendChartExpressionHBox> expressionHBoxes = new ArrayList<>();

    public ArrayList<TrendChartSerieData> getTrendChartSerieDataArrayList() {
        return trendChartSerieDataArrayList;
    }

    public void setTrendChartSerieDataArrayList(ArrayList<TrendChartSerieData> trendChartSerieDataArrayList) {
        this.trendChartSerieDataArrayList = trendChartSerieDataArrayList;
    }

    private ArrayList<TrendChartSerieData> trendChartSerieDataArrayList = new ArrayList<>();
    private StackPane root;

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    private boolean canceled;

    public SetTrendChartPropertiesWindow(double width, double height) {
        this.setTitle("Definir Tags para el Gráfico de tendencias");
        root = new StackPane();
        for (int i = 0; i < 9; i++) {
            expressionHBoxes.add(new TrendChartExpressionHBox());
        }
        expressionHBoxes.get(0).getEnableChartExpression().setSelected(true);
        vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(expressionHBoxes);

        finishSelectionButton = new Button("OK");
        finishSelectionButton.setAlignment(Pos.CENTER);
        finishSelectionButton.setOnAction(actionEvent -> {
            this.canceled = false;
            this.close();
            updateTrendChartSerieDataArrayList();
        });
        cancelButton = new Button("Añadir Tag");
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

    public void updateTrendChartSerieDataArrayList() {
        for (TrendChartExpressionHBox expressionHBox : expressionHBoxes) {
            if (expressionHBox.getEnableChartExpression().isSelected()) {
                trendChartSerieDataArrayList.add(new TrendChartSerieData(expressionHBox.getExpression(), new CanvasColor(expressionHBox.getExpressionColorPicker().getValue()), expressionHBox.getExpressionNameTF().getText()));
            }
        }
    }
}
