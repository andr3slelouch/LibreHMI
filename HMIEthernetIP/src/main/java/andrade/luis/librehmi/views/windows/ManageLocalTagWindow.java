package andrade.luis.librehmi.views.windows;

import andrade.luis.librehmi.models.Tag;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static andrade.luis.librehmi.controllers.TextFormatters.integerFilter;
import static andrade.luis.librehmi.controllers.TextFormatters.numberFilter;

public class ManageLocalTagWindow extends Stage {

    public static final String FLOTANTE_STR = "Flotante";
    public static final String ENTERO_STR = "Entero";
    public static final String BOOL_STR = "Bool";
    private final TextField firstNameField;
    private final ComboBox<String> typesComboBox;
    private final ComboBox<String> actionsComboBox;

    public VBox getVbox() {
        return vbox;
    }

    public void setVbox(VBox vbox) {
        this.vbox = vbox;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    private Tag tag;
    private VBox vbox;
    private TextField valueField;
    private RadioButton falseRadioButton;
    private RadioButton trueRadioButton;
    private ToggleGroup toggleGroup;
    private final HBox booleanConditionsHBox;
    private final HBox doubleLimitHBox;
    Logger logger = Logger.getLogger(this.getClass().getName());

    public ManageLocalTagWindow(Tag localTag) throws SQLException, IOException {
        StackPane root = new StackPane();

        Label firstNameLabel = new Label("Nombre:");
        this.firstNameField = new TextField("");
        firstNameField.setPrefWidth(165);
        HBox firstNameHBox = new HBox(firstNameLabel, firstNameField);
        firstNameHBox.setSpacing(15);

        Label lastNameLabel = new Label("Tipo:");
        String[] tagTypes = {FLOTANTE_STR, ENTERO_STR, BOOL_STR};
        this.typesComboBox =
                new ComboBox<>(FXCollections
                        .observableArrayList(tagTypes));
        typesComboBox.setPrefWidth(165);
        typesComboBox.valueProperty().addListener((observableValue, oldValue, newValue) -> enableInputs(newValue));
        HBox typeHBox = new HBox(lastNameLabel, typesComboBox);
        typeHBox.setSpacing(38);
        Label rolesLabel = new Label("Acción:");
        String[] userRoles = {"Escritura", "Lectura"};
        this.actionsComboBox =
                new ComboBox<>(FXCollections
                        .observableArrayList(userRoles));
        actionsComboBox.setPrefWidth(165);
        HBox rolesHBox = new HBox(rolesLabel, actionsComboBox);
        rolesHBox.setSpacing(25);

        Label numberValueLabel = new Label("Valor:");
        this.valueField = new TextField("0");
        this.valueField.setPrefWidth(165);
        valueField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, numberFilter));
        doubleLimitHBox = new HBox();
        doubleLimitHBox.getChildren().addAll(numberValueLabel, valueField);
        doubleLimitHBox.setVisible(false);
        doubleLimitHBox.setSpacing(34);

        Label boolValueLabel = new Label("Valor:");
        toggleGroup = new ToggleGroup();
        trueRadioButton = new RadioButton("Verdadero");
        trueRadioButton.setToggleGroup(toggleGroup);
        trueRadioButton.setSelected(true);
        falseRadioButton = new RadioButton("Falso");
        falseRadioButton.setToggleGroup(toggleGroup);
        booleanConditionsHBox = new HBox();
        HBox radioButtonsHBox = new HBox();
        radioButtonsHBox.getChildren().addAll(trueRadioButton, falseRadioButton);
        radioButtonsHBox.setSpacing(15);
        booleanConditionsHBox.getChildren().addAll(boolValueLabel, radioButtonsHBox);
        booleanConditionsHBox.setVisible(false);
        booleanConditionsHBox.setSpacing(36);


        Button cancelButton = new Button("Cancelar");
        cancelButton.setOnAction(mouseEvent -> this.close());
        Button registerButton = new Button("Guardar Tag");
        registerButton.setOnAction(actionEvent -> {
            if(verifyFields(firstNameField, typesComboBox, actionsComboBox)){
                String value = "";
                if(typesComboBox.getSelectionModel().getSelectedItem().equals(FLOTANTE_STR)||typesComboBox.getSelectionModel().getSelectedItem().equals(ENTERO_STR)){
                    value = valueField.getText();
                }else{
                    value = trueRadioButton.isSelected() ? "1" : "0";
                }
                if(ManageLocalTagWindow.this.tag == null){
                    this.tag = new Tag(firstNameField.getText(),typesComboBox.getSelectionModel().getSelectedItem(),actionsComboBox.getSelectionModel().getSelectedItem(),value,3);
                }else{
                    this.tag.setValue(value);
                    logger.log(Level.INFO,this.tag.getValue());
                }
                this.close();
            }
        });
        HBox buttonsHBox = new HBox();
        buttonsHBox.setPadding(new Insets(5, 5, 5, 5));
        buttonsHBox.getChildren().addAll(cancelButton, registerButton);
        buttonsHBox.setAlignment(Pos.BOTTOM_RIGHT);

