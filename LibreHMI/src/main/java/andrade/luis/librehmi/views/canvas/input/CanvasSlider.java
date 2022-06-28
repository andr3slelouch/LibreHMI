package andrade.luis.librehmi.views.canvas.input;

import andrade.luis.librehmi.models.CanvasOrientation;
import andrade.luis.librehmi.models.Tag;
import andrade.luis.librehmi.models.CanvasObjectData;
import andrade.luis.librehmi.views.canvas.CanvasPoint;
import andrade.luis.librehmi.views.canvas.CanvasObject;
import andrade.luis.librehmi.models.users.HMIUser;
import andrade.luis.librehmi.views.windows.SetSliderPropertiesWindow;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.Slider;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase que define el objeto CanvasSlider, que permitirá variar el valor del tag de escritura asociado
 */
public class CanvasSlider extends CanvasObject {
    public Slider getSlider() {
        return slider;
    }

    public void setSlider(Slider slider) {
        this.slider = slider;
    }

    private Slider slider;
    private Timeline timeline;

    @Override
    public HMIUser getUser() {
        return user;
    }

    @Override
    public void setUser(HMIUser user) {
        this.user = user;
    }

    private HMIUser user;

    /**
     * Constructor para pegar un CanvasSlider copiado o regenerarlo desde el archivo
     * @param canvasObjectData CanvasObjectData conteniendo la información del objeto a generar
     * @throws SQLException
     * @throws IOException
     */
    public CanvasSlider(CanvasObjectData canvasObjectData) throws SQLException, IOException {
        super(canvasObjectData);
        setData(this.getCanvasObjectData().getPosition().getX(), this.getCanvasObjectData().getPosition().getY(), this.getCanvasObjectData().getWidth(), this.getCanvasObjectData().getHeight(), this.getCanvasObjectData().getTag(), this.getCanvasObjectData().getRotation(),this.getCanvasObjectData().getOrientation());
        setSliderProperties(this.getCanvasObjectData().getMinValue(), this.getCanvasObjectData().getMaxValue(), this.getCanvasObjectData().getMinorTickValue(), this.getCanvasObjectData().getMajorTickValue(), this.getCanvasObjectData().isSnapHandleToTick(), this.getCanvasObjectData().isShowingTicks(), this.getCanvasObjectData().isShowingLabelsTicks());
    }

    /**
     * Constructor para agregar un nuevo CanvasSlider al canvas
     * @param center Posición del objeto
     * @param width Ancho del objeto
     * @param height Altura del objeto
     * @param linkedTag Tag enlazado al objeto
     * @param rotation Ángulo de rotación
     * @param orientation Orientación del CanvasSlider posible entre vertical u horizontal
     * @throws SQLException
     * @throws IOException
     */
    public CanvasSlider(CanvasPoint center, double width, double height,Tag linkedTag,double rotation, CanvasOrientation orientation) throws SQLException, IOException {
        super(center);
        setData(this.getCanvasObjectData().getPosition().getX(), this.getCanvasObjectData().getPosition().getY(), width,height, linkedTag,rotation,orientation);
    }

