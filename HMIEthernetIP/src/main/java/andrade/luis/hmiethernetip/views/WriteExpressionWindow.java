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
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.codehaus.commons.compiler.CompileException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class WriteExpressionWindow extends Stage {
    private TextField textField;

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

    private Button finishSelectionButton;
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
        Button addTagButton = new Button("Añadir Tag");
        addTagButton.setAlignment(Pos.BOTTOM_LEFT);
        addTagButton.setOnAction(actionEvent -> {
            SelectTagWindow selectTagWindow = new SelectTagWindow(false);
            selectTagWindow.showAndWait();
            Tag tag = selectTagWindow.getSelectedTag();
            if(tag!=null){
                addedTags.add(tag);
                textField.setText(textField.getText()+tag.getTagName());
            }
        });
        finishSelectionButton.setOnAction(actionEvent -> {
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

        });
        HBox hbox = new HBox();
        hbox.getChildren().add(addTagButton);
        hbox.getChildren().add(finishSelectionButton);
        hbox.setAlignment(Pos.BASELINE_RIGHT);
        vbox.getChildren().add(hbox);
        root.getChildren().add(vbox);
        mainScene = new Scene(root,width,height);
        this.setScene(mainScene);
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
}