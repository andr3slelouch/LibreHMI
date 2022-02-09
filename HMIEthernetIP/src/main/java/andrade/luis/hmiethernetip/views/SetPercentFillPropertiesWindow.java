package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.models.CanvasColor;
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

    public ColorPicker getPrimaryColorPicker() {
        return primaryColorPicker;
    }

    public void setPrimaryColorPicker(ColorPicker primaryColorPicker) {
        this.primaryColorPicker = primaryColorPicker;
    }

    public ColorPicker getBackgroundColorPicker() {
        return backgroundColorPicker;
    }

    public void setBackgroundColorPicker(ColorPicker backgroundColorPicker) {
        this.backgroundColorPicker = backgroundColorPicker;
    }

    private ColorPicker primaryColorPicker;
    private ColorPicker backgroundColorPicker;
    private CanvasColor primaryColor = new CanvasColor(Color.BLACK);
    private CanvasColor backgroundColor = new CanvasColor(Color.GREEN);
    public SetPercentFillPropertiesWindow(){
        super(500,175);
        this.init(Color.BLACK,Color.GREEN);
    }
    public SetPercentFillPropertiesWindow(Color primaryColor,Color backgroundColor){
        super(500,175);
        this.primaryColor = new CanvasColor(primaryColor);
        this.backgroundColor = new CanvasColor(backgroundColor);
        this.init(this.primaryColor.getColor(),this.backgroundColor.getColor());
    }

    private void init(Color primary, Color background){
        HBox primaryHBox = new HBox();
        Label primaryLabel = new Label("Seleccione el color primario:");
        primaryColorPicker = new ColorPicker(primary);
        primaryColorPicker.setOnAction(actionEvent -> primaryColor = new CanvasColor(primaryColorPicker.getValue()));
        primaryHBox.getChildren().addAll(primaryLabel,primaryColorPicker);

        HBox backgroundHBox = new HBox();
        Label backgroundLabel = new Label("Seleccione el color de fondo:");
        backgroundColorPicker = new ColorPicker(background);
        backgroundColorPicker.setOnAction(actionEvent -> backgroundColor = new CanvasColor(backgroundColorPicker.getValue()));
        backgroundHBox.getChildren().addAll(backgroundLabel,backgroundColorPicker);

        this.getVbox().getChildren().add(2,primaryHBox);
        this.getVbox().getChildren().add(3,backgroundHBox);
    }
}
