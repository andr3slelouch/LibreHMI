package andrade.luis.hmiethernetip.models;

import andrade.luis.hmiethernetip.views.canvas.CanvasColor;

import java.io.Serializable;

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

    public void setSerieDataName(String serieDataName) {
        this.serieDataName = serieDataName;
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
