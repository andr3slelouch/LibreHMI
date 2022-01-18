package andrade.luis.hmiethernetip.controllers;

import andrade.luis.hmiethernetip.models.CanvasPoint;
import andrade.luis.hmiethernetip.views.HMICanvas;
import javafx.scene.Scene;
import javafx.scene.paint.Paint;

public class HMICanvasController extends Scene {
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

    public HMICanvasController(HMICanvas HMICanvas, double v, double v1, Paint paint) {
        super(HMICanvas, v, v1, paint);
        this.setOnMouseClicked(mouseEvent -> {
            if(isAddOnClickEnabled()){
                HMICanvas.canvasClicked(new CanvasPoint(mouseEvent.getX(), mouseEvent.getY()));
                setAddOnClickEnabled(false);
            }
        });
    }
}
