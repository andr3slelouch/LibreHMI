package andrade.luis.hmiethernetip.models;

public class CanvasPoint {
    private double x;
    private double y;

    public CanvasPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "CanvasPoint{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
