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

    private HBox floatPrecisionHBox;
    private HBox samplingTimeHBox;
    private final Button addTagButton;
    private final HBox buttonsHBox;
    protected TextField textField;
    protected Button finishSelectionButton;
    private ArrayList<Tag> addedTags;
    private Scene mainScene;
    private StackPane root;
    private final VBox vbox;
    private Expression localExpression;
    private boolean inputMode;
    private TextField floatPrecisionTextField;
    private TextField samplingTimeTextField;
    private boolean done = false;

    public ArrayList<Tag> getLocalTags() {
        return localTags;
    }

    public void setLocalTags(ArrayList<Tag> localTags) {
        this.localTags = localTags;
    }

    private ArrayList<Tag> localTags;

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
        floatPrecisionHBox = new HBox();
        floatPrecisionHBox.getChildren().addAll(floatPrecision,floatPrecisionTextField);

        textField.textProperty().addListener((observableValue,oldValue,newValue) ->{
            if(prepareExpression(false)){
                floatPrecisionTextField.setDisable(!this.localExpression.getResultType().equals("Flotante"));
            }
        });

        Label samplingTime = new Label("Tiempo de muestreo en segundos:");
        samplingTimeTextField = new TextField();
        samplingTimeTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 1, integerFilter));
        samplingTimeTextField.setPromptText("Segundos");
        samplingTimeHBox = new HBox();
        samplingTimeHBox.getChildren().addAll(samplingTime,samplingTimeTextField);
        samplingTimeHBox.setVisible(false);

        vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, textField,floatPrecisionHBox,samplingTimeHBox);

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
        Button clearAllButton = new Button("Limpiar Tags");

        buttonsHBox.getChildren().add(0,clearAllButton);
        clearAllButton.setOnAction(mouseEvent -> clearAll());
        vbox.getChildren().add(buttonsHBox);
        root.getChildren().add(vbox);
        mainScene = new Scene(root,width,height);
        this.setScene(mainScene);
    }

    protected void addTag(){
        SelectTagWindow selectTagWindow = new SelectTagWindow(inputMode,"",false,this.localTags);
        selectTagWindow.showAndWait();
        Tag tag = selectTagWindow.getSelectedTag();
        if(tag!=null){
            addedTags.add(tag);
            textField.setText(textField.getText()+tag.getName());
            addTagButton.setDisable(true);
        }
    }

    protected void clearAll(){
        this.getTextField().setText("");
        this.getAddedTags().clear();
        this.addTagButton.setDisable(false);
    }

    protected void confirmExit(Alert.AlertType type,String title,String message){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(message);

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
        this.done = true;
    }

    protected void deleteUnneededTags(){
        ArrayList<Tag> toDelete = new ArrayList<>();
        for(int i=0;i< getAddedTags().size();i++){
            if(!textField.getText().contains(getAddedTags().get(i).getName())){
                toDelete.add(getAddedTags().get(i));
            }
        }
        getAddedTags().removeAll(toDelete);
    }

    protected boolean prepareExpression(boolean test){
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
    public HBox getFloatPrecisionHBox() {
        return floatPrecisionHBox;
    }

    public void setFloatPrecisionHBox(HBox floatPrecisionHBox) {
        this.floatPrecisionHBox = floatPrecisionHBox;
    }

    public TextField getFloatPrecisionTextField() {
        return floatPrecisionTextField;
    }

    public void setFloatPrecisionTextField(TextField floatPrecisionTextField) {
        this.floatPrecisionTextField = floatPrecisionTextField;
    }

    public Button getAddTagButton() {
        return addTagButton;
    }

    public HBox getButtonsHBox() {
        return buttonsHBox;
    }

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
        if(!this.addedTags.isEmpty()){
            addTagButton.setDisable(true);
        }
    }

    public Scene getMainScene() {
        return mainScene;
    }

    public void setMainScene(Scene mainScene) {
        this.mainScene = mainScene;
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

    public void setDone(boolean done) {
        this.done = done;
    }
    public HBox getSamplingTimeHBox() {
        return samplingTimeHBox;
    }

    public void setSamplingTimeHBox(HBox samplingTimeHBox) {
        this.samplingTimeHBox = samplingTimeHBox;
    }

    public TextField getSamplingTimeTextField() {
        return samplingTimeTextField;
    }

    public void setSamplingTimeTextField(TextField samplingTimeTextField) {
        this.samplingTimeTextField = samplingTimeTextField;
    }

}
