package andrade.luis.librehmi.views.canvas;

import andrade.luis.librehmi.models.CanvasObjectData;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Clase que el objeto CanvasLabel, que permitirá contener un texto
 */
public class CanvasLabel extends CanvasObject {
    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    private Label label;

    /**
     * Constructor para pegar un CanvasLabel copiado o regenerarlo desde el archivo
     * @param canvasObjectData CanvasObjectData conteniendo la información del objeto a generar
     */
    public CanvasLabel(CanvasObjectData canvasObjectData){
        super(canvasObjectData);
        this.setData(canvasObjectData.getData());
    }

    /**
     * Constructor que permite agregar un nuevo CanvasLabel al canvas
     * @param content Texto del label
     * @param center Posición del objeto
     */
    public CanvasLabel(String content, CanvasPoint center){
        super(center);
        this.getCanvasObjectData().setData(content);
        this.setData(content);
    }
    public CanvasLabel(){
        super();
    }

    /**
     * Permite definir la posición del objeto
     * @param center Posición del objeto en el canvas
     */
    @Override
    public void setPosition(CanvasPoint center){
        super.setPosition(center);
        this.setCenter(this.label);
    }

    /**
     * Permite definir las propiedades del label
     * @param content Texto del label
     */
    public void setData(String content){
        this.label = new Label(content);
        this.setCenter(this.label);
        this.getCanvasObjectData().setType("Label");
        this.getCanvasObjectData().setData(content);
        this.getCanvasObjectData().setWidth(this.label.getWidth());
        this.getCanvasObjectData().setHeight(this.label.getHeight());
    }

    /**
     * Permite definir las propiedades de rotación, Color de fuente, estilo de fuente, el nombre de fuente
     * y el tamaño de fuente
     * @param rotation Valor para rotar la representación
     * @param fontColor Color de fuente
     * @param fontStyle Estilo de fuente
     * @param fontFamily Familia de la fuente
     * @param fontSize Tamaño de fuente
     */
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
