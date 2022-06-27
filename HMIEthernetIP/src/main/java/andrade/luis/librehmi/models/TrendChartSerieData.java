package andrade.luis.librehmi.models;

import andrade.luis.librehmi.views.canvas.CanvasColor;

import java.io.Serializable;

/**
 * Contiene los datos requeridos para el funcionamiento de una serie de un gráfico de tendencias
 */
public class TrendChartSerieData implements Serializable {
    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public CanvasColor getColor() {
        return color;
    }

    public void setColor(CanvasColor color) {
        this.color = color;
    }
    public String getSerieDataName() {
        return serieDataName;
    }

    private Expression expression;
    private CanvasColor color;
    private String serieDataName;

    public TrendChartSerieData(Expression expression, CanvasColor color, String serieDataName) {
        this.expression = expression;
        this.color = color;
        this.serieDataName = serieDataName;
    }
}
