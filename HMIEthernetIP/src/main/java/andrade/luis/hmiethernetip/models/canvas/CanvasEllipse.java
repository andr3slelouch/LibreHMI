package andrade.luis.hmiethernetip.models.canvas;

import andrade.luis.hmiethernetip.views.SetGeometricCanvasObjectPropertiesWindow;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

import java.util.logging.Level;

public class CanvasEllipse extends CanvasObject{
    private Ellipse ellipse;

    public CanvasEllipse(CanvasPoint center){
        super(center);
        setData(this.getCanvasObjectData().getPosition().getX(), this.getCanvasObjectData().getPosition().getY(), 75,75);
    }

    public CanvasEllipse(CanvasObjectData canvasObjectData){
        super(canvasObjectData);
        setData(this.getCanvasObjectData().getPosition().getX(), this.getCanvasObjectData().getPosition().getY(), this.getCanvasObjectData().getWidth(),this.getCanvasObjectData().getHeight());
    }

    private void setData(double x, double y, double width, double height) {
        this.ellipse = new Ellipse(x, y, width, height);
        this.getCanvasObjectData().setWidth(width);
        this.getCanvasObjectData().setHeight(height);
        this.setCenter(this.ellipse);
        this.getCanvasObjectData().setType("Ellipse");
        this.setContextMenu();
        this.setRotate(this.getCanvasObjectData().getRotation());
        if(this.getCanvasObjectData().getPrimaryColor()!=null){
            this.ellipse.setFill(this.getCanvasObjectData().getPrimaryColor().getColor());
        }
    }

    @Override
    public void properties(){
        SetGeometricCanvasObjectPropertiesWindow setGeometricCanvasObjectPropertiesWindow = new SetGeometricCanvasObjectPropertiesWindow(this.getCanvasObjectData().getWidth(),this.getCanvasObjectData().getHeight());
        setGeometricCanvasObjectPropertiesWindow.setTitle("Propiedades de la Elipse");
        setGeometricCanvasObjectPropertiesWindow.setHeight(375);
        if(this.getCanvasObjectData().isModifyingColors()){
            setGeometricCanvasObjectPropertiesWindow.setModifyingColor(true);
            setGeometricCanvasObjectPropertiesWindow.getModColorRB().setSelected(true);
            setGeometricCanvasObjectPropertiesWindow.getBrightnessTextField().setText(String.valueOf(this.getCanvasObjectData().getBrightness()));
            setGeometricCanvasObjectPropertiesWindow.getContrastTextField().setText(String.valueOf(this.getCanvasObjectData().getContrast()));
            setGeometricCanvasObjectPropertiesWindow.getHueTextField().setText(String.valueOf(this.getCanvasObjectData().getHue()));
            setGeometricCanvasObjectPropertiesWindow.getSaturationTextField().setText(String.valueOf(this.getCanvasObjectData().getSaturation()));
        }
        if(this.getCanvasObjectData().getPrimaryColor()!=null){
            setGeometricCanvasObjectPropertiesWindow.getColorPicker().setValue(this.getCanvasObjectData().getPrimaryColor().getColor());
        }else{
            setGeometricCanvasObjectPropertiesWindow.getColorPicker().setValue((Color) this.ellipse.getFill());
        }
        setGeometricCanvasObjectPropertiesWindow.getRotationTextField().setText(String.valueOf(this.getCanvasObjectData().getRotation()));
        setGeometricCanvasObjectPropertiesWindow.showAndWait();

        boolean isModifyingColor = setGeometricCanvasObjectPropertiesWindow.isModifyingColor();
        this.getCanvasObjectData().setModifyingColors(isModifyingColor);
        double rotation = Double.parseDouble(setGeometricCanvasObjectPropertiesWindow.getRotationTextField().getText());
        this.getCanvasObjectData().setRotation(rotation);
        this.setRotate(rotation);
        double contrast = Double.parseDouble(setGeometricCanvasObjectPropertiesWindow.getContrastTextField().getText());
        double brightness = Double.parseDouble(setGeometricCanvasObjectPropertiesWindow.getBrightnessTextField().getText());
        double saturation = Double.parseDouble(setGeometricCanvasObjectPropertiesWindow.getSaturationTextField().getText());
        double hue = Double.parseDouble(setGeometricCanvasObjectPropertiesWindow.getHueTextField().getText());
        CanvasColor color = new CanvasColor(setGeometricCanvasObjectPropertiesWindow.getColorPicker().getValue());
        modifyColors(color,contrast,brightness,saturation,hue);
        this.getCanvasObjectData().setWidth(setGeometricCanvasObjectPropertiesWindow.getWidthFromField());
        this.getCanvasObjectData().setHeight(setGeometricCanvasObjectPropertiesWindow.getHeightFromField());
        this.setSize(this.getCanvasObjectData().getWidth(), this.getCanvasObjectData().getHeight());
        this.getHmiApp().setWasModified(true);
        this.ellipse.setRadiusX(this.getCanvasObjectData().getWidth());
        this.ellipse.setRadiusY(this.getCanvasObjectData().getHeight());
    }

    private void modifyColors(CanvasColor color, double contrast, double brightness, double saturation, double hue) {
        this.ellipse.setFill(color.getColor());
        this.getCanvasObjectData().setPrimaryColor(color);
        if (this.getCanvasObjectData().isModifyingColors()) {
            logger.log(Level.INFO,"Applying colors");
            Lighting lighting = new Lighting(new Light.Distant(45, 90, color.getColor()));
            ColorAdjust bright = new ColorAdjust();
            bright.setContrast(contrast);
            bright.setSaturation(saturation);
            bright.setBrightness(brightness);
            bright.setHue(hue);
            lighting.setContentInput(bright);
            lighting.setSurfaceScale(0.0);
            this.ellipse.setEffect(lighting);
            this.getCanvasObjectData().setModifyingColors(true);
            this.getCanvasObjectData().setContrast(contrast);
            this.getCanvasObjectData().setBrightness(brightness);
            this.getCanvasObjectData().setHue(hue);
        }
    }
}
