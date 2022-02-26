package andrade.luis.hmiethernetip.models.canvas;

import andrade.luis.hmiethernetip.models.GraphicalRepresentationData;
import javafx.scene.control.Label;

public class CanvasLabel extends GraphicalRepresentation {
    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    private Label label;
    public CanvasLabel(GraphicalRepresentationData graphicalRepresentationData){
        super(graphicalRepresentationData);
        this.setData(graphicalRepresentationData.getData());
    }
    public CanvasLabel(String content, CanvasPoint center){
        super(center);
        this.getGraphicalRepresentationData().setData(content);
        this.setData(content);
    }
    public CanvasLabel(){
        super();
    }
    @Override
    public void setCenter(CanvasPoint center){
        super.setCenter(center);
        this.setCenter(this.label);
    }

    public void setData(String content){
        this.label = new Label(content);
        this.setCenter(this.label);
        this.getGraphicalRepresentationData().setType("Label");
        this.getGraphicalRepresentationData().setWidth(this.label.getWidth());
        this.getGraphicalRepresentationData().setHeight(this.label.getHeight());
    }
}