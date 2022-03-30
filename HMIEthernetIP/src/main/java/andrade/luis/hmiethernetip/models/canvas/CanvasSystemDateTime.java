package andrade.luis.hmiethernetip.models.canvas;

import andrade.luis.hmiethernetip.views.SetDateTimeProperties;
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

public class CanvasSystemDateTime extends CanvasLabel {
    public Timeline getTimeline() {
        return timeline;
    }

    private Timeline timeline;
    public CanvasSystemDateTime(CanvasObjectData canvasObjectData) {
        super(canvasObjectData);
        this.getCanvasObjectData().setType("SystemDateTime");
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

    public CanvasSystemDateTime(String content, CanvasPoint center) {
        super(content, center);
        this.getCanvasObjectData().setType("SystemDateTime");
    }

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
        this.getLabel().setFont(
                Font.font(
                        propertiesWindow.getFontFamilyComboBox().getValue(),
                        propertiesWindow.getFontStyle(),
                        Double.parseDouble(propertiesWindow.getFontSizeField().getText()
                        )
                )
        );
        this.getLabel().setTextFill(propertiesWindow.getColorPicker().getValue());
        double rotation = Double.parseDouble(propertiesWindow.getRotationTextField().getText());
        this.setRotate(rotation);
        this.getCanvasObjectData().setRotation(rotation);
        this.getCanvasObjectData().setFontColor(new CanvasColor(propertiesWindow.getColorPicker().getValue()));
        this.getCanvasObjectData().setFontStyle(propertiesWindow.getFontStyle().name());
        this.getCanvasObjectData().setFontFamily(propertiesWindow.getFontFamilyComboBox().getValue());
        this.getCanvasObjectData().setFontSize(Double.parseDouble(propertiesWindow.getFontSizeField().getText()));
        this.getCanvasObjectData().setData(propertiesWindow.getDateTimeField().getText());
        this.getHmiApp().setWasModified(true);
    }

    public void setTimeline() {
        timeline = new Timeline(
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
