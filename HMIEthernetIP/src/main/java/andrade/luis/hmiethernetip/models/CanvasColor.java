package andrade.luis.hmiethernetip.models;

import javafx.scene.paint.Color;

import java.io.Serializable;

public class CanvasColor implements Serializable {
    private double red;
    private double green;
    private double blue;
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
