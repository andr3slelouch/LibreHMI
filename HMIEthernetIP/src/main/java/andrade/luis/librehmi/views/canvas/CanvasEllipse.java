package andrade.luis.librehmi.views.canvas;

import andrade.luis.librehmi.models.CanvasObjectData;
import andrade.luis.librehmi.views.windows.SetGeometricFigurePropertiesWindow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;


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
        this.getCanvasObjectData().setSuperType("Figure");
        this.setContextMenu();
        this.setRotate(this.getCanvasObjectData().getRotation());
        if(this.getCanvasObjectData().getPrimaryColor()!=null){
            this.ellipse.setFill(this.getCanvasObjectData().getPrimaryColor().getColor());
        }
    }

    @Override
    public void setProperties(){
        SetGeometricFigurePropertiesWindow setGeometricFigurePropertiesWindow = new SetGeometricFigurePropertiesWindow(this.getCanvasObjectData().getWidth(),this.getCanvasObjectData().getHeight());
        setGeometricFigurePropertiesWindow.setTitle("Propiedades de la Elipse");
        setGeometricFigurePropertiesWindow.setHeight(210);
        setGeometricFigurePropertiesWindow.setWidth(325);

        if(this.getCanvasObjectData().getPrimaryColor()!=null){
            setGeometricFigurePropertiesWindow.getColorPicker().setValue(this.getCanvasObjectData().getPrimaryColor().getColor());
        }else{
            setGeometricFigurePropertiesWindow.getColorPicker().setValue((Color) this.ellipse.getFill());
        }
        setGeometricFigurePropertiesWindow.getRotationTextField().setText(String.valueOf(this.getCanvasObjectData().getRotation()));
        setGeometricFigurePropertiesWindow.showAndWait();

        boolean isModifyingColor = setGeometricFigurePropertiesWindow.isModifyingColor();
        this.getCanvasObjectData().setModifyingColors(isModifyingColor);
        double rotation = Double.parseDouble(setGeometricFigurePropertiesWindow.getRotationTextField().getText());
        this.getCanvasObjectData().setRotation(rotation);
        this.setRotate(rotation);

        CanvasColor color = new CanvasColor(setGeometricFigurePropertiesWindow.getColorPicker().getValue());
        this.ellipse.setFill(color.getColor());
        this.getCanvasObjectData().setPrimaryColor(color);
        this.getCanvasObjectData().setWidth(setGeometricFigurePropertiesWindow.getVbox().getWidthFromField());
        this.getCanvasObjectData().setHeight(setGeometricFigurePropertiesWindow.getVbox().getHeightFromField());
        this.setSize(this.getCanvasObjectData().getWidth(), this.getCanvasObjectData().getHeight());
        this.getHmiApp().setWasModified(true);
        this.ellipse.setRadiusX(this.getCanvasObjectData().getWidth());
        this.ellipse.setRadiusY(this.getCanvasObjectData().getHeight());
    }
}
