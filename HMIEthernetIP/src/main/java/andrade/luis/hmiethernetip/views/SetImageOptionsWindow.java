package andrade.luis.hmiethernetip.views;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.util.function.UnaryOperator;

public class SetImageOptionsWindow extends Stage {
    private final UnaryOperator<TextFormatter.Change> numberFilter = change -> {
        String newText = change.getControlNewText();
        if (!newText.matches("^(\\+|-)?\\d+\\.\\d+$")) {
            change.setText("");
            change.setRange(change.getRangeStart(), change.getRangeStart());
        }
        return change;
    };

    public TextField getHueTextField() {
        return hueTextField;
    }

    public void setHueTextField(TextField hueTextField) {
        this.hueTextField = hueTextField;
    }

    private TextField hueTextField;

    public TextField getSaturationTextField() {
        return saturationTextField;
    }

    public void setSaturationTextField(TextField saturationTextField) {
        this.saturationTextField = saturationTextField;
    }

    public TextField getBrightnessTextField() {
        return brightnessTextField;
    }

    public void setBrightnessTextField(TextField brightnessTextField) {
        this.brightnessTextField = brightnessTextField;
    }

    public TextField getContrastTextField() {
        return contrastTextField;
    }

    public void setContrastTextField(TextField contrastTextField) {
        this.contrastTextField = contrastTextField;
    }

    public ColorPicker getColorPicker() {
        return colorPicker;
    }

    public void setColorPicker(ColorPicker colorPicker) {
        this.colorPicker = colorPicker;
    }

    public ToggleGroup getToggleGroup() {
        return toggleGroup;
    }

    public void setToggleGroup(ToggleGroup toggleGroup) {
        this.toggleGroup = toggleGroup;
    }

    private TextField saturationTextField;
    private TextField brightnessTextField;
    private TextField contrastTextField;
    private ColorPicker colorPicker;
    private ToggleGroup toggleGroup;
    private TextField rotationTextField;
    private CheckBox mirrorHorizontalCheckBox;
    private CheckBox mirrorVerticalCheckBox;

    public boolean isModifyingColor() {
        return modifyingColor;
    }

    public void setModifyingColor(boolean modifyingColor) {
        this.modifyingColor = modifyingColor;
    }

    private boolean modifyingColor = false;

    public boolean isMirroringHorizontal() {
        return mirroringHorizontal;
    }

    public void setMirroringHorizontal(boolean mirroringHorizontal) {
        this.mirroringHorizontal = mirroringHorizontal;
    }

    public boolean isMirroringVertical() {
        return mirroringVertical;
    }

    public void setMirroringVertical(boolean mirroringVertical) {
        this.mirroringVertical = mirroringVertical;
    }

    public String getRotationValue() {
        return rotationValue;
    }

    public void setRotationValue(String rotationValue) {
        this.rotationValue = rotationValue;
    }

    public TextField getRotationTextField() {
        return rotationTextField;
    }

    public void setRotationTextField(TextField rotationTextField) {
        this.rotationTextField = rotationTextField;
    }

    public CheckBox getMirrorHorizontalCheckBox() {
        return mirrorHorizontalCheckBox;
    }

    public void setMirrorHorizontalCheckBox(CheckBox mirrorHorizontalCheckBox) {
        this.mirrorHorizontalCheckBox = mirrorHorizontalCheckBox;
    }

    public CheckBox getMirrorVerticalCheckBox() {
        return mirrorVerticalCheckBox;
    }

    public void setMirrorVerticalCheckBox(CheckBox mirrorVerticalCheckBox) {
        this.mirrorVerticalCheckBox = mirrorVerticalCheckBox;
    }

    private boolean mirroringHorizontal = false;
    private boolean mirroringVertical = false;
    private String rotationValue = "0";

    public SetImageOptionsWindow() {
        StackPane root = new StackPane();
        Label label = new Label("Opciones de Imagen");
        mirrorHorizontalCheckBox = new CheckBox("Reflejar Horizontalmente");
        mirrorHorizontalCheckBox.selectedProperty().addListener((observableValue, oldBoolean, newBoolean) -> {
            mirroringHorizontal = newBoolean;
        });
        mirrorVerticalCheckBox = new CheckBox("Reflejar Verticalmente");
        mirrorVerticalCheckBox.selectedProperty().addListener((observableValue, oldBoolean, newBoolean) -> {
            mirroringVertical = newBoolean;
        });
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

        HBox rotationHBox = new HBox();
        rotationHBox.getChildren().addAll(rotationLabel, rotationTextField, rotationButton);

        Label colorMode = new Label("Modo de Color:");
        toggleGroup = new ToggleGroup();
        RadioButton originalColorRB = new RadioButton("Original");
        originalColorRB.setToggleGroup(toggleGroup);
        RadioButton modColorRB = new RadioButton("Modificar");
        modColorRB.setToggleGroup(toggleGroup);
        HBox colorModeHBox = new HBox();
        colorModeHBox.getChildren().addAll(colorMode, originalColorRB, modColorRB);

        Label selectColor = new Label("Seleccione el color:");
        colorPicker = new ColorPicker();
        colorPicker.setPrefWidth(100);
        colorPicker.setDisable(true);
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
        hueHBox.getChildren().addAll(hueLabel,hueTextField);

        modColorRB.selectedProperty().addListener((observableValue, oldBoolean, newBoolean) -> {
            modifyingColor = Boolean.TRUE.equals(newBoolean);
            colorPicker.setDisable(!Boolean.TRUE.equals(newBoolean));
            contrastTextField.setDisable(!Boolean.TRUE.equals(newBoolean));
            brightnessTextField.setDisable(!Boolean.TRUE.equals(newBoolean));
            saturationTextField.setDisable(!Boolean.TRUE.equals(newBoolean));
            hueTextField.setDisable(!Boolean.TRUE.equals(newBoolean));
        });

        Button okButton = new Button("OK");
        okButton.setOnAction(mouseEvent -> this.close());
        HBox okHBox = new HBox();
        okHBox.getChildren().addAll(okButton);
        okHBox.setAlignment(Pos.BOTTOM_RIGHT);

        VBox vbox = new VBox();
        vbox.getChildren().addAll(label, mirrorHorizontalCheckBox, mirrorVerticalCheckBox, rotationHBox, colorModeHBox, colorHBox, contrastHBox, brightnessHBox, saturationHBox,hueHBox, okHBox);

        root.getChildren().add(vbox);

        this.setScene(new Scene(root, 300, 300));
    }
}
