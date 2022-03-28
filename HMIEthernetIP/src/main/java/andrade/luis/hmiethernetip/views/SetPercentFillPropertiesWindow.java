package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.models.canvas.CanvasColor;
import andrade.luis.hmiethernetip.models.PercentFillOrientation;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;


public class SetPercentFillPropertiesWindow extends WriteExpressionWindow {
    public CanvasColor getPrimaryColor() {
        return primaryColor;
    }

    public CanvasColor getBackgroundColor() {
        return backgroundColor;
    }

    private ToggleGroup radioGroup;
    private CanvasColor primaryColor = new CanvasColor(Color.BLACK);
    private CanvasColor backgroundColor = new CanvasColor(Color.GREEN);

    public TextField getMinValueField() {
        return minValueField;
    }

    public TextField getMaxValueField() {
        return maxValueField;
    }

    private TextField minValueField;
    private TextField maxValueField;

    public SetPercentFillPropertiesWindow() {
        super(750, 300);
        this.init(Color.BLACK, Color.GREEN);
    }

    public SetPercentFillPropertiesWindow(Color primaryColor, Color backgroundColor) {
        super(750, 300);
        this.primaryColor = new CanvasColor(primaryColor);
        this.backgroundColor = new CanvasColor(backgroundColor);
        this.init(this.primaryColor.getColor(), this.backgroundColor.getColor());
    }

    private void init(Color primary, Color background) {
        HBox primaryHBox = new HBox();
        Label primaryLabel = new Label("Seleccione el color primario:");
        ColorPicker primaryColorPicker = new ColorPicker(primary);
        primaryColorPicker.setOnAction(actionEvent -> primaryColor = new CanvasColor(primaryColorPicker.getValue()));
        primaryHBox.getChildren().addAll(primaryLabel, primaryColorPicker);

        HBox backgroundHBox = new HBox();
        Label backgroundLabel = new Label("Seleccione el color de fondo:");
        ColorPicker backgroundColorPicker = new ColorPicker(background);
        backgroundColorPicker.setOnAction(actionEvent -> backgroundColor = new CanvasColor(backgroundColorPicker.getValue()));
        backgroundHBox.getChildren().addAll(backgroundLabel, backgroundColorPicker);

        Label orientationLabel = new Label("Seleccione la orientación:");
        RadioButton horizontalRadioButton = new RadioButton("Horizontal");
        horizontalRadioButton.setId(String.valueOf(PercentFillOrientation.HORIZONTAL));
        RadioButton verticalRadioButton = new RadioButton("Vertical");
        verticalRadioButton.setId(String.valueOf(PercentFillOrientation.VERTICAL));
        RadioButton horizontalReversedRadioButton = new RadioButton("Horizontal en reversa");
        horizontalReversedRadioButton.setId(String.valueOf(PercentFillOrientation.HORIZONTAL_REVERSED));
        RadioButton verticalReversedRadioButton = new RadioButton("Vertical en reversa");
        verticalReversedRadioButton.setId(String.valueOf(PercentFillOrientation.VERTICAL_REVERSED));
        radioGroup = new ToggleGroup();

        horizontalRadioButton.setToggleGroup(radioGroup);
        verticalRadioButton.setToggleGroup(radioGroup);
        horizontalReversedRadioButton.setToggleGroup(radioGroup);
        verticalReversedRadioButton.setToggleGroup(radioGroup);

        HBox orientationHBox = new HBox(orientationLabel, horizontalRadioButton, verticalRadioButton, horizontalReversedRadioButton, verticalReversedRadioButton);
        orientationHBox.setSpacing(20);

        Label minValueLabel = new Label("Defina el valor mínimo:");
        minValueField = new TextField("0");
        minValueField.setTooltip(new Tooltip("El valor mínimo(así como todos los que sean menores) se tomarán como 0%"));
        HBox minValueHBox = new HBox(minValueLabel, minValueField);
        minValueHBox.setSpacing(25);

        Label maxValueLabel = new Label("Defina el valor máximo:");
        maxValueField = new TextField("100");
        maxValueField.setTooltip(new Tooltip("El valor máximo(así como todos los que sean mayores) se tomarán como 100%"));
        HBox maxValueHBox = new HBox(maxValueLabel, maxValueField);
        maxValueHBox.setSpacing(25);

        this.getVbox().getChildren().add(2, primaryHBox);
        this.getVbox().getChildren().add(3, backgroundHBox);
        this.getVbox().getChildren().add(4, orientationHBox);
        this.getVbox().getChildren().add(5, minValueHBox);
        this.getVbox().getChildren().add(6, maxValueHBox);
        this.getVbox().setSpacing(5);

        this.finishSelectionButton.setOnAction(actionEvent -> this.finishingAction());
    }

    public PercentFillOrientation getSelectedOrientation() {
        RadioButton selectedRadioButton = (RadioButton) radioGroup.getSelectedToggle();
        if (selectedRadioButton != null) {
            switch (selectedRadioButton.getText()) {
                case "Vertical":
                    return PercentFillOrientation.VERTICAL;
                case "Horizontal en reversa":
                    return PercentFillOrientation.HORIZONTAL_REVERSED;
                case "Vertical en reversa":
                    return PercentFillOrientation.VERTICAL_REVERSED;
                default:
                    return PercentFillOrientation.HORIZONTAL;
            }
        }
        return PercentFillOrientation.HORIZONTAL;
    }

    public void setSelectedOrientation(String selected) {
        for (int i = 0; i < radioGroup.getToggles().size(); i++) {
            if (((RadioButton) radioGroup.getToggles().get(i)).getId().toLowerCase().equals(selected)) {
                radioGroup.getToggles().get(i).setSelected(true);
            }
        }
    }

    public double getMinValue() {
        if (minValueField.getText().isEmpty()) {
            return 0;
        } else {
            return Double.parseDouble(minValueField.getText());
        }
    }

    public double getMaxValue() {
        if (maxValueField.getText().isEmpty()) {
            return 100;
        } else {
            return Double.parseDouble(maxValueField.getText());
        }
    }
}
