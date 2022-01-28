package andrade.luis.hmiethernetip.controllers;

import andrade.luis.hmiethernetip.models.GraphicalRepresentation;
import andrade.luis.hmiethernetip.models.CanvasPoint;
import andrade.luis.hmiethernetip.views.HMICanvas;
import javafx.scene.Scene;
import javafx.scene.input.*;
import javafx.scene.paint.Paint;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Logger;

public class HMICanvasController extends Scene {
    Logger logger
            = Logger.getLogger(
            HMICanvasController.class.getName());
    private HMICanvas hmiCanvas;

    public HMICanvas getCanvas() {
        return hmiCanvas;
    }

    public void setCanvas(HMICanvas HMICanvas) {
        this.hmiCanvas = HMICanvas;
    }

    public HMICanvasController(HMICanvas hmiCanvas, double v, double v1, Paint paint) {
        super(hmiCanvas, v, v1, paint);
        this.hmiCanvas = hmiCanvas;
        this.setOnMouseClicked(mouseEvent -> {
            if (this.hmiCanvas.isAddOnClickEnabled()) {
                hmiCanvas.addFigureOnCanvasClicked(new CanvasPoint(mouseEvent.getX(), mouseEvent.getY()));
                this.hmiCanvas.setAddOnClickEnabled(false);
            }else if(mouseEvent.getButton() == MouseButton.SECONDARY){
                hmiCanvas.onCanvasClicked(new CanvasPoint(mouseEvent.getScreenX(), mouseEvent.getScreenY()));
            }
            updateSelected();
        });
        this.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.DELETE) {
                GraphicalRepresentation selected = getCanvas().getSelectedFigure();
                if (selected != null) {
                    selected.delete();
                }
            }
        });
        this.getAccelerators().put(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY), () -> {
            GraphicalRepresentation selected = getCanvas().getSelectedFigure();
            selected.copy("Copy");
        });
        this.getAccelerators().put(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_ANY), () -> {
            GraphicalRepresentation selected = getCanvas().getSelectedFigure();
            selected.cut();
        });
        this.getAccelerators().put(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_ANY), () -> {
            GraphicalRepresentation selected = getCanvas().getSelectedFigure();
            getCanvas().paste();
        });

    }

    public void updateSelected(){
        ArrayList<GraphicalRepresentation> canvasObjects = getCanvas().getCurrentCanvasObjects();
        LocalDateTime max = null;
        int index = -1;
        for (int i = 0; i < canvasObjects.size(); i++) {
            if (i == 0) {
                max = canvasObjects.get(i).getLastTimeSelected();
                index = i;
            } else {
                GraphicalRepresentation rectangle = canvasObjects.get(i);
                if (rectangle.getLastTimeSelected() != null && max != null) {
                    if (max.isBefore(rectangle.getLastTimeSelected())) {
                        max = canvasObjects.get(i).getLastTimeSelected();
                        index = i;
                    }
                }
            }
        }
        if (index > -1 && max != null) {
            for (int i = 0; i < canvasObjects.size(); i++) {
                if (i != index) {
                    canvasObjects.get(i).setSelected(false);
                }
            }
        }
    }
}
