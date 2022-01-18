package andrade.luis.hmiethernetip.models;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class CanvasRectangle extends BorderPane {
    private double width;
    private double height;
    private CanvasPoint center;
    private CanvasPoint position;
    private Rectangle rectangle;
    private PseudoClass rectangleBorder;
    private BorderPane rectangleWrapper;
    private SimpleBooleanProperty rectangleBorderActive;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    private double x;
    private double y;

    public CanvasRectangle() {

    }

    public CanvasPoint getPosition() {
        return position;
    }

    public void setPosition(CanvasPoint position) {
        this.position = position;
        this.setLayoutX(position.getX());
        this.setLayoutY(position.getY());
    }

    public CanvasPoint getRectangleCenter() {
        return center;
    }

    public void setCenter(CanvasPoint center) {
        this.center = center;

        double x = center.getX() - getWidth() / 2;
        double y = center.getY() - getHeight() / 2;

        setX(x);
        setY(y);
        setPosition(new CanvasPoint(x, y));

        this.rectangle = new Rectangle(getX(), getY(), getWidth(), getHeight());
        this.rectangleBorder = PseudoClass.getPseudoClass("border");
        this.rectangleWrapper = new BorderPane(this.rectangle);
        this.rectangleBorderActive = new SimpleBooleanProperty() {
            @Override
            protected void invalidated() {
                CanvasRectangle.this.rectangleWrapper.pseudoClassStateChanged(CanvasRectangle.this.rectangleBorder, get());
            }
        };
        this.setCenter(this.rectangleWrapper);
        this.setPadding(new Insets(15));
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


            setPosition(new CanvasPoint(CanvasRectangle.this.getLayoutX(), CanvasRectangle.this.getLayoutY()));
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

    private EventHandler<MouseEvent> onMyMouseDoubleClicked = mouseEvent -> {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
            CanvasRectangle.this.rectangleWrapper.setStyle("-fx-border-color: red;-fx-border-style: segments(10, 15, 15, 15)  line-cap round;-fx-border-width: 2;");
            Circle circleOne = new Circle();
            circleOne.setCenterX(0);
            circleOne.setCenterY(0);
            circleOne.setFill(Color.BLUE);
            circleOne.setRadius(5f);
            Circle circleTwo = new Circle();
            circleTwo.setCenterX(0);
            circleTwo.setCenterY(CanvasRectangle.this.rectangle.getHeight()/2);
            circleTwo.setFill(Color.BLUE);
            circleTwo.setRadius(5f);
            Circle circleThree = new Circle();
            circleThree.setCenterX(0);
            circleThree.setCenterY(CanvasRectangle.this.rectangle.getHeight());
            circleThree.setFill(Color.BLUE);
            circleThree.setRadius(5f);
            Circle circleFour = new Circle();
            circleFour.setCenterX(CanvasRectangle.this.rectangle.getWidth()/2);
            circleFour.setCenterY(0);
            circleFour.setRadius(5f);
            Circle circleFive = new Circle();
            circleFive.setCenterX(CanvasRectangle.this.rectangle.getWidth()/2);
            circleFive.setCenterY(CanvasRectangle.this.rectangle.getHeight());
            circleFive.setRadius(5f);
            Circle circleSix = new Circle();
            circleSix.setCenterX(CanvasRectangle.this.rectangle.getWidth());
            circleSix.setCenterY(0);
            circleSix.setRadius(5f);
            Circle circleSeven = new Circle();
            circleSeven.setCenterX(CanvasRectangle.this.rectangle.getWidth());
            circleSeven.setCenterY(CanvasRectangle.this.rectangle.getHeight()/2);
            circleSeven.setRadius(5f);
            Circle circleEight = new Circle();
            circleEight.setCenterX(CanvasRectangle.this.rectangle.getWidth());
            circleEight.setCenterY(CanvasRectangle.this.rectangle.getHeight());
            circleEight.setRadius(5f);
            CanvasRectangle.this.rectangleWrapper.getChildren().add(circleOne);
            CanvasRectangle.this.rectangleWrapper.getChildren().add(circleTwo);
            CanvasRectangle.this.rectangleWrapper.getChildren().add(circleThree);
            CanvasRectangle.this.rectangleWrapper.getChildren().add(circleFour);
            CanvasRectangle.this.rectangleWrapper.getChildren().add(circleFive);
            CanvasRectangle.this.rectangleWrapper.getChildren().add(circleSix);
            CanvasRectangle.this.rectangleWrapper.getChildren().add(circleSeven);
            CanvasRectangle.this.rectangleWrapper.getChildren().add(circleEight);
        }
    };
}
