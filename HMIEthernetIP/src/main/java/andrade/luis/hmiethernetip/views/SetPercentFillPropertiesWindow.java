package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.models.CanvasColor;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class SetPercentFillPropertiesWindow extends WriteExpressionWindow{
    public CanvasColor getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(CanvasColor primaryColor) {
        this.primaryColor = primaryColor;
    }

    public CanvasColor getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(CanvasColor backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    private CanvasColor primaryColor = new CanvasColor(Color.BLACK);
    private CanvasColor backgroundColor = new CanvasColor(Color.GREEN);
    public SetPercentFillPropertiesWindow(){
        super(500,175);
        HBox primaryHBox = new HBox();
        Label primaryLabel = new Label("Seleccione el color primario:");
        ColorPicker primaryColorPicker = new ColorPicker();
        primaryColorPicker.setOnAction(actionEvent -> primaryColor = new CanvasColor(primaryColorPicker.getValue()));
        primaryHBox.getChildren().addAll(primaryLabel,primaryColorPicker);
        HBox backgroundHBox = new HBox();
        Label backgroundLabel = new Label("Seleccione el color de fondo:");
        ColorPicker backgroundColorPicker = new ColorPicker(Color.BLACK);
        backgroundColorPicker.setOnAction(actionEvent -> backgroundColor = new CanvasColor(backgroundColorPicker.getValue()));
        backgroundHBox.getChildren().addAll(backgroundLabel,backgroundColorPicker);
        this.getVbox().getChildren().add(2,primaryHBox);
        this.getVbox().getChildren().add(3,backgroundHBox);
    }
}
