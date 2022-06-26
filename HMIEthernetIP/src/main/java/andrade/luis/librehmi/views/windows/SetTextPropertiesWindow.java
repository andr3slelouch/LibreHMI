package andrade.luis.librehmi.views.windows;

import andrade.luis.librehmi.views.RotationHBox;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.util.function.UnaryOperator;

import static andrade.luis.librehmi.controllers.TextFormatters.numberFilter;

public class SetTextPropertiesWindow extends Stage {
    protected final VBox vbox;
    private final RotationHBox rotationHBox;

    public boolean isCancelled() {
        return cancelled;
    }

    private boolean cancelled;

    public HBox getColorHBox() {
        return colorHBox;
    }

    private final HBox colorHBox;

    public TextField getRotationTextField() {
        return rotationHBox.getRotationTextField();
    }


    private String rotationValue;
    private final ColorPicker colorPicker;
    private final ComboBox<String> fontStyleComboBox;
    private final ComboBox<String> fontFamilyComboBox;
    private final TextField fontSizeField;

    public SetTextPropertiesWindow(){
        this(350,200);
    }

    public SetTextPropertiesWindow(double width, double height) {
        StackPane root = new StackPane();
        // font weight names
        String[] weight = {
                "Normal",
                "Delgado",
                "Extra Delgado",
                "Ligero",
                "Medio",
                "Semi Negrita",
                "Negrita",
                "Ultra Negrita",
                "Negro",
        };

        // Create a combo box
        fontStyleComboBox =
                new ComboBox<>(FXCollections.observableArrayList(weight));
        fontStyleComboBox.setPrefWidth(325);

        // Create a combo box
        fontFamilyComboBox =
                new ComboBox<>(FXCollections.observableArrayList(Font.getFontNames()));

        fontFamilyComboBox.setPrefWidth(325);

        Label fontSizeLabel = new Label("Tamaño de Fuente:");
        fontSizeField = new TextField("12.0");
        fontSizeField.setPrefWidth(195);
        fontSizeField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, numberFilter));
        HBox fontSizeHBox = new HBox(fontSizeLabel, fontSizeField);
        fontSizeHBox.setSpacing(12);

        Label selectColor = new Label("Seleccione el color:");
        colorPicker = new ColorPicker();
        colorPicker.setPrefWidth(195);
        colorHBox = new HBox();
        colorHBox.setSpacing(13);
        colorHBox.getChildren().addAll(selectColor, colorPicker);

        rotationHBox = new RotationHBox(rotationValue);
        rotationHBox.getRotationTextField().setPrefWidth(150);
        rotationHBox.setSpacing(92);

        vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(fontStyleComboBox, fontFamilyComboBox, fontSizeHBox, rotationHBox,colorHBox);

        Button finishSelectionButton = new Button("OK");
        finishSelectionButton.setAlignment(Pos.CENTER);

        finishSelectionButton.setOnAction(actionEvent -> finishingAction());

        Button cancelButton = new Button("Cancelar");
        cancelButton.setAlignment(Pos.CENTER);
        cancelButton.setOnAction(actionEvent -> this.close());

        HBox hbox = new HBox();
        hbox.getChildren().addAll(cancelButton,finishSelectionButton);
        hbox.setAlignment(Pos.BASELINE_RIGHT);
        hbox.setPadding(new Insets(5, 5, 5, 5));
        vbox.getChildren().add(hbox);

        root.getChildren().add(vbox);
        Scene mainScene = new Scene(root, width, height);
        this.setScene(mainScene);
    }

    private void finishingAction() {
        this.close();
        cancelled = false;
    }

    public ComboBox<String> getFontStyleComboBox() {
        return fontStyleComboBox;
    }

    public ComboBox<String> getFontFamilyComboBox() {
        return fontFamilyComboBox;
    }

    public TextField getFontSizeField() {
        return fontSizeField;
    }

    public FontWeight getFontStyle(){
        String style = fontStyleComboBox.getValue();
        switch (style) {
            case "Thin":
            case "Delgado":
                return FontWeight.THIN;
            case "Extra Light":
            case "Ultra Delgado":
            case "Ultra Light":
                return FontWeight.EXTRA_LIGHT;
            case "Light":
            case "Ligero":
                return FontWeight.LIGHT;
            case "Medium":
            case "Medio":
                return FontWeight.MEDIUM;
            case "Semi Bold":
            case "Semi Negrita":
            case "Demi Bold":
                return FontWeight.SEMI_BOLD;
            case "Bold":
            case "Negrita":
                return FontWeight.BOLD;
            case "Extra Bold":
            case "Ultra Negrita":
            case "Ultra Bold":
                return FontWeight.EXTRA_BOLD;
            case "Black":
            case "Negro":
            case "Heavy":
                return FontWeight.BLACK;
            case "Normal":
            case "Regular":
            default:
                return FontWeight.NORMAL;
        }
    }

    public ColorPicker getColorPicker() {
        return colorPicker;
    }

}
