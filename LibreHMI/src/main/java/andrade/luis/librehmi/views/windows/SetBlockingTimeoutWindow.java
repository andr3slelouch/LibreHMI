package andrade.luis.librehmi.views.windows;

import andrade.luis.librehmi.util.TextFormatters;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import static andrade.luis.librehmi.util.Alerts.showAlert;

public class SetBlockingTimeoutWindow extends Stage {
    private final TextField timeoutTextField;

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    int timeout;
    int minValue=30;
    int maxValue=60*5;

    public SetBlockingTimeoutWindow(int timeout) {
        this.timeout = timeout;
        StackPane root = new StackPane();
        this.setTitle("Propiedades de bloqueo");
        Label label = new Label("Defina el tiempo de bloqueo (Segundos):");
        timeoutTextField = new TextField("");
        timeoutTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), timeout, TextFormatters.digitFilter));
        HBox dataHBox = new HBox();
        dataHBox.getChildren().addAll(label,timeoutTextField);

        Button finishSelectionButton = new Button("OK");
        finishSelectionButton.setAlignment(Pos.CENTER);

        finishSelectionButton.setOnAction(actionEvent -> finishingAction());

        Button cancelButton = new Button("Cancelar");
        cancelButton.setAlignment(Pos.CENTER);
        cancelButton.setOnAction(actionEvent -> this.close());

        HBox hbox = new HBox();
        hbox.getChildren().addAll(cancelButton, finishSelectionButton);
        hbox.setAlignment(Pos.BASELINE_RIGHT);

        VBox vbox = new VBox();
        vbox.getChildren().addAll(dataHBox,hbox);
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(25,5,25,5));

        root.getChildren().add(vbox);

        this.setScene(new Scene(root,422,75));
    }

    private void finishingAction() {
        timeout = Integer.parseInt(timeoutTextField.getText());
        boolean isValid = false;
        if(timeout<minValue){
            isValid = showAlert(Alert.AlertType.WARNING,"Advertencia: El tiempo de bloqueo es muy bajo","Definir un tiempo de bloqueo tan bajo puede ser perjudicial para la experiencia de usuario, ¿Continuar?");
        }else if(timeout>maxValue){
            isValid = showAlert(Alert.AlertType.WARNING,"Advertencia: El tiempo de bloqueo es muy alto","Definir un tiempo de bloqueo tan alto podría vulnerar la seguridad del sistema, ¿Continuar?");
        }
        if(isValid){
            this.close();
        }
    }
}
