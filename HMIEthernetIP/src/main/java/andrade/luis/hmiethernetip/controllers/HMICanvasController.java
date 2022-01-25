package andrade.luis.hmiethernetip.controllers;

import andrade.luis.hmiethernetip.models.CanvasPoint;
import andrade.luis.hmiethernetip.models.CanvasRectangle;
import andrade.luis.hmiethernetip.views.HMICanvas;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.*;
import javafx.scene.paint.Paint;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HMICanvasController extends Scene {
    Logger logger
            = Logger.getLogger(
            HMICanvasController.class.getName());
    private HMICanvas HMICanvas;

    public boolean isAddOnClickEnabled() {
        return addOnClickEnabled;
    }

    public void setAddOnClickEnabled(boolean addOnClickEnabled) {
        this.addOnClickEnabled = addOnClickEnabled;
    }

    private boolean addOnClickEnabled;

    public HMICanvas getCanvas() {
        return HMICanvas;
    }

    public void setCanvas(HMICanvas HMICanvas) {
        this.HMICanvas = HMICanvas;
    }

    public HMICanvasController(HMICanvas hmiCanvas, double v, double v1, Paint paint) {
        super(hmiCanvas, v, v1, paint);
        this.HMICanvas = hmiCanvas;
        this.setOnMouseClicked(mouseEvent -> {
            if (isAddOnClickEnabled()) {
                hmiCanvas.addFigureOnCanvasClicked(new CanvasPoint(mouseEvent.getX(), mouseEvent.getY()));
                setAddOnClickEnabled(false);
            }else if(mouseEvent.getButton() == MouseButton.SECONDARY){
                hmiCanvas.onCanvasClicked(new CanvasPoint(mouseEvent.getScreenX(), mouseEvent.getScreenY()));
            }
            updateSelected();
        });
        this.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.DELETE) {
                CanvasRectangle selected = getCanvas().getSelectedFigure();
                if (selected != null) {
                    selected.delete();
                }
            }
        });
        this.getAccelerators().put(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY), () -> {
            CanvasRectangle selected = getCanvas().getSelectedFigure();
            selected.copy("Copy");
        });
        this.getAccelerators().put(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_ANY), () -> {
            CanvasRectangle selected = getCanvas().getSelectedFigure();
            selected.cut();
        });
        this.getAccelerators().put(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_ANY), () -> {
            CanvasRectangle selected = getCanvas().getSelectedFigure();
            getCanvas().paste(getCanvas().getCurrentMousePosition());
        });

    }

    public void updateSelected(){
        ArrayList<CanvasRectangle> canvasRectangles = getCanvas().getCurrentCanvasObjects();
        LocalDateTime max = null;
        int index = -1;
        for (int i = 0; i < canvasRectangles.size(); i++) {
            if (i == 0) {
                max = canvasRectangles.get(i).getLastTimeSelected();
                index = i;
            } else {
                CanvasRectangle rectangle = canvasRectangles.get(i);
                if (rectangle.getLastTimeSelected() != null && max != null) {
                    if (max.isBefore(rectangle.getLastTimeSelected())) {
                        max = canvasRectangles.get(i).getLastTimeSelected();
                        index = i;
                    }
                }
            }
        }
        if (index > -1 && max != null) {
            for (int i = 0; i < canvasRectangles.size(); i++) {
                if (i != index) {
                    canvasRectangles.get(i).setSelected(false);
                }
            }
        }
    }
}
