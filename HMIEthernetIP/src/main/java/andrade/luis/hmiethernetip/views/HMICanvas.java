package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.models.*;
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
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HMICanvas extends Pane implements CanvasInterface {

    private String type;

    private static final String FIGURE_ID = "#createdShape";

    public boolean isAddOnClickEnabled() {
        return addOnClickEnabled;
    }

    public void setAddOnClickEnabled(boolean addOnClickEnabled) {
        this.addOnClickEnabled = addOnClickEnabled;
    }

    private boolean addOnClickEnabled;

    public ArrayList<GraphicalRepresentation> getShapeArrayList() {
        return shapeArrayList;
    }

    public void addNewShape(GraphicalRepresentation shape) {
        this.shapeArrayList.add(shape);
    }

    private ArrayList<GraphicalRepresentation> shapeArrayList = new ArrayList<>();
    private CanvasPoint currentMousePosition;

    public ContextMenu getRightClickMenu() {
        return rightClickMenu;
    }

    private ContextMenu rightClickMenu;

    public HMICanvas() {
        this.setId("MainCanvas");
        rightClickMenu = new ContextMenu();
        rightClickMenu.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                event.consume();
            }
        });

        MenuItem pasteMenuItem = new MenuItem("Paste");
        pasteMenuItem.setOnAction(actionEvent -> paste());

        rightClickMenu.getItems().addAll(pasteMenuItem);
    }

    public void addFigureOnCanvasClicked(CanvasPoint current) {
        switch (type) {
            case "Rectangle":
                addRectangleOnCanvasClicked(current);
                break;
            case "Label":
                addLabelOnCanvasClicked(current);
                break;
            case "SystemDateTimeLabel":
                addSystemDateTimeLabelOnCanvasClicked(current);
                break;
            case "Text":
                addTextOnCanvasClicked(current);
                break;
            default:
                break;
        }
    }

    public void addRectangleOnCanvasClicked(CanvasPoint current) {
        CanvasRectangle newCreatedRectangle = new CanvasRectangle(current);
        newCreatedRectangle.setCanvas(this);
        if (this.getShapeArrayList().isEmpty()) {
            newCreatedRectangle.setId(FIGURE_ID + "0");
        } else {
            newCreatedRectangle.setId(FIGURE_ID + this.getShapeArrayList().size());
        }
        this.addNewShape(newCreatedRectangle);
        this.getChildren().add(newCreatedRectangle);
    }

    public void addLabelOnCanvasClicked(CanvasPoint current) {
        CanvasLabel canvasLabel = new CanvasLabel("Test", current);
        canvasLabel.setCanvas(this);
        if (this.getShapeArrayList().isEmpty()) {
            canvasLabel.setId(FIGURE_ID + "0");
        } else {
            canvasLabel.setId(FIGURE_ID + this.getShapeArrayList().size());
        }
        this.addNewShape(canvasLabel);
        this.getChildren().add(canvasLabel);
    }

    public void addSystemDateTimeLabelOnCanvasClicked(CanvasPoint current) {
        CanvasSystemDateTimeLabel canvasSystemDateTimeLabel = new CanvasSystemDateTimeLabel("yyyy/MM/dd HH:mm:ss", current);
        canvasSystemDateTimeLabel.setCanvas(this);
        if (this.getShapeArrayList().isEmpty()) {
            canvasSystemDateTimeLabel.setId(FIGURE_ID + "0");
        } else {
            canvasSystemDateTimeLabel.setId(FIGURE_ID + this.getShapeArrayList().size());
        }
        this.addNewShape(canvasSystemDateTimeLabel);
        this.getChildren().add(canvasSystemDateTimeLabel);
        canvasSystemDateTimeLabel.setTimeline();
    }

    public void addTextOnCanvasClicked(CanvasPoint current) {
        CanvasText canvasText = new CanvasText("0.0", current);
        canvasText.setCanvas(this);
        if (this.getShapeArrayList().isEmpty()) {
            canvasText.setId(FIGURE_ID + "0");
        } else {
            canvasText.setId(FIGURE_ID + this.getShapeArrayList().size());
        }
        SelectTagWindow selectTagWindow = new SelectTagWindow();
        selectTagWindow.showAndWait();
        Tag tag = selectTagWindow.getSelectedTag();
        Logger logger = Logger.getLogger(this.getClass().getSimpleName());
        logger.log(Level.INFO,tag.getTagName());
        canvasText.setTag(tag);
        this.addNewShape(canvasText);
        this.getChildren().add(canvasText);
        canvasText.setTimeline();


    }

    public ArrayList<GraphicalRepresentation> getCurrentCanvasObjects() {
        ArrayList<GraphicalRepresentation> arrayList = new ArrayList<>();
        for (int i = 0; i < this.getChildren().size(); i++) {
            Node tempNode = this.getChildren().get(i);

            if (Objects.requireNonNullElse(tempNode.getId(), "").startsWith(FIGURE_ID)) {
                arrayList.add((GraphicalRepresentation) tempNode);
            }

        }
        return arrayList;
    }

    public boolean existsId(String id) {
        for (GraphicalRepresentation rect : getCurrentCanvasObjects()) {
            if (rect.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public boolean isFigureContextMenuShowing() {
        for (GraphicalRepresentation rect : this.getCurrentCanvasObjects()) {
            if (rect.getRightClickMenu().isShowing()) {
                return true;
            }
        }
        return false;
    }

    public GraphicalRepresentation getSelectedFigure() {
        for (GraphicalRepresentation rect : this.getCurrentCanvasObjects()) {
            if (rect.isSelected()) {
                return rect;
            }
        }
        return null;
    }

    public void showContextMenu(double screenX, double screenY) {
        currentMousePosition = new CanvasPoint(screenX, screenY);
        rightClickMenu.show(HMICanvas.this, screenX, screenY);
    }

    public void onCanvasClicked(CanvasPoint canvasPoint) {
        if (!isFigureContextMenuShowing()) {
            showContextMenu(canvasPoint.getX(), canvasPoint.getY());
        }
    }

    public void paste() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            DataFlavor flavor = new DataFlavor("application/x-java-serialized-object;class=andrade.luis.hmiethernetip.models.GraphicalRepresentationData");
            if (clipboard.isDataFlavorAvailable(flavor)) {
                GraphicalRepresentationData graphicalRepresentationData = (GraphicalRepresentationData) clipboard.getData(flavor);
                switch (graphicalRepresentationData.getType()) {
                    case "Rectangle":
                        addPastedRectangle(graphicalRepresentationData);
                        break;
                    case "Label":
                        addPastedLabel(graphicalRepresentationData);
                        break;
                    case "SystemDateTimeLabel":
                        addPastedSystemDateTimeLabel(graphicalRepresentationData);
                        break;
                    default:
                        break;
                }
            }
        } catch (ClassNotFoundException | IOException | UnsupportedFlavorException e) {
            e.printStackTrace();
        }
    }

    public void addPastedRectangle(GraphicalRepresentationData graphicalRepresentationData) {
        CanvasRectangle canvasRectangle = new CanvasRectangle(graphicalRepresentationData);
        canvasRectangle.setCanvas(this);
        canvasRectangle.setCenter(Objects.requireNonNullElseGet(currentMousePosition, () -> new CanvasPoint(graphicalRepresentationData.getCenter().getX() + 10, graphicalRepresentationData.getCenter().getY() + 10)));
        canvasRectangle.setId(generateIdForPasteOperation(graphicalRepresentationData));
        this.addNewShape(canvasRectangle);
        this.getChildren().add(canvasRectangle);
    }

    public void addPastedLabel(GraphicalRepresentationData graphicalRepresentationData) {
        CanvasLabel canvasLabel = new CanvasLabel(graphicalRepresentationData);
        canvasLabel.setCanvas(this);
        canvasLabel.setCenter(Objects.requireNonNullElseGet(currentMousePosition, () -> new CanvasPoint(graphicalRepresentationData.getCenter().getX() + 10, graphicalRepresentationData.getCenter().getY() + 10)));
        canvasLabel.setId(generateIdForPasteOperation(graphicalRepresentationData));
        this.addNewShape(canvasLabel);
        this.getChildren().add(canvasLabel);
    }

    public void addPastedSystemDateTimeLabel(GraphicalRepresentationData graphicalRepresentationData) {
        CanvasSystemDateTimeLabel canvasSystemDateTimeLabel = new CanvasSystemDateTimeLabel(graphicalRepresentationData);
        canvasSystemDateTimeLabel.setCanvas(this);
        canvasSystemDateTimeLabel.setCenter(Objects.requireNonNullElseGet(currentMousePosition, () -> new CanvasPoint(graphicalRepresentationData.getCenter().getX() + 10, graphicalRepresentationData.getCenter().getY() + 10)));
        canvasSystemDateTimeLabel.setId(generateIdForPasteOperation(graphicalRepresentationData));
        this.addNewShape(canvasSystemDateTimeLabel);
        this.getChildren().add(canvasSystemDateTimeLabel);
        canvasSystemDateTimeLabel.setTimeline();
    }

    public String generateIdForPasteOperation(GraphicalRepresentationData graphicalRepresentationData) {
        if (graphicalRepresentationData.getOperation().equals("Copy")) {
            int copyNumber = 0;
            for (int i = 0; i < getCurrentCanvasObjects().size(); i++) {
                if (i == 0 && existsId(graphicalRepresentationData.getId())) {
                    copyNumber = i + 1;
                } else {
                    if (existsId(graphicalRepresentationData.getId() + "(" + i + ")")) {
                        copyNumber = i + 1;
                    }
                }
            }
            return graphicalRepresentationData.getId() + "(" + copyNumber + ")";
        } else {
            return graphicalRepresentationData.getId();
        }
    }

    @Override
    public void delete(GraphicalRepresentationData graphicalRepresentationData) {
        for (GraphicalRepresentation temp : getCurrentCanvasObjects()) {
            if (temp.getId().equals(graphicalRepresentationData.getId())) {
                this.shapeArrayList.remove(temp);
                this.getChildren().remove(temp);
            }
        }
    }

    public void setType(String type) {
        this.type = type;
    }
}
