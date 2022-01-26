package andrade.luis.hmiethernetip.models;

import javafx.scene.control.Label;

public class CanvasLabel extends CanvasBorderPane {
    private Label label;
    public CanvasLabel(GraphicalRepresentation graphicalRepresentation){
        super(graphicalRepresentation);
        this.label = new Label(graphicalRepresentation.getData());
        this.setCenter(this.label);
    }
    public CanvasLabel(String content,CanvasPoint center){
        super(center);
        this.getGraphicalRepresentation().setData(content);
        this.label = new Label(content);
        this.setCenter(this.label);
    }
    @Override
    public void setCenter(CanvasPoint center){
        super.setCenter(center);
        this.setCenter(this.label);
    }
}
