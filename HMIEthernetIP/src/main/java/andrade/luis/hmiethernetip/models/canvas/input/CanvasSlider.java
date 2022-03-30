package andrade.luis.hmiethernetip.models.canvas.input;

import andrade.luis.hmiethernetip.models.CanvasOrientation;
import andrade.luis.hmiethernetip.models.Tag;
import andrade.luis.hmiethernetip.models.canvas.CanvasObjectData;
import andrade.luis.hmiethernetip.models.canvas.CanvasPoint;
import andrade.luis.hmiethernetip.models.canvas.CanvasObject;
import andrade.luis.hmiethernetip.models.users.HMIUser;
import andrade.luis.hmiethernetip.views.SetSliderPropertiesWindow;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CanvasSlider extends CanvasObject {
    private Slider slider;
    private Timeline timeline;

    public HMIUser getUser() {
        return user;
    }

    public void setUser(HMIUser user) {
        this.user = user;
    }

    private HMIUser user;

    public CanvasSlider(CanvasObjectData canvasObjectData) throws SQLException, IOException {
        super(canvasObjectData);
        setData(this.getCanvasObjectData().getPosition().getX(), this.getCanvasObjectData().getPosition().getY(), this.getCanvasObjectData().getWidth(), this.getCanvasObjectData().getHeight(), this.getCanvasObjectData().getTag(), this.getCanvasObjectData().getMinValue(), this.getCanvasObjectData().getMaxValue(), this.getCanvasObjectData().getMinorTickValue(), this.getCanvasObjectData().getMajorTickValue(), this.getCanvasObjectData().isSnapHandleToTick(), this.getCanvasObjectData().isShowingTicks(), this.getCanvasObjectData().isShowingLabelsTicks(),this.getCanvasObjectData().getRotation(),this.getCanvasObjectData().getOrientation());
    }

    public CanvasSlider(CanvasPoint center, double width, double height,Tag linkedTag, double minValue, double maxValue, double minorTickValue, double majorTickValue, boolean snapHandleToTick, boolean showTicks, boolean showLabelsTicks,double rotation, CanvasOrientation orientation) throws SQLException, IOException {
        super(center);
        setData(this.getCanvasObjectData().getPosition().getX(), this.getCanvasObjectData().getPosition().getY(), width,height, linkedTag, minValue, maxValue, minorTickValue, majorTickValue, snapHandleToTick, showTicks, showLabelsTicks,rotation,orientation);
    }

    public void setData(double x, double y, double width, double height, Tag linkedTag, double minValue, double maxValue, double minorTickValue, double majorTickValue, boolean snapHandleToTick, boolean showTicks, boolean showLabelsTicks, double rotation, CanvasOrientation orientation) throws SQLException, IOException {
        this.slider = new Slider();
        this.getCanvasObjectData().setPosition(new CanvasPoint(x,y));
        slider.setMin(minValue);
        slider.setMax(maxValue);
        slider.setShowTickLabels(showLabelsTicks);
        slider.setShowTickMarks(showTicks);
        slider.setMajorTickUnit(majorTickValue);
        slider.setMinorTickCount((int) minorTickValue);
        slider.setSnapToTicks(snapHandleToTick);
        this.slider.setDisable(true);
        this.slider.setPrefWidth(width);
        this.slider.setPrefHeight(height);
        this.getCanvasObjectData().setWidth(width);
        this.getCanvasObjectData().setHeight(height);
        this.getCanvasObjectData().setMaxValue(maxValue);
        this.getCanvasObjectData().setMinValue(minValue);
        this.getCanvasObjectData().setMinorTickValue(minorTickValue);
        this.getCanvasObjectData().setMajorTickValue(majorTickValue);
        this.getCanvasObjectData().setSnapHandleToTick(snapHandleToTick);
        this.getCanvasObjectData().setShowingTicks(showTicks);
        this.getCanvasObjectData().setShowingLabelsTicks(showLabelsTicks);
        this.getCanvasObjectData().setTag(linkedTag);
        this.getCanvasObjectData().setType("Slider");
        this.getCanvasObjectData().setOrientation(orientation);
        this.getCanvasObjectData().setRotation(rotation);
        this.setCenter(this.slider);
        slider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if ((double) newValue < this.getCanvasObjectData().getMinValue() || (double) newValue > this.getCanvasObjectData().getMaxValue()) {
                this.slider.setValue((double) oldValue);
                this.getCanvasObjectData().setData(String.valueOf(oldValue));
            } else {
                this.getCanvasObjectData().setData(String.valueOf(newValue));
            }
            if (linkedTag != null && timeline != null) {
                linkedTag.setValue(this.getCanvasObjectData().getData());
                try {
                    timeline.pause();
                    if (!linkedTag.updateInDatabase()) {
                        this.errorLabel = new Label("Error en Tag de Escritura");
                        this.setTop(errorLabel);
                    } else {
                        this.setTop(null);
                    }
                    timeline.play();
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                    this.errorLabel = new Label("Error en Tag de Escritura");
                    this.setTop(errorLabel);
                }
            }
        });
        if (linkedTag != null) {
            linkedTag.readFromDatabase();
            slider.setValue(Double.parseDouble(linkedTag.getValue()));
            this.setTimeline();
        }
        this.setRotate(rotation);
        this.slider.setOrientation(getOrientation(orientation));
    }

    private Orientation getOrientation(CanvasOrientation orientation) {
        if(orientation == CanvasOrientation.VERTICAL){
            return Orientation.VERTICAL;
        }else{
            return Orientation.HORIZONTAL;
        }
    }

    @Override
    public void setProperties() {
        SetSliderPropertiesWindow setSliderPropertiesWindow = new SetSliderPropertiesWindow(this.slider.getPrefWidth(),this.slider.getPrefHeight());
        setSliderPropertiesWindow.getMinValueField().setText(String.valueOf(this.getCanvasObjectData().getMinValue()));
        setSliderPropertiesWindow.getMaxValueField().setText(String.valueOf(this.getCanvasObjectData().getMaxValue()));
        setSliderPropertiesWindow.getMinorTickField().setText(String.valueOf(this.getCanvasObjectData().getMinorTickValue()));
        setSliderPropertiesWindow.getMajorTickField().setText(String.valueOf(this.getCanvasObjectData().getMajorTickValue()));
        setSliderPropertiesWindow.getSnapHandleToTick().setSelected(this.getCanvasObjectData().isSnapHandleToTick());
        setSliderPropertiesWindow.getShowTicks().setSelected(this.getCanvasObjectData().isShowingTicks());
        setSliderPropertiesWindow.getShowLabelsTicks().setSelected(this.getCanvasObjectData().isShowingLabelsTicks());
        setSliderPropertiesWindow.setAddedTags(new ArrayList<>(List.of(this.getCanvasObjectData().getTag())));
        setSliderPropertiesWindow.getTextField().setText(this.getCanvasObjectData().getTag().getName());
        setSliderPropertiesWindow.getRotationTextField().setText(String.valueOf(this.getCanvasObjectData().getRotation()));
        if(this.getCanvasObjectData().getOrientation() == CanvasOrientation.VERTICAL){
            setSliderPropertiesWindow.getVerticalRadioButton().setSelected(true);
        }else{
            setSliderPropertiesWindow.getHorizontalRadioButton().setSelected(true);
        }
        setSliderPropertiesWindow.getSizeVBox().getWidthField().setText(String.valueOf(this.getCanvasObjectData().getWidth()));
        setSliderPropertiesWindow.getSizeVBox().getHeightField().setText(String.valueOf(this.getCanvasObjectData().getHeight()));
        setSliderPropertiesWindow.showAndWait();

        double minValue = Double.parseDouble(setSliderPropertiesWindow.getMinValueField().getText());
        double maxValue = Double.parseDouble(setSliderPropertiesWindow.getMaxValueField().getText());
        double minorTickValue = Double.parseDouble(setSliderPropertiesWindow.getMinorTickField().getText());
        double majorTickValue = Double.parseDouble(setSliderPropertiesWindow.getMajorTickField().getText());
        boolean snapHandleToTick = setSliderPropertiesWindow.getSnapHandleToTick().isSelected();
        boolean showTicks = setSliderPropertiesWindow.getShowTicks().isSelected();
        boolean showLabelsTicks = setSliderPropertiesWindow.getShowLabelsTicks().isSelected();
        double rotation = Double.parseDouble(setSliderPropertiesWindow.getRotationTextField().getText());
        this.getCanvasObjectData().setWidth(setSliderPropertiesWindow.getSizeVBox().getWidthFromField());
        this.getCanvasObjectData().setHeight(setSliderPropertiesWindow.getSizeVBox().getHeightFromField());
        this.getCanvasObjectData().setRotation(rotation);
        this.getCanvasObjectData().setOrientation(setSliderPropertiesWindow.getSelectedOrientation());
        try {
            setData(this.getCanvasObjectData().getPosition().getX(), this.getCanvasObjectData().getPosition().getY(),this.getCanvasObjectData().getWidth(), this.getCanvasObjectData().getHeight(), setSliderPropertiesWindow.getLocalExpression().getParameters().get(0),minValue,maxValue,minorTickValue,majorTickValue,snapHandleToTick,showTicks,showLabelsTicks,this.getCanvasObjectData().getRotation(),this.getCanvasObjectData().getOrientation());
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        this.getHmiApp().setWasModified(true);
    }

    @Override
    public void setEnable(String enabled) {
        if (user.getRole().equals("Operador")) {
            enabled = "Stop";
        }
        switch (enabled) {
            case "Play":
                super.setEnable("Play");
                this.slider.setDisable(false);
                break;
            case "Stop":
                super.setEnable("Stop");
                this.slider.setDisable(true);
                break;
            default:
                super.setEnable("True");
                this.slider.setDisable(true);
                break;
        }
    }

    public void setTimeline() {
        timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        (ActionEvent actionEvent) -> {
                            double evaluatedValue = 0;
                            try {
                                String value = this.getCanvasObjectData().getTag().readFromDatabase();
                                if (value != null) {
                                    evaluatedValue = Double.parseDouble(value);
                                }
                            } catch (IOException | SQLException e) {
                                e.printStackTrace();
                            }
                            this.slider.setValue(evaluatedValue);
                        }), new KeyFrame(Duration.seconds(5)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}

