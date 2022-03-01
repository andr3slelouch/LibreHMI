package andrade.luis.hmiethernetip.views;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class SelectHMISymbolWindow extends Stage {
    private Image selectedImage;
    public SelectHMISymbolWindow(){
        StackPane root = new StackPane();
        final Label label = new Label("Seleccione un símbolo HMi para añadir");
        label.setFont(new Font("Arial", 20));

    }
}
