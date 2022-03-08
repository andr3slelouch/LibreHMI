package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.models.PercentFillOrientation;
import andrade.luis.hmiethernetip.models.Tag;
import andrade.luis.hmiethernetip.models.canvas.CanvasColor;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SetColorCommandPushButtonWindow extends WriteExpressionWindow {
    private final ToggleGroup radioGroup;
    private final ColorPicker primaryColorPicker;
    private final ColorPicker backgroundColorPicker;
    private final TextField buttonLabelTextField;
    private CanvasColor primaryColor = new CanvasColor(Color.BLACK);
    private CanvasColor backgroundColor = new CanvasColor(Color.GREEN);
    public SetColorCommandPushButtonWindow(){
        textField.setEditable(false);
        HBox buttonLabelHBox = new HBox();
        Label buttonLabel = new Label("Etiqueta del Botón:");
        buttonLabelTextField = new TextField("Button");
        buttonLabelHBox.getChildren().addAll(buttonLabel,buttonLabelTextField);

        HBox primaryHBox = new HBox();
        Label primaryLabel = new Label("Color en Verdadero:");
        primaryColorPicker = new ColorPicker(Color.GREEN);
        primaryColorPicker.setOnAction(actionEvent -> primaryColor = new CanvasColor(primaryColorPicker.getValue()));
        primaryHBox.getChildren().addAll(primaryLabel, primaryColorPicker);

        HBox backgroundHBox = new HBox();
        Label backgroundLabel = new Label("Color en Falso:");
        backgroundColorPicker = new ColorPicker(Color.RED);
        backgroundColorPicker.setOnAction(actionEvent -> backgroundColor = new CanvasColor(backgroundColorPicker.getValue()));
        backgroundHBox.getChildren().addAll(backgroundLabel, backgroundColorPicker);

        Label actionLabel = new Label("Modo de Acción:");
        RadioButton toggleRadioButton = new RadioButton("Toggle");
        toggleRadioButton.setSelected(true);
        RadioButton directRadioButton = new RadioButton("Directo");
        RadioButton reverseRadioButton = new RadioButton("Reversa");

        radioGroup = new ToggleGroup();

        toggleRadioButton.setToggleGroup(radioGroup);
        directRadioButton.setToggleGroup(radioGroup);
        reverseRadioButton.setToggleGroup(radioGroup);

        HBox orientationHBox = new HBox(actionLabel, toggleRadioButton, directRadioButton, reverseRadioButton);
        orientationHBox.setSpacing(20);

        this.getVbox().getChildren().add(2, buttonLabelHBox);
        this.getVbox().getChildren().add(3, primaryHBox);
        this.getVbox().getChildren().add(4, backgroundHBox);
        this.getVbox().getChildren().add(5, orientationHBox);
        this.getVbox().setSpacing(5);
        this.setInputMode(true);

        this.finishSelectionButton.setOnAction(actionEvent -> this.finishingAction());
    }
    //private init(Color trueColor, Color falseColor, )
    public ToggleGroup getRadioGroup() {
        return radioGroup;
    }

    public ColorPicker getPrimaryColorPicker() {
        return primaryColorPicker;
    }

    public ColorPicker getBackgroundColorPicker() {
        return backgroundColorPicker;
    }

    public TextField getButtonLabelTextField() {
        return buttonLabelTextField;
    }

    @Override
    protected void addTag(){
        SelectTagWindow selectTagWindow = new SelectTagWindow(this.isInputMode(),true,false);
        selectTagWindow.showAndWait();
        if(!selectTagWindow.isCancelled()){
            clearAll();
            Tag tag = selectTagWindow.getSelectedTag();
            if(tag!=null){
                this.getAddedTags().add(tag);
                textField.setText(textField.getText()+tag.getName());
            }
        }
    }
}