package andrade.luis.librehmi.views.canvas;

import andrade.luis.librehmi.models.CanvasObjectData;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class CanvasLabel extends CanvasObject {
    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    private Label label;
    public CanvasLabel(CanvasObjectData canvasObjectData){
        super(canvasObjectData);
        this.setData(canvasObjectData.getData());
    }
    public CanvasLabel(String content, CanvasPoint center){
        super(center);
        this.getCanvasObjectData().setData(content);
        this.setData(content);
    }
    public CanvasLabel(){
        super();
    }


    @Override
    public void setPosition(CanvasPoint center){
        super.setPosition(center);
        this.setCenter(this.label);
    }

    public void setData(String content){
        this.label = new Label(content);
        this.setCenter(this.label);
        this.getCanvasObjectData().setType("Label");
        this.getCanvasObjectData().setData(content);
        this.getCanvasObjectData().setWidth(this.label.getWidth());
        this.getCanvasObjectData().setHeight(this.label.getHeight());
    }

    public void setProperties(double rotation,CanvasColor fontColor,FontWeight fontStyle,String fontFamily, double fontSize){
        this.getLabel().setFont(
                Font.font(
                        fontFamily,
                        fontStyle,
                        fontSize
                )
        );
        this.getLabel().setTextFill(fontColor.getColor());
        this.setRotate(rotation);
        this.getCanvasObjectData().setRotation(rotation);
        this.getCanvasObjectData().setFontColor(fontColor);
        this.getCanvasObjectData().setFontStyle(fontStyle.name());
        this.getCanvasObjectData().setFontFamily(fontFamily);
        this.getCanvasObjectData().setFontSize(fontSize);
    }
}
