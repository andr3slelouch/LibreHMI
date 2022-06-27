package andrade.luis.librehmi.views.windows;

import andrade.luis.librehmi.views.RotationHBox;
import andrade.luis.librehmi.views.SizeVBox;
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

import static andrade.luis.librehmi.util.TextFormatters.numberFilter;

public class SetImageOptionsWindow extends Stage {
    protected final VBox vbox;
    private final RotationHBox rotationHBox;

    public SizeVBox getSizeVBox() {
        return sizeVBox;
    }

    private final SizeVBox sizeVBox;

    public RadioButton getOriginalColorRB() {
        return originalColorRB;
    }

    public RadioButton getModColorRB() {
        return modColorRB;
    }

    private final RadioButton originalColorRB;
    private final RadioButton modColorRB;

    public CheckBox getPreserveRatioCheckBox() {
        return preserveRatioCheckBox;
    }

    private final CheckBox preserveRatioCheckBox;

    public Button getImagePathButton() {
        return imagePathButton;
    }

    private final Button imagePathButton;

    public TextField getImagePathTextField() {
        return imagePathTextField;
    }

    private final TextField imagePathTextField;

    public TextField getHueTextField() {
        return hueTextField;
    }

    private final TextField hueTextField;

    public TextField getSaturationTextField() {
        return saturationTextField;
    }

    public TextField getBrightnessTextField() {
        return brightnessTextField;
    }

    public TextField getContrastTextField() {
        return contrastTextField;
    }

    public ColorPicker getColorPicker() {
        return colorPicker;
    }

    private final TextField saturationTextField;
    private final TextField brightnessTextField;
    private final TextField contrastTextField;
    private final ColorPicker colorPicker;
    private final CheckBox mirrorHorizontalCheckBox;
    private final CheckBox mirrorVerticalCheckBox;

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

    public boolean isMirroringVertical() {
        return mirroringVertical;
    }

    public TextField getRotationTextField() {
        return rotationHBox.getRotationTextField();
    }

    public CheckBox getMirrorHorizontalCheckBox() {
        return mirrorHorizontalCheckBox;
    }

    public CheckBox getMirrorVerticalCheckBox() {
        return mirrorVerticalCheckBox;
    }

    public boolean isPreservingRatio() {
        return preservingRatio;
    }

    private boolean preservingRatio = false;
    private boolean mirroringHorizontal = false;
    private boolean mirroringVertical = false;
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
        preserveRatioCheckBox.selectedProperty().addListener((observableValue, oldBoolean, newBoolean) -> preservingRatio = newBoolean);
        mirrorHorizontalCheckBox = new CheckBox("Reflejar Horizontalmente");
        mirrorHorizontalCheckBox.selectedProperty().addListener((observableValue, oldBoolean, newBoolean) -> mirroringHorizontal = newBoolean);
        mirrorVerticalCheckBox = new CheckBox("Reflejar Verticalmente");
        mirrorVerticalCheckBox.selectedProperty().addListener((observableValue, oldBoolean, newBoolean) -> mirroringVertical = newBoolean);

        String rotationValue = "0";
        rotationHBox = new RotationHBox(rotationValue);
        rotationHBox.setSpacing(100);

        Label colorMode = new Label("Modo de Color:");
        ToggleGroup toggleGroup = new ToggleGroup();
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
