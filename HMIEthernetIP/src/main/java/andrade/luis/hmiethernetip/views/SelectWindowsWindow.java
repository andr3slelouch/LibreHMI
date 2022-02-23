package andrade.luis.hmiethernetip.views;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class SelectWindowsWindow extends Stage {
    private final StackPane root;

    public SelectWindowsWindow(ArrayList<String> itemsForList){
        root = new StackPane();
        final Label label = new Label("Seleccione una Ventana");
        for(String item : itemsForList){

        }

    }
}
