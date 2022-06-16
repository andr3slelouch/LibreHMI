package andrade.luis.hmiethernetip.views.windows;

import andrade.luis.hmiethernetip.models.Tag;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

import java.util.ArrayList;

public class SetVisibilityAnimationWindow extends WriteExpressionWindow{
    private RadioButton falseRadioButton;
    private RadioButton trueRadioButton;

    public ToggleGroup getToggleGroup() {
        return toggleGroup;
    }

    public void setToggleGroup(ToggleGroup toggleGroup) {
        this.toggleGroup = toggleGroup;
    }

    private ToggleGroup toggleGroup;

    @Override
    public ArrayList<Tag> getLocalTags() {
        return localTags;
    }

    @Override
    public void setLocalTags(ArrayList<Tag> localTags) {
        this.localTags = localTags;
    }

    private ArrayList<Tag> localTags;

    public SetVisibilityAnimationWindow(){
        Label visibilityLabel = new Label("Visible si:");
        toggleGroup = new ToggleGroup();
        trueRadioButton = new RadioButton("Verdadero");
        trueRadioButton.setToggleGroup(toggleGroup);
        trueRadioButton.setSelected(true);
        falseRadioButton = new RadioButton("Falso");
        falseRadioButton.setToggleGroup(toggleGroup);
        HBox visibilityHBox = new HBox();
        visibilityHBox.getChildren().addAll(visibilityLabel,trueRadioButton,falseRadioButton);

        this.getVbox().getChildren().add(2,visibilityHBox);
        this.finishSelectionButton.setOnAction(mouseEvent -> finishingAction());
    }

    @Override
    public void finishingAction(){
        if(!this.textField.getText().isEmpty() && this.getLocalExpression()!=null){
            if(this.getLocalExpression().getResultType().equals("Bool")){
                this.confirmExit(Alert.AlertType.ERROR, "Error de tipo de Expresión","La expresión debe tener un resultado del tipo Bool");
            }
        }
        super.finishingAction();
    }
    public RadioButton getFalseRadioButton() {
        return falseRadioButton;
    }

    public void setFalseRadioButton(RadioButton falseRadioButton) {
        this.falseRadioButton = falseRadioButton;
    }

    public RadioButton getTrueRadioButton() {
        return trueRadioButton;
    }

    public void setTrueRadioButton(RadioButton trueRadioButton) {
        this.trueRadioButton = trueRadioButton;
    }
}
