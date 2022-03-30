package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.models.CanvasOrientation;
import andrade.luis.hmiethernetip.models.SizeVBox;
import andrade.luis.hmiethernetip.models.Tag;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.converter.DoubleStringConverter;

import java.util.function.UnaryOperator;

public class SetSliderPropertiesWindow extends WriteExpressionWindow {
    private String rotationValue;
    private SizeVBox sizeVBox;
    private ToggleGroup radioGroup;

    public RadioButton getHorizontalRadioButton() {
        return horizontalRadioButton;
    }

    public void setHorizontalRadioButton(RadioButton horizontalRadioButton) {
        this.horizontalRadioButton = horizontalRadioButton;
    }

    public RadioButton getVerticalRadioButton() {
        return verticalRadioButton;
    }

    public void setVerticalRadioButton(RadioButton verticalRadioButton) {
        this.verticalRadioButton = verticalRadioButton;
    }

    private RadioButton horizontalRadioButton;
    private RadioButton verticalRadioButton;

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

    private TextField minValueField;
    private TextField rotationTextField;
    private TextField maxValueField;
    private TextField majorTickField;
    private TextField minorTickField;
    private CheckBox snapHandleToTick;
    private CheckBox showTicks;
    private CheckBox showLabelsTicks;
    private final UnaryOperator<TextFormatter.Change> numberFilter = change -> {
        String newText = change.getControlNewText();
        if (!newText.matches("^(\\+|-)?\\d+\\.\\d+$")) {
            change.setText("");
            change.setRange(change.getRangeStart(), change.getRangeStart());
        }
        return change;
    };

    public SetSliderPropertiesWindow(double width, double height) {
        super(415, 450);
        this.init(width, height);
    }

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

        Label rotationLabel = new Label("Rotar:");
        rotationTextField = new TextField("0");
        rotationTextField.setPrefWidth(116);
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
        rotationInputHBox.getChildren().addAll(rotationTextField, rotationButton);
        HBox rotationHBox = new HBox();
        rotationHBox.getChildren().addAll(rotationLabel, rotationInputHBox);
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
        radioButtons.getChildren().addAll(horizontalRadioButton,verticalRadioButton);
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

    @Override
    protected void addTag() {
        SelectTagWindow selectTagWindow = new SelectTagWindow(this.isInputMode(), "numbers", false);
        selectTagWindow.showAndWait();
        if (!selectTagWindow.isCancelled()) {
            clearAll();
            Tag tag = selectTagWindow.getSelectedTag();
            if (tag != null) {
                this.getAddedTags().add(tag);
                textField.setText(textField.getText() + tag.getName());
            }
        }
    }

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

    public void setSelectedOrientation(String selected) {
        for (int i = 0; i < radioGroup.getToggles().size(); i++) {
            if (((RadioButton) radioGroup.getToggles().get(i)).getId().toLowerCase().equals(selected)) {
                radioGroup.getToggles().get(i).setSelected(true);
            }
        }
    }

    public TextField getMinValueField() {
        return minValueField;
    }

    public void setMinValueField(TextField minValueField) {
        this.minValueField = minValueField;
    }

    public TextField getMaxValueField() {
        return maxValueField;
    }

    public void setMaxValueField(TextField maxValueField) {
        this.maxValueField = maxValueField;
    }

    public TextField getMajorTickField() {
        return majorTickField;
    }

    public void setMajorTickField(TextField majorTickField) {
        this.majorTickField = majorTickField;
    }

    public TextField getMinorTickField() {
        return minorTickField;
    }

    public void setMinorTickField(TextField minorTickField) {
        this.minorTickField = minorTickField;
    }

    public CheckBox getSnapHandleToTick() {
        return snapHandleToTick;
    }

    public void setSnapHandleToTick(CheckBox snapHandleToTick) {
        this.snapHandleToTick = snapHandleToTick;
    }

    public CheckBox getShowTicks() {
        return showTicks;
    }

    public void setShowTicks(CheckBox showTicks) {
        this.showTicks = showTicks;
    }

    public CheckBox getShowLabelsTicks() {
        return showLabelsTicks;
    }

    public void setShowLabelsTicks(CheckBox showLabelsTicks) {
        this.showLabelsTicks = showLabelsTicks;
    }

    public SizeVBox getSizeVBox() {
        return sizeVBox;
    }

    public void setSizeVBox(SizeVBox sizeVBox) {
        this.sizeVBox = sizeVBox;
    }
}
