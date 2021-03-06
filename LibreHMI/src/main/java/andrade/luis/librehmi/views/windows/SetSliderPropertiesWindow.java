package andrade.luis.librehmi.views.windows;

import andrade.luis.librehmi.models.CanvasOrientation;
import andrade.luis.librehmi.views.RotationHBox;
import andrade.luis.librehmi.views.SizeVBox;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

/**
 * Ventana de definición de propiedades del slider
 */
public class SetSliderPropertiesWindow extends WriteExpressionWindow {
    private String rotationValue;
    private SizeVBox sizeVBox;
    private ToggleGroup radioGroup;
    private RotationHBox rotationHBox;

    public RadioButton getHorizontalRadioButton() {
        return horizontalRadioButton;
    }

    public RadioButton getVerticalRadioButton() {
        return verticalRadioButton;
    }

    private RadioButton horizontalRadioButton;
    private RadioButton verticalRadioButton;

    public TextField getRotationTextField() {
        return rotationHBox.getRotationTextField();
    }

    private TextField minValueField;

    private TextField maxValueField;
    private TextField majorTickField;
    private TextField minorTickField;
    private CheckBox snapHandleToTick;
    private CheckBox showTicks;
    private CheckBox showLabelsTicks;

    /**
     * Constructor de la ventana
     * @param width Ancho de la ventana
     * @param height Alto de la ventana
     */
    public SetSliderPropertiesWindow(double width, double height) {
        super(415, 475);
        this.init(width, height);
    }

    /**
     * Permite inicializar los campos de la ventana
     * @param width Ancho de la ventana
     * @param height Alto de la ventana
     */
    private void init(double width, double height) {
        textField.setEditable(false);
        this.setTitle("Propiedades del Slider");
        Label minValueLabel = new Label("Defina el valor mínimo:");
        minValueField = new TextField("0");
        HBox minValueHBox = new HBox(minValueLabel, minValueField);
        minValueHBox.setSpacing(90);

        Label maxValueLabel = new Label("Defina el valor máximo:");
        maxValueField = new TextField("100");
        HBox maxValueHBox = new HBox(maxValueLabel, maxValueField);
        maxValueHBox.setSpacing(89);

        Label majorTickLabel = new Label("Defina el valor de la marca mayor:");
        majorTickField = new TextField("10");
        HBox majorTickHBox = new HBox(majorTickLabel, majorTickField);
        majorTickHBox.setSpacing(25);

        Label minorTickLabel = new Label("Defina el valor de la marca menor:");
        minorTickField = new TextField("5");
        HBox minorTickHBox = new HBox(minorTickLabel, minorTickField);
        minorTickHBox.setSpacing(23);

        snapHandleToTick = new CheckBox("Ajustar el indicador a las marcas");
        showTicks = new CheckBox("Mostrar marcas");
        showTicks.setSelected(true);
        showLabelsTicks = new CheckBox("Mostrar etiquetas en las marcas");
        showLabelsTicks.setSelected(true);

        sizeVBox = new SizeVBox(width, height, -1, -1);
        sizeVBox.getWidthValueHBox().setSpacing(135);
        sizeVBox.getHeightValueHBox().setSpacing(149);
        sizeVBox.setSpacing(5);

        rotationHBox = new RotationHBox(this.rotationValue);
        rotationHBox.setSpacing(196);

        Label orientationLabel = new Label("Seleccione la orientación:");
        horizontalRadioButton = new RadioButton("Horizontal");
        horizontalRadioButton.setId(String.valueOf(CanvasOrientation.HORIZONTAL));
        horizontalRadioButton.setSelected(true);
        verticalRadioButton = new RadioButton("Vertical");
        verticalRadioButton.setId(String.valueOf(CanvasOrientation.VERTICAL));
        radioGroup = new ToggleGroup();

        horizontalRadioButton.setToggleGroup(radioGroup);
        verticalRadioButton.setToggleGroup(radioGroup);
        HBox radioButtons = new HBox();
        radioButtons.getChildren().addAll(horizontalRadioButton, verticalRadioButton);
        radioButtons.setSpacing(5);

        this.getFloatPrecisionHBox().setSpacing(91);

        HBox orientationHBox = new HBox(orientationLabel, radioButtons);
        orientationHBox.setSpacing(75);

        this.getVbox().getChildren().add(2, snapHandleToTick);
        this.getVbox().getChildren().add(3, showTicks);
        this.getVbox().getChildren().add(4, showLabelsTicks);
        this.getVbox().getChildren().add(5, orientationHBox);
        this.getVbox().getChildren().add(6, minValueHBox);
        this.getVbox().getChildren().add(7, maxValueHBox);
        this.getVbox().getChildren().add(8, majorTickHBox);
        this.getVbox().getChildren().add(9, minorTickHBox);
        this.getVbox().getChildren().add(10, sizeVBox);
        this.getVbox().getChildren().add(11, rotationHBox);
        this.getVbox().setSpacing(5);
        this.setInputMode(true);
    }

    /**
     * Permite añadir un tag del tipo de escritura
     */
    @Override
    protected void addTag() {
        textField.setText(super.updateInputExpression(this.isInputMode(), "numbers", false,textField.getText()));

    }

    /**
     * Permite determinar la orientación seleccionada
     * @return CanvasOrientation con la orientación seleccionada
     */
    public CanvasOrientation getSelectedOrientation() {
        RadioButton selectedRadioButton = (RadioButton) radioGroup.getSelectedToggle();
        if (selectedRadioButton != null) {
            switch (selectedRadioButton.getText()) {
                case "Vertical":
                    return CanvasOrientation.VERTICAL;
                case "Horizontal en reversa":
                    return CanvasOrientation.HORIZONTAL_REVERSED;
                case "Vertical en reversa":
                    return CanvasOrientation.VERTICAL_REVERSED;
                default:
                    return CanvasOrientation.HORIZONTAL;
            }
        }
        return CanvasOrientation.HORIZONTAL;
    }

    public TextField getMinValueField() {
        return minValueField;
    }

    public TextField getMaxValueField() {
        return maxValueField;
    }

    public TextField getMajorTickField() {
        return majorTickField;
    }

    public TextField getMinorTickField() {
        return minorTickField;
    }

    public CheckBox getSnapHandleToTick() {
        return snapHandleToTick;
    }

    public CheckBox getShowTicks() {
        return showTicks;
    }

    public CheckBox getShowLabelsTicks() {
        return showLabelsTicks;
    }

    public SizeVBox getSizeVBox() {
        return sizeVBox;
    }

}
