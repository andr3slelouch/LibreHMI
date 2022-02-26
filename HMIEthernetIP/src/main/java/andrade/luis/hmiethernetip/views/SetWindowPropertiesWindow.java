package andrade.luis.hmiethernetip.views;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Optional;

public class SetWindowPropertiesWindow extends Stage {
    private StackPane root;

    public TextField getNameField() {
        return nameField;
    }

    public void setNameField(TextField nameField) {
        this.nameField = nameField;
    }

    public TextField getCommentField() {
        return commentField;
    }

    public void setCommentField(TextField commentField) {
        this.commentField = commentField;
    }

    public ColorPicker getWindowColorPicker() {
        return windowColorPicker;
    }

    public void setWindowColorPicker(ColorPicker windowColorPicker) {
        this.windowColorPicker = windowColorPicker;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    private boolean cancelled =true;
    private TextField nameField;
    private TextField commentField;
    private ColorPicker windowColorPicker;
    private ArrayList<String> windowsTitles = new ArrayList<>();
    public SetWindowPropertiesWindow(){
        init("","",Color.WHITESMOKE);
    }
    public SetWindowPropertiesWindow(String title, String commentary, Color color){
        init(title,commentary,color);
    }

    private void init(String title, String commentary, Color color){
        root = new StackPane();
        this.setTitle("Window Propierties");

        HBox nameHBox = new HBox();
        Label nameLabel = new Label("Nombre:  ");
        nameField = new TextField();
        nameField.setPromptText("Ingrese el nombre de la ventana");
        nameField.setText(title);
        Label windowColorLabel = new Label("Color de la Ventana:");
        windowColorPicker = new ColorPicker();
        windowColorPicker.setValue(color);
        nameHBox.getChildren().addAll(nameLabel,nameField,windowColorLabel,windowColorPicker);
        nameHBox.setSpacing(20);

        HBox commentHBox = new HBox();
        Label commentLabel = new Label("Comentario:");
        commentField = new TextField();
        commentField.setPromptText("Ingrese un comentario de la ventana");
        commentField.setText(commentary);
        commentHBox.getChildren().addAll(commentLabel,commentField);
        commentHBox.setSpacing(5);

        Button finishButton = new Button("OK");
        finishButton.setAlignment(Pos.CENTER);
        finishButton.setOnAction(actionEvent -> {
            if(nameField.getText().isEmpty()){
                confirmExit(Alert.AlertType.WARNING, "Advertencia","El campo nombre no puede estar vacío");
            } else if(windowsTitles.contains(nameField.getText())){
                confirmExit(Alert.AlertType.ERROR, "Error","Ya existe una página con el nombre '"+nameField.getText()+"',utilice un nombre diferente");
            }
            else{
                cancelled = false;
                this.close();
            }
        });
        Button cancelButton = new Button("Cancelar");
        cancelButton.setOnAction(actionEvent -> this.close());
        HBox bottomHBox = new HBox();
        bottomHBox.setSpacing(5);
        bottomHBox.getChildren().addAll(cancelButton,finishButton);
        bottomHBox.setAlignment(Pos.BASELINE_RIGHT);

        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(nameHBox,commentHBox,bottomHBox);
        root.getChildren().add(vbox);
        Scene mainScene = new Scene(root,600,110);
        this.setScene(mainScene);
    }

    private void confirmExit(Alert.AlertType type,String title ,String message) {
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

    public ArrayList<String> getWindowsTitles() {
        return windowsTitles;
    }

    public void setPagesTitles(ArrayList<String> windowsTitles) {
        this.windowsTitles = windowsTitles;
    }
}
