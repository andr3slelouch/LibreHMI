package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.models.CanvasColor;
import andrade.luis.hmiethernetip.models.PercentFillOrientation;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class SetPercentFillPropertiesWindow extends WriteExpressionWindow{
    public CanvasColor getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(CanvasColor primaryColor) {
        this.primaryColor = primaryColor;
    }

    public CanvasColor getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(CanvasColor backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public ColorPicker getPrimaryColorPicker() {
        return primaryColorPicker;
    }

    public void setPrimaryColorPicker(ColorPicker primaryColorPicker) {
        this.primaryColorPicker = primaryColorPicker;
    }

    public ColorPicker getBackgroundColorPicker() {
        return backgroundColorPicker;
    }

    public void setBackgroundColorPicker(ColorPicker backgroundColorPicker) {
        this.backgroundColorPicker = backgroundColorPicker;
    }

    private ToggleGroup radioGroup;
    private ColorPicker primaryColorPicker;
    private ColorPicker backgroundColorPicker;
    private CanvasColor primaryColor = new CanvasColor(Color.BLACK);
    private CanvasColor backgroundColor = new CanvasColor(Color.GREEN);
    public SetPercentFillPropertiesWindow(){
        super(750,250);
        this.init(Color.BLACK,Color.GREEN);
    }
    public SetPercentFillPropertiesWindow(Color primaryColor,Color backgroundColor){
        super(750,250);
        this.primaryColor = new CanvasColor(primaryColor);
        this.backgroundColor = new CanvasColor(backgroundColor);
        this.init(this.primaryColor.getColor(),this.backgroundColor.getColor());
    }

    private void init(Color primary, Color background){
        HBox primaryHBox = new HBox();
        Label primaryLabel = new Label("Seleccione el color primario:");
        primaryColorPicker = new ColorPicker(primary);
        primaryColorPicker.setOnAction(actionEvent -> primaryColor = new CanvasColor(primaryColorPicker.getValue()));
        primaryHBox.getChildren().addAll(primaryLabel,primaryColorPicker);

        HBox backgroundHBox = new HBox();
        Label backgroundLabel = new Label("Seleccione el color de fondo:");
        backgroundColorPicker = new ColorPicker(background);
        backgroundColorPicker.setOnAction(actionEvent -> backgroundColor = new CanvasColor(backgroundColorPicker.getValue()));
        backgroundHBox.getChildren().addAll(backgroundLabel,backgroundColorPicker);

        Label orientationLabel = new Label("Seleccione la orientación:");
        RadioButton horizontalRadioButton = new RadioButton("Horizontal");
        RadioButton verticalRadioButton = new RadioButton("Vertical");
        RadioButton horizontalReversedRadioButton = new RadioButton("Horizontal en reversa");
        RadioButton verticalReversedRadioButton = new RadioButton("Vertical en reversa");
        radioGroup = new ToggleGroup();

        horizontalRadioButton.setToggleGroup(radioGroup);
        verticalRadioButton.setToggleGroup(radioGroup);
        horizontalReversedRadioButton.setToggleGroup(radioGroup);
        verticalReversedRadioButton.setToggleGroup(radioGroup);

        HBox orientationHBox = new HBox(orientationLabel,horizontalRadioButton, verticalRadioButton, horizontalReversedRadioButton, verticalReversedRadioButton);
        orientationHBox.setSpacing(20);

        Label minValueLabel = new Label("Defina el valor mínimo:");
        TextField minValueField = new TextField("0");
        minValueField.setTooltip(new Tooltip("El valor mínimo(así como todos los que sean menores) se tomarán como 0%"));
        HBox minValueHBox = new HBox(minValueLabel,minValueField);
        minValueHBox.setSpacing(25);

        Label maxValueLabel = new Label("Defina el valor máximo:");
        TextField maxValueField = new TextField("100");
        maxValueField.setTooltip(new Tooltip("El valor máximo(así como todos los que sean mayores) se tomarán como 100%"));
        HBox maxValueHBox = new HBox(maxValueLabel,maxValueField);
        maxValueHBox.setSpacing(25);

        this.getVbox().getChildren().add(2,primaryHBox);
        this.getVbox().getChildren().add(3,backgroundHBox);
        this.getVbox().getChildren().add(4,orientationHBox);
        this.getVbox().getChildren().add(5,minValueHBox);
        this.getVbox().getChildren().add(6,maxValueHBox);
        this.getVbox().setSpacing(5);
    }

    public PercentFillOrientation getSelectedOrientation() {
        RadioButton selectedRadioButton = (RadioButton) radioGroup.getSelectedToggle();
        if(selectedRadioButton != null){
            switch (selectedRadioButton.getText()) {
                case "Horizontal":
                    return PercentFillOrientation.HORIZONTAL;
                case "Vertical":
                    return PercentFillOrientation.VERTICAL;
                case "Horizontal en reversa":
                    return PercentFillOrientation.HORIZONTAL_REVERSED;
                case "Vertical en reversa":
                    return PercentFillOrientation.VERTICAL_REVERSED;
            }
        }
        return PercentFillOrientation.HORIZONTAL;
    }

    public void setSelectedOrientation(String selected){
        for(int i=0;i<radioGroup.getToggles().size();i++){
            if(((RadioButton) radioGroup.getToggles().get(i)).getText().toLowerCase().equals(selected)){
                radioGroup.getToggles().get(i).setSelected(true);
            }
        }
    }
}
