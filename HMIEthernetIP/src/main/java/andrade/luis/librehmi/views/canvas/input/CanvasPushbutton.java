package andrade.luis.librehmi.views.canvas.input;

import andrade.luis.librehmi.models.CanvasObjectData;
import andrade.luis.librehmi.models.Tag;
import andrade.luis.librehmi.views.canvas.CanvasColor;
import andrade.luis.librehmi.views.canvas.CanvasPoint;
import andrade.luis.librehmi.views.windows.SetColorCommandPushButtonWindow;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Esta es la clase que contiene define a un CanvasPushbutton, se utiliza para cambiar el valor de un Tag del tipo
 * Booleano asociado al hacer clic en él cambia el valor del Tag Booleano asociado
 */
public class CanvasPushbutton extends CanvasButton {
    private static final String DIRECT_STR = "Directo";
    private static final String REVERSE_STR = "Reversa";
    private static final String TOGGLE_STR = "Toggle";

    public CanvasPushbutton(CanvasPoint center) {
        super(center);
        this.getCanvasObjectData().setType("Pushbutton");
        this.getCanvasObjectData().setDataType("Pulsador");
    }

    public CanvasPushbutton(CanvasObjectData canvasObjectData){
        super(canvasObjectData);
        if(this.getCanvasObjectData().getPrimaryColor()!=null && this.getCanvasObjectData().getBackgroundColor()!=null && this.getCanvasObjectData().getMode()!=null){
            setDynamicColors(this.getCanvasObjectData().getData(),this.getCanvasObjectData().getMode(),this.getCanvasObjectData().getTag(),this.getCanvasObjectData().getPrimaryColor(),this.getCanvasObjectData().getBackgroundColor());
        }
        this.getCanvasObjectData().setType("Pushbutton");
        this.getCanvasObjectData().setSuperType("TagInputObject");
    }

    @Override
    public void setNewMenuItem() {
        MenuItem colorCommandPushActionMI = new MenuItem("Cambio de Color");
        colorCommandPushActionMI.setId("#colorCommandPushButtonMI");
        colorCommandPushActionMI.setOnAction(actionEvent -> buttonAction());
        this.getRightClickMenu().getItems().add(colorCommandPushActionMI);
    }

    @Override
    public void buttonAction() {
        SetColorCommandPushButtonWindow setColorCommandPushButtonWindow = new SetColorCommandPushButtonWindow();
        setColorCommandPushButtonWindow.setLocalTags(this.getHmiApp().getLocalTags());
        if(this.getCanvasObjectData().getPrimaryColor()!=null && this.getCanvasObjectData().getBackgroundColor()!=null && this.getCanvasObjectData().getMode()!=null){
            setColorCommandPushButtonWindow.getButtonLabelTextField().setText(this.getCanvasObjectData().getData());
            setColorCommandPushButtonWindow.setAddedTags(new ArrayList<>(List.of(this.getCanvasObjectData().getTag())));
            setColorCommandPushButtonWindow.getTextField().setText(this.getCanvasObjectData().getTag().getName());
            setColorCommandPushButtonWindow.getBackgroundColorPicker().setValue(this.getCanvasObjectData().getBackgroundColor().getColor());
            setColorCommandPushButtonWindow.getPrimaryColorPicker().setValue(this.getCanvasObjectData().getPrimaryColor().getColor());
            for(Toggle toggle : setColorCommandPushButtonWindow.getRadioGroup().getToggles()){
                if(((RadioButton) toggle).getText().equals(this.getCanvasObjectData().getMode())){
                    toggle.setSelected(true);
                }
            }
        }
        setColorCommandPushButtonWindow.showAndWait();
        setDynamicColors(setColorCommandPushButtonWindow.getButtonLabelTextField().getText(), ((RadioButton) setColorCommandPushButtonWindow.getRadioGroup().getSelectedToggle()).getText(), setColorCommandPushButtonWindow.getLocalExpression().getParameters().get(0), new CanvasColor(setColorCommandPushButtonWindow.getPrimaryColorPicker().getValue()), new CanvasColor(setColorCommandPushButtonWindow.getBackgroundColorPicker().getValue()));
        this.getHmiApp().setWasModified(true);
    }

