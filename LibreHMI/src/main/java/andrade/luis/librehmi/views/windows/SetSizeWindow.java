package andrade.luis.librehmi.views.windows;

import andrade.luis.librehmi.views.SizeVBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.util.Optional;

/**
 * Ventana para la definición de propiedades de tamaño
 */
public class SetSizeWindow extends Stage {
    protected final StackPane root;

    private boolean cancelled = true;

    protected final Scene mainScene;
    protected final Label titleLabel;

    public SizeVBox getVbox() {
        return vbox;
    }

    public void setVbox(SizeVBox vbox) {
        this.vbox = vbox;
    }

    protected SizeVBox vbox;


    /**
     * Constructor de la ventana
     * @param width Ancho de la ventana
     * @param height Alto de la ventana
     */
    public SetSizeWindow(double width, double height) {
        root = new StackPane();
        setTitle("Propiedades de tamaño");
        titleLabel = new Label("Defina los tamaños");

        vbox = new SizeVBox(width,height,-1,-1);
        vbox.getHeightValueHBox().setSpacing(15);
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));

        Button finishSelectionButton = new Button("OK");
        finishSelectionButton.setAlignment(Pos.CENTER);
        finishSelectionButton.setOnAction(actionEvent -> finishingAction());
        Button cancelButton = new Button("Cancelar");
        cancelButton.setAlignment(Pos.CENTER);
        cancelButton.setOnAction(actionEvent -> this.close());

        HBox hbox = new HBox();
        hbox.getChildren().addAll(cancelButton,finishSelectionButton);
        hbox.setAlignment(Pos.BOTTOM_RIGHT);
        hbox.setPadding(new Insets(5, 5, 5, 5));
        vbox.getChildren().add(hbox);

        root.getChildren().add(vbox);
        mainScene = new Scene(root,310,130);
        this.setScene(mainScene);

    }

    /**
     * Permite verificar los campos de la ventana
     */
    protected void finishingAction() {
        if(!vbox.getWidthField().getText().isEmpty() && !vbox.getHeightField().getText().isEmpty()){
            this.close();
            cancelled = false;
        } else if(vbox.getWidthField().getText().isEmpty() && vbox.getHeightField().getText().isEmpty()){
            confirmExit("No pueden existir valores vacíos");
        }
        else if(Double.parseDouble(vbox.getWidthField().getText())> vbox.getMinWidth() || Double.parseDouble(vbox.getHeightField().getText())> vbox.getMinHeight()){
            confirmExit("Los valores deben ser mayores a "+vbox.getMinWidth());
        }
    }

    /**
     * Permite mostrar la ventana de confirmación
     * @param message Mensaje a ser mostrado en la ventana
     */
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

    public boolean isCancelled() {
        return cancelled;
    }

}
