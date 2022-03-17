package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.models.Alarm;
import andrade.luis.hmiethernetip.models.canvas.CanvasAlarmDisplay;
import andrade.luis.hmiethernetip.models.canvas.CanvasPoint;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ManageAlarmsWindow extends Stage {
    private ArrayList<Alarm> alarmsList;
    public ManageAlarmsWindow(ArrayList<Alarm> alarmsList) {
        StackPane root = new StackPane();
        final Label label = new Label("Seleccione la alarma a administrar");
        CanvasAlarmDisplay canvasAlarmDisplay= new CanvasAlarmDisplay(new CanvasPoint(0,0),false);
        canvasAlarmDisplay.setSelected(false);
        canvasAlarmDisplay.setTableItems(alarmsList);
        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, canvasAlarmDisplay);

        root.getChildren().add(vbox);
        Scene scene = new Scene(root,980,500);
        this.setScene(scene);
    }
}
