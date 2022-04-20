package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.models.RotationHBox;
import andrade.luis.hmiethernetip.models.SizeVBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.io.File;
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
    protected final VBox vbox;
    private RotationHBox rotationHBox;

    public SizeVBox getSizeVBox() {
        return sizeVBox;
    }

    public void setSizeVBox(SizeVBox sizeVBox) {
        this.sizeVBox = sizeVBox;
    }

    private SizeVBox sizeVBox;

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

    private RadioButton originalColorRB;
    private RadioButton modColorRB;

    public CheckBox getPreserveRatioCheckBox() {
        return preserveRatioCheckBox;
    }

    public void setPreserveRatioCheckBox(CheckBox preserveRatioCheckBox) {
        this.preserveRatioCheckBox = preserveRatioCheckBox;
    }

    private CheckBox preserveRatioCheckBox;

    public Button getImagePathButton() {
        return imagePathButton;
    }

    public void setImagePathButton(Button imagePathButton) {
        this.imagePathButton = imagePathButton;
    }

    private Button imagePathButton;

    public TextField getImagePathTextField() {
        return imagePathTextField;
    }

    public void setImagePathTextField(TextField imagePathTextField) {
        this.imagePathTextField = imagePathTextField;
    }

    private TextField imagePathTextField;

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
        return rotationHBox.getRotationTextField();
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

    public boolean isPreservingRatio() {
        return preservingRatio;
    }

    public void setPreservingRatio(boolean preservingRatio) {
        this.preservingRatio = preservingRatio;
    }

    private boolean preservingRatio = false;
    private boolean mirroringHorizontal = false;
    private boolean mirroringVertical = false;
    private String rotationValue = "0";
    final FileChooser fileChooser = new FileChooser();


    public SetImageOptionsWindow(double imageViewWidth, double imageViewHeight) {
        StackPane root = new StackPane();
       this.setTitle("Propiedades de Imagen");

        sizeVBox = new SizeVBox(imageViewWidth,imageViewHeight,252,252);
        sizeVBox.getWidthValueHBox().setSpacing(32);
        sizeVBox.getHeightValueHBox().setSpacing(45);

        Label imagePathLabel = new Label("Ubicación de Imagen:");
        imagePathTextField = new TextField();
        imagePathTextField.setDisable(true);
        imagePathButton = new Button("Seleccionar");
        imagePathButton.setOnAction(mouseEvent -> {
            fileChooser.setTitle("Seleccione una imagen");
            fileChooser.setInitialDirectory(
                    new File(System.getProperty("user.home"))
            );
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Imagen", "*.bmp","*.gif","*.jpg","*.jpeg","*.png","*.BMP","*.GIF","*.JPG","*.JPEG","*.PNG"),
                    new FileChooser.ExtensionFilter("BMP","*.bmp","*.BMP"),
                    new FileChooser.ExtensionFilter("GIF", "*.gif","*.GIF"),
                    new FileChooser.ExtensionFilter("JPG", "*.jpg","*.JPG"),
                    new FileChooser.ExtensionFilter("JPEG", "*.jpeg","*.JPEG"),
                    new FileChooser.ExtensionFilter("PNG", "*.png","*.PNG")
            );
            File file = fileChooser.showOpenDialog(this);
            if (file != null) {
                this.imagePathTextField.setText(file.getAbsolutePath());
            }
        });
        HBox imagePathHBox = new HBox();
        imagePathHBox.getChildren().addAll(imagePathLabel, imagePathTextField, imagePathButton);

        preserveRatioCheckBox = new CheckBox("Preservar Radio al Redimensionar");
        preserveRatioCheckBox.selectedProperty().addListener((observableValue, oldBoolean, newBoolean) -> {
            preservingRatio = newBoolean;
        });
        mirrorHorizontalCheckBox = new CheckBox("Reflejar Horizontalmente");
        mirrorHorizontalCheckBox.selectedProperty().addListener((observableValue, oldBoolean, newBoolean) -> {
            mirroringHorizontal = newBoolean;
        });
        mirrorVerticalCheckBox = new CheckBox("Reflejar Verticalmente");
        mirrorVerticalCheckBox.selectedProperty().addListener((observableValue, oldBoolean, newBoolean) -> {
            mirroringVertical = newBoolean;
        });

        rotationHBox = new RotationHBox(rotationValue);
        rotationHBox.setSpacing(100);

        Label colorMode = new Label("Modo de Color:");
        toggleGroup = new ToggleGroup();
        originalColorRB = new RadioButton("Original");
        originalColorRB.setToggleGroup(toggleGroup);
        modColorRB = new RadioButton("Modificar");
        modColorRB.setToggleGroup(toggleGroup);
        HBox colorModeHBox = new HBox();
        colorModeHBox.getChildren().addAll(colorMode, originalColorRB, modColorRB);
        colorModeHBox.setSpacing(40);

        Label selectColor = new Label("Seleccione el color:");
        colorPicker = new ColorPicker();
        colorPicker.setPrefWidth(242);
        colorPicker.setDisable(true);
        HBox colorHBox = new HBox();
        colorHBox.setSpacing(22);
        colorHBox.getChildren().addAll(selectColor, colorPicker);

        Label contrastLabel = new Label("Contraste:");
        contrastTextField = new TextField("0");
        contrastTextField.setPrefWidth(242);
        contrastTextField.setDisable(true);
        contrastTextField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, numberFilter));
        HBox contrastHBox = new HBox();
        contrastHBox.setSpacing(76);
        contrastHBox.getChildren().addAll(contrastLabel, contrastTextField);

        Label brightnessLabel = new Label("Brillo:");
        brightnessTextField = new TextField("0");
        brightnessTextField.setPrefWidth(242);
        brightnessTextField.setDisable(true);
        brightnessTextField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, numberFilter));
        HBox brightnessHBox = new HBox();
        brightnessHBox.setSpacing(105);
        brightnessHBox.getChildren().addAll(brightnessLabel, brightnessTextField);

        Label saturationLabel = new Label("Saturación:");
        saturationTextField = new TextField("0");
        saturationTextField.setPrefWidth(242);
        saturationTextField.setDisable(true);
        saturationTextField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, numberFilter));
        HBox saturationHBox = new HBox();
        saturationHBox.setSpacing(71);
        saturationHBox.getChildren().addAll(saturationLabel, saturationTextField);

        Label hueLabel = new Label("Hue:");
        hueTextField = new TextField("0");
        hueTextField.setPrefWidth(242);
        hueTextField.setDisable(true);
        hueTextField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, numberFilter));
        HBox hueHBox = new HBox();
        hueHBox.setSpacing(113);
        hueHBox.getChildren().addAll(hueLabel, hueTextField);

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

        vbox = new VBox();
        vbox.getChildren().addAll(sizeVBox, imagePathHBox, preserveRatioCheckBox,mirrorHorizontalCheckBox, mirrorVerticalCheckBox, rotationHBox, colorModeHBox, colorHBox, contrastHBox, brightnessHBox, saturationHBox, hueHBox, okHBox);
        vbox.setPadding(new Insets(5,5,5,5));
        vbox.setSpacing(5);
        root.getChildren().add(vbox);

        this.setScene(new Scene(root, 400, 420));
    }
}
