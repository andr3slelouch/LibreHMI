package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.models.Expression;
import andrade.luis.hmiethernetip.models.Tag;
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
import java.util.function.UnaryOperator;

public class WriteExpressionWindow extends Stage {
    public TextField getFloatPrecisionTextField() {
        return floatPrecisionTextField;
    }

    public void setFloatPrecisionTextField(TextField floatPrecisionTextField) {
        this.floatPrecisionTextField = floatPrecisionTextField;
    }

    private TextField floatPrecisionTextField;

    public Button getAddTagButton() {
        return addTagButton;
    }

    private final Button addTagButton;

    public HBox getButtonsHBox() {
        return buttonsHBox;
    }

    private final HBox buttonsHBox;
    protected TextField textField;

    public TextField getTextField() {
        return textField;
    }

    public void setTextField(TextField textField) {
        this.textField = textField;
    }

    public ArrayList<Tag> getAddedTags() {
        return addedTags;
    }

    public void setAddedTags(ArrayList<Tag> addedTags) {
        this.addedTags = addedTags;
    }

    protected Button finishSelectionButton;
    private ArrayList<Tag> addedTags;

    public Scene getMainScene() {
        return mainScene;
    }

    public void setMainScene(Scene mainScene) {
        this.mainScene = mainScene;
    }

    private Scene mainScene;

    public StackPane getRoot() {
        return root;
    }

    public void setRoot(StackPane root) {
        this.root = root;
    }

    private StackPane root;

    public VBox getVbox() {
        return vbox;
    }

    private final VBox vbox;
    public Expression getLocalExpression() {
        return localExpression;
    }

    public void setLocalExpression(Expression localExpression) {
        this.localExpression = localExpression;
    }

    private Expression localExpression;

    public boolean isInputMode() {
        return inputMode;
    }

    public void setInputMode(boolean inputMode) {
        this.inputMode = inputMode;
    }

    private boolean inputMode;
    public WriteExpressionWindow(){
        this(750,250);
    }
    public WriteExpressionWindow(double width, double height) {
        root = new StackPane();
        addedTags = new ArrayList<>();


        final Label label = new Label("Escriba la expresión");

        textField = new TextField();
        textField.setPromptText("Ingrese la expresión");

        Label floatPrecision = new Label("Precisión de decimales:");
        floatPrecisionTextField = new TextField();
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (!newText.matches("^\\d+$")) {
                change.setText("");
                change.setRange(change.getRangeStart(), change.getRangeStart());
            }
            return change;
        };
        floatPrecisionTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 3, integerFilter));
        floatPrecisionTextField.setPromptText("# decimales");
        HBox floatPrecisionHBox = new HBox();
        floatPrecisionHBox.getChildren().addAll(floatPrecision,floatPrecisionTextField);

        textField.textProperty().addListener((observableValue,oldValue,newValue) ->{
            if(prepareExpression(false)){
                floatPrecisionTextField.setDisable(!this.localExpression.getResultType().equals("Flotante"));
            }
        });

        vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, textField,floatPrecisionHBox);

        finishSelectionButton = new Button("OK");
        finishSelectionButton.setAlignment(Pos.CENTER);
        addTagButton = new Button("Añadir Tag");
        addTagButton.setAlignment(Pos.BOTTOM_LEFT);
        addTagButton.setOnAction(actionEvent -> addTag());
        finishSelectionButton.setOnAction(actionEvent -> finishingAction());
        buttonsHBox = new HBox();
        buttonsHBox.getChildren().add(addTagButton);
        buttonsHBox.getChildren().add(finishSelectionButton);
        buttonsHBox.setAlignment(Pos.BASELINE_RIGHT);
        vbox.getChildren().add(buttonsHBox);
        root.getChildren().add(vbox);
        mainScene = new Scene(root,width,height);
        this.setScene(mainScene);
    }

    protected void addTag(){
        SelectTagWindow selectTagWindow = new SelectTagWindow(inputMode,"",false);
        selectTagWindow.showAndWait();
        Tag tag = selectTagWindow.getSelectedTag();
        if(tag!=null){
            addedTags.add(tag);
            textField.setText(textField.getText()+tag.getName());
        }
    }

    protected void clearAll(){
        this.getTextField().setText("");
        this.getAddedTags().clear();
    }

    protected void confirmExit(Alert.AlertType type,String title,String message){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText("Debe escribir una expresión con un Tag añadido a través del botón 'Añadir Tag' ");

        ButtonType okButton = new ButtonType("OK",ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(okButton);

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == okButton)
        {
            alert.close();
        }
    }
    
    public void finishingAction(){
        deleteUnneededTags();
        if(prepareExpression(true)){
            this.close();
        }else{
            confirmExit(Alert.AlertType.WARNING,"No existe expresión definida","Debe agregar una expresión o un Tag");
        }
    }

    private void deleteUnneededTags(){
        ArrayList<Tag> toDelete = new ArrayList<>();
        for(int i=0;i< getAddedTags().size();i++){
            if(!textField.getText().contains(getAddedTags().get(i).getName())){
                toDelete.add(getAddedTags().get(i));
            }
        }
        getAddedTags().removeAll(toDelete);
    }

    private boolean prepareExpression(boolean test){
        for (Tag addedTag : addedTags) {
            addedTag.setFloatPrecision(Integer.parseInt(floatPrecisionTextField.getText()));
        }
        if(addedTags.isEmpty() && !textField.getText().isEmpty()){
            this.localExpression = new Expression(textField.getText(),addedTags,Integer.parseInt(floatPrecisionTextField.getText()));
            if(test){
                try{
                    this.localExpression.evaluate();
                } catch (CompileException | InvocationTargetException | SQLException | IOException e) {
                    confirmExit(Alert.AlertType.ERROR,"Error al agregar expresión","Error:"+e.getMessage());
                }
            }
            return true;
        } else if(!textField.getText().isEmpty()){
            this.localExpression = new Expression(textField.getText(),addedTags,Integer.parseInt(floatPrecisionTextField.getText()));
            return true;
        }

        return false;
    }
}