    /**
     * Permite definir las propiedades básicas del objeto
     * @param x Posición del objeto en X
     * @param y Posición del objeto en Y
     * @param width Ancho del objeto
     * @param height Ancho del objeto
     * @param linkedTag Tag enlazado al objeto
     * @param rotation Ángulo de rotación
     * @param orientation Orientación del CanvasSlider posible entre vertical u horizontal
     * @throws SQLException
     * @throws IOException
     */
    public void setData(double x, double y, double width, double height, Tag linkedTag, double rotation, CanvasOrientation orientation) throws SQLException, IOException {
        this.slider = new Slider();
        this.getCanvasObjectData().setPosition(new CanvasPoint(x,y));
        this.slider.setDisable(true);
        this.slider.setPrefWidth(width);
        this.slider.setPrefHeight(height);
        this.getCanvasObjectData().setWidth(width);
        this.getCanvasObjectData().setHeight(height);
        this.getCanvasObjectData().setTag(linkedTag);
        this.getCanvasObjectData().setType("Slider");
        this.getCanvasObjectData().setDataType("Slider");
        this.getCanvasObjectData().setOrientation(orientation);
        this.getCanvasObjectData().setRotation(rotation);
        this.getCanvasObjectData().setSuperType("TagInputObject");
        this.setCenter(this.slider);
        slider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if ((double) newValue < this.getCanvasObjectData().getMinValue() || (double) newValue > this.getCanvasObjectData().getMaxValue()) {
                this.slider.setValue((double) oldValue);
                this.getCanvasObjectData().setData(String.valueOf(oldValue));
            } else {
                this.getCanvasObjectData().setData(String.valueOf(newValue));
            }
            updateTimeline(linkedTag,timeline);
        });
        if (linkedTag != null) {
            linkedTag.read();
            slider.setValue(Double.parseDouble(linkedTag.getValue()));
            this.setTimeline();
        }
        this.setRotate(rotation);
        this.slider.setOrientation(getOrientation(orientation));
    }

    /**
     * Método que permite definir las propiedades del Slider de JavaFX
     * @param minValue Valor mínimo del Slider
     * @param maxValue Valor máximo del Slider
     * @param minorTickValue Valor del tick mínimo
     * @param majorTickValue Valor del tick máximo
     * @param snapHandleToTick Bandera para forzar a que el indicador este siempre sobre un tick
     * @param showTicks Bandera para mostrar ticks
     * @param showLabelsTicks Bandera para mostrar las etiquetas de los ticks
     */
    public void setSliderProperties(double minValue, double maxValue, double minorTickValue, double majorTickValue, boolean snapHandleToTick, boolean showTicks, boolean showLabelsTicks){
        slider.setMin(minValue);
        slider.setMax(maxValue);
        slider.setShowTickLabels(showLabelsTicks);
        slider.setShowTickMarks(showTicks);
        slider.setMajorTickUnit(majorTickValue);
        slider.setMinorTickCount((int) minorTickValue);
        slider.setSnapToTicks(snapHandleToTick);
        this.getCanvasObjectData().setMaxValue(maxValue);
        this.getCanvasObjectData().setMinValue(minValue);
        this.getCanvasObjectData().setMinorTickValue(minorTickValue);
        this.getCanvasObjectData().setMajorTickValue(majorTickValue);
        this.getCanvasObjectData().setSnapHandleToTick(snapHandleToTick);
        this.getCanvasObjectData().setShowingTicks(showTicks);
        this.getCanvasObjectData().setShowingLabelsTicks(showLabelsTicks);
    }

    /**
     * Permite retornar un enum de orientación disponible para el Slider
     * @param orientation Objeto CanvasOrientation para definir la orientación del canvas
     * @return Objeto Orientation compatible con el Slider de JavaFX
     */
    private Orientation getOrientation(CanvasOrientation orientation) {
        if(orientation == CanvasOrientation.VERTICAL){
            return Orientation.VERTICAL;
        }else{
            return Orientation.HORIZONTAL;
        }
    }

    /**
     * Permite mostrar una ventana de definición de propiedades y definir las propiedades del CanvasSlider
     */
    @Override
    public void setProperties() {
        SetSliderPropertiesWindow setSliderPropertiesWindow = new SetSliderPropertiesWindow(this.slider.getPrefWidth(),this.slider.getPrefHeight());
        setSliderPropertiesWindow.setLocalTags(this.getHmiApp().getLocalTags());
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
            setData(this.getCanvasObjectData().getPosition().getX(), this.getCanvasObjectData().getPosition().getY(),this.getCanvasObjectData().getWidth(), this.getCanvasObjectData().getHeight(), setSliderPropertiesWindow.getLocalExpression().getParameters().get(0),this.getCanvasObjectData().getRotation(),this.getCanvasObjectData().getOrientation());
            this.setSliderProperties(minValue,maxValue,minorTickValue,majorTickValue,snapHandleToTick,showTicks,showLabelsTicks);
        } catch (SQLException | IOException e) {
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.log(Level.INFO,e.getMessage());
        }
        this.getHmiApp().setWasModified(true);
    }

    @Override
    public void setEnable(String mode) {
        switch (mode) {
            case "Ejecutar":
                if(!this.getUser().getRole().equals("Administrador")){
                    this.enableListeners(false);
                    this.setOnMousePressed(this.onDoubleClick);
                    this.slider.setDisable(true);
                }else{
                    this.enableListeners(true);
                    this.slider.setDisable(false);
                }
                break;
            case "Stop":
                super.setEnable("Stop");
                this.slider.setDisable(true);
                break;
            default:
                super.setEnable("True");
                this.enableListeners(true);
                this.slider.setDisable(true);
                break;
        }
    }

    /**
     * Permite definir el hilo para actualizar los valores del Slider
     */
    public void setTimeline() {
        timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        (ActionEvent actionEvent) -> {
                            double evaluatedValue = 0;
                            try {
                                String value = this.getCanvasObjectData().getTag().read();
                                if (value != null) {
                                    evaluatedValue = Double.parseDouble(value);
                                }
                            } catch (IOException | SQLException e) {
                                Logger logger = Logger.getLogger(this.getClass().getName());
                                logger.log(Level.INFO,e.getMessage());
                            }
                            this.slider.setValue(evaluatedValue);
                        }), new KeyFrame(Duration.seconds(5)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public String updateInputTag(Tag tag, boolean forceUpdate){
        String value = super.updateInputTag(tag);
        if (forceUpdate && value != null) {
            double evaluatedValue = Double.parseDouble(value);
            this.slider.setValue(evaluatedValue);
        }
        return value;
    }
}

