package andrade.luis.hmiethernetip.models.canvas.input;

import andrade.luis.hmiethernetip.models.canvas.CanvasObjectData;
import andrade.luis.hmiethernetip.models.Tag;
import andrade.luis.hmiethernetip.models.canvas.CanvasPoint;
import andrade.luis.hmiethernetip.models.canvas.CanvasObject;
import andrade.luis.hmiethernetip.models.users.HMIUser;
import andrade.luis.hmiethernetip.views.SetTextFieldPropertiesWindow;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.Duration;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;

public class CanvasTextField extends CanvasObject {
    private TextField textField;
    private String type;
    private Tag linkedTag;
    private double maxValue = 100;
    private double minValue = 0;
    Logger logger = Logger.getLogger(this.getClass().getName());
    private Timeline timeline;

    public HMIUser getUser() {
        return user;
    }

    public void setUser(HMIUser user) {
        this.user = user;
    }

    private HMIUser user;

    private final UnaryOperator<TextFormatter.Change> integerFilter = change -> {
        String newText = change.getControlNewText();
        if (!newText.matches("^(\\+|-)?\\d+$")) {
            change.setText("");
            change.setRange(change.getRangeStart(), change.getRangeStart());
        }
        return change;
    };

    private final UnaryOperator<TextFormatter.Change> numberFilter = change -> {
        String newText = change.getControlNewText();
        if (!newText.matches("^(\\+|-)?\\d+\\.\\d+$")) {
            change.setText("");
            change.setRange(change.getRangeStart(), change.getRangeStart());
        }
        return change;
    };

    private final UnaryOperator<TextFormatter.Change> booleanFilter = change -> {
        String newText = change.getControlNewText();
        if (!newText.matches("(\\b0\\b)|(\\b1\\b)|(\\b00\\b)|(\\b01\\b)|")) {
            change.setText("");
            change.setRange(change.getRangeStart(), change.getRangeStart());
        }
        if (!change.getText().isEmpty()) {
            change.setText(String.valueOf(Integer.valueOf(change.getText())));
        }
        return change;
    };

    public CanvasTextField(CanvasPoint center, Tag linkedTag, double minValue, double maxValue, String type) {
        super(center);
        this.getCanvasObjectData().setType("TextField");
        setData(this.getCanvasObjectData().getPosition().getX(), this.getCanvasObjectData().getPosition().getY(), 150, 150, linkedTag, minValue, maxValue,type);
        setNewMenuItem();
    }

    public CanvasTextField(CanvasObjectData canvasObjectData) {
        super(canvasObjectData);
        this.getCanvasObjectData().setType("TextField");
        setData(this.getCanvasObjectData().getPosition().getX(), this.getCanvasObjectData().getPosition().getY(), this.getCanvasObjectData().getWidth(), this.getCanvasObjectData().getHeight(), this.getCanvasObjectData().getTag(), this.getCanvasObjectData().getMinValue(), this.getCanvasObjectData().getMaxValue(),this.getCanvasObjectData().getType());
        setNewMenuItem();
    }

