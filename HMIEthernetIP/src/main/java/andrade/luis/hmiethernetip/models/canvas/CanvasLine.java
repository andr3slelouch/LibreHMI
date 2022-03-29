package andrade.luis.hmiethernetip.models.canvas;

import andrade.luis.hmiethernetip.views.SetGeometricCanvasObjectPropertiesWindow;
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
        this.line = new Line();
        this.line.setStartX(x1);
        this.line.setStartY(y1);
        this.line.setEndX(x2);
        this.line.setEndY(y2);

        this.getCanvasObjectData().setType("Line");
        this.setCenter(this.line);
        if(this.getCanvasObjectData().isRotated()){
            this.line.setRotate(this.getCanvasObjectData().getRotation());
        }
        this.setContextMenu();
        MenuItem percentFillMI = new MenuItem("Propiedades");
        percentFillMI.setId("#propertiesMI");
        percentFillMI.setOnAction(actionEvent -> this.properties());
        this.getRightClickMenu().getItems().add(percentFillMI);
        if(this.getCanvasObjectData().getPrimaryColor()!=null){
            modifyColors(this.getCanvasObjectData().getPrimaryColor(),this.getCanvasObjectData().getContrast(),this.getCanvasObjectData().getBrightness(),this.getCanvasObjectData().getSaturation(),this.getCanvasObjectData().getHue());
        }else{
            this.getCanvasObjectData().setPrimaryColor(new CanvasColor((Color) this.line.getStroke()));
        }
        this.line.setStrokeWidth(this.getCanvasObjectData().getWidth());
        this.setRotate(this.getCanvasObjectData().getRotation());
    }

    @Override
    public void properties(){
        SetGeometricCanvasObjectPropertiesWindow setGeometricCanvasObjectPropertiesWindow = new SetGeometricCanvasObjectPropertiesWindow(this.line.getStrokeWidth(),this.getCanvasObjectData().getHeight());
        setGeometricCanvasObjectPropertiesWindow.setTitle("Propiedades de Línea");
        setGeometricCanvasObjectPropertiesWindow.getHeightField().setDisable(true);
        setGeometricCanvasObjectPropertiesWindow.setHeight(375);
        if(this.getCanvasObjectData().isModifyingColors()){
            setGeometricCanvasObjectPropertiesWindow.setModifyingColor(true);
            setGeometricCanvasObjectPropertiesWindow.getModColorRB().setSelected(true);
            setGeometricCanvasObjectPropertiesWindow.getBrightnessTextField().setText(String.valueOf(this.getCanvasObjectData().getBrightness()));
            setGeometricCanvasObjectPropertiesWindow.getContrastTextField().setText(String.valueOf(this.getCanvasObjectData().getContrast()));
            setGeometricCanvasObjectPropertiesWindow.getHueTextField().setText(String.valueOf(this.getCanvasObjectData().getHue()));
            setGeometricCanvasObjectPropertiesWindow.getSaturationTextField().setText(String.valueOf(this.getCanvasObjectData().getSaturation()));
        }

        setGeometricCanvasObjectPropertiesWindow.getColorPicker().setValue(this.getCanvasObjectData().getPrimaryColor().getColor());
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
        this.line.setStrokeWidth(this.getCanvasObjectData().getWidth());
    }

    public void modifyColors(CanvasColor color, double contrast, double brightness, double saturation, double hue) {
        this.line.setStroke(color.getColor());
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
            this.line.setEffect(lighting);
            this.getCanvasObjectData().setModifyingColors(true);
            this.getCanvasObjectData().setContrast(contrast);
            this.getCanvasObjectData().setBrightness(brightness);
            this.getCanvasObjectData().setHue(hue);
        }
    }
}
