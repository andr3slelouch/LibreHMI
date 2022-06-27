package andrade.luis.librehmi.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Clase que contiene los métodos estáticos para mostrar ventanas de alerta
 */
public class Alerts {
    private Alerts(){
        throw new IllegalStateException("Alerts");
    }

    /**
     * Permite mostrar una ventana de alerta con los botones de OK y Cancelar
     * @param type Tipo de alerta a mostrarse del tipo AlertType
     * @param title Título de la ventana
     * @param headerMessage Mensaje central de la ventana
     * @param contentMessage Contenido de la ventana
     * @return true si el usuario dió clic en OK
     */
    public static boolean showAlert(Alert.AlertType type, String title, String headerMessage, String contentMessage) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerMessage);
        if(contentMessage.isEmpty()){
            alert.setContentText(contentMessage);
        }

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);


        alert.getButtonTypes().setAll(cancelButton, okButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == okButton) {
            alert.close();
            return true;
        } else if (result.isPresent() && result.get() == cancelButton) {
            alert.close();
            return false;
        }
        return true;
    }

    /**
     * Permite mostrar una ventana de alerta con los botones de Sí y No
     * @param type Tipo de alerta a mostrarse del tipo AlertType
     * @param title Título de la ventana
     * @param message Mensaje central de la ventana
     * @return true si el usuario dió clic en Sí
     */
    public static boolean showAlert(Alert.AlertType type,String title,String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(message);

        ButtonType okButton = new ButtonType("Sí",ButtonBar.ButtonData.YES);
        ButtonType cancelButton = new ButtonType("No",ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(cancelButton,okButton);

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == okButton)
        {
            alert.close();
            return true;
        }else if(result.isPresent() && result.get() == cancelButton){
            alert.close();
            return false;
        }
        return false;
    }
}
