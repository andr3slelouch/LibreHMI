package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.models.Tag;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class SetSliderOptionsWindow extends WriteExpressionWindow{
    private TextField minValueField;

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

    private TextField maxValueField;
    private TextField majorTickField;
    private TextField minorTickField;
    private CheckBox snapHandleToTick;
    private CheckBox showTicks;
    private CheckBox showLabelsTicks;

    public SetSliderOptionsWindow(){
        super(750,300);
        this.init();
    }

    private void init() {
        textField.setEditable(false);
        Label minValueLabel = new Label("Defina el valor mínimo:");
        minValueField = new TextField("0");
        HBox minValueHBox = new HBox(minValueLabel, minValueField);
        minValueHBox.setSpacing(25);

        Label maxValueLabel = new Label("Defina el valor máximo:");
        maxValueField = new TextField("100");
        HBox maxValueHBox = new HBox(maxValueLabel, maxValueField);
        maxValueHBox.setSpacing(25);

        Label majorTickLabel = new Label("Defina el valor de la marca mayor:");
        majorTickField = new TextField("10");
        HBox majorTickHBox = new HBox(majorTickLabel, majorTickField);
        majorTickHBox.setSpacing(25);

        Label minorTickLabel = new Label("Defina el valor de la marca menor:");
        minorTickField = new TextField("5");
        HBox minorTickHBox = new HBox(minorTickLabel, minorTickField);
        minorTickHBox.setSpacing(25);

        snapHandleToTick = new CheckBox("Ajustar el indicador a las marcas");
        showTicks = new CheckBox("Mostrar marcas");
        showTicks.setSelected(true);
        showLabelsTicks = new CheckBox("Mostrar etiquetas en las marcas");
        showLabelsTicks.setSelected(true);

        this.getVbox().getChildren().add(2,minValueHBox);
        this.getVbox().getChildren().add(3,maxValueHBox);
        this.getVbox().getChildren().add(4,majorTickHBox);
        this.getVbox().getChildren().add(5,minorTickHBox);
        this.getVbox().getChildren().add(6,snapHandleToTick);
        this.getVbox().getChildren().add(7,showTicks);
        this.getVbox().getChildren().add(8,showLabelsTicks);
        this.getVbox().setSpacing(5);
        this.setInputMode(true);

        Button clearAllButton = new Button("Quitar Tag");

        this.getButtonsHBox().getChildren().add(0,clearAllButton);
        clearAllButton.setOnAction(mouseEvent -> clearAll());
    }
    @Override
    protected void addTag(){
        SelectTagWindow selectTagWindow = new SelectTagWindow(this.isInputMode(),"numbers",false);
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
