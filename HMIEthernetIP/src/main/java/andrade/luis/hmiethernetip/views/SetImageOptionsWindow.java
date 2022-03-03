package andrade.luis.hmiethernetip.views;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.util.function.UnaryOperator;

public class SetImageOptionsWindow extends Stage {
    private final UnaryOperator<TextFormatter.Change> numberFilter = change -> {
        String newText = change.getControlNewText();
        if (!newText.matches("^(\\+|-)?\\d+\\.\\d+$")) {
            change.setText("");
            change.setRange(change.getRangeStart(), change.getRangeStart());
        }
        return change;
    };
    private TextField rotationTextField;
    private CheckBox mirrorHorizontalCheckBox;
    private CheckBox mirrorVerticalCheckBox;

    public boolean isMirroringHorizontal() {
        return mirroringHorizontal;
    }

    public void setMirroringHorizontal(boolean mirroringHorizontal) {
        this.mirroringHorizontal = mirroringHorizontal;
    }

    public boolean isMirroringVertical() {
        return mirroringVertical;
    }

    public void setMirroringVertical(boolean mirroringVertical) {
        this.mirroringVertical = mirroringVertical;
    }

    public String getRotationValue() {
        return rotationValue;
    }

    public void setRotationValue(String rotationValue) {
        this.rotationValue = rotationValue;
    }

    public TextField getRotationTextField() {
        return rotationTextField;
    }

    public void setRotationTextField(TextField rotationTextField) {
        this.rotationTextField = rotationTextField;
    }

    public CheckBox getMirrorHorizontalCheckBox() {
        return mirrorHorizontalCheckBox;
    }

    public void setMirrorHorizontalCheckBox(CheckBox mirrorHorizontalCheckBox) {
        this.mirrorHorizontalCheckBox = mirrorHorizontalCheckBox;
    }

    public CheckBox getMirrorVerticalCheckBox() {
        return mirrorVerticalCheckBox;
    }

    public void setMirrorVerticalCheckBox(CheckBox mirrorVerticalCheckBox) {
        this.mirrorVerticalCheckBox = mirrorVerticalCheckBox;
    }

    private boolean mirroringHorizontal =false;
    private boolean mirroringVertical =false;
    private String rotationValue="0";
    public SetImageOptionsWindow(){
        StackPane root = new StackPane();
        Label label = new Label("Opciones de Imagen");
        mirrorHorizontalCheckBox = new CheckBox("Reflejar Horizontalmente");
        mirrorHorizontalCheckBox.selectedProperty().addListener((observableValue, oldBoolean, newBoolean) -> {
            mirroringHorizontal = newBoolean;
        });
        mirrorVerticalCheckBox = new CheckBox("Reflejar Verticalmente");
        mirrorVerticalCheckBox.selectedProperty().addListener((observableValue, oldBoolean, newBoolean) -> {
           mirroringVertical = newBoolean;
        });
        Label rotationLabel = new Label("Rotar:");
        rotationTextField = new TextField("0");
        rotationTextField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, numberFilter));
        rotationTextField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            rotationValue = newValue;
        });
        Button rotationButton = new Button("+90°");
        rotationButton.setOnAction(mouseEvent -> {
            double value = Double.parseDouble(rotationTextField.getText());
            rotationTextField.setText(String.valueOf(value+90));
        });

        HBox rotationHBox = new HBox();
        rotationHBox.getChildren().addAll(rotationLabel,rotationTextField,rotationButton);

        Button okButton = new Button("OK");
        okButton.setOnAction(mouseEvent -> this.close());
        HBox okHBox = new HBox();
        okHBox.getChildren().addAll(okButton);
        okHBox.setAlignment(Pos.BOTTOM_RIGHT);

        VBox vbox = new VBox();
        vbox.getChildren().addAll(label,mirrorHorizontalCheckBox,mirrorVerticalCheckBox, rotationHBox, okHBox);

        root.getChildren().add(vbox);

        this.setScene(new Scene(root,250,100));
    }
}
