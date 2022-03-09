package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.models.Tag;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class SetTextFieldPropertiesWindow extends WriteExpressionWindow{
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private TextField minValueField;
    private TextField maxValueField;
    private String type = "String";
    private ArrayList<RadioButton> radioButtons;

    public SetTextFieldPropertiesWindow(){
        super(750, 275);
        this.init();
    }
    private void init(){
        textField.setEditable(false);
        Label minValueLabel = new Label("Defina el valor mínimo:");
        minValueField = new TextField("0");
        HBox minValueHBox = new HBox(minValueLabel, minValueField);
        minValueHBox.setSpacing(25);

        Label maxValueLabel = new Label("Defina el valor máximo:");
        maxValueField = new TextField("100");
        HBox maxValueHBox = new HBox(maxValueLabel, maxValueField);
        maxValueHBox.setSpacing(25);

        final ToggleGroup group = new ToggleGroup();

        Label typeLabel = new Label("Defina el tipo de entrada:");

        radioButtons = new ArrayList<>();
        RadioButton rb1 = new RadioButton("Entero");
        rb1.setToggleGroup(group);
        rb1.setSelected(true);
        rb1.selectedProperty().addListener((observableValue, oldBoolean, newBoolean) -> {
            disableMaxMinInputs(!newBoolean);
            if(Boolean.TRUE.equals(newBoolean)){
                type = rb1.getText();
            }
        });
        radioButtons.add(rb1);

        RadioButton rb2 = new RadioButton("Flotante");
        rb2.setToggleGroup(group);
        rb2.selectedProperty().addListener((observableValue, oldBoolean, newBoolean) -> {
            disableMaxMinInputs(!newBoolean);
            if(Boolean.TRUE.equals(newBoolean)){
                type = rb1.getText();
            }
        });
        radioButtons.add(rb2);

        RadioButton rb3 = new RadioButton("String");
        rb3.setToggleGroup(group);
        rb3.selectedProperty().addListener((observableValue, oldBoolean, newBoolean) -> {
            disableMaxMinInputs(newBoolean);
            if(Boolean.TRUE.equals(newBoolean)){
                type = rb1.getText();
            }
        });
        radioButtons.add(rb3);

        RadioButton rb4 = new RadioButton("Bool");
        rb4.selectedProperty().addListener((observableValue, oldBoolean, newBoolean) -> {
            disableMaxMinInputs(newBoolean);
            if(Boolean.TRUE.equals(newBoolean)){
                type = rb1.getText();
            }
        });
        rb4.setOnAction(mouseEvent -> disableMaxMinInputs(true));
        rb4.setToggleGroup(group);
        radioButtons.add(rb4);

        VBox defineTypeVBox = new VBox();
        defineTypeVBox.getChildren().addAll(typeLabel,rb1,rb2,rb4,rb3);

        this.getVbox().getChildren().add(2,minValueHBox);
        this.getVbox().getChildren().add(3,maxValueHBox);
        this.getVbox().getChildren().add(4,defineTypeVBox);
        this.getVbox().setSpacing(5);
        this.setInputMode(true);

        Button clearAllButton = new Button("Quitar Tag");

        this.getButtonsHBox().getChildren().add(0,clearAllButton);
        clearAllButton.setOnAction(mouseEvent -> clearAll());
    }

    private void disableMaxMinInputs(boolean disable) {
        minValueField.setDisable(disable);
        maxValueField.setDisable(disable);
    }

    @Override
    protected void addTag(){
        SelectTagWindow selectTagWindow = new SelectTagWindow(this.isInputMode(),"",false);
        selectTagWindow.showAndWait();
        if(!selectTagWindow.isCancelled()){
            clearAll();
            Tag tag = selectTagWindow.getSelectedTag();
            if(tag!=null){
                this.getAddedTags().add(tag);
                textField.setText(textField.getText()+tag.getName());
                for (RadioButton radioButton : radioButtons) {
                    if (radioButton.getText().equals(tag.getType())) {
                        radioButton.setSelected(true);
                    }
                }
            }
        }
    }
}