        this.vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(firstNameHBox, typeHBox, rolesHBox ,doubleLimitHBox, booleanConditionsHBox, buttonsHBox);

        root.getChildren().add(vbox);
        
        try{
            initUI(localTag);
        }catch (Exception e){
            logger.log(Level.INFO,e.getMessage());
        }

        this.setScene(new Scene(root, 250, 200));
    }
    
    private void initUI(Tag localTag) throws IOException, SQLException {
        if (localTag != null) {
            this.tag = localTag;
            this.setTitle("Actualizar Tag");
            firstNameField.setDisable(true);
            firstNameField.setText(localTag.getName());
            typesComboBox.setDisable(true);
            typesComboBox.getSelectionModel().select(localTag.getType());
            actionsComboBox.setDisable(true);
            actionsComboBox.getSelectionModel().select(localTag.getAction());
            try {
                switch (localTag.getType()) {
                    case BOOL_STR:
                        enableInputs(localTag.getType());
                        if (localTag.read().equals("0")) {
                            falseRadioButton.setSelected(true);
                        } else {
                            trueRadioButton.setSelected(true);
                        }
                        break;
                    case FLOTANTE_STR:
                        enableInputs(localTag.getType());
                        double valueDouble;
                        valueDouble = Double.parseDouble(localTag.read());
                        valueField.setText(String.valueOf(valueDouble));
                        break;

                    case ENTERO_STR:
                        enableInputs(localTag.getType());
                        int valueInt;
                        valueInt = Integer.parseInt(localTag.read());
                        valueField.setText(String.valueOf(valueInt));
                        break;
                    default:
                        break;

                }
            } catch (SQLException e) {
                throw new SQLException(e);
            } catch (IOException e){
                throw new IOException(e);
            }
        } else {
            this.setTitle("Creación de Tag");
        }
    }

    private void enableInputs(String type) {
        if (type.equals(FLOTANTE_STR) || type.equals(ENTERO_STR)) {
            this.getVbox().getChildren().remove(doubleLimitHBox);
            this.getVbox().getChildren().remove(booleanConditionsHBox);
            this.getVbox().getChildren().add(3, doubleLimitHBox);
            this.getVbox().getChildren().add(4, booleanConditionsHBox);
            doubleLimitHBox.setVisible(true);
            booleanConditionsHBox.setVisible(false);
        } else if (type.equals(BOOL_STR)) {
            this.getVbox().getChildren().remove(doubleLimitHBox);
            this.getVbox().getChildren().remove(booleanConditionsHBox);
            this.getVbox().getChildren().add(3, booleanConditionsHBox);
            this.getVbox().getChildren().add(4, doubleLimitHBox);
            doubleLimitHBox.setVisible(false);
            booleanConditionsHBox.setVisible(true);
        } else {
            doubleLimitHBox.setVisible(false);
            booleanConditionsHBox.setVisible(false);
        }
        if (type.equals(FLOTANTE_STR)) {
            valueField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, numberFilter));
        } else if (type.equals(ENTERO_STR)) {
            valueField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter));
        }
    }

    public boolean verifyFields(TextField nameField, ComboBox<String> typesComboBox, ComboBox<String> actionsComboBox) {
        String message = "";
        if (nameField.getText().isEmpty()) {
            message = "Existen campos vacíos";
        } else if (typesComboBox.getSelectionModel().isEmpty()) {
            message = "No se ha seleccionado un Tipo";
        } else if (actionsComboBox.getSelectionModel().isEmpty()) {
            message = "No se ha seleccionado un modo de Acción del Tag";
        }
        if (message.isEmpty()) {
            return true;
        } else {
            showAlert(Alert.AlertType.WARNING, "Error en los campos ingresados", message);
            return false;
        }
    }

    public void showAlert(Alert.AlertType type, String title, String message) {
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
}
