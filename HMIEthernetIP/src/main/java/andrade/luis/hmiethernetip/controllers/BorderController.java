package andrade.luis.hmiethernetip.controllers;

import andrade.luis.hmiethernetip.models.CanvasPoint;
import andrade.luis.hmiethernetip.models.CanvasRectangle;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;

public class BorderController extends Circle {
    public CanvasPoint getPosition() {
        return position;
    }

    public void setPosition(CanvasPoint position) {
        this.position = position;
    }

    private CanvasPoint position;
    public EventHandler<MouseEvent> getOnMyMousePressed() {
        return onMyMousePressed;
    }

    public void setOnMyMousePressed(EventHandler<MouseEvent> onMyMousePressed) {
        this.onMyMousePressed = onMyMousePressed;
    }

    public EventHandler<MouseEvent> getOnMyMouseDragged() {
        return onMyMouseDragged;
    }

    public void setOnMyMouseDragged(EventHandler<MouseEvent> onMyMouseDragged) {
        this.onMyMouseDragged = onMyMouseDragged;
    }

    public EventHandler<MouseEvent> getOnMyMouseReleased() {
        return onMyMouseReleased;
    }

    public void setOnMyMouseReleased(EventHandler<MouseEvent> onMyMouseReleased) {
        this.onMyMouseReleased = onMyMouseReleased;
    }

    private CanvasPoint start;
    private CanvasPoint end;
    private EventHandler<MouseEvent> onMyMousePressed = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            start = new CanvasPoint(t.getSceneX(), t.getSceneY());
            end = new CanvasPoint(((BorderPane) (t.getSource())).getTranslateX(), ((BorderPane) (t.getSource())).getTranslateY());
            selected = true;
        }
    };

    private EventHandler<MouseEvent> onMyMouseDragged = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {

            double offsetX = t.getSceneX() - start.getX();
            double offsetY = t.getSceneY() - start.getY();
            double newTranslateX = end.getX() + offsetX;
            double newTranslateY = end.getY() + offsetY;

            ((BorderPane) (t.getSource())).setTranslateX(newTranslateX);
            ((BorderPane) (t.getSource())).setTranslateY(newTranslateY);


            setPosition(new CanvasPoint(BorderController.this.getCenterX(), BorderController.this.getCenterY()));
        }
    };

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    private boolean selected;
    private EventHandler<MouseEvent> onMyMouseReleased = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            selected = false;
        }
    };

    private boolean pointIsInSide(double side, double point){
        return side + 5 > point && side - 5 < point;
    }

    private boolean isInTop(MouseEvent event){
        return pointIsInSide(0,event.getY());
    }
}
