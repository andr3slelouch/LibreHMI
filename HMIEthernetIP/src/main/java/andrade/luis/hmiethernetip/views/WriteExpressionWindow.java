package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.models.Expression;
import andrade.luis.hmiethernetip.models.Tag;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.codehaus.commons.compiler.CompileException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Optional;

public class WriteExpressionWindow extends Stage {
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

        vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, textField);

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
        SelectTagWindow selectTagWindow = new SelectTagWindow(inputMode,false);
        selectTagWindow.showAndWait();
        Tag tag = selectTagWindow.getSelectedTag();
        if(tag!=null){
            addedTags.add(tag);
            textField.setText(textField.getText()+tag.getTagName());
        }
    }

    private void confirmExit(){
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
        ArrayList<Tag> toDelete = new ArrayList<>();
        for(int i=0;i< getAddedTags().size();i++){
            if(!textField.getText().contains(getAddedTags().get(i).getTagName())){
                toDelete.add(getAddedTags().get(i));
            }
        }
        getAddedTags().removeAll(toDelete);
        if(addedTags.isEmpty() && !textField.getText().isEmpty()){
            this.localExpression = new Expression(textField.getText(),addedTags);
            try{
                this.localExpression.evaluate();
            } catch (CompileException | InvocationTargetException e) {
                confirmExit();
            }
            this.close();
        } else if(!textField.getText().isEmpty()){
            this.localExpression = new Expression(textField.getText(),addedTags);
            this.close();
        }else{
            confirmExit();
        }

    }
}
