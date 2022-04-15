package andrade.luis.hmiethernetip.models;

import andrade.luis.hmiethernetip.models.canvas.CanvasColor;

import java.io.Serializable;

public class TrendChartSerieData implements Serializable {
    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public CanvasColor getColor() {
        return color;
    }

    public void setColor(CanvasColor color) {
        this.color = color;
    }

    private Tag tag;
    private CanvasColor color;
}
