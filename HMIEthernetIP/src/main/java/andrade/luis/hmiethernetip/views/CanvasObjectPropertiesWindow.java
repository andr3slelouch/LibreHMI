package andrade.luis.hmiethernetip.views;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.converter.DoubleStringConverter;

import java.util.function.UnaryOperator;

public class CanvasObjectPropertiesWindow extends SetSizeWindow {

    private final UnaryOperator<TextFormatter.Change> numberFilter = change -> {
        String newText = change.getControlNewText();
        if (!newText.matches("^(\\+|-)?\\d+\\.\\d+$")) {
            change.setText("");
            change.setRange(change.getRangeStart(), change.getRangeStart());
        }
        return change;
    };
    private final ToggleGroup toggleGroup;

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

    public CanvasObjectPropertiesWindow(double width, double height) {
        super(width, height);
        this.titleLabel.setText("Propiedades del Objeto");
        Label rotationLabel = new Label("Rotar:");
        rotationTextField = new TextField("0");
        rotationTextField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, numberFilter));
        rotationTextField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            rotationValue = newValue;
        });
        Button rotationButton = new Button("+90°");
        rotationButton.setOnAction(mouseEvent -> {
            double value = Double.parseDouble(rotationTextField.getText());
            rotationTextField.setText(String.valueOf(value + 90));
        });

        Label colorMode = new Label("Modo de Color:");
        toggleGroup = new ToggleGroup();
        RadioButton originalColorRB = new RadioButton("Original");
        originalColorRB.setToggleGroup(toggleGroup);
        originalColorRB.setSelected(true);
        RadioButton modColorRB = new RadioButton("Modificar");
        modColorRB.setToggleGroup(toggleGroup);
        HBox colorModeHBox = new HBox();
        colorModeHBox.getChildren().addAll(colorMode, originalColorRB, modColorRB);

        HBox rotationHBox = new HBox();
        rotationHBox.getChildren().addAll(rotationLabel, rotationTextField, rotationButton);
        Label selectColor = new Label("Seleccione el color:");

        colorPicker = new ColorPicker();
        colorPicker.setPrefWidth(100);
        HBox colorHBox = new HBox();
        colorHBox.setSpacing(25);
        colorHBox.getChildren().addAll(selectColor, colorPicker);

        Label contrastLabel = new Label("Contraste:");
        contrastTextField = new TextField("0");
        contrastTextField.setPrefWidth(100);
        contrastTextField.setDisable(true);
        contrastTextField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, numberFilter));
        HBox contrastHBox = new HBox();
        contrastHBox.setSpacing(81);
        contrastHBox.getChildren().addAll(contrastLabel, contrastTextField);

        Label brightnessLabel = new Label("Brillo:");
        brightnessTextField = new TextField("0");
        brightnessTextField.setPrefWidth(100);
        brightnessTextField.setDisable(true);
        brightnessTextField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, numberFilter));
        HBox brightnessHBox = new HBox();
        brightnessHBox.setSpacing(110);
        brightnessHBox.getChildren().addAll(brightnessLabel, brightnessTextField);

        Label saturationLabel = new Label("Saturación:");
        saturationTextField = new TextField("0");
        saturationTextField.setPrefWidth(100);
        saturationTextField.setDisable(true);
        saturationTextField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, numberFilter));
        HBox saturationHBox = new HBox();
        saturationHBox.setSpacing(76);
        saturationHBox.getChildren().addAll(saturationLabel, saturationTextField);

        Label hueLabel = new Label("Hue:");
        hueTextField = new TextField("0");
        hueTextField.setPrefWidth(100);
        hueTextField.setDisable(true);
        hueTextField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, numberFilter));
        HBox hueHBox = new HBox();
        hueHBox.setSpacing(118);
        hueHBox.getChildren().addAll(hueLabel, hueTextField);

        modColorRB.selectedProperty().addListener((observableValue, oldBoolean, newBoolean) -> {
            modifyingColor = Boolean.TRUE.equals(newBoolean);
            contrastTextField.setDisable(!Boolean.TRUE.equals(newBoolean));
            brightnessTextField.setDisable(!Boolean.TRUE.equals(newBoolean));
            saturationTextField.setDisable(!Boolean.TRUE.equals(newBoolean));
            hueTextField.setDisable(!Boolean.TRUE.equals(newBoolean));
        });
        
        this.vbox.getChildren().add(3,rotationHBox);
        this.vbox.getChildren().add(4,colorHBox);
        this.vbox.getChildren().add(5,colorModeHBox);
        this.vbox.getChildren().add(6,contrastHBox);
        this.vbox.getChildren().add(7,brightnessHBox);
        this.vbox.getChildren().add(8,saturationHBox);
        this.vbox.getChildren().add(9,hueHBox);
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
}
