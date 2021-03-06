package andrade.luis.librehmi.views.windows;

import andrade.luis.librehmi.models.Expression;
import andrade.luis.librehmi.models.Tag;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import org.codehaus.commons.compiler.CompileException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import static andrade.luis.librehmi.util.TextFormatters.digitFilter;

/**
 * Ventana de definición de expresiones para asociar a las representaciones gráficas y alarmas
 */
public class WriteExpressionWindow extends Stage {

    private final HBox floatPrecisionHBox;
    private final HBox samplingTimeHBox;
    private final Button addTagButton;
    protected TextField textField;
    protected Button finishSelectionButton;
    private ArrayList<Tag> addedTags;
    private StackPane root;
    private final VBox vbox;
    private Expression localExpression;
    private boolean inputMode;
    private final TextField floatPrecisionTextField;
    private final TextField samplingTimeTextField;
    private boolean done = false;

    public ArrayList<Tag> getLocalTags() {
        return localTags;
    }

    public void setLocalTags(ArrayList<Tag> localTags) {
        this.localTags = localTags;
    }

    private ArrayList<Tag> localTags;

    /**
     * Constructor de la ventana
     */
    public WriteExpressionWindow() {
        this(750, 250);
    }

    /**
     * Constructor de la ventana
     * @param width Ancho de la ventana
     * @param height Altura de la ventana
     */
    public WriteExpressionWindow(double width, double height) {
        root = new StackPane();
        addedTags = new ArrayList<>();

        final Label label = new Label("Escriba la expresión");

        textField = new TextField();
        textField.setPromptText("Ingrese la expresión");

        Label floatPrecision = new Label("Precisión de decimales:");
        floatPrecisionTextField = new TextField();
        floatPrecisionTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 3, digitFilter));
        floatPrecisionTextField.setPromptText("# decimales");
        floatPrecisionHBox = new HBox();
        floatPrecisionHBox.getChildren().addAll(floatPrecision, floatPrecisionTextField);

        textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (prepareExpression(false)) {
                floatPrecisionTextField.setDisable(!this.localExpression.getResultType().equals("Flotante"));
            }
        });

        Label samplingTime = new Label("Tiempo de muestreo:");
        samplingTimeTextField = new TextField();
        samplingTimeTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 1, digitFilter));
        samplingTimeTextField.setPromptText("Segundos");
        samplingTimeTextField.setTooltip(new Tooltip("En Segundos"));
        samplingTimeHBox = new HBox();
        samplingTimeHBox.getChildren().addAll(samplingTime, samplingTimeTextField);
        samplingTimeHBox.setVisible(false);
        samplingTimeHBox.setSpacing(12);

        vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, textField, floatPrecisionHBox, samplingTimeHBox);

        finishSelectionButton = new Button("OK");
        finishSelectionButton.setAlignment(Pos.CENTER);
        addTagButton = new Button("Añadir Tag");
        addTagButton.setAlignment(Pos.BOTTOM_LEFT);
        addTagButton.setOnAction(actionEvent -> addTag());
        finishSelectionButton.setOnAction(actionEvent -> finishingAction());
        HBox buttonsHBox = new HBox();
        buttonsHBox.getChildren().add(addTagButton);
        buttonsHBox.getChildren().add(finishSelectionButton);
        buttonsHBox.setAlignment(Pos.BASELINE_RIGHT);
        Button clearAllButton = new Button("Limpiar Tags");

        buttonsHBox.getChildren().add(0, clearAllButton);
        clearAllButton.setOnAction(mouseEvent -> clearAll());
        vbox.getChildren().add(buttonsHBox);
        vbox.setPadding(new Insets(5, 5, 10, 5));
        root.getChildren().add(vbox);
        Scene mainScene = new Scene(root, width, height);
        this.setScene(mainScene);

    }

    /**
     * Permite añadir un tag a la expresión
     */
    protected void addTag() {
        SelectTagWindow selectTagWindow = new SelectTagWindow(inputMode, "", false, this.localTags);
        selectTagWindow.showAndWait();
        Tag tag = selectTagWindow.getSelectedTag();
        if (tag != null) {
            addedTags.add(tag);
            textField.setText(textField.getText() + tag.getName());
            addTagButton.setDisable(true);
        }
    }

    /**
     * Permite añadir un tag y retornarlo
     * @param inputMode Bandera de modo de entrada
     * @param filter Filtro del tipo de tag
     * @param testMode Bandera para activar el modo de pruebas
     * @return Tag añadido
     */
    public Tag addTag(boolean inputMode, String filter, boolean testMode) {
        SelectTagWindow selectTagWindow = new SelectTagWindow(inputMode, filter, testMode, this.getLocalTags());
        selectTagWindow.showAndWait();
        if (!selectTagWindow.isCancelled()) {
            clearAll();
            Tag tag = selectTagWindow.getSelectedTag();
            if (tag != null) {
                this.getAddedTags().add(tag);
            }
            return tag;
        }else{
            return null;
        }
    }

    /**
     * Permite retornar la nueva expresión a evaluar con el tag añadido
     * @param inputMode Bandera de modo de entrada
     * @param filter Filtro del tipo de tag
     * @param testMode Bandera para activar el modo de pruebas
     * @param expression Expresión a actualizarse
     * @return Expresión actualizada
     */
    public String updateInputExpression(boolean inputMode, String filter, boolean testMode,String expression){
        Tag tag = addTag(inputMode, filter, testMode);
        if (tag != null) {
            return expression + tag.getName();
        }
        return "";
    }

    /**
     * Permite eliminar la expresión escrita junto con los tags definidos
     */
    public void clearAll() {
        this.getTextField().setText("");
        this.getAddedTags().clear();
        this.addTagButton.setDisable(false);
    }

    /**
     * Permite mostrar una ventana de confirmación de salida
     * @param type Tipo de alerta a mostrarse
     * @param title Título
     * @param message Mensaje a mostrarse en la ventana
     */
    protected void confirmExit(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(message);

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(okButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == okButton) {
            alert.close();
        }
    }

    /**
     * Permite verificar los campos antes de cerrar la ventana
     */
    public void finishingAction() {
        deleteUnneededTags();
        if (prepareExpression(true)) {
            this.close();
        } else {
            confirmExit(Alert.AlertType.WARNING, "No existe expresión definida", "Debe agregar una expresión o un Tag");
        }
        this.done = true;
    }

    /**
     * Permite eliminar los tags agregados pero no seleccionados
     */
    protected void deleteUnneededTags() {
        ArrayList<Tag> toDelete = new ArrayList<>();
        for (int i = 0; i < getAddedTags().size(); i++) {
            if (!textField.getText().contains(getAddedTags().get(i).getName())) {
                toDelete.add(getAddedTags().get(i));
            }
        }
        getAddedTags().removeAll(toDelete);
    }

    /**
     * Permite preparar la expresión antes de retornarla
     * @param test Bandera de modo de pruebas
     * @return true si la operación se realizó exitosamente
     */
    protected boolean prepareExpression(boolean test) {
        for (Tag addedTag : addedTags) {
            addedTag.setFloatPrecision(Integer.parseInt(floatPrecisionTextField.getText()));
        }
        if (addedTags.isEmpty() && !textField.getText().isEmpty()) {
            this.localExpression = new Expression(textField.getText(), addedTags, Integer.parseInt(floatPrecisionTextField.getText()));
            if (test) {
                try {
                    this.localExpression.evaluate();
                } catch (CompileException | InvocationTargetException | SQLException | IOException e) {
                    confirmExit(Alert.AlertType.ERROR, "Error al agregar expresión", "Error:" + e.getMessage());
                }
            }
            return true;
        } else if (!textField.getText().isEmpty()) {
            this.localExpression = new Expression(textField.getText(), addedTags, Integer.parseInt(floatPrecisionTextField.getText()));
            return true;
        }

        return false;
    }

    public HBox getFloatPrecisionHBox() {
        return floatPrecisionHBox;
    }

    public TextField getFloatPrecisionTextField() {
        return floatPrecisionTextField;
    }

    public TextField getTextField() {
        return textField;
    }

    public ArrayList<Tag> getAddedTags() {
        return addedTags;
    }

    /**
     * Permite definir los tags añadidos
     * @param addedTags ArrayList de tags añadidos
     */
    public void setAddedTags(ArrayList<Tag> addedTags) {
        this.addedTags = addedTags;
        if (!this.addedTags.isEmpty()) {
            addTagButton.setDisable(true);
        }
    }

    public StackPane getRoot() {
        return root;
    }

    public void setRoot(StackPane root) {
        this.root = root;
    }

    public VBox getVbox() {
        return vbox;
    }

    public Expression getLocalExpression() {
        return localExpression;
    }

    public void setLocalExpression(Expression localExpression) {
        this.localExpression = localExpression;
    }

    public boolean isInputMode() {
        return inputMode;
    }

    public void setInputMode(boolean inputMode) {
        this.inputMode = inputMode;
    }

    public boolean isDone() {
        return done;
    }

    public HBox getSamplingTimeHBox() {
        return samplingTimeHBox;
    }

    public TextField getSamplingTimeTextField() {
        return samplingTimeTextField;
    }
    public void setDone(boolean done) {
        this.done = done;
    }

}
