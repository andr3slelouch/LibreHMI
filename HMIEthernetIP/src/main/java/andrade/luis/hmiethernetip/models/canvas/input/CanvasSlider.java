package andrade.luis.hmiethernetip.models.canvas.input;

import andrade.luis.hmiethernetip.models.Tag;
import andrade.luis.hmiethernetip.models.canvas.CanvasObjectData;
import andrade.luis.hmiethernetip.models.canvas.CanvasPoint;
import andrade.luis.hmiethernetip.models.canvas.CanvasObject;
import andrade.luis.hmiethernetip.models.users.HMIUser;
import andrade.luis.hmiethernetip.views.SetSliderOptionsWindow;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CanvasSlider extends CanvasObject {
    private Slider slider;
    private Timeline timeline;
    Logger logger = Logger.getLogger(this.getClass().getName());

    public HMIUser getUser() {
        return user;
    }

    public void setUser(HMIUser user) {
        this.user = user;
    }

    private HMIUser user;

    public CanvasSlider(CanvasObjectData canvasObjectData) throws SQLException, IOException {
        super(canvasObjectData);
        setData(this.getCanvasObjectData().getPosition().getX(), this.getCanvasObjectData().getPosition().getY(), this.getCanvasObjectData().getWidth(), this.getCanvasObjectData().getHeight(), this.getCanvasObjectData().getTag(), this.getCanvasObjectData().getMinValue(), this.getCanvasObjectData().getMaxValue(), this.getCanvasObjectData().getMinorTickValue(), this.getCanvasObjectData().getMajorTickValue(), this.getCanvasObjectData().isSnapHandleToTick(), this.getCanvasObjectData().isShowingTicks(), this.getCanvasObjectData().isShowingLabelsTicks());
    }

    public CanvasSlider(CanvasPoint center, Tag linkedTag, double minValue, double maxValue, double minorTickValue, double majorTickValue, boolean snapHandleToTick, boolean showTicks, boolean showLabelsTicks) throws SQLException, IOException {
        super(center);
        setData(this.getCanvasObjectData().getPosition().getX(), this.getCanvasObjectData().getPosition().getY(), 150, 150, linkedTag, minValue, maxValue, minorTickValue, majorTickValue, snapHandleToTick, showTicks, showLabelsTicks);

    }

    public void setData(double x, double y, double width, double height, Tag linkedTag, double minValue, double maxValue, double minorTickValue, double majorTickValue, boolean snapHandleToTick, boolean showTicks, boolean showLabelsTicks) throws SQLException, IOException {
        this.slider = new Slider();
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
        this.getCanvasObjectData().setPosition(new CanvasPoint(x, y));
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
        setNewMenuItem();
    }

    public void setNewMenuItem() {
        MenuItem attachShowHideWindowsActionMI = new MenuItem("Editar");
        attachShowHideWindowsActionMI.setId("#editMI");
        attachShowHideWindowsActionMI.setOnAction(actionEvent -> {
            try {
                buttonAction();
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        });
        this.getRightClickMenu().getItems().add(attachShowHideWindowsActionMI);
    }

    private void buttonAction() throws SQLException, IOException {
        SetSliderOptionsWindow setSliderOptionsWindow = new SetSliderOptionsWindow();
        setSliderOptionsWindow.getMinValueField().setText(String.valueOf(this.getCanvasObjectData().getMinValue()));
        setSliderOptionsWindow.getMaxValueField().setText(String.valueOf(this.getCanvasObjectData().getMaxValue()));
        setSliderOptionsWindow.getMinorTickField().setText(String.valueOf(this.getCanvasObjectData().getMinorTickValue()));
        setSliderOptionsWindow.getMajorTickField().setText(String.valueOf(this.getCanvasObjectData().getMajorTickValue()));
        setSliderOptionsWindow.getSnapHandleToTick().setSelected(this.getCanvasObjectData().isSnapHandleToTick());
        setSliderOptionsWindow.getShowTicks().setSelected(this.getCanvasObjectData().isShowingTicks());
        setSliderOptionsWindow.getShowLabelsTicks().setSelected(this.getCanvasObjectData().isShowingLabelsTicks());
        setSliderOptionsWindow.setAddedTags(new ArrayList<>(List.of(this.getCanvasObjectData().getTag())));
        setSliderOptionsWindow.getTextField().setText(this.getCanvasObjectData().getTag().getName());
        setSliderOptionsWindow.showAndWait();

        double minValue = Double.parseDouble(setSliderOptionsWindow.getMinValueField().getText());
        double maxValue = Double.parseDouble(setSliderOptionsWindow.getMaxValueField().getText());
        double minorTickValue = Double.parseDouble(setSliderOptionsWindow.getMinorTickField().getText());
        double majorTickValue = Double.parseDouble(setSliderOptionsWindow.getMajorTickField().getText());
        boolean snapHandleToTick = setSliderOptionsWindow.getSnapHandleToTick().isSelected();
        boolean showTicks = setSliderOptionsWindow.getShowTicks().isSelected();
        boolean showLabelsTicks = setSliderOptionsWindow.getShowLabelsTicks().isSelected();

        setData(this.getCanvasObjectData().getPosition().getX(), this.getCanvasObjectData().getPosition().getY(),this.getCanvasObjectData().getWidth(), this.getCanvasObjectData().getHeight(),setSliderOptionsWindow.getLocalExpression().getParameters().get(0),minValue,maxValue,minorTickValue,majorTickValue,snapHandleToTick,showTicks,showLabelsTicks);
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

