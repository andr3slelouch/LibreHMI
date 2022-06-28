package andrade.luis.librehmi.views;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.util.converter.DoubleStringConverter;

import static andrade.luis.librehmi.util.TextFormatters.numberFilter;

/**
 * HBox con el campo de rotación utilizado en distintas ventanas
 */
public class RotationHBox extends HBox {
    public TextField getRotationTextField() {
        return rotationTextField;
    }

    private final TextField rotationTextField;

    private String rotationValue;

    /**
     * Constructor de HBox
     * @param rotationValue Valor de inicialización de rotación
     */
    public RotationHBox(String rotationValue){
        this.rotationValue = rotationValue;
        Label rotationLabel = new Label("Rotar:");
        rotationTextField = new TextField("0");
        rotationTextField.setPrefWidth(115);
        rotationTextField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, numberFilter));
        rotationTextField.textProperty().addListener((observableValue, oldValue, newValue) -> this.rotationValue = newValue);
        Button rotationButton = new Button("+90°");
        rotationButton.setOnAction(mouseEvent -> {
            double value = Double.parseDouble(rotationTextField.getText());
            rotationTextField.setText(String.valueOf(value + 90));
        });
        HBox rotationInputHBox = new HBox();
        rotationInputHBox.getChildren().addAll(rotationTextField,rotationButton);
        this.getChildren().addAll(rotationLabel, rotationInputHBox);
    }
}
