package andrade.luis.hmiethernetip.models.canvas.input;

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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CanvasPushbutton extends CanvasButton {
    Logger logger = Logger.getLogger(this.getClass().getName());

    private BooleanProperty selectedProperty = new SimpleBooleanProperty();

    public CanvasPushbutton(CanvasPoint center) {
        super(center);
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
        setColorCommandPushButtonWindow.showAndWait();
        setDynamicColors(setColorCommandPushButtonWindow.getButtonLabelTextField().getText(), ((RadioButton) setColorCommandPushButtonWindow.getRadioGroup().getSelectedToggle()).getText(), setColorCommandPushButtonWindow.getLocalExpression().getParameters().get(0), new CanvasColor(setColorCommandPushButtonWindow.getPrimaryColorPicker().getValue()), new CanvasColor(setColorCommandPushButtonWindow.getBackgroundColorPicker().getValue()));
    }

    public void setDynamicColors(String buttonText, String mode, Tag linkedTag, CanvasColor primaryColor, CanvasColor backgroundColor) {
        this.getGraphicalRepresentationData().setData("");
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
                        logger.log(Level.INFO, "Directo On click");
                        changeValues(true);
                        break;
                    case "Reversa":
                        logger.log(Level.INFO, "Reversa On click");
                        changeValues(false);
                        break;
                    case "Toggle":
                        logger.log(Level.INFO, "Toggle On click");
                        changeValues(this.getGraphicalRepresentationData().getData().equals("clicked"));
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
            this.getGraphicalRepresentationData().setData("clicked");
            this.button.setBackground(new Background(new BackgroundFill(this.getGraphicalRepresentationData().getPrimaryColor().getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
            if (this.getGraphicalRepresentationData().getTag() != null) {
                this.getGraphicalRepresentationData().getTag().setValue("1");
                this.getGraphicalRepresentationData().getTag().updateInDatabase();
            }
        } else {
            this.getGraphicalRepresentationData().setData("");
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
