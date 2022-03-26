package andrade.luis.hmiethernetip.models.canvas;

import andrade.luis.hmiethernetip.views.CanvasObjectPropertiesWindow;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.logging.Level;

public class CanvasLine extends CanvasObject{
    private Line line;

    public CanvasLine(CanvasPoint positionCanvasPoint, ArrayList<Double> positionCanvasPoints){
        super(positionCanvasPoint);
        if(positionCanvasPoints.size()==4){
            this.getCanvasObjectData().setPolygonPoints(positionCanvasPoints);
            setData(this.getCanvasObjectData().getPolygonPoints().get(0), this.getCanvasObjectData().getPolygonPoints().get(1),this.getCanvasObjectData().getPolygonPoints().get(2),this.getCanvasObjectData().getPolygonPoints().get(3));
        }
    }

    public CanvasLine(CanvasObjectData canvasObjectData){
        super(canvasObjectData);
        this.getCanvasObjectData().setPolygonPoints(canvasObjectData.getPolygonPoints());
        setData(this.getCanvasObjectData().getPolygonPoints().get(0), this.getCanvasObjectData().getPolygonPoints().get(1),this.getCanvasObjectData().getPolygonPoints().get(2),this.getCanvasObjectData().getPolygonPoints().get(3));
    }

    public void setData(double x1, double y1,double x2,double y2){
        logger.log(Level.INFO,"X1"+x1+"Y1"+y1+"X2"+x2+"Y2"+y2);
        this.line = new Line();
        this.line.setStartX(x1);
        this.line.setStartY(y1);
        this.line.setEndX(x2);
        this.line.setEndY(y2);

        this.getCanvasObjectData().setPrimaryColor(new CanvasColor((Color) this.line.getStroke()));
        this.getCanvasObjectData().setType("Line");
        this.setCenter(this.line);
        if(this.getCanvasObjectData().isRotated()){
            this.line.setRotate(this.getCanvasObjectData().getRotation());
        }
        this.setContextMenu();
        MenuItem percentFillMI = new MenuItem("Propiedades");
        percentFillMI.setId("#propertiesMI");
        percentFillMI.setOnAction(actionEvent -> this.resize());
        this.getRightClickMenu().getItems().add(percentFillMI);
    }

    @Override
    public void resize(){
        CanvasObjectPropertiesWindow canvasObjectPropertiesWindow = new CanvasObjectPropertiesWindow(this.line.getStrokeWidth(),this.getCanvasObjectData().getHeight());
        canvasObjectPropertiesWindow.setHeight(500);
        if(this.getCanvasObjectData().isModifyingColors()){
            canvasObjectPropertiesWindow.getBrightnessTextField().setText(String.valueOf(this.getCanvasObjectData().getBrightness()));
            canvasObjectPropertiesWindow.getContrastTextField().setText(String.valueOf(this.getCanvasObjectData().getContrast()));
            canvasObjectPropertiesWindow.getHueTextField().setText(String.valueOf(this.getCanvasObjectData().getHue()));
            canvasObjectPropertiesWindow.getSaturationTextField().setText(String.valueOf(this.getCanvasObjectData().getSaturation()));
        }

        canvasObjectPropertiesWindow.getColorPicker().setValue(this.getCanvasObjectData().getPrimaryColor().getColor());
        canvasObjectPropertiesWindow.getRotationTextField().setText(String.valueOf(this.getCanvasObjectData().getRotation()));

        canvasObjectPropertiesWindow.showAndWait();


        boolean isModifyingColor = canvasObjectPropertiesWindow.isModifyingColor();
        this.getCanvasObjectData().setModifyingColors(isModifyingColor);
        double rotation = Double.parseDouble(canvasObjectPropertiesWindow.getRotationTextField().getText());
        this.setRotate(rotation);
        double contrast = Double.parseDouble(canvasObjectPropertiesWindow.getContrastTextField().getText());
        double brightness = Double.parseDouble(canvasObjectPropertiesWindow.getBrightnessTextField().getText());
        double saturation = Double.parseDouble(canvasObjectPropertiesWindow.getSaturationTextField().getText());
        double hue = Double.parseDouble(canvasObjectPropertiesWindow.getHueTextField().getText());
        CanvasColor color = new CanvasColor(canvasObjectPropertiesWindow.getColorPicker().getValue());
        modifyImageViewColors(color,contrast,brightness,saturation,hue);
        this.getCanvasObjectData().setWidth(canvasObjectPropertiesWindow.getWidthFromField());
        this.getCanvasObjectData().setHeight(canvasObjectPropertiesWindow.getHeightFromField());
        this.setSize(this.getCanvasObjectData().getWidth(), this.getCanvasObjectData().getHeight());
        this.getHmiApp().setWasModified(true);
        this.line.setStrokeWidth(this.getCanvasObjectData().getWidth());
    }

    public void modifyImageViewColors(CanvasColor color, double contrast, double brightness, double saturation, double hue) {
        this.line.setStroke(color.getColor());
        this.getCanvasObjectData().setPrimaryColor(color);
        if (this.line != null && this.getCanvasObjectData().isModifyingColors()) {
            Lighting lighting = new Lighting(new Light.Distant(45, 90, color.getColor()));
            ColorAdjust bright = new ColorAdjust();
            bright.setContrast(contrast);
            bright.setHue(-0.05);
            bright.setSaturation(saturation);
            bright.setBrightness(brightness);
            bright.setHue(hue);
            lighting.setContentInput(bright);
            lighting.setSurfaceScale(0.0);
            this.line.setEffect(lighting);
            this.getCanvasObjectData().setModifyingColors(true);
            this.getCanvasObjectData().setContrast(contrast);
            this.getCanvasObjectData().setBrightness(brightness);
            this.getCanvasObjectData().setHue(hue);
        }
    }

    public void modifyRotation(double rotation){
        if(this.line!=null){
            this.setRotate(rotation);
        }
    }
}
