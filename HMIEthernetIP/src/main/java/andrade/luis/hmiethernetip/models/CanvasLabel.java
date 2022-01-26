package andrade.luis.hmiethernetip.models;

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
        this.label = new Label(graphicalRepresentationData.getData());
        this.setCenter(this.label);
        this.getGraphicalRepresentationData().setType("Label");
    }
    public CanvasLabel(String content,CanvasPoint center){
        super(center);
        this.getGraphicalRepresentationData().setData(content);
        this.label = new Label(content);
        this.setCenter(this.label);
        this.getGraphicalRepresentationData().setType("Label");
    }
    @Override
    public void setCenter(CanvasPoint center){
        super.setCenter(center);
        this.setCenter(this.label);
    }
}