    private void setData(double x, double y, double width, double height, Tag linkedTag, double minValue, double maxValue, String type) {
        this.linkedTag = linkedTag;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.type = type;
        this.textField = new TextField();
        this.getCanvasObjectData().setTag(linkedTag);
        this.getCanvasObjectData().setMinValue(minValue);
        this.getCanvasObjectData().setMaxValue(maxValue);
        this.getCanvasObjectData().setDataType(type);
        this.textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.isEmpty() && (this.type.equals("Entero") || this.type.equals("Flotante"))) {
                double value = Double.parseDouble(newValue);
                if (value < this.minValue || value > this.maxValue) {
                    this.textField.setText(oldValue);
                    this.getCanvasObjectData().setData(oldValue);
                } else {
                    this.getCanvasObjectData().setData(newValue);
                }
            }else{
                this.getCanvasObjectData().setData(newValue);
            }
            if (linkedTag != null && timeline!=null) {
                linkedTag.setValue(this.getCanvasObjectData().getData());
                try {
                    timeline.pause();
                    if (!linkedTag.updateInDatabase()) {
                        this.errorLabel = new Label("Error en Tag de Escritura");
                        this.setTop(errorLabel);
                    } else {
                        this.setTop(null);
                    }
                    timeline.play();
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                    this.errorLabel = new Label("Error en Tag de Escritura");
                    this.setTop(errorLabel);
                }
            }

        });
        this.textField.setDisable(true);
        this.textField.setPrefWidth(width);
        this.textField.setPrefHeight(height);
        switch (type) {
            case "Entero":
                this.textField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), (int) (minValue + 1), integerFilter));
                break;
            case "Flotante":
                this.textField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, numberFilter));
                break;
            case "Bool":
                this.textField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, booleanFilter));
                break;
            default:
                this.textField.setTextFormatter(null);
                break;
        }
        if (this.getCanvasObjectData().getData() != null) {
            this.textField.setText(this.getCanvasObjectData().getData());
        }
        if (this.linkedTag != null) {
            this.textField.setText(this.linkedTag.getValue());
            this.setTimeline();
        }
        this.getCanvasObjectData().setPosition(new CanvasPoint(x, y));
        this.getCanvasObjectData().setWidth(width);
        this.getCanvasObjectData().setHeight(height);
        this.setCenter(this.textField);
    }
    public void setNewMenuItem() {
        MenuItem attachShowHideWindowsActionMI = new MenuItem("Editar");
        attachShowHideWindowsActionMI.setId("#editMI");
        attachShowHideWindowsActionMI.setOnAction(actionEvent -> buttonAction());
        this.getRightClickMenu().getItems().add(attachShowHideWindowsActionMI);
    }

    private void buttonAction() {
        SetTextFieldPropertiesWindow setTextFieldPropertiesWindow = new SetTextFieldPropertiesWindow();
        setTextFieldPropertiesWindow.getMinValueField().setText(String.valueOf(this.getCanvasObjectData().getMinValue()));
        setTextFieldPropertiesWindow.getMaxValueField().setText(String.valueOf(this.getCanvasObjectData().getMaxValue()));
        setTextFieldPropertiesWindow.setSelectedRadioButton(this.getCanvasObjectData().getType());
        setTextFieldPropertiesWindow.setAddedTags(new ArrayList<>(List.of(this.getCanvasObjectData().getTag())));
        setTextFieldPropertiesWindow.getTextField().setText(this.getCanvasObjectData().getTag().getName());
        setTextFieldPropertiesWindow.showAndWait();
        setData(this.getCanvasObjectData().getPosition().getX(), this.getCanvasObjectData().getPosition().getY(), this.getCanvasObjectData().getWidth(), this.getCanvasObjectData().getHeight(),setTextFieldPropertiesWindow.getLocalExpression().getParameters().get(0), Double.parseDouble(setTextFieldPropertiesWindow.getMinValueField().getText()), Double.parseDouble(setTextFieldPropertiesWindow.getMaxValueField().getText()), setTextFieldPropertiesWindow.getType());
    }

    @Override
    public void setEnable(String enabled) {
        if (user.getRole().equals("Operador")) {
            enabled = "Stop";
        }
        switch (enabled) {
            case "Play":
                super.setEnable("Play");
                this.textField.setDisable(false);
                break;
            case "Stop":
                super.setEnable("Stop");
                this.textField.setDisable(true);
                break;
            default:
                super.setEnable("True");
                this.textField.setDisable(true);
                break;
        }
    }

    public void setTimeline() {
        timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        (ActionEvent actionEvent) -> {
                            String type = this.linkedTag.getType();
                            String evaluatedValue = "";
                            try {
                                switch (type) {
                                    case "Entero":
                                    case "Bool":
                                        evaluatedValue = String.valueOf(Integer.parseInt(this.linkedTag.readFromDatabase()));
                                        break;
                                    case "Flotante":
                                        evaluatedValue = String.valueOf(Double.parseDouble(this.linkedTag.readFromDatabase()));
                                        break;
                                    case "String":
                                        evaluatedValue = this.linkedTag.readFromDatabase();
                                        break;
                                    default:
                                        break;
                                }
                            } catch (IOException | SQLException e) {
                                e.printStackTrace();
                            }
                            this.textField.setText(evaluatedValue);
                        }), new KeyFrame(Duration.seconds(5)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

}
