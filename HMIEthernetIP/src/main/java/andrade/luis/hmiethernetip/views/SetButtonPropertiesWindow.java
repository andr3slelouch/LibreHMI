package andrade.luis.hmiethernetip.views;

import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.util.converter.DoubleStringConverter;

public class SetButtonPropertiesWindow extends SetInputTextPropertiesWindow{

    public TextField getButtonLabelField() {
        return buttonLabelField;
    }

    public void setButtonLabelField(TextField buttonLabelField) {
        this.buttonLabelField = buttonLabelField;
    }

    private TextField buttonLabelField;
    private HBox colorLabelHBox;

    public ColorPicker getColorPickerLabel() {
        return colorPickerLabel;
    }

    public void setColorPickerLabel(ColorPicker colorPickerLabel) {
        this.colorPickerLabel = colorPickerLabel;
    }

    private ColorPicker colorPickerLabel;

    public SetButtonPropertiesWindow(double width, double height) {
        super(width, height);

        Label buttonLabel = new Label("Etiqueta del Botón:");
        this.buttonLabelField = new TextField("");
        buttonLabelField.setPrefWidth(195);
        HBox buttonLabelHBox = new HBox(buttonLabel, buttonLabelField);
        buttonLabelHBox.setSpacing(12);
        this.vbox.getChildren().add(3,buttonLabelHBox);

        Label selectColorLabel = new Label("Color de Fuente:");
        colorPickerLabel = new ColorPicker();
        colorPickerLabel.setPrefWidth(195);
        colorLabelHBox = new HBox();
        colorLabelHBox.setSpacing(29);
        colorLabelHBox.getChildren().addAll(selectColorLabel, colorPickerLabel);

        this.vbox.getChildren().add(6,colorLabelHBox);
    }
}
