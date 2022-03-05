package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.HMIApp;
import andrade.luis.hmiethernetip.models.*;
import andrade.luis.hmiethernetip.models.canvas.*;
import andrade.luis.hmiethernetip.models.canvas.input.CanvasButton;
import andrade.luis.hmiethernetip.models.canvas.input.CanvasPushbutton;
import andrade.luis.hmiethernetip.models.canvas.input.CanvasSlider;
import andrade.luis.hmiethernetip.models.canvas.input.CanvasTextField;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;


import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HMICanvas extends Pane implements CanvasObjectInterface, Cloneable {
    Logger logger = Logger.getLogger(this.getClass().getName());
    private String mode;
    private String type;
    private boolean addOnClickEnabled;
    private static final String FIGURE_ID = "#createdShape";
    private ContextMenu rightClickMenu;
    private ArrayList<CanvasObject> shapeArrayList = new ArrayList<>();
    private CanvasPoint currentMousePosition;
    private HMIApp hmiApp;

    public HMIApp getHmiApp() {
        return hmiApp;
    }

    public void setHmiApp(HMIApp hmiApp) {
        this.hmiApp = hmiApp;
    }

    public boolean isAddOnClickEnabled() {
        return addOnClickEnabled;
    }

    public void setAddOnClickEnabled(boolean addOnClickEnabled) {
        this.addOnClickEnabled = addOnClickEnabled;
    }

    public ArrayList<CanvasObject> getShapeArrayList() {
        return shapeArrayList;
    }

    public void addNewShape(CanvasObject shape) {
        this.shapeArrayList.add(shape);
    }

    public ContextMenu getRightClickMenu() {
        return rightClickMenu;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public HMICanvas() {
        this.setId("MainCanvas");
        rightClickMenu = new ContextMenu();
        rightClickMenu.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                event.consume();
            }
        });
        this.mode = "Default";

        MenuItem pasteMenuItem = new MenuItem("Paste");
        pasteMenuItem.setOnAction(actionEvent -> paste());

        rightClickMenu.getItems().addAll(pasteMenuItem);
    }

    public void addFigureOnCanvasClicked(CanvasPoint current) {
        switch (type) {
            case "Rectangle":
                addRectangleOnCanvasClicked(current);
                break;
            case "Label":
                addLabelOnCanvasClicked(current);
                break;
            case "SystemDateTimeLabel":
                addSystemDateTimeLabelOnCanvasClicked(current);
                break;
            case "Text":
                addTextOnCanvasClicked(current);
                break;
            case "Button":
                addButtonOnCanvasClicked(current);
                break;
            case "Pushbutton":
                addPushbuttonOnCanvasClicked(current);
                break;
            case "Slider":
                addSliderOnCanvasClicked(current);
                break;
            case "TextField":
                addTextFieldOnCanvasClicked(current);
                break;
            case "Symbol":
                addSymbolViewOnCanvasClicked(current);
                break;
            case "Image":
                addImageViewOnCanvasClicked(current);
                break;
            default:
                break;
        }
    }

    private void addImageViewOnCanvasClicked(CanvasPoint current) {
        Image selectedImage = null;
        String selectedImagePath = null;
        boolean isMirroringVertical = false;
        boolean isMirroringHorizontal = false;
        boolean isModifyingColor = false;
        double contrast = 0;
        double brightness = 0;
        double saturation = 0;
        double hue = 0;
        double rotation = 0;
        CanvasColor color = new CanvasColor(Color.WHITE);
        try {
            SetImageOptionsWindow imageOptionsWindow = new SetImageOptionsWindow();
            imageOptionsWindow.showAndWait();
            selectedImagePath = imageOptionsWindow.getImagePathTextField().getText();
            selectedImage = new Image(new FileInputStream(selectedImagePath));
            isMirroringVertical = imageOptionsWindow.isMirroringVertical();
            isMirroringHorizontal = imageOptionsWindow.isMirroringHorizontal();
            rotation = Double.parseDouble(imageOptionsWindow.getRotationTextField().getText());
            isModifyingColor = imageOptionsWindow.isModifyingColor();
            contrast = Double.parseDouble(imageOptionsWindow.getContrastTextField().getText());
            brightness = Double.parseDouble(imageOptionsWindow.getBrightnessTextField().getText());
            saturation = Double.parseDouble(imageOptionsWindow.getSaturationTextField().getText());
            hue = Double.parseDouble(imageOptionsWindow.getHueTextField().getText());
            color = new CanvasColor(imageOptionsWindow.getColorPicker().getValue());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        CanvasImage canvasImage = new CanvasImage(selectedImage, current, true, selectedImagePath, false);
        canvasImage.modifyImageViewSizeRotation(isMirroringHorizontal, isMirroringVertical, rotation);
        if(isModifyingColor){
            canvasImage.modifyImageViewColors(color,contrast,brightness,saturation,hue);
        }
        canvasImage.setCanvas(this);

        if (this.getShapeArrayList().isEmpty()) {
            canvasImage.setId(FIGURE_ID + "0");
        } else {
            canvasImage.setId(FIGURE_ID + this.getShapeArrayList().size());
        }
        this.addNewShape(canvasImage);
        this.getChildren().add(canvasImage);
    }

    private void addPushbuttonOnCanvasClicked(CanvasPoint current) {
        SetColorCommandPushButtonWindow setColorCommandPushButtonWindow = new SetColorCommandPushButtonWindow();
        setColorCommandPushButtonWindow.showAndWait();
        CanvasPushbutton canvasPushbutton = new CanvasPushbutton(current);
        canvasPushbutton.setCanvas(this);
        canvasPushbutton.setUser(hmiApp.getUser());
        if (this.getShapeArrayList().isEmpty()) {
            canvasPushbutton.setId(FIGURE_ID + "0");
        } else {
            canvasPushbutton.setId(FIGURE_ID + this.getShapeArrayList().size());
        }
        canvasPushbutton.setDynamicColors(setColorCommandPushButtonWindow.getButtonLabelTextField().getText(),((RadioButton) setColorCommandPushButtonWindow.getRadioGroup().getSelectedToggle()).getText(),setColorCommandPushButtonWindow.getLocalExpression().getParameters().get(0),new CanvasColor(setColorCommandPushButtonWindow.getPrimaryColorPicker().getValue()),new CanvasColor(setColorCommandPushButtonWindow.getBackgroundColorPicker().getValue()));
        this.addNewShape(canvasPushbutton);
        this.getChildren().add(canvasPushbutton);
    }

    private void addSymbolViewOnCanvasClicked(CanvasPoint current) {
        Image selectedSymbol = null;
        String selectedSymbolPath = null;
        boolean isMirroringVertical = false;
        boolean isMirroringHorizontal = false;
        boolean isModifyingColor = false;
        double contrast = 0;
        double brightness = 0;
        double saturation = 0;
        double hue = 0;
        double rotation = 0;
        CanvasColor color = new CanvasColor(Color.WHITE);
        try {
            SelectHMISymbolWindow selectHMISymbolWindow = new SelectHMISymbolWindow();
            selectHMISymbolWindow.showAndWait();
            selectedSymbol = selectHMISymbolWindow.getSelectedImage();
            selectedSymbolPath = selectHMISymbolWindow.getSelectedImagePath();
            isMirroringVertical = selectHMISymbolWindow.isMirroringVertical();
            isMirroringHorizontal = selectHMISymbolWindow.isMirroringHorizontal();
            rotation = selectHMISymbolWindow.getRotation();
            isModifyingColor = selectHMISymbolWindow.isModifyingColor();
            contrast = selectHMISymbolWindow.getContrast();
            brightness = selectHMISymbolWindow.getBrightness();
            saturation = selectHMISymbolWindow.getSaturation();
            hue = selectHMISymbolWindow.getHue();
            color = selectHMISymbolWindow.getColor();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        CanvasImage canvasImage = new CanvasImage(selectedSymbol, current, true, selectedSymbolPath, true);
        canvasImage.modifyImageViewSizeRotation(isMirroringHorizontal, isMirroringVertical, rotation);
        logger.log(Level.INFO, String.valueOf(isModifyingColor));
        if(isModifyingColor){
            canvasImage.modifyImageViewColors(color,contrast,brightness,saturation,hue);
        }
        canvasImage.setCanvas(this);

        if (this.getShapeArrayList().isEmpty()) {
            canvasImage.setId(FIGURE_ID + "0");
        } else {
            canvasImage.setId(FIGURE_ID + this.getShapeArrayList().size());
        }
        this.addNewShape(canvasImage);
        this.getChildren().add(canvasImage);
    }

    private void addTextFieldOnCanvasClicked(CanvasPoint current) {
        SetTextFieldPropertiesWindow setTextFieldPropertiesWindow = new SetTextFieldPropertiesWindow();
        setTextFieldPropertiesWindow.showAndWait();
        CanvasTextField canvasTextField = new CanvasTextField(current, setTextFieldPropertiesWindow.getLocalExpression().getParameters().get(0), Double.parseDouble(setTextFieldPropertiesWindow.getMinValueField().getText()), Double.parseDouble(setTextFieldPropertiesWindow.getMaxValueField().getText()), setTextFieldPropertiesWindow.getType());
        canvasTextField.setCanvas(this);
        canvasTextField.setUser(hmiApp.getUser());
        if (this.getShapeArrayList().isEmpty()) {
            canvasTextField.setId(FIGURE_ID + "0");
        } else {
            canvasTextField.setId(FIGURE_ID + this.getShapeArrayList().size());
        }
        this.addNewShape(canvasTextField);
        this.getChildren().add(canvasTextField);
    }

    private void addSliderOnCanvasClicked(CanvasPoint current) {
        CanvasSlider canvasSlider = new CanvasSlider(current);
        canvasSlider.setCanvas(this);
        canvasSlider.setUser(hmiApp.getUser());
        if (this.getShapeArrayList().isEmpty()) {
            canvasSlider.setId(FIGURE_ID + "0");
        } else {
            canvasSlider.setId(FIGURE_ID + this.getShapeArrayList().size());
        }
        this.addNewShape(canvasSlider);
        this.getChildren().add(canvasSlider);
    }

    private void addButtonOnCanvasClicked(CanvasPoint current) {
        CanvasButton canvasButton = new CanvasButton(current);
        canvasButton.setHmiApp(hmiApp);
        canvasButton.setCanvas(this);
        canvasButton.setUser(hmiApp.getUser());
        if (this.getShapeArrayList().isEmpty()) {
            canvasButton.setId(FIGURE_ID + "0");
        } else {
            canvasButton.setId(FIGURE_ID + this.getShapeArrayList().size());
        }
        this.addNewShape(canvasButton);
        this.getChildren().add(canvasButton);
    }

    public void addRectangleOnCanvasClicked(CanvasPoint current) {
        CanvasRectangle newCreatedRectangle = new CanvasRectangle(current);
        newCreatedRectangle.setCanvas(this);
        if (this.getShapeArrayList().isEmpty()) {
            newCreatedRectangle.setId(FIGURE_ID + "0");
        } else {
            newCreatedRectangle.setId(FIGURE_ID + this.getShapeArrayList().size());
        }
        this.addNewShape(newCreatedRectangle);
        this.getChildren().add(newCreatedRectangle);
    }

    public void addLabelOnCanvasClicked(CanvasPoint current) {
        CanvasLabel canvasLabel = new CanvasLabel("Test", current);
        canvasLabel.setCanvas(this);
        if (this.getShapeArrayList().isEmpty()) {
            canvasLabel.setId(FIGURE_ID + "0");
        } else {
            canvasLabel.setId(FIGURE_ID + this.getShapeArrayList().size());
        }
        this.addNewShape(canvasLabel);
        this.getChildren().add(canvasLabel);
    }

    public void addSystemDateTimeLabelOnCanvasClicked(CanvasPoint current) {
        CanvasSystemDateTimeLabel canvasSystemDateTimeLabel = new CanvasSystemDateTimeLabel("yyyy/MM/dd HH:mm:ss", current);
        canvasSystemDateTimeLabel.setCanvas(this);
        if (this.getShapeArrayList().isEmpty()) {
            canvasSystemDateTimeLabel.setId(FIGURE_ID + "0");
        } else {
            canvasSystemDateTimeLabel.setId(FIGURE_ID + this.getShapeArrayList().size());
        }
        this.addNewShape(canvasSystemDateTimeLabel);
        this.getChildren().add(canvasSystemDateTimeLabel);
        canvasSystemDateTimeLabel.setTimeline();
    }

    public void addTextOnCanvasClicked(CanvasPoint current) {
        CanvasText canvasText = new CanvasText("0.0", current);
        canvasText.setCanvas(this);
        if (this.getShapeArrayList().isEmpty()) {
            canvasText.setId(FIGURE_ID + "0");
        } else {
            canvasText.setId(FIGURE_ID + this.getShapeArrayList().size());
        }
        Expression expression;
        expression = writeExpression();
        canvasText.setExpression(expression);
        this.addNewShape(canvasText);
        this.getChildren().add(canvasText);


    }

    public Expression writeExpression() {
        WriteExpressionWindow writeExpressionWindow = new WriteExpressionWindow();
        writeExpressionWindow.showAndWait();
        return writeExpressionWindow.getLocalExpression();
    }

    public ArrayList<CanvasObject> getCurrentCanvasObjects() {
        ArrayList<andrade.luis.hmiethernetip.models.canvas.CanvasObject> arrayList = new ArrayList<>();
        for (int i = 0; i < this.getChildren().size(); i++) {
            Node tempNode = this.getChildren().get(i);

            if (Objects.requireNonNullElse(tempNode.getId(), "").startsWith(FIGURE_ID)) {
                arrayList.add((andrade.luis.hmiethernetip.models.canvas.CanvasObject) tempNode);
            }

        }
        return arrayList;
    }

    public boolean existsId(String id) {
        for (andrade.luis.hmiethernetip.models.canvas.CanvasObject rect : getCurrentCanvasObjects()) {
            if (rect.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public boolean isFigureContextMenuShowing() {
        for (andrade.luis.hmiethernetip.models.canvas.CanvasObject rect : this.getCurrentCanvasObjects()) {
            if (rect.getRightClickMenu().isShowing()) {
                return true;
            }
        }
        return false;
    }

    public CanvasObject getSelectedFigure() {
        for (andrade.luis.hmiethernetip.models.canvas.CanvasObject rect : this.getCurrentCanvasObjects()) {
            if (rect.isSelected()) {
                return rect;
            }
        }
        return null;
    }

    public void showContextMenu(double screenX, double screenY) {
        currentMousePosition = new CanvasPoint(screenX, screenY);
        rightClickMenu.show(HMICanvas.this, screenX, screenY);
    }

    public void onCanvasClicked(CanvasPoint canvasPoint) {
        if (!isFigureContextMenuShowing()) {
            showContextMenu(canvasPoint.getX(), canvasPoint.getY());
        }
    }

    public void paste() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            DataFlavor flavor = new DataFlavor("application/x-java-serialized-object;class=andrade.luis.hmiethernetip.models.canvas.CanvasObjectData");
            if (clipboard.isDataFlavorAvailable(flavor)) {
                CanvasObjectData canvasObjectData = (CanvasObjectData) clipboard.getData(flavor);
                switch (canvasObjectData.getType()) {
                    case "Rectangle":
                        addPastedRectangle(canvasObjectData);
                        break;
                    case "Label":
                        addPastedLabel(canvasObjectData);
                        break;
                    case "SystemDateTimeLabel":
                        addPastedSystemDateTimeLabel(canvasObjectData);
                        break;
                    case "Text":
                        addPastedTextOnCanvasClicked(canvasObjectData);
                        break;
                    case "Button":
                        addPastedButtonOnCanvasClicked(canvasObjectData);
                        break;
                    case "Pushbutton":
                        addPastedPushbuttonOnCanvasClicked(canvasObjectData);
                        break;
                    case "Slider":
                        addPastedSliderOnCanvasClicked(canvasObjectData);
                        break;
                    case "TextField":
                        addPastedTextFieldOnCanvasClicked(canvasObjectData);
                        break;
                    case "Symbol":
                        addPastedSymbolViewOnCanvasClicked(canvasObjectData);
                        break;
                    case "Image":
                        addPastedImageViewOnCanvasClicked(canvasObjectData);
                        break;
                    default:
                        break;
                }
            }
        } catch (ClassNotFoundException | IOException | UnsupportedFlavorException e) {
            e.printStackTrace();
        }
    }

    private void addPastedImageViewOnCanvasClicked(CanvasObjectData canvasObjectData) {
        try {
            CanvasImage canvasImage = new CanvasImage(canvasObjectData);
            canvasImage.setCanvas(this);
            canvasImage.setCenter(Objects.requireNonNullElseGet(currentMousePosition, () -> new CanvasPoint(canvasObjectData.getCenter().getX() + 10, canvasObjectData.getCenter().getY() + 10)));
            canvasImage.setId(generateIdForPasteOperation(canvasObjectData));
            this.addNewShape(canvasImage);
            this.getChildren().add(canvasImage);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error al agregar imagen", "Error:'" + e.getMessage() + "'");
        }

    }

    private void addPastedPushbuttonOnCanvasClicked(CanvasObjectData canvasObjectData) {
        CanvasPushbutton canvasPushbutton = new CanvasPushbutton(canvasObjectData);
        canvasPushbutton.setCanvas(this);
        canvasPushbutton.setUser(hmiApp.getUser());
        canvasPushbutton.setId(generateIdForPasteOperation(canvasObjectData));
        this.addNewShape(canvasPushbutton);
        this.getChildren().add(canvasPushbutton);
    }

    private void addPastedSymbolViewOnCanvasClicked(CanvasObjectData canvasObjectData) {
        try {
            CanvasImage canvasImage = new CanvasImage(canvasObjectData);
            canvasImage.setCanvas(this);
            canvasImage.setCenter(Objects.requireNonNullElseGet(currentMousePosition, () -> new CanvasPoint(canvasObjectData.getCenter().getX() + 10, canvasObjectData.getCenter().getY() + 10)));
            canvasImage.setId(generateIdForPasteOperation(canvasObjectData));
            this.addNewShape(canvasImage);
            this.getChildren().add(canvasImage);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error al agregar imagen", "Error:'" + e.getMessage() + "'");
        }
    }

    private void addPastedTextFieldOnCanvasClicked(CanvasObjectData canvasObjectData) {
        CanvasTextField canvasTextField = new CanvasTextField(canvasObjectData);
        canvasTextField.setCanvas(this);
        canvasTextField.setCenter(Objects.requireNonNullElseGet(currentMousePosition, () -> new CanvasPoint(canvasObjectData.getCenter().getX() + 10, canvasObjectData.getCenter().getY() + 10)));
        canvasTextField.setUser(hmiApp.getUser());
        canvasTextField.setId(generateIdForPasteOperation(canvasObjectData));
        this.addNewShape(canvasTextField);
        this.getChildren().add(canvasTextField);
    }

    private void addPastedSliderOnCanvasClicked(CanvasObjectData canvasObjectData) {
    }

    private void addPastedButtonOnCanvasClicked(CanvasObjectData canvasObjectData) {
    }

    private void addPastedTextOnCanvasClicked(CanvasObjectData canvasObjectData) {
        CanvasText canvasText = new CanvasText(canvasObjectData);
        canvasText.setCanvas(this);
        canvasText.setCenter(Objects.requireNonNullElseGet(currentMousePosition, () -> new CanvasPoint(canvasObjectData.getCenter().getX() + 10, canvasObjectData.getCenter().getY() + 10)));
        canvasText.setId(generateIdForPasteOperation(canvasObjectData));
        this.addNewShape(canvasText);
        this.getChildren().add(canvasText);
        canvasText.setExpression(canvasObjectData.getExpression());
    }

    public void addPastedRectangle(CanvasObjectData canvasObjectData) {
        CanvasRectangle canvasRectangle = new CanvasRectangle(canvasObjectData);
        canvasRectangle.setCanvas(this);
        canvasRectangle.setCenter(Objects.requireNonNullElseGet(currentMousePosition, () -> new CanvasPoint(canvasObjectData.getCenter().getX() + 10, canvasObjectData.getCenter().getY() + 10)));
        canvasRectangle.setId(generateIdForPasteOperation(canvasObjectData));
        this.addNewShape(canvasRectangle);
        this.getChildren().add(canvasRectangle);
        canvasRectangle.setPercentFill(canvasObjectData.getExpression(), canvasObjectData.getPrimaryColor(), canvasObjectData.getBackgroundColor(), canvasObjectData.getOrientation());

        Gson gson = new Gson();
        logger.log(Level.INFO,gson.toJson(canvasObjectData));
    }

    public void addPastedLabel(CanvasObjectData canvasObjectData) {
        CanvasLabel canvasLabel = new CanvasLabel(canvasObjectData);
        canvasLabel.setCanvas(this);
        canvasLabel.setCenter(Objects.requireNonNullElseGet(currentMousePosition, () -> new CanvasPoint(canvasObjectData.getCenter().getX() + 10, canvasObjectData.getCenter().getY() + 10)));
        canvasLabel.setId(generateIdForPasteOperation(canvasObjectData));
        this.addNewShape(canvasLabel);
        this.getChildren().add(canvasLabel);
    }

    public void addPastedSystemDateTimeLabel(CanvasObjectData canvasObjectData) {
        CanvasSystemDateTimeLabel canvasSystemDateTimeLabel = new CanvasSystemDateTimeLabel(canvasObjectData);
        canvasSystemDateTimeLabel.setCanvas(this);
        canvasSystemDateTimeLabel.setCenter(Objects.requireNonNullElseGet(currentMousePosition, () -> new CanvasPoint(canvasObjectData.getCenter().getX() + 10, canvasObjectData.getCenter().getY() + 10)));
        canvasSystemDateTimeLabel.setId(generateIdForPasteOperation(canvasObjectData));
        this.addNewShape(canvasSystemDateTimeLabel);
        this.getChildren().add(canvasSystemDateTimeLabel);
        canvasSystemDateTimeLabel.setTimeline();
    }

    public String generateIdForPasteOperation(CanvasObjectData canvasObjectData) {
        if (canvasObjectData.getOperation().equals("Copy")) {
            int copyNumber = 0;
            for (int i = 0; i < getCurrentCanvasObjects().size(); i++) {
                if (i == 0 && existsId(canvasObjectData.getId())) {
                    copyNumber = i + 1;
                } else {
                    if (existsId(canvasObjectData.getId() + "(" + i + ")")) {
                        copyNumber = i + 1;
                    }
                }
            }
            return canvasObjectData.getId() + "(" + copyNumber + ")";
        } else {
            return canvasObjectData.getId();
        }
    }

    @Override
    public void delete(CanvasObjectData canvasObjectData) {
        for (andrade.luis.hmiethernetip.models.canvas.CanvasObject temp : getCurrentCanvasObjects()) {
            if (temp.getId().equals(canvasObjectData.getId())) {
                this.shapeArrayList.remove(temp);
                this.getChildren().remove(temp);
            }
        }
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public HMICanvas clone() throws CloneNotSupportedException {
        HMICanvas clone = (HMICanvas) super.clone();
        return clone;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(message);

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(okButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == okButton) {
            alert.close();
        }
    }
}
