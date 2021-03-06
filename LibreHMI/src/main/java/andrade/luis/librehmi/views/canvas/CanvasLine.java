package andrade.luis.librehmi.views.canvas;

import andrade.luis.librehmi.models.CanvasObjectData;
import andrade.luis.librehmi.views.windows.SetGeometricFigurePropertiesWindow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import java.util.ArrayList;

/**
 * Clase que define el objeto CanvasLine, que permitirá tener la figura de una línea en el canvas
 */
public class CanvasLine extends CanvasObject{
    private Line line;

    /**
     * Constructor que permite agregar un nuevo CanvasLine al canvas
     * @param positionCanvasPoint Posición del objeto en el canvas
     * @param positionCanvasPoints Puntos del canvas para dibujar la línea
     */
    public CanvasLine(CanvasPoint positionCanvasPoint, ArrayList<Double> positionCanvasPoints){
        super(positionCanvasPoint);
        if(positionCanvasPoints.size()==4){
            this.getCanvasObjectData().setPolygonPoints(positionCanvasPoints);
            this.getCanvasObjectData().setWidth(1);
            setData(this.getCanvasObjectData().getPolygonPoints().get(0), this.getCanvasObjectData().getPolygonPoints().get(1),this.getCanvasObjectData().getPolygonPoints().get(2),this.getCanvasObjectData().getPolygonPoints().get(3));
        }
    }

    /**
     * Constructor para pegar un CanvasLine copiado o regenerarlo desde el archivo
     * @param canvasObjectData CanvasObjectData conteniendo la información del objeto a generar
     */
    public CanvasLine(CanvasObjectData canvasObjectData){
        super(canvasObjectData);
        this.getCanvasObjectData().setPolygonPoints(canvasObjectData.getPolygonPoints());
        setData(this.getCanvasObjectData().getPolygonPoints().get(0), this.getCanvasObjectData().getPolygonPoints().get(1),this.getCanvasObjectData().getPolygonPoints().get(2),this.getCanvasObjectData().getPolygonPoints().get(3));
    }

    /**
     * Permite definir las propiedades de la línea con los puntos de inicio y de fin
     * @param x1 Posición de inicio en X
     * @param y1 Posición de inicio en Y
     * @param x2 Posición de fin en X
     * @param y2 Posición de fin en Y
     */
    public void setData(double x1, double y1,double x2,double y2){
        this.line = new Line();
        this.line.setStartX(x1);
        this.line.setStartY(y1);
        this.line.setEndX(x2);
        this.line.setEndY(y2);

        this.getCanvasObjectData().setType("Line");
        this.getCanvasObjectData().setDataType("Línea");
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

    /**
     * Permite mostrar la ventana de propiedades
     */
    @Override
    public void setProperties(){
        SetGeometricFigurePropertiesWindow setGeometricFigurePropertiesWindow = new SetGeometricFigurePropertiesWindow(this.line.getStrokeWidth(),this.getCanvasObjectData().getHeight());
        setGeometricFigurePropertiesWindow.setTitle("Propiedades de Línea");
        setGeometricFigurePropertiesWindow.getVbox().getHeightField().setDisable(true);
        setGeometricFigurePropertiesWindow.setHeight(220);
        setGeometricFigurePropertiesWindow.setWidth(293);

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