    public void setDynamicColors(String buttonText, String mode, Tag linkedTag, CanvasColor primaryColor, CanvasColor backgroundColor) {
        this.button.setText(buttonText);
        if(mode.equals(REVERSE_STR)){
            this.button.setBackground(new Background(new BackgroundFill(this.getCanvasObjectData().getPrimaryColor().getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
        }else{
            this.button.setBackground(new Background(new BackgroundFill(backgroundColor.getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
        }
        this.button.setOnMousePressed(onPushbuttonOnMyMousePressed);
        this.button.setOnMouseReleased(onPushbuttonOnMyMouseReleased);
        this.getCanvasObjectData().setMode(mode);
        this.getCanvasObjectData().setTag(linkedTag);
        this.getCanvasObjectData().setPrimaryColor(primaryColor);
        this.getCanvasObjectData().setBackgroundColor(backgroundColor);
        this.getCanvasObjectData().setData(buttonText);
    }

    private void changeValues(boolean clicked) throws SQLException, IOException {
        if (clicked) {
            this.getCanvasObjectData().setStatus("clicked");
            this.button.setBackground(new Background(new BackgroundFill(this.getCanvasObjectData().getPrimaryColor().getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
            if (this.getCanvasObjectData().getTag() != null) {
                this.getCanvasObjectData().getTag().setValue("1");
                this.setLastTimeSelected();
                if(!this.getCanvasObjectData().getTag().update()){
                    this.errorLabel = new Label("Error en Tag de Escritura");
                    this.setTop(this.errorLabel);
                }else{
                    this.setTop(null);
                }
            }
        } else {
            this.getCanvasObjectData().setStatus("");
            this.button.setBackground(new Background(new BackgroundFill(this.getCanvasObjectData().getBackgroundColor().getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
            if (this.getCanvasObjectData().getTag() != null) {
                this.getCanvasObjectData().getTag().setValue("0");
                this.setLastTimeSelected();
                if(!this.getCanvasObjectData().getTag().update()){
                    this.errorLabel = new Label("Error en Tag de Escritura");
                    this.setTop(this.errorLabel);
                }else{
                    this.setTop(null);
                }
            }
        }
    }

    private final EventHandler<MouseEvent> onPushbuttonOnMyMouseReleased = mouseEvent -> {
        try {
            switch (this.getCanvasObjectData().getMode()) {
                case DIRECT_STR:
                    changeValues(false);
                    break;
                case REVERSE_STR:
                    changeValues(true);
                    break;
                default:
                    break;
            }
        } catch (SQLException | IOException e) {
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.log(Level.INFO,e.getMessage());
        }
    };

    private final EventHandler<MouseEvent> onPushbuttonOnMyMousePressed = mouseEvent -> {
        try {
            switch (this.getCanvasObjectData().getMode()) {
                case DIRECT_STR:
                    changeValues(true);
                    break;
                case REVERSE_STR:
                    changeValues(false);
                    break;
                case TOGGLE_STR:
                    changeValues(!this.getCanvasObjectData().getStatus().equals("clicked"));
                    break;
                default:
                    break;
            }
        } catch (SQLException | IOException e) {
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.log(Level.INFO,e.getMessage());
        }
    };

    @Override
    public void setEnable(String mode) {
        switch (mode) {
            case "Ejecutar":
                if(!this.getUser().getRole().equals("Administrador")){
                    this.enableListeners(false);
                    this.setOnMousePressed(this.onDoubleClick);
                    this.button.setDisable(true);
                }else{
                    this.button.setDisable(false);
                }
                break;
            case "Stop":
                super.setEnable("Stop");
                this.button.setDisable(true);
                break;
            default:
                super.setEnable("True");
                this.enableListeners(true);
                this.button.setDisable(true);
                break;
        }
    }
    @Override
    public void updateTag(Tag tag){
        super.updateTag(tag);
        if(this.getCanvasObjectData().getTag().compareToTag(tag)){
            this.getCanvasObjectData().setTag(tag);
        }
    }
}
