package andrade.luis.librehmi.views.windows;

import andrade.luis.librehmi.models.Tag;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.ArrayList;

public class SetTagInputPropertiesWindow extends WriteExpressionWindow{
    public TextField getMinValueField() {
        return minValueField;
    }

    public TextField getMaxValueField() {
        return maxValueField;
    }

    public String getType() {
        for (RadioButton radioButton : radioButtons) {
            if (radioButton.isSelected()) {
                type = radioButton.getText();
            }
        }
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private TextField minValueField;
    private TextField maxValueField;
    private String type = "String";
    private ArrayList<RadioButton> radioButtons;

    public SetTagInputPropertiesWindow(){
        super(750, 320);
        this.init();
    }
    private void init(){
        textField.setEditable(false);
        Label minValueLabel = new Label("Defina el valor mínimo:");
        minValueField = new TextField("0");
        HBox minValueHBox = new HBox(minValueLabel, minValueField);
        minValueHBox.setSpacing(17);

        Label maxValueLabel = new Label("Defina el valor máximo:");
        maxValueField = new TextField("100");
        HBox maxValueHBox = new HBox(maxValueLabel, maxValueField);
        maxValueHBox.setSpacing(15);

        final ToggleGroup group = new ToggleGroup();

        Label typeLabel = new Label("Defina el tipo de entrada:");

        radioButtons = new ArrayList<>();
        RadioButton rb1 = new RadioButton("Entero");
        rb1.setToggleGroup(group);
        rb1.setSelected(true);
        radioButtons.add(rb1);

        RadioButton rb2 = new RadioButton("Flotante");
        rb2.setToggleGroup(group);
        radioButtons.add(rb2);

        RadioButton rb3 = new RadioButton("String");
        rb3.setToggleGroup(group);
        radioButtons.add(rb3);

        RadioButton rb4 = new RadioButton("Bool");
        rb4.setOnAction(mouseEvent -> disableMaxMinInputs(true));
        rb4.setToggleGroup(group);
        radioButtons.add(rb4);

        HBox defineTypeVBox = new HBox();
        defineTypeVBox.getChildren().addAll(typeLabel,rb1,rb2,rb4,rb3);
        defineTypeVBox.setSpacing(10);

        getFloatPrecisionHBox().setSpacing(17);

        this.getVbox().getChildren().add(2,minValueHBox);
        this.getVbox().getChildren().add(3,maxValueHBox);
        this.getVbox().getChildren().add(5,defineTypeVBox);
        this.getVbox().setSpacing(5);
        this.setInputMode(true);
        textField.textProperty().addListener((observableValue,oldValue,newValue) ->{
            if(prepareExpression(false)){
                getFloatPrecisionTextField().setDisable(!this.getLocalExpression().getResultType().equals("Flotante"));
                maxValueField.setDisable(this.getLocalExpression().getResultType().equals("Bool"));
                minValueField.setDisable(this.getLocalExpression().getResultType().equals("Bool"));
            }
        });


    }

    private void disableMaxMinInputs(boolean disable) {
        minValueField.setDisable(disable);
        maxValueField.setDisable(disable);
    }

    public void setSelectedRadioButton(String buttonName) {
        for (RadioButton radioButton : radioButtons) {
            if (radioButton.getText().equals(buttonName)) {
                radioButton.setSelected(true);
            }
        }
    }

    @Override
    protected void addTag(){
        SelectTagWindow selectTagWindow = new SelectTagWindow(this.isInputMode(),"",false,this.getLocalTags());
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
