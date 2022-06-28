package andrade.luis.librehmi.views.windows;

import andrade.luis.librehmi.views.RotationHBox;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

/**
 * Ventana de definición de propiedades de figuras geométricas
 */
public class SetGeometricFigurePropertiesWindow extends SetSizeWindow {
    private final RotationHBox rotationHBox;

    public boolean isModifyingColor() {
        return modifyingColor;
    }

    private boolean modifyingColor;

    private final ColorPicker colorPicker;
    private String rotationValue;

    /**
     * Constructor de la ventana
     * @param width Ancho de la ventana
     * @param height Alto de la ventana
     */
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

        this.vbox.getChildren().add(2, rotationHBox);
        this.vbox.getChildren().add(3, colorHBox);
    }

    public TextField getRotationTextField() {
        return rotationHBox.getRotationTextField();
    }


    public ColorPicker getColorPicker() {
        return colorPicker;
    }


}
