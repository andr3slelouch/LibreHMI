package andrade.luis.librehmi.views.canvas;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import javafx.scene.paint.Color;

import java.io.Serializable;

/**
 * Clase que define el objeto CanvasColor que permitirá exportar los valores de color
 */
public class CanvasColor implements Serializable {
    @SerializedName("red")
    @Expose
    private double red;
    @SerializedName("green")
    @Expose
    private double green;
    @SerializedName("blue")
    @Expose
    private double blue;
    @SerializedName("opacity")
    @Expose
    private double opacity;

    public CanvasColor(double red, double green, double blue, double opacity){
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.opacity = opacity;
    }
    public CanvasColor(Color color){
        this.red = color.getRed();
        this.green = color.getGreen();
        this.blue = color.getBlue();
        this.opacity = color.getOpacity();
    }

    public CanvasColor() {

    }

    /**
     * Permite generar el valor hexadecimal del color definido
     * @return String de valor hexadecimal del color
     */
    public String toHexString() {
        int r = ((int) Math.round(red     * 255)) << 24;
        int g = ((int) Math.round(green   * 255)) << 16;
        int b = ((int) Math.round(blue    * 255)) << 8;
        int a = ((int) Math.round(opacity * 255));
        return String.format("#%08X", (r + g + b + a));
    }

    /**
     * Permite retornar un objeto Color compatible con los objetos JavaFX
     * @return Objeto Color de JavaFX
     */
    public Color getColor() {
        return new Color(red, green, blue, opacity);
    }

    @Override
    public String toString() {
        return "CanvasColor{" +
                "red=" + red +
                ", green=" + green +
                ", blue=" + blue +
                ", opacity=" + opacity +
                '}';
    }

}
