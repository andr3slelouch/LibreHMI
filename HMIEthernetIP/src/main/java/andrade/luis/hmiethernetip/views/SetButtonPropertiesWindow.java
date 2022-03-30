package andrade.luis.hmiethernetip.views;

import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class SetButtonPropertiesWindow extends SetInputTextPropertiesWindow{

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
        Label selectColorLabel = new Label("Color de Fuente:");
        colorPickerLabel = new ColorPicker();
        colorPickerLabel.setPrefWidth(195);
        colorLabelHBox = new HBox();
        colorLabelHBox.setSpacing(29);
        colorLabelHBox.getChildren().addAll(selectColorLabel, colorPickerLabel);

        this.vbox.getChildren().add(6,colorLabelHBox);
    }
}
