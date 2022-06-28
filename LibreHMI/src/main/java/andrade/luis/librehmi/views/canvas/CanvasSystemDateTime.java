package andrade.luis.librehmi.views.canvas;

import andrade.luis.librehmi.models.CanvasObjectData;
import andrade.luis.librehmi.views.SetDateTimeProperties;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.animation.Animation;

/**
 * Clase que define el objeto CanvasSystemDateTime, que permitirá mostrar la fecha y hora del sistema en tiempo real
 */
public class CanvasSystemDateTime extends CanvasLabel {

    /**
     * Constructor para pegar un CanvasSystemDateTime copiado o regenerarlo desde el archivo
     * @param canvasObjectData CanvasObjectData conteniendo la información del objeto a generar
     */
    public CanvasSystemDateTime(CanvasObjectData canvasObjectData) {
        super(canvasObjectData);
        this.getCanvasObjectData().setType("SystemDateTime");
        this.getCanvasObjectData().setDataType("Texto de Fecha y Hora");
        this.getCanvasObjectData().setSuperType("Figure");
        if(this.getCanvasObjectData().getFontFamily()!=null && this.getCanvasObjectData().getFontStyle()!=null){
            this.getLabel().setFont(
                    Font.font(
                            this.getCanvasObjectData().getFontFamily(),
                            FontWeight.valueOf(this.getCanvasObjectData().getFontStyle()),
                            this.getCanvasObjectData().getFontSize()
                    )
            );
        }
        if(this.getCanvasObjectData().getFontColor()!=null){
            this.getLabel().setTextFill(this.getCanvasObjectData().getFontColor().getColor());
        }
        this.setRotate(this.getCanvasObjectData().getRotation());
    }

    /**
     * Constructor que permite agregar un nuevo CanvasSystemDateTime al canvas
     * @param content Texto del label
     * @param center Posición del objeto
     */
    public CanvasSystemDateTime(String content, CanvasPoint center) {
        super(content, center);
        this.getCanvasObjectData().setType("SystemDateTime");
    }

    /**
     * Permite mostrar la ventana de definición de propiedades del objeto
     */
    @Override
    public void setProperties(){
        SetDateTimeProperties propertiesWindow = new SetDateTimeProperties();
        propertiesWindow.getFontStyleComboBox().getSelectionModel().select(this.getLabel().getFont().getStyle());
        propertiesWindow.getFontFamilyComboBox().getSelectionModel().select(this.getLabel().getFont().getFamily());
        propertiesWindow.getFontSizeField().setText(String.valueOf(this.getLabel().getFont().getSize()));
        propertiesWindow.getRotationTextField().setText(String.valueOf(this.getCanvasObjectData().getRotation()));
        propertiesWindow.getColorPicker().setValue((Color) this.getLabel().getTextFill());
        propertiesWindow.getDateTimeField().setText(this.getCanvasObjectData().getData());
        propertiesWindow.showAndWait();
        double rotation = Double.parseDouble(propertiesWindow.getRotationTextField().getText());
        double fontSize = Double.parseDouble(propertiesWindow.getFontSizeField().getText());
        this.getCanvasObjectData().setData(propertiesWindow.getDateTimeField().getText());
        this.setProperties(rotation,new CanvasColor(propertiesWindow.getColorPicker().getValue()),propertiesWindow.getFontStyle(),propertiesWindow.getFontFamilyComboBox().getValue(),fontSize);
        this.getHmiApp().setWasModified(true);
    }

    /**
     * Permite definir el hilo de actualización de fecha y hora
     */
    public void setTimeline() {
        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        (ActionEvent actionEvent) -> {
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(this.getCanvasObjectData().getData());
                            LocalDateTime now = LocalDateTime.now();
                            this.getLabel().setText(dtf.format(now));
                        }), new KeyFrame(Duration.seconds(1)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
