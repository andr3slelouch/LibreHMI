package andrade.luis.hmiethernetip.models.canvas.input;

import andrade.luis.hmiethernetip.models.canvas.CanvasObjectData;
import andrade.luis.hmiethernetip.models.Tag;
import andrade.luis.hmiethernetip.models.canvas.CanvasColor;
import andrade.luis.hmiethernetip.models.canvas.CanvasPoint;
import andrade.luis.hmiethernetip.views.SetColorCommandPushButtonWindow;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
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
import java.util.logging.Logger;

public class CanvasPushbutton extends CanvasButton {
    Logger logger = Logger.getLogger(this.getClass().getName());
    private static final String DIRECT_STR = "Directo";
    private static final String REVERSE_STR = "Reversa";
    private static final String TOGGLE_STR = "Toggle";

    private BooleanProperty selectedProperty = new SimpleBooleanProperty();

    public CanvasPushbutton(CanvasPoint center) {
        super(center);
        this.getCanvasObjectData().setType("Pushbutton");
    }

    public CanvasPushbutton(CanvasObjectData canvasObjectData){
        super(canvasObjectData);
        if(this.getCanvasObjectData().getPrimaryColor()!=null && this.getCanvasObjectData().getBackgroundColor()!=null && this.getCanvasObjectData().getMode()!=null){
            setDynamicColors(this.getCanvasObjectData().getData(),this.getCanvasObjectData().getMode(),this.getCanvasObjectData().getTag(),this.getCanvasObjectData().getPrimaryColor(),this.getCanvasObjectData().getBackgroundColor());
        }
        this.getCanvasObjectData().setType("Pushbutton");
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
        if(this.getCanvasObjectData().getPrimaryColor()!=null && this.getCanvasObjectData().getBackgroundColor()!=null && this.getCanvasObjectData().getMode()!=null){
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
    }

    public void setDynamicColors(String buttonText, String mode, Tag linkedTag, CanvasColor primaryColor, CanvasColor backgroundColor) {
        this.getCanvasObjectData().setData(buttonText);
        this.button.setText(buttonText);
        this.button.setStyle("-fx-font-family: \"Verdana\"; -fx-font-size: 18px;-fx-font-weight: bold;");
        this.button.setBackground(new Background(new BackgroundFill(backgroundColor.getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
        this.getCanvasObjectData().setMode(mode);
        this.getCanvasObjectData().setTag(linkedTag);
        this.getCanvasObjectData().setPrimaryColor(primaryColor);
        this.getCanvasObjectData().setBackgroundColor(backgroundColor);
        this.button.setOnMousePressed(onPushbuttonOnMyMousePressed);
        this.button.setOnMouseReleased(onPushbuttonOnMyMouseReleased);
    }

    private void changeValues(boolean clicked) throws SQLException, IOException {
        if (clicked) {
            this.getCanvasObjectData().setStatus("clicked");
            this.button.setBackground(new Background(new BackgroundFill(this.getCanvasObjectData().getPrimaryColor().getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
            if (this.getCanvasObjectData().getTag() != null) {
                this.getCanvasObjectData().getTag().setValue("1");
                this.getCanvasObjectData().getTag().updateInDatabase();
            }
        } else {
            this.getCanvasObjectData().setStatus("");
            this.button.setBackground(new Background(new BackgroundFill(this.getCanvasObjectData().getBackgroundColor().getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
            if (this.getCanvasObjectData().getTag() != null) {
                this.getCanvasObjectData().getTag().setValue("0");
                this.getCanvasObjectData().getTag().updateInDatabase();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    };
}