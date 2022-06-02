package andrade.luis.hmiethernetip.models.canvas;

import andrade.luis.hmiethernetip.views.SetGeometricFigurePropertiesWindow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import java.util.ArrayList;

public class CanvasLine extends CanvasObject{
    private Line line;

    public CanvasLine(CanvasPoint positionCanvasPoint, ArrayList<Double> positionCanvasPoints){
        super(positionCanvasPoint);
        if(positionCanvasPoints.size()==4){
            this.getCanvasObjectData().setPolygonPoints(positionCanvasPoints);
            this.getCanvasObjectData().setWidth(1);
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
        this.getCanvasObjectData().setSuperType("Figure");
        this.setCenter(this.line);
        if(this.getCanvasObjectData().isRotated()){
            this.line.setRotate(this.getCanvasObjectData().getRotation());
        }
        this.setContextMenu();
        if(this.getCanvasObjectData().getPrimaryColor()!=null){
            this.line.setStroke(this.getCanvasObjectData().getPrimaryColor().getColor());
            this.getCanvasObjectData().setPrimaryColor(this.getCanvasObjectData().getPrimaryColor());
        }else{
            this.getCanvasObjectData().setPrimaryColor(new CanvasColor((Color) this.line.getStroke()));
        }
        this.line.setStrokeWidth(this.getCanvasObjectData().getWidth());
        this.setRotate(this.getCanvasObjectData().getRotation());
    }

    @Override
    public void setProperties(){
        SetGeometricFigurePropertiesWindow setGeometricFigurePropertiesWindow = new SetGeometricFigurePropertiesWindow(this.line.getStrokeWidth(),this.getCanvasObjectData().getHeight());
        setGeometricFigurePropertiesWindow.setTitle("Propiedades de Línea");
        setGeometricFigurePropertiesWindow.getVbox().getHeightField().setDisable(true);
        setGeometricFigurePropertiesWindow.setHeight(210);
        setGeometricFigurePropertiesWindow.setWidth(295);

        setGeometricFigurePropertiesWindow.getColorPicker().setValue(this.getCanvasObjectData().getPrimaryColor().getColor());
        setGeometricFigurePropertiesWindow.getRotationTextField().setText(String.valueOf(this.getCanvasObjectData().getRotation()));

        setGeometricFigurePropertiesWindow.showAndWait();


        boolean isModifyingColor = setGeometricFigurePropertiesWindow.isModifyingColor();
        this.getCanvasObjectData().setModifyingColors(isModifyingColor);
        double rotation = Double.parseDouble(setGeometricFigurePropertiesWindow.getRotationTextField().getText());
        this.getCanvasObjectData().setRotation(rotation);
        this.setRotate(rotation);
        CanvasColor color = new CanvasColor(setGeometricFigurePropertiesWindow.getColorPicker().getValue());
        this.line.setStroke(color.getColor());
        this.getCanvasObjectData().setPrimaryColor(color);
        this.getCanvasObjectData().setWidth(setGeometricFigurePropertiesWindow.getVbox().getWidthFromField());
        this.getCanvasObjectData().setHeight(setGeometricFigurePropertiesWindow.getVbox().getHeightFromField());
        this.setSize(this.getCanvasObjectData().getWidth(), this.getCanvasObjectData().getHeight());
        this.getHmiApp().setWasModified(true);
        this.line.setStrokeWidth(this.getCanvasObjectData().getWidth());
    }
}
