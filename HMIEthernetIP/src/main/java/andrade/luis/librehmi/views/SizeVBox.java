package andrade.luis.librehmi.views;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.DoubleStringConverter;

import java.util.function.UnaryOperator;

public class SizeVBox extends VBox {
    private final HBox heightValueHBox;
    private final HBox widthValueHBox;
    private final TextField heightField;
    private final TextField widthField;

    public double getWidthFromField() {
        double minWidth = 50.0;
        return widthField.getText().isEmpty() ? minWidth : Double.parseDouble(widthField.getText());
    }

    public double getHeightFromField() {
        double minHeight = 50.0;
        return heightField.getText().isEmpty() ? minHeight : Double.parseDouble(heightField.getText());
    }

    public SizeVBox(double defWidth,double defHeight,double widthFieldWidth,double heightFieldWidth) {
        Label widthLabel = new Label("Defina el ancho:");
        widthField = new TextField(String.valueOf(defWidth));
        if(widthFieldWidth>0) widthField.setPrefWidth(widthFieldWidth);
        UnaryOperator<TextFormatter.Change> numberFilter = change -> {
            String newText = change.getControlNewText();
            if (!newText.matches("^(\\+|-)?\\d+\\.\\d+$")) {
                change.setText("");
                change.setRange(change.getRangeStart(), change.getRangeStart());
            }
            return change;
        };
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

    public HBox getWidthValueHBox() {
        return widthValueHBox;
    }

    public TextField getHeightField() {
        return heightField;
    }

    public TextField getWidthField() {
        return widthField;
    }

}
