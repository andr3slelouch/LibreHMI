package andrade.luis.hmiethernetip.models.canvas;

import javafx.scene.control.Slider;

public class CanvasSlider extends GraphicalRepresentation {
    private Slider slider;
    public CanvasSlider(CanvasPoint center){
        super(center);
        setData(this.getGraphicalRepresentationData().getPosition().getX(), this.getGraphicalRepresentationData().getPosition().getY(), 150, 150);

    }
    public void setData(double x, double y, double width, double height) {
        this.slider = new Slider();
        slider.setMin(0);
        slider.setMax(100);
        slider.setValue(40);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(50);
        slider.setMinorTickCount(5);
        slider.setBlockIncrement(10);
        this.slider.setDisable(true);
        this.slider.setPrefWidth(width);
        this.slider.setPrefHeight(height);
        this.getGraphicalRepresentationData().setPosition(new CanvasPoint(x, y));
        this.setCenter(this.slider);
    }
}
