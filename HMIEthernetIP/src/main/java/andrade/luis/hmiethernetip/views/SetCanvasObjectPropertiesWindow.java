package andrade.luis.hmiethernetip.views;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.converter.DoubleStringConverter;

import java.util.function.UnaryOperator;

public class SetCanvasObjectPropertiesWindow extends SetSizeWindow {

    private final UnaryOperator<TextFormatter.Change> numberFilter = change -> {
        String newText = change.getControlNewText();
        if (!newText.matches("^(\\+|-)?\\d+\\.\\d+$")) {
            change.setText("");
            change.setRange(change.getRangeStart(), change.getRangeStart());
        }
        return change;
    };
    private final ToggleGroup toggleGroup;
    private RadioButton originalColorRB;
    private RadioButton modColorRB;

    public boolean isModifyingColor() {
        return modifyingColor;
    }

    public void setModifyingColor(boolean modifyingColor) {
        this.modifyingColor = modifyingColor;
    }

    private boolean modifyingColor;

    private TextField rotationTextField;
    private ColorPicker colorPicker;
    private TextField contrastTextField;
    private TextField brightnessTextField;
    private TextField saturationTextField;
    private TextField hueTextField;
    private String rotationValue;

    public SetCanvasObjectPropertiesWindow(double width, double height) {
        super(width, height);
        Label rotationLabel = new Label("Rotar:");
        rotationTextField = new TextField("0");
        rotationTextField.setPrefWidth(115);
        rotationTextField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, numberFilter));
        rotationTextField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            rotationValue = newValue;
        });
        Button rotationButton = new Button("+90°");
        rotationButton.setOnAction(mouseEvent -> {
            double value = Double.parseDouble(rotationTextField.getText());
            rotationTextField.setText(String.valueOf(value + 90));
        });
        HBox rotationInputHBox = new HBox();
        rotationInputHBox.getChildren().addAll(rotationTextField,rotationButton);

        Label colorMode = new Label("Modo de Color:");
        toggleGroup = new ToggleGroup();
        originalColorRB = new RadioButton("Original");
        originalColorRB.setToggleGroup(toggleGroup);
        originalColorRB.setSelected(true);
        modColorRB = new RadioButton("Modificar");
        modColorRB.setToggleGroup(toggleGroup);
        HBox colorModeHBox = new HBox();
        colorModeHBox.getChildren().addAll(colorMode, originalColorRB, modColorRB);
        colorModeHBox.setSpacing(15);
        HBox rotationHBox = new HBox();
        rotationHBox.getChildren().addAll(rotationLabel, rotationInputHBox);
        rotationHBox.setSpacing(73);
        Label selectColor = new Label("Seleccione el color:");

        colorPicker = new ColorPicker();
        colorPicker.setPrefWidth(100);
        HBox colorHBox = new HBox();
        colorHBox.setSpacing(55);
        colorHBox.getChildren().addAll(selectColor, colorPicker);

        Label contrastLabel = new Label("Contraste:");
        contrastTextField = new TextField("0");
        contrastTextField.setPrefWidth(163);
        contrastTextField.setDisable(true);
        contrastTextField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, numberFilter));
        HBox contrastHBox = new HBox();
        contrastHBox.setSpacing(45);
        contrastHBox.getChildren().addAll(contrastLabel, contrastTextField);

        Label brightnessLabel = new Label("Brillo:");
        brightnessTextField = new TextField("0");
        brightnessTextField.setPrefWidth(163);
        brightnessTextField.setDisable(true);
        brightnessTextField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, numberFilter));
        HBox brightnessHBox = new HBox();
        brightnessHBox.setSpacing(73);
        brightnessHBox.getChildren().addAll(brightnessLabel, brightnessTextField);

        Label saturationLabel = new Label("Saturación:");
        saturationTextField = new TextField("0");
        saturationTextField.setPrefWidth(163);
        saturationTextField.setDisable(true);
        saturationTextField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, numberFilter));
        HBox saturationHBox = new HBox();
        saturationHBox.setSpacing(39);
        saturationHBox.getChildren().addAll(saturationLabel, saturationTextField);

        Label hueLabel = new Label("Hue:");
        hueTextField = new TextField("0");
        hueTextField.setPrefWidth(163);
        hueTextField.setDisable(true);
        hueTextField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, numberFilter));
        HBox hueHBox = new HBox();
        hueHBox.setSpacing(80);
        hueHBox.getChildren().addAll(hueLabel, hueTextField);

        modColorRB.selectedProperty().addListener((observableValue, oldBoolean, newBoolean) -> {
            modifyingColor = Boolean.TRUE.equals(newBoolean);
            contrastTextField.setDisable(!Boolean.TRUE.equals(newBoolean));
            brightnessTextField.setDisable(!Boolean.TRUE.equals(newBoolean));
            saturationTextField.setDisable(!Boolean.TRUE.equals(newBoolean));
            hueTextField.setDisable(!Boolean.TRUE.equals(newBoolean));
        });
        
        this.vbox.getChildren().add(2,rotationHBox);
        this.vbox.getChildren().add(3,colorHBox);
        this.vbox.getChildren().add(4,colorModeHBox);
        this.vbox.getChildren().add(5,contrastHBox);
        this.vbox.getChildren().add(6,brightnessHBox);
        this.vbox.getChildren().add(7,saturationHBox);
        this.vbox.getChildren().add(8,hueHBox);
    }

    public TextField getRotationTextField() {
        return rotationTextField;
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

    public TextField getContrastTextField() {
        return contrastTextField;
    }

    public void setContrastTextField(TextField contrastTextField) {
        this.contrastTextField = contrastTextField;
    }

    public TextField getBrightnessTextField() {
        return brightnessTextField;
    }

    public void setBrightnessTextField(TextField brightnessTextField) {
        this.brightnessTextField = brightnessTextField;
    }

    public TextField getSaturationTextField() {
        return saturationTextField;
    }

    public void setSaturationTextField(TextField saturationTextField) {
        this.saturationTextField = saturationTextField;
    }

    public TextField getHueTextField() {
        return hueTextField;
    }

    public void setHueTextField(TextField hueTextField) {
        this.hueTextField = hueTextField;
    }

    public String getRotationValue() {
        return rotationValue;
    }

    public void setRotationValue(String rotationValue) {
        this.rotationValue = rotationValue;
    }

    public RadioButton getOriginalColorRB() {
        return originalColorRB;
    }

    public void setOriginalColorRB(RadioButton originalColorRB) {
        this.originalColorRB = originalColorRB;
    }

    public RadioButton getModColorRB() {
        return modColorRB;
    }

    public void setModColorRB(RadioButton modColorRB) {
        this.modColorRB = modColorRB;
    }
}
