package andrade.luis.libreHMI.views.windows;

import andrade.luis.libreHMI.HMIApp;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.DoubleStringConverter;

import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SetAlarmWindow extends WriteExpressionWindow {
    private final HBox doubleLimitHBox;
    private final HBox booleanConditionsHBox;
    private final RadioButton falseRadioButton;
    private final RadioButton trueRadioButton;
    private final TextField alarmNameTF;
    private CheckBox hiHiLimitCheckBox;
    private CheckBox highLimitCheckBox;
    private CheckBox loloLimitCheckBox;
    private CheckBox lowLimitCheckBox;
    private final TextField alarmCommentTF;
    private TextField hiHiLimitTF;
    private TextField highLimitTF;
    private TextField loloLimitTF;
    private TextField lowLimitTF;
    private final HMIApp hmiApp;
    private final UnaryOperator<TextFormatter.Change> numberFilter = change -> {
        String newText = change.getControlNewText();
        if (!newText.matches("^(\\+|-)?\\d+\\.\\d+$")) {
            change.setText("");
            change.setRange(change.getRangeStart(), change.getRangeStart());
        }
        return change;
    };

    public SetAlarmWindow(HMIApp hmiApp) {
        super(750, 300);
        setTitle("Crear alarma");
        this.hmiApp = hmiApp;
        setLocalTags(this.hmiApp.getLocalTags());
        textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            deleteUnneededTags();
            if (prepareExpression(false)) {
                this.getFloatPrecisionTextField().setDisable(!this.getLocalExpression().getResultType().equals("Flotante"));
                enableInputs(this.getLocalExpression().getResultType());
            }
        });

        Label alarmName = new Label("Nombre de Alarma:");
        alarmNameTF = new TextField();
        HBox alarmNameHBox = new HBox();
        alarmNameHBox.setSpacing(22);
        alarmNameHBox.getChildren().addAll(alarmName, alarmNameTF);

        Label alarmComment = new Label("Comentario de Alarma:");
        alarmCommentTF = new TextField();
        HBox alarmCommentHBox = new HBox();
        alarmCommentHBox.getChildren().addAll(alarmComment, alarmCommentTF);

        initHiHiLimitField();
        HBox hiHiLimitHBox = new HBox();
        hiHiLimitHBox.getChildren().addAll(hiHiLimitCheckBox, hiHiLimitTF);
        hiHiLimitHBox.setSpacing(10);

        initHighLimitField();
        HBox highLimitHBox = new HBox();
        highLimitHBox.getChildren().addAll(highLimitCheckBox, highLimitTF);
        highLimitHBox.setSpacing(6);

        initLoloLimitField();
        HBox loloLimitHBox = new HBox();
        loloLimitHBox.getChildren().addAll(loloLimitCheckBox, loloLimitTF);
        loloLimitHBox.setSpacing(5);

        initLowLimitField();
        HBox lowLimitHBox = new HBox();
        lowLimitHBox.getChildren().addAll(lowLimitCheckBox, lowLimitTF);
        lowLimitHBox.setSpacing(10);

        VBox lowVBBox = new VBox();
        lowVBBox.getChildren().addAll(loloLimitHBox, lowLimitHBox);

        VBox highVBBox = new VBox();
        highVBBox.getChildren().addAll(highLimitHBox, hiHiLimitHBox);

        doubleLimitHBox = new HBox();
        doubleLimitHBox.getChildren().addAll(lowVBBox, highVBBox);
        doubleLimitHBox.setVisible(false);

        Label visibilityLabel = new Label("Alarma en el estado:");
        ToggleGroup toggleGroup = new ToggleGroup();
        trueRadioButton = new RadioButton("Verdadero");
        trueRadioButton.setToggleGroup(toggleGroup);
        trueRadioButton.setSelected(true);
        falseRadioButton = new RadioButton("Falso");
        falseRadioButton.setToggleGroup(toggleGroup);
        booleanConditionsHBox = new HBox();
        booleanConditionsHBox.getChildren().addAll(visibilityLabel, trueRadioButton, falseRadioButton);
        booleanConditionsHBox.setVisible(false);

        this.getVbox().getChildren().add(3, alarmNameHBox);
        this.getVbox().getChildren().add(4, alarmCommentHBox);
        this.getVbox().getChildren().add(5, doubleLimitHBox);
        this.getVbox().getChildren().add(6, booleanConditionsHBox);
        this.finishSelectionButton.setOnAction(mouseEvent -> finishingAction());
    }

    private void initHiHiLimitField(){
        hiHiLimitCheckBox = new CheckBox("HiHi");
        hiHiLimitTF = new TextField("0");
        hiHiLimitTF.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, numberFilter));
        hiHiLimitTF.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (Double.parseDouble(newValue) < Double.parseDouble(highLimitTF.getText()) && highLimitCheckBox.isSelected()) {
                hiHiLimitTF.setText(oldValue);
            }
            if (Double.parseDouble(newValue) < Double.parseDouble(lowLimitTF.getText()) && lowLimitCheckBox.isSelected()) {
                hiHiLimitTF.setText(oldValue);
            }
            if (Double.parseDouble(newValue) < Double.parseDouble(loloLimitTF.getText()) && loloLimitCheckBox.isSelected()) {
                hiHiLimitTF.setText(oldValue);
            }
        });
        hiHiLimitTF.setDisable(true);
        hiHiLimitCheckBox.selectedProperty().addListener((observableValue, oldBoolean, newBoolean) -> hiHiLimitTF.setDisable(!Boolean.TRUE.equals(newBoolean)));
    }

    private void initHighLimitField(){
        highLimitCheckBox = new CheckBox("High");
        highLimitTF = new TextField("0");
        highLimitTF.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, numberFilter));
        highLimitTF.setDisable(true);
        highLimitTF.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (Double.parseDouble(newValue) > Double.parseDouble(hiHiLimitTF.getText()) && hiHiLimitCheckBox.isSelected()) {
                highLimitTF.setText(oldValue);
            }
            if (Double.parseDouble(newValue) < Double.parseDouble(lowLimitTF.getText()) && lowLimitCheckBox.isSelected()) {
                highLimitTF.setText(oldValue);
            }
            if (Double.parseDouble(newValue) < Double.parseDouble(loloLimitTF.getText()) && loloLimitCheckBox.isSelected()) {
                highLimitTF.setText(oldValue);
            }
        });
        highLimitCheckBox.selectedProperty().addListener((observableValue, oldBoolean, newBoolean) -> highLimitTF.setDisable(!Boolean.TRUE.equals(newBoolean)));
    }

    private void initLoloLimitField(){
        loloLimitCheckBox = new CheckBox("LoLo");
        loloLimitTF = new TextField("0");
        loloLimitTF.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, numberFilter));
        loloLimitTF.setDisable(true);
        loloLimitTF.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (Double.parseDouble(newValue) > Double.parseDouble(hiHiLimitTF.getText()) && hiHiLimitCheckBox.isSelected()) {
                loloLimitTF.setText(oldValue);
            }
            if (Double.parseDouble(newValue) > Double.parseDouble(highLimitTF.getText()) && highLimitCheckBox.isSelected()) {
                loloLimitTF.setText(oldValue);
            }
            if (Double.parseDouble(newValue) > Double.parseDouble(lowLimitTF.getText()) && lowLimitCheckBox.isSelected()) {
                loloLimitTF.setText(oldValue);
            }
        });
        loloLimitCheckBox.selectedProperty().addListener((observableValue, oldBoolean, newBoolean) -> loloLimitTF.setDisable(!Boolean.TRUE.equals(newBoolean)));
    }

    private void initLowLimitField(){
        lowLimitCheckBox = new CheckBox("Low");
        lowLimitTF = new TextField("0");
        lowLimitTF.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, numberFilter));
        lowLimitTF.setDisable(true);
        lowLimitTF.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (Double.parseDouble(newValue) > Double.parseDouble(hiHiLimitTF.getText()) && hiHiLimitCheckBox.isSelected()) {
                lowLimitTF.setText(oldValue);
            }
            if (Double.parseDouble(newValue) > Double.parseDouble(highLimitTF.getText()) && highLimitCheckBox.isSelected()) {
                lowLimitTF.setText(oldValue);
            }
            if (Double.parseDouble(newValue) < Double.parseDouble(loloLimitTF.getText()) && loloLimitCheckBox.isSelected()) {
                lowLimitTF.setText(oldValue);
            }
        });
        lowLimitCheckBox.selectedProperty().addListener((observableValue, oldBoolean, newBoolean) -> lowLimitTF.setDisable(!Boolean.TRUE.equals(newBoolean)));
    }

    @Override
    public void finishingAction() {
        if (
                (
                        Double.parseDouble(hiHiLimitTF.getText()) < Double.parseDouble(highLimitTF.getText())
                                ||
                                Double.parseDouble(hiHiLimitTF.getText()) < Double.parseDouble(lowLimitTF.getText())
                                ||
                                Double.parseDouble(hiHiLimitTF.getText()) < Double.parseDouble(loloLimitTF.getText())
                ) && hiHiLimitCheckBox.isSelected()
        ) {
            confirmExit(Alert.AlertType.ERROR, "Error en límites de HiHi", "El límite HiHi debe ser mayor que los demás límites");
        } else if (
                (
                        Double.parseDouble(highLimitTF.getText()) > Double.parseDouble(hiHiLimitTF.getText())
                                ||
                                Double.parseDouble(highLimitTF.getText()) < Double.parseDouble(lowLimitTF.getText())
                                ||
                                Double.parseDouble(highLimitTF.getText()) < Double.parseDouble(loloLimitTF.getText())
                ) && highLimitCheckBox.isSelected()
        ) {
            confirmExit(Alert.AlertType.ERROR, "Error en límites de High", "El límite High debe ser mayor que los límites Low y Lolo, así como menor que el límite HiHi");
        } else if (
                (
                        Double.parseDouble(lowLimitTF.getText()) > Double.parseDouble(hiHiLimitTF.getText())
                                ||
                                Double.parseDouble(lowLimitTF.getText()) > Double.parseDouble(highLimitTF.getText())
                                ||
                                Double.parseDouble(lowLimitTF.getText()) < Double.parseDouble(loloLimitTF.getText())
                ) && lowLimitCheckBox.isSelected()
        ) {
            confirmExit(Alert.AlertType.ERROR, "Error en límites de Low", "El límite Low debe ser mayor que el límite Lolo, así como menor que los límites HiHi y High");
        } else if (
                (
                        Double.parseDouble(loloLimitTF.getText()) > Double.parseDouble(hiHiLimitTF.getText())
                                ||
                                Double.parseDouble(loloLimitTF.getText()) > Double.parseDouble(highLimitTF.getText())
                                ||
                                Double.parseDouble(loloLimitTF.getText()) > Double.parseDouble(lowLimitTF.getText())
                ) && loloLimitCheckBox.isSelected()
        ) {
            confirmExit(Alert.AlertType.ERROR, "Error en límites de Lolo", "El límite Low debe ser menor que los demás límites");
        } else if(alarmNameTF.getText().isEmpty()){
            confirmExit(Alert.AlertType.ERROR, "Error en nombre de alarma", "El nombre de la alarma no debe estar vacío");
        } else if(hmiApp.getIndexForAlarm(alarmNameTF.getText()) != -1){
            confirmExit(Alert.AlertType.ERROR, "Error en nombre de alarma", "El nombre de la alarma ya existe");
        }
        else {
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.log(Level.INFO, "Finishing adding alarm");
            super.finishingAction();
        }
    }

    private void enableInputs(String type) {
        if (type.equals("Flotante") || type.equals("Entero")) {
            this.getVbox().getChildren().remove(doubleLimitHBox);
            this.getVbox().getChildren().remove(booleanConditionsHBox);
            this.getVbox().getChildren().add(5, doubleLimitHBox);
            this.getVbox().getChildren().add(6, booleanConditionsHBox);
            doubleLimitHBox.setVisible(true);
            booleanConditionsHBox.setVisible(false);
        } else if (type.equals("Bool")) {
            this.getVbox().getChildren().remove(doubleLimitHBox);
            this.getVbox().getChildren().remove(booleanConditionsHBox);
            this.getVbox().getChildren().add(5, booleanConditionsHBox);
            this.getVbox().getChildren().add(6, doubleLimitHBox);
            doubleLimitHBox.setVisible(false);
            booleanConditionsHBox.setVisible(true);
        } else {
            doubleLimitHBox.setVisible(false);
            booleanConditionsHBox.setVisible(false);
        }
    }

    public TextField getAlarmCommentTF() {
        return alarmCommentTF;
    }

    public TextField getHiHiLimitTF() {
        return hiHiLimitTF;
    }

    public TextField getHighLimitTF() {
        return highLimitTF;
    }

    public TextField getLoloLimitTF() {
        return loloLimitTF;
    }

    public TextField getLowLimitTF() {
        return lowLimitTF;
    }

    public CheckBox getHiHiLimitCheckBox() {
        return hiHiLimitCheckBox;
    }

    public CheckBox getHighLimitCheckBox() {
        return highLimitCheckBox;
    }

    public CheckBox getLoloLimitCheckBox() {
        return loloLimitCheckBox;
    }

    public CheckBox getLowLimitCheckBox() {
        return lowLimitCheckBox;
    }

    public TextField getAlarmNameTF() {
        return alarmNameTF;
    }

    public RadioButton getFalseRadioButton() {
        return falseRadioButton;
    }

    public RadioButton getTrueRadioButton() {
        return trueRadioButton;
    }

}
