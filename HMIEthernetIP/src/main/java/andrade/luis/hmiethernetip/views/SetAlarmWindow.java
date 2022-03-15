package andrade.luis.hmiethernetip.views;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.DoubleStringConverter;

import java.util.function.UnaryOperator;

public class SetAlarmWindow extends WriteExpressionWindow{
    private final HBox doubleLimitHBox;
    private final HBox booleanConditionsHBox;
    private TextField alarmCommentTF;
    private TextField hiHiLimitTF;
    private TextField highLimitTF;
    private TextField loloLimitTF;
    private TextField lowLimitTF;
    private ToggleGroup toggleGroup;

    public SetAlarmWindow() {
        super(750, 250);

        textField.textProperty().addListener((observableValue,oldValue,newValue) ->{
            deleteUnneededTags();
            if(prepareExpression(false)){
                this.getFloatPrecisionTextField().setDisable(!this.getLocalExpression().getResultType().equals("Flotante"));
                enableInputs(this.getLocalExpression().getResultType());
            }
        });

        Label alarmComment = new Label("Comentario de Alarma:");
        alarmCommentTF = new TextField();
        HBox alarmCommentHBox = new HBox();
        alarmCommentHBox.getChildren().addAll(alarmComment,alarmCommentTF);

        CheckBox hiHiLimitCheckBox = new CheckBox("HiHi");
        hiHiLimitTF = new TextField("0");
        UnaryOperator<TextFormatter.Change> numberFilter = change -> {
            String newText = change.getControlNewText();
            if (!newText.matches("^(\\+|-)?\\d+\\.\\d+$")) {
                change.setText("");
                change.setRange(change.getRangeStart(), change.getRangeStart());
            }
            return change;
        };
        hiHiLimitTF.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, numberFilter));
        hiHiLimitTF.setDisable(true);
        hiHiLimitCheckBox.selectedProperty().addListener((observableValue, oldBoolean, newBoolean) -> hiHiLimitTF.setDisable(!Boolean.TRUE.equals(newBoolean)));
        HBox hiHiLimitHBox = new HBox();
        hiHiLimitHBox.getChildren().addAll(hiHiLimitCheckBox,hiHiLimitTF);
        hiHiLimitHBox.setSpacing(10);

        CheckBox highLimitCheckBox = new CheckBox("High");
        highLimitTF = new TextField("0");
        highLimitTF.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, numberFilter));
        highLimitTF.setDisable(true);
        highLimitCheckBox.selectedProperty().addListener((observableValue, oldBoolean, newBoolean) -> highLimitTF.setDisable(!Boolean.TRUE.equals(newBoolean)));
        HBox highLimitHBox = new HBox();
        highLimitHBox.getChildren().addAll(highLimitCheckBox,highLimitTF);
        highLimitHBox.setSpacing(6);

        CheckBox loloLimitCheckBox = new CheckBox("LoLo");
        loloLimitTF = new TextField("0");
        loloLimitTF.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, numberFilter));
        loloLimitTF.setDisable(true);
        loloLimitCheckBox.selectedProperty().addListener((observableValue, oldBoolean, newBoolean) -> loloLimitTF.setDisable(!Boolean.TRUE.equals(newBoolean)));
        HBox loloLimitHBox = new HBox();
        loloLimitHBox.getChildren().addAll(loloLimitCheckBox,loloLimitTF);
        loloLimitHBox.setSpacing(5);

        CheckBox lowLimitCheckBox = new CheckBox("Low");
        lowLimitTF = new TextField("0");
        lowLimitTF.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, numberFilter));
        lowLimitTF.setDisable(true);
        lowLimitCheckBox.selectedProperty().addListener((observableValue, oldBoolean, newBoolean) -> lowLimitTF.setDisable(!Boolean.TRUE.equals(newBoolean)));
        HBox lowLimitHBox = new HBox();
        lowLimitHBox.getChildren().addAll(lowLimitCheckBox,lowLimitTF);
        lowLimitHBox.setSpacing(10);

        VBox lowVBBox = new VBox();
        lowVBBox.getChildren().addAll(loloLimitHBox,lowLimitHBox);

        VBox highVBBox = new VBox();
        highVBBox.getChildren().addAll(highLimitHBox,hiHiLimitHBox);

        doubleLimitHBox = new HBox();
        doubleLimitHBox.getChildren().addAll(lowVBBox,highVBBox);
        doubleLimitHBox.setVisible(false);

        Label visibilityLabel = new Label("Alarma en el estado:");
        toggleGroup = new ToggleGroup();
        RadioButton trueRadioButton = new RadioButton("Verdadero");
        trueRadioButton.setToggleGroup(toggleGroup);
        trueRadioButton.setSelected(true);
        RadioButton falseRadioButton = new RadioButton("Falso");
        falseRadioButton.setToggleGroup(toggleGroup);
        booleanConditionsHBox = new HBox();
        booleanConditionsHBox.getChildren().addAll(visibilityLabel,trueRadioButton,falseRadioButton);
        booleanConditionsHBox.setVisible(false);

        this.getVbox().getChildren().add(3,alarmCommentHBox);
        this.getVbox().getChildren().add(4,doubleLimitHBox);
        this.getVbox().getChildren().add(5,booleanConditionsHBox);
    }

    private void enableInputs(String type){
        if(type.equals("Flotante") || type.equals("Entero")){
            this.getVbox().getChildren().remove(doubleLimitHBox);
            this.getVbox().getChildren().remove(booleanConditionsHBox);
            this.getVbox().getChildren().add(4,doubleLimitHBox);
            this.getVbox().getChildren().add(5,booleanConditionsHBox);
            doubleLimitHBox.setVisible(true);
            booleanConditionsHBox.setVisible(false);
        }else if(type.equals("Bool")){
            this.getVbox().getChildren().remove(doubleLimitHBox);
            this.getVbox().getChildren().remove(booleanConditionsHBox);
            this.getVbox().getChildren().add(4,booleanConditionsHBox);
            this.getVbox().getChildren().add(5,doubleLimitHBox);
            doubleLimitHBox.setVisible(false);
            booleanConditionsHBox.setVisible(true);
        }else{
            doubleLimitHBox.setVisible(false);
            booleanConditionsHBox.setVisible(false);
        }
    }

    public TextField getAlarmCommentTF() {
        return alarmCommentTF;
    }

    public void setAlarmCommentTF(TextField alarmCommentTF) {
        this.alarmCommentTF = alarmCommentTF;
    }

    public TextField getHiHiLimitTF() {
        return hiHiLimitTF;
    }

    public void setHiHiLimitTF(TextField hiHiLimitTF) {
        this.hiHiLimitTF = hiHiLimitTF;
    }

    public TextField getHighLimitTF() {
        return highLimitTF;
    }

    public void setHighLimitTF(TextField highLimitTF) {
        this.highLimitTF = highLimitTF;
    }

    public TextField getLoloLimitTF() {
        return loloLimitTF;
    }

    public void setLoloLimitTF(TextField loloLimitTF) {
        this.loloLimitTF = loloLimitTF;
    }

    public TextField getLowLimitTF() {
        return lowLimitTF;
    }

    public void setLowLimitTF(TextField lowLimitTF) {
        this.lowLimitTF = lowLimitTF;
    }

    public ToggleGroup getToggleGroup() {
        return toggleGroup;
    }

    public void setToggleGroup(ToggleGroup toggleGroup) {
        this.toggleGroup = toggleGroup;
    }

}
