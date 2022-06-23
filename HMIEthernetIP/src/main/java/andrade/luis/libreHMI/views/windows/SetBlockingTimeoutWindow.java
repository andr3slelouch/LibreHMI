package andrade.luis.libreHMI.views.windows;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import java.util.Optional;
import java.util.function.UnaryOperator;

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
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (!newText.matches("^(\\+|-)?\\d+$")) {
                change.setText("");
                change.setRange(change.getRangeStart(), change.getRangeStart());
            }
            return change;
        };
        timeoutTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), timeout, integerFilter));
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
            isValid = showAlert("Advertencia: El tiempo de bloqueo es muy bajo","Definir un tiempo de bloqueo tan bajo puede ser perjudicial para la experiencia de usuario, ¿Continuar?");
        }else if(timeout>maxValue){
            isValid = showAlert("Advertencia: El tiempo de bloqueo es muy alto","Definir un tiempo de bloqueo tan alto podría vulnerar la seguridad del sistema, ¿Continuar?");
        }
        if(isValid){
            this.close();
        }
    }

    private boolean showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(message);

        ButtonType yesButton = new ButtonType("Sí", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(noButton,yesButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            alert.close();
            return true;
        } else if (result.isPresent() && result.get() == noButton) {
            alert.close();
            return false;
        }
        return true;
    }
}
