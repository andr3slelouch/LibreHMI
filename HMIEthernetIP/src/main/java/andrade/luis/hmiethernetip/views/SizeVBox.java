package andrade.luis.hmiethernetip.views;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.DoubleStringConverter;

import java.util.function.UnaryOperator;

public class SizeVBox extends VBox {
    private HBox heightValueHBox;
    private HBox widthValueHBox;
    private TextField heightField;
    private TextField widthField;
    private Double minWidth = 50.0;
    private Double minHeight = 50.0;
    private final UnaryOperator<TextFormatter.Change> numberFilter = change -> {
        String newText = change.getControlNewText();
        if (!newText.matches("^(\\+|-)?\\d+\\.\\d+$")) {
            change.setText("");
            change.setRange(change.getRangeStart(), change.getRangeStart());
        }
        return change;
    };

    public double getWidthFromField() {
        return widthField.getText().isEmpty() ? minWidth : Double.parseDouble(widthField.getText());
    }

    public double getHeightFromField() {
        return heightField.getText().isEmpty() ? minHeight : Double.parseDouble(heightField.getText());
    }

    public SizeVBox(double defWidth,double defHeight,double widthFieldWidth,double heightFieldWidth) {
        Label widthLabel = new Label("Defina el ancho:");
        widthField = new TextField(String.valueOf(defWidth));
        if(widthFieldWidth>0) widthField.setPrefWidth(widthFieldWidth);
        widthField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), defWidth, numberFilter));
        widthValueHBox = new HBox(widthLabel, widthField);
        Label heightLabel = new Label("Defina el alto:");
        heightField = new TextField(String.valueOf(defHeight));
        if(heightFieldWidth>0) heightField.setPrefWidth(heightFieldWidth);
        heightValueHBox = new HBox(heightLabel, heightField);
        heightField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), defHeight, numberFilter));

        this.getChildren().addAll(widthValueHBox,heightValueHBox);
    }

    public HBox getHeightValueHBox() {
        return heightValueHBox;
    }

    public void setHeightValueHBox(HBox heightValueHBox) {
        this.heightValueHBox = heightValueHBox;
    }

    public HBox getWidthValueHBox() {
        return widthValueHBox;
    }

    public void setWidthValueHBox(HBox widthValueHBox) {
        this.widthValueHBox = widthValueHBox;
    }

    public TextField getHeightField() {
        return heightField;
    }

    public void setHeightField(TextField heightField) {
        this.heightField = heightField;
    }

    public TextField getWidthField() {
        return widthField;
    }

    public void setWidthField(TextField widthField) {
        this.widthField = widthField;
    }
}
