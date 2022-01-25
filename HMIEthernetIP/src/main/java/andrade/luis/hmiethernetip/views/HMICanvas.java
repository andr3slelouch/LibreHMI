package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.models.CanvasInterface;
import andrade.luis.hmiethernetip.models.CanvasPoint;
import andrade.luis.hmiethernetip.models.CanvasRectangle;
import andrade.luis.hmiethernetip.models.GraphicalRepresentation;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

public class HMICanvas extends Pane implements CanvasInterface {

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

    public void addNewShape(CanvasRectangle shape) {
        this.shapeArrayList.add(shape);
    }

    private ArrayList<CanvasRectangle> shapeArrayList = new ArrayList<>();
    private CanvasPoint currentMousePosition;

    public ContextMenu getRightClickMenu() {
        return rightClickMenu;
    }

    public void setRightClickMenu(ContextMenu rightClickMenu) {
        this.rightClickMenu = rightClickMenu;
    }

    private ContextMenu rightClickMenu;

    public HMICanvas(){
        this.setId("MainCanvas");
        rightClickMenu = new ContextMenu();
        rightClickMenu.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                event.consume();
            }
        });

        MenuItem pasteMenuItem = new MenuItem("Paste");
        pasteMenuItem.setOnAction(actionEvent -> paste(currentMousePosition));

        rightClickMenu.getItems().addAll(pasteMenuItem);
    }

    public void addFigureOnCanvasClicked(CanvasPoint current) {
        CanvasRectangle newCreatedRectangle = new CanvasRectangle(current);
        newCreatedRectangle.setCanvas(this);
        if (this.getShapeArrayList().isEmpty()) {
            newCreatedRectangle.setId("#createdShape0");
        } else {
            newCreatedRectangle.setId("#createdShape" + this.getShapeArrayList().size());
        }
        this.addNewShape(newCreatedRectangle);
        this.getChildren().add(newCreatedRectangle);
    }

    public ArrayList<CanvasRectangle> getCurrentCanvasObjects() {
        ArrayList<CanvasRectangle> arrayList = new ArrayList<>();
        for (int i = 0; i < this.getChildren().size(); i++) {
            Node tempNode = this.getChildren().get(i);
            if(tempNode.getId()!=null && tempNode.getId().length()>=13){
                if (tempNode.getId().substring(0, 13).equals("#createdShape")) {
                    arrayList.add((CanvasRectangle) tempNode);
                }
            }
        }
        return arrayList;
    }

    public boolean existsId(String id){
        for(CanvasRectangle rect:getCurrentCanvasObjects()){
            if(rect.getId().equals(id)){
                return true;
            }
        }
        return false;
    }

    public boolean isFigureContextMenuShowing(){
        for(CanvasRectangle rect: this.getCurrentCanvasObjects()){
            if(rect.getRightClickMenu().isShowing()){
                return true;
            }
        }
        return false;
    }

    public CanvasRectangle getSelectedFigure(){
        for(CanvasRectangle rect: this.getCurrentCanvasObjects()){
            if(rect.isSelected()){
                return rect;
            }
        }
        return null;
    }

    public void showContextMenu(double screenX,double screenY){
        currentMousePosition = new CanvasPoint(screenX,screenY);
        rightClickMenu.show(HMICanvas.this,screenX,screenY);
    }

    public void onCanvasClicked(CanvasPoint canvasPoint) {
        if(!isFigureContextMenuShowing()){
            showContextMenu(canvasPoint.getX(),canvasPoint.getY());
        }
    }

    public void paste(CanvasPoint currentMousePosition){
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            DataFlavor flavor = new DataFlavor("application/x-java-serialized-object;class=andrade.luis.hmiethernetip.models.GraphicalRepresentation");
            if(clipboard.isDataFlavorAvailable(flavor)){
                GraphicalRepresentation graphicalRepresentation = (GraphicalRepresentation) clipboard.getData(flavor);
                CanvasRectangle canvasRectangle = new CanvasRectangle(graphicalRepresentation);
                canvasRectangle.setCanvas(this);
                if(currentMousePosition!=null){
                    canvasRectangle.setCenter(currentMousePosition);
                }else{
                    canvasRectangle.setCenter(new CanvasPoint(graphicalRepresentation.getCenter().getX()+10,graphicalRepresentation.getCenter().getY()+10));
                }
                if(graphicalRepresentation.getOperation().equals("Copy")){
                    int copyNumber = 0;
                    for(int i=0;i<getCurrentCanvasObjects().size();i++){
                        if(i==0){
                            if (existsId(graphicalRepresentation.getId())){
                                copyNumber = i+1;
                            }
                        }else{
                            if (existsId(graphicalRepresentation.getId()+"("+i+")")){
                                copyNumber = i+1;
                            }
                        }
                    }
                    canvasRectangle.setId(graphicalRepresentation.getId()+"("+copyNumber+")");
                }else{
                    canvasRectangle.setId(graphicalRepresentation.getId());
                }
                this.addNewShape(canvasRectangle);
                this.getChildren().add(canvasRectangle);
            }
        }catch (ClassNotFoundException | IOException | UnsupportedFlavorException e){
            e.printStackTrace();
        }
    }

    @Override
    public void delete(GraphicalRepresentation graphicalRepresentation) {
        for(CanvasRectangle temp: getCurrentCanvasObjects()){
            if(temp.getId().equals(graphicalRepresentation.getId())){
                this.shapeArrayList.remove(temp);
                this.getChildren().remove(temp);
            }
        }
    }
}
