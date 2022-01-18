package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.models.CanvasPoint;
import andrade.luis.hmiethernetip.models.CanvasRectangle;
import andrade.luis.hmiethernetip.models.CanvasShape;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class HMICanvas extends Pane {

    public ArrayList<CanvasRectangle> getShapeArrayList() {
        return shapeArrayList;
    }

    public void setShapeArrayList(ArrayList<CanvasRectangle> shapeArrayList) {
        this.shapeArrayList = shapeArrayList;
    }

    public CanvasPoint getCurrentMousePosition() {
        return currentMousePosition;
    }

    public void setCurrentMousePosition(CanvasPoint currentMousePosition) {
        this.currentMousePosition = currentMousePosition;
    }

    public void addNewShape(CanvasRectangle shape){
        this.shapeArrayList.add(shape);
    }

    private ArrayList<CanvasRectangle> shapeArrayList= new ArrayList<>();
    private CanvasPoint currentMousePosition;

    public void canvasClicked(CanvasPoint current){
        CanvasRectangle newCreatedRectangle = new CanvasRectangle(current);
        if(this.getShapeArrayList().isEmpty()){
            newCreatedRectangle.setId("#createdShape0");
        }else{
            newCreatedRectangle.setId("#createdShape"+this.getShapeArrayList().size());
        }
        this.addNewShape(newCreatedRectangle);
        this.getChildren().add(newCreatedRectangle);
    }

}
