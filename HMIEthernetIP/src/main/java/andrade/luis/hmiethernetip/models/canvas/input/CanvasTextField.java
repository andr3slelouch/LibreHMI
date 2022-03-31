package andrade.luis.hmiethernetip.models.canvas.input;

import andrade.luis.hmiethernetip.models.canvas.CanvasColor;
import andrade.luis.hmiethernetip.models.canvas.CanvasObjectData;
import andrade.luis.hmiethernetip.models.Tag;
import andrade.luis.hmiethernetip.models.canvas.CanvasPoint;
import andrade.luis.hmiethernetip.models.canvas.CanvasObject;
import andrade.luis.hmiethernetip.models.users.HMIUser;
import andrade.luis.hmiethernetip.views.SetInputTextPropertiesWindow;
import andrade.luis.hmiethernetip.views.SetTagInputPropertiesWindow;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
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
    private static final String ENTERO_STR = "Entero";
    private static final String FLOTANTE_STR = "Flotante";
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
            if (!newValue.isEmpty() && (this.type.equals(ENTERO_STR) || this.type.equals(FLOTANTE_STR))) {
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
            case ENTERO_STR:
                this.textField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), (int) (minValue + 1), integerFilter));
                break;
            case FLOTANTE_STR:
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
        this.getCanvasObjectData().setWidth(width);
        this.getCanvasObjectData().setHeight(height);
        this.setCenter(this.textField);

        if(this.getCanvasObjectData().getFontFamily()!=null && this.getCanvasObjectData().getFontStyle()!=null){
            this.textField.setFont(
                    Font.font(
                            this.getCanvasObjectData().getFontFamily(),
                            FontWeight.valueOf(this.getCanvasObjectData().getFontStyle()),
                            this.getCanvasObjectData().getFontSize()
                    )
            );
        }
        this.setRotate(this.getCanvasObjectData().getRotation());
    }
    public void setNewMenuItem() {
        MenuItem editMI = new MenuItem("Editar");
        editMI.setId("#editMI");
        editMI.setOnAction(actionEvent -> buttonAction());
        this.getRightClickMenu().getItems().add(2,editMI);
    }

    private void buttonAction() {
        SetTagInputPropertiesWindow setTagInputPropertiesWindow = new SetTagInputPropertiesWindow();
        setTagInputPropertiesWindow.getMinValueField().setText(String.valueOf(this.getCanvasObjectData().getMinValue()));
        setTagInputPropertiesWindow.getMaxValueField().setText(String.valueOf(this.getCanvasObjectData().getMaxValue()));
        setTagInputPropertiesWindow.setSelectedRadioButton(this.getCanvasObjectData().getType());
        setTagInputPropertiesWindow.setAddedTags(new ArrayList<>(List.of(this.getCanvasObjectData().getTag())));
        setTagInputPropertiesWindow.getTextField().setText(this.getCanvasObjectData().getTag().getName());
        setTagInputPropertiesWindow.getFloatPrecisionTextField().setText(String.valueOf(this.getCanvasObjectData().getTag().getFloatPrecision()));
        setTagInputPropertiesWindow.showAndWait();
        setData(this.getCanvasObjectData().getPosition().getX(), this.getCanvasObjectData().getPosition().getY(), this.getCanvasObjectData().getWidth(), this.getCanvasObjectData().getHeight(), setTagInputPropertiesWindow.getLocalExpression().getParameters().get(0), Double.parseDouble(setTagInputPropertiesWindow.getMinValueField().getText()), Double.parseDouble(setTagInputPropertiesWindow.getMaxValueField().getText()), setTagInputPropertiesWindow.getType());
        this.getHmiApp().setWasModified(true);
    }

    @Override
    public void setProperties() {
        SetInputTextPropertiesWindow propertiesWindow = new SetInputTextPropertiesWindow(this.textField.getWidth(),this.textField.getHeight());
        propertiesWindow.setTitle("Propiedades de Campo de Texto");
        propertiesWindow.getFontStyleComboBox().getSelectionModel().select(this.textField.getFont().getStyle());
        propertiesWindow.getFontFamilyComboBox().getSelectionModel().select(this.textField.getFont().getFamily());
        propertiesWindow.getFontSizeField().setText(String.valueOf(this.textField.getFont().getSize()));
        propertiesWindow.getRotationTextField().setText(String.valueOf(this.getCanvasObjectData().getRotation()));
        propertiesWindow.getColorHBox().setVisible(false);
        propertiesWindow.showAndWait();
        this.textField.setFont(
                Font.font(
                        propertiesWindow.getFontFamilyComboBox().getValue(),
                        propertiesWindow.getFontStyle(),
                        Double.parseDouble(propertiesWindow.getFontSizeField().getText()
                        )
                )
        );
        double rotation = Double.parseDouble(propertiesWindow.getRotationTextField().getText());
        this.setRotate(rotation);
        this.getCanvasObjectData().setWidth(propertiesWindow.getSizeVBox().getWidthFromField());
        this.getCanvasObjectData().setHeight(propertiesWindow.getSizeVBox().getHeightFromField());
        this.textField.setPrefWidth(this.getCanvasObjectData().getWidth());
        this.textField.setPrefHeight(this.getCanvasObjectData().getHeight());
        this.setSize(this.getCanvasObjectData().getWidth(),this.getCanvasObjectData().getHeight());
        this.getCanvasObjectData().setRotation(rotation);
        this.getCanvasObjectData().setPrimaryColor(new CanvasColor(propertiesWindow.getColorPicker().getValue()));
        this.getCanvasObjectData().setFontStyle(propertiesWindow.getFontStyle().name());
        this.getCanvasObjectData().setFontFamily(propertiesWindow.getFontFamilyComboBox().getValue());
        this.getCanvasObjectData().setFontSize(Double.parseDouble(propertiesWindow.getFontSizeField().getText()));
        this.getHmiApp().setWasModified(true);
    }

    @Override
    public void setEnable(String mode) {
        switch (mode) {
            case "Ejecutar":
                if(!this.getUser().getRole().equals("Administrador")){
                    this.enableListeners(false);
                    this.setOnMousePressed(this.onDoubleClick);
                    this.textField.setDisable(true);
                }else{
                    this.enableListeners(true);
                    this.textField.setDisable(false);
                }
                break;
            case "Stop":
                super.setEnable("Stop");
                this.textField.setDisable(true);
                break;
            default:
                super.setEnable("True");
                this.enableListeners(true);
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
                                    case ENTERO_STR:
                                    case "Bool":
                                        evaluatedValue = String.valueOf(Integer.parseInt(this.linkedTag.readFromDatabase()));
                                        break;
                                    case FLOTANTE_STR:
                                        evaluatedValue = String.valueOf(Double.parseDouble(this.linkedTag.readFromDatabase()));
                                        if(this.linkedTag.getFloatPrecision()>-1){
                                            DecimalFormat decimalFormat = this.linkedTag.generateDecimalFormat();
                                            evaluatedValue = decimalFormat.format(Double.parseDouble(evaluatedValue));
                                        }
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
