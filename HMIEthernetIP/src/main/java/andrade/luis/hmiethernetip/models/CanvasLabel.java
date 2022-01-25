package andrade.luis.hmiethernetip.models;

import javafx.scene.control.Label;

public class CanvasLabel extends Label {
    private GraphicalRepresentation graphicalRepresentation = new GraphicalRepresentation();

    public GraphicalRepresentation getGraphicalRepresentation() {
        return graphicalRepresentation;
    }

    public void setGraphicalRepresentation(GraphicalRepresentation graphicalRepresentation) {
        this.graphicalRepresentation = graphicalRepresentation;
    }

    public CanvasLabel(){

    }
}
