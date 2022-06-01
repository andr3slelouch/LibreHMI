package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.models.Tag;
import andrade.luis.hmiethernetip.models.users.HMIUser;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateLocalTagWindow extends Stage {

    public static final String FLOTANTE_STR = "Flotante";
    public static final String ENTERO_STR = "Entero";
    public static final String BOOL_STR = "Bool";
    private HBox precisionHBox;
    private TextField precisionField;

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

    public CreateLocalTagWindow(Tag localTag) {
        StackPane root = new StackPane();

        Label firstNameLabel = new Label("Nombre:");
        TextField firstNameField = new TextField("");
        firstNameField.setPrefWidth(165);
        HBox firstNameHBox = new HBox(firstNameLabel, firstNameField);
        firstNameHBox.setSpacing(15);

        Label lastNameLabel = new Label("Tipo:");
        String[] tagTypes = {FLOTANTE_STR, ENTERO_STR, BOOL_STR};
        ComboBox<String> typesComboBox =
                new ComboBox<>(FXCollections
                        .observableArrayList(tagTypes));
        typesComboBox.setPrefWidth(165);
        typesComboBox.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            enableInputs(newValue);
        });
        HBox typeHBox = new HBox(lastNameLabel, typesComboBox);
        typeHBox.setSpacing(38);
        Label rolesLabel = new Label("Acción:");
        String[] userRoles = {"Escritura", "Lectura"};
        ComboBox<String> actionsComboBox =
                new ComboBox<>(FXCollections
                        .observableArrayList(userRoles));
        actionsComboBox.setPrefWidth(165);
        HBox rolesHBox = new HBox(rolesLabel, actionsComboBox);
        rolesHBox.setSpacing(25);

        /*Label precisionLabel = new Label("Precisión:");
        this.precisionField = new TextField("3");
        this.precisionField.setPrefWidth(165);
        this.precisionField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter));
        this.precisionHBox = new HBox();
        this.precisionHBox.getChildren().addAll(precisionLabel, precisionField);
        this.precisionHBox.setSpacing(34);*/

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
                if(typesComboBox.getSelectionModel().getSelectedItem().equals("Flotante")||typesComboBox.getSelectionModel().getSelectedItem().equals("Entero")){
                    value = valueField.getText();
                }else{
                    if(trueRadioButton.isSelected()){
                        value = "1";
                    }else{
                        value = "0";
                    }
                }
                if(CreateLocalTagWindow.this.tag == null){
                    this.tag = new Tag(firstNameField.getText(),typesComboBox.getSelectionModel().getSelectedItem(),actionsComboBox.getSelectionModel().getSelectedItem(),value,3);
                }else{
                    this.tag.setName(firstNameField.getText());
                    this.tag.setType(typesComboBox.getSelectionModel().getSelectedItem());
                    this.tag.setAction(actionsComboBox.getSelectionModel().getSelectedItem());
                    this.tag.setValue(value);
                    this.tag.setFloatPrecision(3);
                }
                this.close();
            }
        });
        HBox buttonsHBox = new HBox();
        buttonsHBox.setPadding(new Insets(5, 5, 5, 5));
        buttonsHBox.getChildren().addAll(cancelButton, registerButton);
        buttonsHBox.setAlignment(Pos.BOTTOM_RIGHT);

        if (localTag != null) {
            this.tag = localTag;
            this.setTitle("Actualizar Tag");
            firstNameField.setText(localTag.getName());
            typesComboBox.getSelectionModel().select(localTag.getType());
            actionsComboBox.getSelectionModel().select(localTag.getAction());
            try {
                switch (localTag.getType()) {
                    case BOOL_STR:
                        if (localTag.read().equals("0")) {
                            falseRadioButton.setSelected(true);
                        } else {
                            trueRadioButton.setSelected(true);
                        }
                        break;
                    case FLOTANTE_STR:
                        double valueDouble;
                        valueDouble = Double.parseDouble(localTag.read());
                        valueField.setText(String.valueOf(valueDouble));
                        break;

                    case ENTERO_STR:
                        int valueInt;
                        valueInt = Integer.parseInt(localTag.read());
                        valueField.setText(String.valueOf(valueInt));
                        break;
                    default:
                        break;

                }
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.setTitle("Creación de Tag");
        }

        this.vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(firstNameHBox, typeHBox, rolesHBox ,doubleLimitHBox, booleanConditionsHBox, buttonsHBox);

        root.getChildren().add(vbox);

        this.setScene(new Scene(root, 250, 200));
    }

    private void enableInputs(String type) {
        if (type.equals(FLOTANTE_STR) || type.equals(ENTERO_STR)) {
            this.getVbox().getChildren().remove(doubleLimitHBox);
            this.getVbox().getChildren().remove(booleanConditionsHBox);
            this.getVbox().getChildren().add(3, doubleLimitHBox);
            this.getVbox().getChildren().add(4, booleanConditionsHBox);
            doubleLimitHBox.setVisible(true);
            booleanConditionsHBox.setVisible(false);
            /*if(type.equals(ENTERO_STR)){
                precisionField.setDisable(true);
            }*/
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
        }/*else if(HMIUser.existsEmail(email.getText(), this.hmiUser.getUsername() )){
            message = "El correo electrónico ya se encuentra asociado a una cuenta";
        }else if(!this.hmiUser.getUsername().equals(username.getText())){
            if(HMIUser.existsUsername(username.getText())){
                message = "El nombre de usuario ya existe";
            }
        }else if(role.getSelectionModel().getSelectedIndex()==-1){
            message = "Debe seleccionar un rol";
        }else if(!passwordField.getText().equals(repeatPasswordField.getText())){
            message = "Las contraseñas no coindicen";
        }*/
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
