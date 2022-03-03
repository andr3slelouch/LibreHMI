package andrade.luis.hmiethernetip.models.canvas.input;

import andrade.luis.hmiethernetip.models.GraphicalRepresentationData;
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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class CanvasPushbutton extends CanvasButton {
    Logger logger = Logger.getLogger(this.getClass().getName());

    private BooleanProperty selectedProperty = new SimpleBooleanProperty();

    public CanvasPushbutton(CanvasPoint center) {
        super(center);
        this.getGraphicalRepresentationData().setType("Pushbutton");
    }

    public CanvasPushbutton(GraphicalRepresentationData graphicalRepresentationData){
        super(graphicalRepresentationData);
        if(this.getGraphicalRepresentationData().getPrimaryColor()!=null && this.getGraphicalRepresentationData().getBackgroundColor()!=null && this.getGraphicalRepresentationData().getMode()!=null){
            setDynamicColors(this.getGraphicalRepresentationData().getData(),this.getGraphicalRepresentationData().getMode(),this.getGraphicalRepresentationData().getTag(),this.getGraphicalRepresentationData().getPrimaryColor(),this.getGraphicalRepresentationData().getBackgroundColor());
        }
        this.getGraphicalRepresentationData().setType("Pushbutton");
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
        if(this.getGraphicalRepresentationData().getPrimaryColor()!=null && this.getGraphicalRepresentationData().getBackgroundColor()!=null && this.getGraphicalRepresentationData().getMode()!=null){
            setColorCommandPushButtonWindow.setAddedTags(new ArrayList<>(List.of(this.getGraphicalRepresentationData().getTag())));
            setColorCommandPushButtonWindow.getTextField().setText(this.getGraphicalRepresentationData().getTag().getName());
            setColorCommandPushButtonWindow.getBackgroundColorPicker().setValue(this.getGraphicalRepresentationData().getBackgroundColor().getColor());
            setColorCommandPushButtonWindow.getPrimaryColorPicker().setValue(this.getGraphicalRepresentationData().getPrimaryColor().getColor());
            for(Toggle toggle : setColorCommandPushButtonWindow.getRadioGroup().getToggles()){
                if(((RadioButton) toggle).getText().equals(this.getGraphicalRepresentationData().getMode())){
                    toggle.setSelected(true);
                }
            }
        }
        setColorCommandPushButtonWindow.showAndWait();
        setDynamicColors(setColorCommandPushButtonWindow.getButtonLabelTextField().getText(), ((RadioButton) setColorCommandPushButtonWindow.getRadioGroup().getSelectedToggle()).getText(), setColorCommandPushButtonWindow.getLocalExpression().getParameters().get(0), new CanvasColor(setColorCommandPushButtonWindow.getPrimaryColorPicker().getValue()), new CanvasColor(setColorCommandPushButtonWindow.getBackgroundColorPicker().getValue()));
    }

    public void setDynamicColors(String buttonText, String mode, Tag linkedTag, CanvasColor primaryColor, CanvasColor backgroundColor) {
        this.getGraphicalRepresentationData().setData(buttonText);
        this.button.setText(buttonText);
        this.button.setStyle("-fx-font-family: \"Verdana\"; -fx-font-size: 18px;-fx-font-weight: bold;");
        this.button.setBackground(new Background(new BackgroundFill(backgroundColor.getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
        this.getGraphicalRepresentationData().setMode(mode);
        this.getGraphicalRepresentationData().setTag(linkedTag);
        this.getGraphicalRepresentationData().setPrimaryColor(primaryColor);
        this.getGraphicalRepresentationData().setBackgroundColor(backgroundColor);
        this.button.setOnAction(mouseEvent -> {
            try {
                switch (this.getGraphicalRepresentationData().getMode()) {
                    case "Directo":
                        changeValues(true);
                        break;
                    case "Reversa":
                        changeValues(false);
                        break;
                    case "Toggle":
                        changeValues(this.getGraphicalRepresentationData().getData().equals("clicked"));
                        break;
                    default:
                        break;
                }
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        });
        this.button.setOnMousePressed(onPushbuttonOnMyMouseDragged);
        this.button.setOnMouseReleased(onPushbuttonOnMyMouseReleased);
    }

    private void changeValues(boolean clicked) throws SQLException, IOException {
        if (clicked) {
            this.getGraphicalRepresentationData().setStatus("clicked");
            this.button.setBackground(new Background(new BackgroundFill(this.getGraphicalRepresentationData().getPrimaryColor().getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
            if (this.getGraphicalRepresentationData().getTag() != null) {
                this.getGraphicalRepresentationData().getTag().setValue("1");
                this.getGraphicalRepresentationData().getTag().updateInDatabase();
            }
        } else {
            this.getGraphicalRepresentationData().setStatus("");
            this.button.setBackground(new Background(new BackgroundFill(this.getGraphicalRepresentationData().getBackgroundColor().getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
            if (this.getGraphicalRepresentationData().getTag() != null) {
                this.getGraphicalRepresentationData().getTag().setValue("0");
                this.getGraphicalRepresentationData().getTag().updateInDatabase();
            }
        }
    }

    private final EventHandler<MouseEvent> onPushbuttonOnMyMouseReleased = mouseEvent -> {
        try {
            switch (this.getGraphicalRepresentationData().getMode()) {
                case "Directo":
                    changeValues(false);
                    break;
                case "Reversa":
                    changeValues(true);
                    break;
                case "Toggle":
                    changeValues(!this.getGraphicalRepresentationData().getData().equals("clicked"));
                    break;
                default:
                    break;
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    };

    private final EventHandler<MouseEvent> onPushbuttonOnMyMouseDragged = mouseEvent -> {
        try {
            switch (this.getGraphicalRepresentationData().getMode()) {
                case "Directo":
                    changeValues(true);
                    break;
                case "Reversa":
                    changeValues(false);
                    break;
                case "Toggle":
                    changeValues(this.getGraphicalRepresentationData().getData().equals("clicked"));
                    break;
                default:
                    break;
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    };
}
