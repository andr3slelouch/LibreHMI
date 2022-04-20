package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.models.RotationHBox;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.converter.DoubleStringConverter;

import java.util.function.UnaryOperator;

public class SetGeometricFigurePropertiesWindow extends SetSizeWindow {
    private RotationHBox rotationHBox;

    public boolean isModifyingColor() {
        return modifyingColor;
    }

    public void setModifyingColor(boolean modifyingColor) {
        this.modifyingColor = modifyingColor;
    }

    private boolean modifyingColor;

    private TextField rotationTextField;
    private ColorPicker colorPicker;
    private String rotationValue;

    public SetGeometricFigurePropertiesWindow(double width, double height) {
        super(width, height);
        this.getVbox().getWidthValueHBox().setSpacing(16);
        this.getVbox().getHeightValueHBox().setSpacing(30);

        rotationHBox = new RotationHBox(this.rotationValue);
        rotationHBox.setSpacing(78);
        Label selectColor = new Label("Seleccione el color:");

        colorPicker = new ColorPicker();
        colorPicker.setPrefWidth(161);
        HBox colorHBox = new HBox();
        colorHBox.getChildren().addAll(selectColor, colorPicker);

        this.vbox.getChildren().add(2,rotationHBox);
        this.vbox.getChildren().add(3,colorHBox);
    }

    public TextField getRotationTextField() {
        return rotationHBox.getRotationTextField();
    }

    public void setRotationTextField(TextField rotationTextField) {
        this.rotationTextField = rotationTextField;
    }

    public ColorPicker getColorPicker() {
        return colorPicker;
    }

    public void setColorPicker(ColorPicker colorPicker) {
        this.colorPicker = colorPicker;
    }


    public String getRotationValue() {
        return rotationValue;
    }

    public void setRotationValue(String rotationValue) {
        this.rotationValue = rotationValue;
    }

}
