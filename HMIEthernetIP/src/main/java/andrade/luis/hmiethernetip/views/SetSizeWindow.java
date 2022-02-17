package andrade.luis.hmiethernetip.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.Optional;

public class SetSizeWindow extends Stage {
    private final StackPane root;
    private final Scene mainScene;

    public double getWidthFromField() {
        return widthField.getText().isEmpty() ? minWidth : Double.parseDouble(widthField.getText());
    }

    public double getHeightFromField() {
        return heightField.getText().isEmpty() ? minHeight : Double.parseDouble(heightField.getText());
    }

    private final TextField widthField;
    private final TextField heightField;
    private Double minWidth = 50.0;
    private Double minHeight = 50.0;

    public SetSizeWindow(double width, double height) {
        root = new StackPane();
        
        final Label label = new Label("Escriba los tamaños para el objeto");
        Label widthLabel = new Label("Defina el ancho:");
        widthField = new TextField(width+"");
        HBox widthValueHBox = new HBox(widthLabel, widthField);
        widthValueHBox.setSpacing(10);

        Label heightLabel = new Label("Defina el alto:");
        heightField = new TextField(height+"");
        HBox heightValueHBox = new HBox(heightLabel, heightField);
        heightValueHBox.setSpacing(25);

        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(widthValueHBox,heightValueHBox);

        Button finishSelectionButton = new Button("OK");
        finishSelectionButton.setAlignment(Pos.CENTER);

        finishSelectionButton.setOnAction(actionEvent -> {
            finishingAction();
        });

        HBox hbox = new HBox();
        hbox.getChildren().add(finishSelectionButton);
        hbox.setAlignment(Pos.BASELINE_RIGHT);
        vbox.getChildren().add(hbox);

        root.getChildren().add(vbox);
        mainScene = new Scene(root,300,150);
        this.setScene(mainScene);

    }

    private void finishingAction() {
        if(!widthField.getText().isEmpty() && !heightField.getText().isEmpty()){
            this.close();
        } else if(widthField.getText().isEmpty() && heightField.getText().isEmpty()){
            confirmExit("No pueden existir valores vacíos");
        }
        else if(Double.parseDouble(widthField.getText())> minWidth || Double.parseDouble(heightField.getText())> minHeight){
            confirmExit("Los valores deben ser mayores a "+minWidth);
        }
    }

    private void confirmExit(String message){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(message);

        ButtonType okButton = new ButtonType("OK",ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(okButton);

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == okButton)
        {
            alert.close();
        }
    }
}
