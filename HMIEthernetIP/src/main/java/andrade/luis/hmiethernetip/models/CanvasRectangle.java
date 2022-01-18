package andrade.luis.hmiethernetip.models;

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

public class CanvasRectangle extends Rectangle {
    private double width;
    private double height;
    private CanvasPoint center;
    private CanvasPoint position;

    public CanvasRectangle() {

    }

    public CanvasPoint getPosition() {
        return position;
    }

    public void setPosition(CanvasPoint position) {
        this.position = position;
    }

    public CanvasPoint getCenter() {
        return center;
    }

    public void setCenter(CanvasPoint center) {
        this.center = center;

        double x = center.getX() - getWidth() / 2;
        double y = center.getY() - getHeight() / 2;

        setX(x);
        setY(y);
        setPosition(new CanvasPoint(x, y));
    }

    public CanvasRectangle(CanvasPoint center) {
        setWidth(150);
        setHeight(150);

        setOnMousePressed(getOnMyMousePressed());
        setOnMouseDragged(getOnMyMouseDragged());
        setOnMouseReleased(getOnMyMouseReleased());
        setOnMouseClicked(getOnMyMouseDoubleClicked());

        setCenter(center);

    }

    private CanvasPoint start, end;

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

    private EventHandler<MouseEvent> onMyMousePressed = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            start = new CanvasPoint(t.getSceneX(), t.getSceneY());
            end = new CanvasPoint(((Rectangle) (t.getSource())).getTranslateX(), ((Rectangle) (t.getSource())).getTranslateY());
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

            ((Rectangle) (t.getSource())).setTranslateX(newTranslateX);
            ((Rectangle) (t.getSource())).setTranslateY(newTranslateY);

            setPosition(new CanvasPoint(newTranslateX,newTranslateY));
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

    public EventHandler<MouseEvent> getOnMyMouseDoubleClicked() {
        return onMyMouseDoubleClicked;
    }

    public void setOnMyMouseDoubleClicked(EventHandler<MouseEvent> onMyMouseDoubleClicked) {
        this.onMyMouseDoubleClicked = onMyMouseDoubleClicked;
    }

    private EventHandler<MouseEvent> onMyMouseDoubleClicked = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                if(mouseEvent.getClickCount() == 2){
                    CanvasRectangle.this.setStyle(".graphical-representation:border {\n" +
                            "    -fx-border-color: red;\n" +
                            "    -fx-border-style: solid;\n" +
                            "    -fx-border-width: 5;\n" +
                            "}");
                }
            }

        }
    };
}
