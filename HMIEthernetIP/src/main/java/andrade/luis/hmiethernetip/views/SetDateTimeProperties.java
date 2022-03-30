package andrade.luis.hmiethernetip.views;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class SetDateTimeProperties extends SetTextPropertiesWindow{
    public TextField getDateTimeField() {
        return dateTimeField;
    }

    public void setDateTimeField(TextField dateTimeField) {
        this.dateTimeField = dateTimeField;
    }

    private TextField dateTimeField;

    public SetDateTimeProperties() {
        super(340,250);
        this.setTitle("Propiedades de Etiqueta de Fecha y Hora");
        Label dateTimeFormat = new Label("Formato de fecha y hora:");
        dateTimeField = new TextField();
        HBox dateTimeHBox = new HBox();
        dateTimeHBox.getChildren().addAll(dateTimeFormat,dateTimeField);
        dateTimeHBox.setSpacing(8);
        vbox.getChildren().add(vbox.getChildren().size()-1,dateTimeHBox);
    }
}