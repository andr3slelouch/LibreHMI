package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.HMIApp;
import andrade.luis.hmiethernetip.models.*;
import andrade.luis.hmiethernetip.models.canvas.*;
import andrade.luis.hmiethernetip.models.canvas.input.CanvasButton;
import andrade.luis.hmiethernetip.models.canvas.input.CanvasPushbutton;
import andrade.luis.hmiethernetip.models.canvas.input.CanvasSlider;
import andrade.luis.hmiethernetip.models.canvas.input.CanvasTextField;
import javafx.geometry.Orientation;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public class HMICanvas extends Pane implements CanvasObjectInterface {
    Logger logger = Logger.getLogger(this.getClass().getName());
    private String mode;
    private String type;
    private int pasteOffset = 10;
    private boolean addOnClickEnabled;
    private static final String LINE_STR = "Line";
    private static final String RECTANGLE_STR = "Rectangle";
    private static final String ELLIPSE_STR = "Ellipse";
    private static final String SYS_DATE_TIME_STR = "SystemDateTime";
    private static final String TEXT_STR = "Text";
    private static final String IMAGE_STR = "Image";
    private static final String SYMBOL_STR = "Symbol";
    private static final String BUTTON_STR = "Button";
    private static final String PUSHBUTTON_STR = "Pushbutton";
    private static final String SLIDER_STR = "Slider";
    private static final String TEXTFIELD_STR = "TextField";
    private static final String ALARM_DISPLAY_STR = "AlarmDisplay";
    private static final String FIGURE_ID = "#createdShape";
    private final ContextMenu rightClickMenu;
    private CanvasPoint lastClickPoint;
    private ArrayList<CanvasPoint> canvasObjectPoints = new ArrayList<>();
    private int expectedPoints = 0;


    public void setShapeArrayList(ArrayList<CanvasObjectData> shapeArrayList) {
        pasteOffset = 0;
        for(CanvasObjectData canvasObjectData : shapeArrayList){
            switch(canvasObjectData.getType()){
                case LINE_STR:
                    addPastedLine(canvasObjectData);
                    continue;
                case RECTANGLE_STR:
                    addPastedRectangle(canvasObjectData);
                    continue;
                case ELLIPSE_STR:
                    addPastedEllipse(canvasObjectData);
                    continue;
                case SYS_DATE_TIME_STR:
                    addPastedSystemDateTimeLabel(canvasObjectData);
                    continue;
                case TEXT_STR:
                    addPastedTextOnCanvasClicked(canvasObjectData);
                    continue;
                case IMAGE_STR:
                    addPastedImageViewOnCanvasClicked(canvasObjectData);
                    continue;
                case BUTTON_STR:
                    addPastedButtonOnCanvasClicked(canvasObjectData);
                    continue;
                case PUSHBUTTON_STR:
                    addPastedPushbuttonOnCanvasClicked(canvasObjectData);
                    continue;
                case SLIDER_STR:
                    addPastedSliderOnCanvasClicked(canvasObjectData);
                    continue;
                case TEXTFIELD_STR:
                    addPastedTextFieldOnCanvasClicked(canvasObjectData);
                    continue;
                case SYMBOL_STR:
                    addPastedSymbolViewOnCanvasClicked(canvasObjectData);
                    continue;
                case ALARM_DISPLAY_STR:
                    addPastedAlarmDisplayOnCanvasClicked(canvasObjectData);
                    continue;
                default:
            }
        }
        pasteOffset = 10;
    }

    private final ArrayList<CanvasObject> shapeArrayList = new ArrayList<>();
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
            case LINE_STR:
                addLineOnCanvasClicked(current);
                break;
            case RECTANGLE_STR:
                addRectangleOnCanvasClicked(current);
                break;
            case ELLIPSE_STR:
                addEllipseOnCanvasClicked(current);
                break;
            case SYS_DATE_TIME_STR:
                addSystemDateTimeLabelOnCanvasClicked(current);
                break;
            case TEXT_STR:
                addTextOnCanvasClicked(current);
                break;
            case BUTTON_STR:
                addButtonOnCanvasClicked(current);
                break;
            case PUSHBUTTON_STR:
                addPushbuttonOnCanvasClicked(current);
                break;
            case SLIDER_STR:
                addSliderOnCanvasClicked(current);
                break;
            case TEXTFIELD_STR:
                addTextFieldOnCanvasClicked(current);
                break;
            case SYMBOL_STR:
                addSymbolViewOnCanvasClicked(current);
                break;
            case IMAGE_STR:
                addImageViewOnCanvasClicked(current);
                break;
            case ALARM_DISPLAY_STR:
                addAlarmDisplayOnCanvasClicked(current);
                break;
            default:
                break;
        }
    }

    private void addEllipseOnCanvasClicked(CanvasPoint current) {
        CanvasEllipse canvasEllipse = new CanvasEllipse(current);
        canvasEllipse.setCanvas(this);
        canvasEllipse.setHmiApp(hmiApp);
        if (this.getShapeArrayList().isEmpty()) {
            canvasEllipse.setObjectId(FIGURE_ID + "0");
        } else {
            canvasEllipse.setObjectId(FIGURE_ID + this.getShapeArrayList().size());
        }
        this.addNewShape(canvasEllipse);
        this.getChildren().add(canvasEllipse);
        canvasEllipse.getHmiApp().setWasModified(true);
        this.setAddOnClickEnabled(false);
    }

    private void addLineOnCanvasClicked(CanvasPoint current) {
        expectedPoints = 2;
        if(canvasObjectPoints.size()!=expectedPoints-1){
            canvasObjectPoints.add(current);
        }else{
            canvasObjectPoints.add(current);
            CanvasPoint beginCanvasPoint;
            if(canvasObjectPoints.get(0).getY()>canvasObjectPoints.get(1).getY() && canvasObjectPoints.get(0).getX()<canvasObjectPoints.get(1).getX()){
                beginCanvasPoint = new CanvasPoint(canvasObjectPoints.get(0).getX(),canvasObjectPoints.get(1).getY());
            }else if(canvasObjectPoints.get(0).getY()>canvasObjectPoints.get(1).getY() && canvasObjectPoints.get(0).getX()>canvasObjectPoints.get(1).getX()){
                beginCanvasPoint = new CanvasPoint(canvasObjectPoints.get(1).getX(),canvasObjectPoints.get(1).getY());
            }else if(canvasObjectPoints.get(0).getY()<canvasObjectPoints.get(1).getY() && canvasObjectPoints.get(0).getX()>canvasObjectPoints.get(1).getX()){
                beginCanvasPoint = new CanvasPoint(canvasObjectPoints.get(1).getX(),canvasObjectPoints.get(0).getY());
            }
            else{
                beginCanvasPoint = canvasObjectPoints.get(0);
            }
            ArrayList<Double> points = new ArrayList<>();
            for (CanvasPoint canvasObjectPoint : canvasObjectPoints) {
                points.add(canvasObjectPoint.getX());
                points.add(canvasObjectPoint.getY());
            }
            CanvasLine canvasLine = new CanvasLine(beginCanvasPoint,points);
            canvasLine.setCanvas(this);
            canvasLine.setHmiApp(this.hmiApp);
            if (this.getShapeArrayList().isEmpty()) {
                canvasLine.setObjectId(FIGURE_ID + "0");
            } else {
                canvasLine.setObjectId(FIGURE_ID + this.getShapeArrayList().size());
            }
            this.addNewShape(canvasLine);
            this.getChildren().add(canvasLine);
            this.setAddOnClickEnabled(false);
            canvasLine.getHmiApp().setWasModified(true);
            canvasObjectPoints.clear();
        }
    }

    private void addAlarmDisplayOnCanvasClicked(CanvasPoint current) {
        CanvasAlarmDisplay canvasAlarmDisplay = new CanvasAlarmDisplay(current,true);
        canvasAlarmDisplay.setCanvas(this);
        canvasAlarmDisplay.setHmiApp(hmiApp);
        canvasAlarmDisplay.setUser(hmiApp.getUser());
        if (this.getShapeArrayList().isEmpty()) {
            canvasAlarmDisplay.setObjectId(FIGURE_ID + "0");
        } else {
            canvasAlarmDisplay.setObjectId(FIGURE_ID + this.getShapeArrayList().size());
        }

        this.addNewShape(canvasAlarmDisplay);
        this.getChildren().add(canvasAlarmDisplay);
        canvasAlarmDisplay.getHmiApp().setWasModified(true);
        canvasAlarmDisplay.setUpdateTableTimeline();
        this.setAddOnClickEnabled(false);

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
            SetImageOptionsWindow imageOptionsWindow = new SetImageOptionsWindow(150,150);
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
        canvasImage.setHmiApp(hmiApp);
        if (this.getShapeArrayList().isEmpty()) {
            canvasImage.setObjectId(FIGURE_ID + "0");
        } else {
            canvasImage.setObjectId(FIGURE_ID + this.getShapeArrayList().size());
        }
        this.addNewShape(canvasImage);
        this.getChildren().add(canvasImage);
        canvasImage.getHmiApp().setWasModified(true);
        this.setAddOnClickEnabled(false);

    }

    private void addPushbuttonOnCanvasClicked(CanvasPoint current) {
        SetColorCommandPushButtonWindow setColorCommandPushButtonWindow = new SetColorCommandPushButtonWindow();
        setColorCommandPushButtonWindow.showAndWait();
        CanvasPushbutton canvasPushbutton = new CanvasPushbutton(current);
        canvasPushbutton.setCanvas(this);
        canvasPushbutton.setHmiApp(hmiApp);
        canvasPushbutton.setUser(hmiApp.getUser());
        if (this.getShapeArrayList().isEmpty()) {
            canvasPushbutton.setObjectId(FIGURE_ID + "0");
        } else {
            canvasPushbutton.setObjectId(FIGURE_ID + this.getShapeArrayList().size());
        }
        canvasPushbutton.setDynamicColors(setColorCommandPushButtonWindow.getButtonLabelTextField().getText(),((RadioButton) setColorCommandPushButtonWindow.getRadioGroup().getSelectedToggle()).getText(),setColorCommandPushButtonWindow.getLocalExpression().getParameters().get(0),new CanvasColor(setColorCommandPushButtonWindow.getPrimaryColorPicker().getValue()),new CanvasColor(setColorCommandPushButtonWindow.getBackgroundColorPicker().getValue()));
        this.addNewShape(canvasPushbutton);
        this.getChildren().add(canvasPushbutton);
        canvasPushbutton.getHmiApp().setWasModified(true);
        this.setAddOnClickEnabled(false);

    }

    private void addSymbolViewOnCanvasClicked(CanvasPoint current) {
        Image selectedSymbol = null;
        String selectedSymbolPath = null;
        String selectedSymbolCategory = null;
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
            SelectHMISymbolWindow selectHMISymbolWindow = new SelectHMISymbolWindow(150,150);
            selectHMISymbolWindow.showAndWait();
            selectedSymbol = selectHMISymbolWindow.getSelectedImage();
            selectedSymbolPath = selectHMISymbolWindow.getSelectedImagePath();
            selectedSymbolCategory = selectHMISymbolWindow.getSymbolCategory();
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
        if(selectedSymbolPath!=null){
            CanvasImage canvasImage = new CanvasImage(selectedSymbol, current, true, selectedSymbolPath, true);
            canvasImage.getCanvasObjectData().setSymbolCategory(selectedSymbolCategory);
            canvasImage.modifyImageViewSizeRotation(isMirroringHorizontal, isMirroringVertical, rotation);
            if(isModifyingColor){
                canvasImage.modifyImageViewColors(color,contrast,brightness,saturation,hue);
            }
            canvasImage.setCanvas(this);
            canvasImage.setHmiApp(hmiApp);
            if (this.getShapeArrayList().isEmpty()) {
                canvasImage.setObjectId(FIGURE_ID + "0");
            } else {
                canvasImage.setObjectId(FIGURE_ID + this.getShapeArrayList().size());
            }
            this.addNewShape(canvasImage);
            this.getChildren().add(canvasImage);
            canvasImage.getHmiApp().setWasModified(true);
            this.setAddOnClickEnabled(false);

        }
    }

    private void addTextFieldOnCanvasClicked(CanvasPoint current) {
        SetTagInputPropertiesWindow setTagInputPropertiesWindow = new SetTagInputPropertiesWindow();
        setTagInputPropertiesWindow.showAndWait();
        CanvasTextField canvasTextField = new CanvasTextField(current, setTagInputPropertiesWindow.getLocalExpression().getParameters().get(0), Double.parseDouble(setTagInputPropertiesWindow.getMinValueField().getText()), Double.parseDouble(setTagInputPropertiesWindow.getMaxValueField().getText()), setTagInputPropertiesWindow.getType());
        canvasTextField.setCanvas(this);
        canvasTextField.setHmiApp(hmiApp);
        canvasTextField.setUser(hmiApp.getUser());
        if (this.getShapeArrayList().isEmpty()) {
            canvasTextField.setObjectId(FIGURE_ID + "0");
        } else {
            canvasTextField.setObjectId(FIGURE_ID + this.getShapeArrayList().size());
        }
        this.addNewShape(canvasTextField);
        this.getChildren().add(canvasTextField);
        canvasTextField.getHmiApp().setWasModified(true);
        this.setAddOnClickEnabled(false);

    }

    private void addSliderOnCanvasClicked(CanvasPoint current) {
        SetSliderPropertiesWindow setSliderPropertiesWindow = new SetSliderPropertiesWindow(150,150);
        setSliderPropertiesWindow.showAndWait();
        double width = setSliderPropertiesWindow.getSizeVBox().getWidthFromField();
        double height = setSliderPropertiesWindow.getSizeVBox().getHeightFromField();
        double minValue = Double.parseDouble(setSliderPropertiesWindow.getMinValueField().getText());
        double maxValue = Double.parseDouble(setSliderPropertiesWindow.getMaxValueField().getText());
        double minorTickValue = Double.parseDouble(setSliderPropertiesWindow.getMinorTickField().getText());
        double majorTickValue = Double.parseDouble(setSliderPropertiesWindow.getMajorTickField().getText());
        boolean snapHandleToTick = setSliderPropertiesWindow.getSnapHandleToTick().isSelected();
        boolean showTicks = setSliderPropertiesWindow.getShowTicks().isSelected();
        boolean showLabelsTicks = setSliderPropertiesWindow.getShowLabelsTicks().isSelected();
        double rotation = Double.parseDouble(setSliderPropertiesWindow.getRotationTextField().getText());
        CanvasOrientation orientation = CanvasOrientation.HORIZONTAL;
        if(setSliderPropertiesWindow.getSelectedOrientation() == CanvasOrientation.VERTICAL){
            orientation = CanvasOrientation.VERTICAL;
        }
        CanvasSlider canvasSlider = null;
        try {
            canvasSlider = new CanvasSlider(current, width,height,setSliderPropertiesWindow.getLocalExpression().getParameters().get(0),minValue,maxValue,minorTickValue,majorTickValue,snapHandleToTick,showTicks,showLabelsTicks,rotation,orientation);

            canvasSlider.setCanvas(this);
            canvasSlider.setHmiApp(hmiApp);
            canvasSlider.setUser(hmiApp.getUser());
            if (this.getShapeArrayList().isEmpty()) {
                canvasSlider.setObjectId(FIGURE_ID + "0");
            } else {
                canvasSlider.setObjectId(FIGURE_ID + this.getShapeArrayList().size());
            }
            this.addNewShape(canvasSlider);
            this.getChildren().add(canvasSlider);
            canvasSlider.getHmiApp().setWasModified(true);
            this.setAddOnClickEnabled(false);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private void addButtonOnCanvasClicked(CanvasPoint current) {
        CanvasButton canvasButton = new CanvasButton(current);
        canvasButton.setHmiApp(hmiApp);
        canvasButton.setCanvas(this);
        canvasButton.setUser(hmiApp.getUser());
        if (this.getShapeArrayList().isEmpty()) {
            canvasButton.setObjectId(FIGURE_ID + "0");
        } else {
            canvasButton.setObjectId(FIGURE_ID + this.getShapeArrayList().size());
        }
        this.addNewShape(canvasButton);
        this.getChildren().add(canvasButton);
        canvasButton.getHmiApp().setWasModified(true);
        this.setAddOnClickEnabled(false);

    }

    public void addRectangleOnCanvasClicked(CanvasPoint current) {
        CanvasRectangle newCreatedRectangle = new CanvasRectangle(current);
        newCreatedRectangle.setCanvas(this);
        newCreatedRectangle.setHmiApp(this.hmiApp);
        if (this.getShapeArrayList().isEmpty()) {
            newCreatedRectangle.setObjectId(FIGURE_ID + "0");
        } else {
            newCreatedRectangle.setObjectId(FIGURE_ID + this.getShapeArrayList().size());
        }
        this.addNewShape(newCreatedRectangle);
        this.getChildren().add(newCreatedRectangle);
        newCreatedRectangle.getHmiApp().setWasModified(true);
        this.setAddOnClickEnabled(false);
    }

    public void addSystemDateTimeLabelOnCanvasClicked(CanvasPoint current) {
        CanvasSystemDateTime canvasSystemDateTime = new CanvasSystemDateTime("yyyy/MM/dd HH:mm:ss", current);
        canvasSystemDateTime.setCanvas(this);
        canvasSystemDateTime.setHmiApp(hmiApp);
        if (this.getShapeArrayList().isEmpty()) {
            canvasSystemDateTime.setObjectId(FIGURE_ID + "0");
        } else {
            canvasSystemDateTime.setObjectId(FIGURE_ID + this.getShapeArrayList().size());
        }
        this.addNewShape(canvasSystemDateTime);
        this.getChildren().add(canvasSystemDateTime);
        canvasSystemDateTime.setTimeline();
        canvasSystemDateTime.getHmiApp().setWasModified(true);
        this.setAddOnClickEnabled(false);

    }

    public void addTextOnCanvasClicked(CanvasPoint current) {
        WriteExpressionWindow writeExpressionWindow = new WriteExpressionWindow();
        writeExpressionWindow.showAndWait();
        if(writeExpressionWindow.isDone()){
            Expression expression = writeExpressionWindow.getLocalExpression();;
            CanvasText canvasText = new CanvasText("0.0", current);
            canvasText.setCanvas(this);
            canvasText.setHmiApp(hmiApp);
            if (this.getShapeArrayList().isEmpty()) {
                canvasText.setObjectId(FIGURE_ID + "0");
            } else {
                canvasText.setObjectId(FIGURE_ID + this.getShapeArrayList().size());
            }
            canvasText.setExpression(expression);
            this.addNewShape(canvasText);
            this.getChildren().add(canvasText);
            canvasText.getHmiApp().setWasModified(true);
            this.setAddOnClickEnabled(false);

        }
    }

    public ArrayList<CanvasObject> getCurrentCanvasObjects() {
        ArrayList<CanvasObject> arrayList = new ArrayList<>();
        for (int i = 0; i < this.getChildren().size(); i++) {
            Node tempNode = this.getChildren().get(i);

            if (Objects.requireNonNullElse(tempNode.getId(), "").startsWith(FIGURE_ID)) {
                arrayList.add((CanvasObject) tempNode);
            }

        }
        return arrayList;
    }

    public boolean existsId(String id) {
        for (CanvasObject canvasObject : getShapeArrayList()) {
            if (canvasObject.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public boolean isFigureContextMenuShowing() {
        for (CanvasObject rect : getShapeArrayList()) {
            if (rect.getRightClickMenu().isShowing()) {
                return true;
            }
        }
        return false;
    }

    public CanvasObject getSelectedFigure() {
        for (CanvasObject canvasObject : getShapeArrayList()) {
            if (canvasObject.isSelected()) {
                return canvasObject;
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
            lastClickPoint=canvasPoint;
        }
    }

    public void paste() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            DataFlavor flavor = new DataFlavor("application/x-java-serialized-object;class=andrade.luis.hmiethernetip.models.canvas.CanvasObjectData");
            if (clipboard.isDataFlavorAvailable(flavor)) {
                CanvasObjectData canvasObjectData = (CanvasObjectData) clipboard.getData(flavor);
                canvasObjectData.setPosition(Objects.requireNonNullElseGet(lastClickPoint, () -> new CanvasPoint(canvasObjectData.getPosition().getX() + 10, canvasObjectData.getPosition().getY() + 10)));
                switch (canvasObjectData.getType()) {
                    case LINE_STR:
                        addPastedLine(canvasObjectData);
                        break;
                    case RECTANGLE_STR:
                        addPastedRectangle(canvasObjectData);
                        break;
                    case ELLIPSE_STR:
                        addPastedEllipse(canvasObjectData);
                        break;
                    case SYS_DATE_TIME_STR:
                        addPastedSystemDateTimeLabel(canvasObjectData);
                        break;
                    case TEXT_STR:
                        addPastedTextOnCanvasClicked(canvasObjectData);
                        break;
                    case BUTTON_STR:
                        addPastedButtonOnCanvasClicked(canvasObjectData);
                        break;
                    case PUSHBUTTON_STR:
                        addPastedPushbuttonOnCanvasClicked(canvasObjectData);
                        break;
                    case SLIDER_STR:
                        addPastedSliderOnCanvasClicked(canvasObjectData);
                        break;
                    case TEXTFIELD_STR:
                        addPastedTextFieldOnCanvasClicked(canvasObjectData);
                        break;
                    case SYMBOL_STR:
                        addPastedSymbolViewOnCanvasClicked(canvasObjectData);
                        break;
                    case IMAGE_STR:
                        addPastedImageViewOnCanvasClicked(canvasObjectData);
                        break;
                    case ALARM_DISPLAY_STR:
                        addPastedAlarmDisplayOnCanvasClicked(canvasObjectData);
                        break;
                    default:
                        break;
                }
            }
        } catch (ClassNotFoundException | IOException | UnsupportedFlavorException e) {
            e.printStackTrace();
        }
    }

    private void addPastedEllipse(CanvasObjectData canvasObjectData) {
        CanvasEllipse canvasEllipse = new CanvasEllipse(canvasObjectData);
        canvasEllipse.setCanvas(this);
        canvasEllipse.setHmiApp(hmiApp);
        canvasEllipse.setObjectId(generateIdForPasteOperation(canvasObjectData));
        canvasEllipse.setPosition(Objects.requireNonNullElseGet(currentMousePosition, () -> new CanvasPoint(canvasObjectData.getPosition().getX() + pasteOffset, canvasObjectData.getPosition().getY() + pasteOffset)));
        this.addNewShape(canvasEllipse);
        this.getChildren().add(canvasEllipse);
        canvasEllipse.getHmiApp().setWasModified(true);
    }

    private void addPastedLine(CanvasObjectData canvasObjectData) {
        CanvasLine canvasLine = new CanvasLine(canvasObjectData);
        canvasLine.setCanvas(this);
        canvasLine.setHmiApp(hmiApp);
        canvasLine.setObjectId(generateIdForPasteOperation(canvasObjectData));
        canvasLine.setPosition(Objects.requireNonNullElseGet(currentMousePosition, () -> new CanvasPoint(canvasObjectData.getPosition().getX() + pasteOffset, canvasObjectData.getPosition().getY() + pasteOffset)));
        this.addNewShape(canvasLine);
        this.getChildren().add(canvasLine);
        canvasLine.getHmiApp().setWasModified(true);
    }

    private void addPastedAlarmDisplayOnCanvasClicked(CanvasObjectData canvasObjectData) {
        CanvasAlarmDisplay canvasAlarmDisplay = new CanvasAlarmDisplay(canvasObjectData);
        canvasAlarmDisplay.setCanvas(this);
        canvasAlarmDisplay.setHmiApp(hmiApp);
        canvasAlarmDisplay.setUser(hmiApp.getUser());
        canvasAlarmDisplay.setObjectId(generateIdForPasteOperation(canvasObjectData));
        canvasAlarmDisplay.setPosition(Objects.requireNonNullElseGet(currentMousePosition, () -> new CanvasPoint(canvasObjectData.getPosition().getX() + pasteOffset, canvasObjectData.getPosition().getY() + pasteOffset)));
        this.addNewShape(canvasAlarmDisplay);
        this.getChildren().add(canvasAlarmDisplay);
        canvasAlarmDisplay.getHmiApp().setWasModified(true);
        canvasAlarmDisplay.setUpdateTableTimeline();
    }

    private void addPastedImageViewOnCanvasClicked(CanvasObjectData canvasObjectData) {
        try {
            CanvasImage canvasImage = new CanvasImage(canvasObjectData);
            canvasImage.setCanvas(this);
            canvasImage.setHmiApp(this.hmiApp);
            canvasImage.setPosition(Objects.requireNonNullElseGet(currentMousePosition, () -> new CanvasPoint(canvasObjectData.getPosition().getX() + pasteOffset, canvasObjectData.getPosition().getY() + pasteOffset)));
            canvasImage.setObjectId(generateIdForPasteOperation(canvasObjectData));
            this.addNewShape(canvasImage);
            this.getChildren().add(canvasImage);
            canvasImage.getHmiApp().setWasModified(true);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error al agregar imagen", "Error:'" + e.getMessage() + "'");
        }

    }

    private void addPastedPushbuttonOnCanvasClicked(CanvasObjectData canvasObjectData) {
        CanvasPushbutton canvasPushbutton = new CanvasPushbutton(canvasObjectData);
        canvasPushbutton.setCanvas(this);
        canvasPushbutton.setHmiApp(hmiApp);
        canvasPushbutton.setUser(hmiApp.getUser());
        canvasPushbutton.setObjectId(generateIdForPasteOperation(canvasObjectData));
        this.addNewShape(canvasPushbutton);
        this.getChildren().add(canvasPushbutton);
        canvasPushbutton.getHmiApp().setWasModified(true);
    }

    private void addPastedSymbolViewOnCanvasClicked(CanvasObjectData canvasObjectData) {
        try {
            CanvasImage canvasImage = new CanvasImage(canvasObjectData);
            canvasImage.setCanvas(this);
            canvasImage.setHmiApp(hmiApp);
            canvasImage.setPosition(Objects.requireNonNullElseGet(currentMousePosition, () -> new CanvasPoint(canvasObjectData.getPosition().getX() + pasteOffset, canvasObjectData.getPosition().getY() + pasteOffset)));
            canvasImage.setObjectId(generateIdForPasteOperation(canvasObjectData));
            this.addNewShape(canvasImage);
            this.getChildren().add(canvasImage);
            canvasImage.getHmiApp().setWasModified(true);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error al agregar imagen", "Error:'" + e.getMessage() + "'");
        }
    }

    private void addPastedTextFieldOnCanvasClicked(CanvasObjectData canvasObjectData) {
        CanvasTextField canvasTextField = new CanvasTextField(canvasObjectData);
        canvasTextField.setCanvas(this);
        canvasTextField.setHmiApp(hmiApp);
        canvasTextField.setPosition(Objects.requireNonNullElseGet(currentMousePosition, () -> new CanvasPoint(canvasObjectData.getPosition().getX() + pasteOffset, canvasObjectData.getPosition().getY() + pasteOffset)));
        canvasTextField.setUser(hmiApp.getUser());
        canvasTextField.setObjectId(generateIdForPasteOperation(canvasObjectData));
        this.addNewShape(canvasTextField);
        this.getChildren().add(canvasTextField);
        canvasTextField.getHmiApp().setWasModified(true);
    }

    private void addPastedSliderOnCanvasClicked(CanvasObjectData canvasObjectData) {
        CanvasSlider canvasSlider = null;
        try {
            canvasSlider = new CanvasSlider(canvasObjectData);
            canvasSlider.setCanvas(this);
            canvasSlider.setHmiApp(hmiApp);
            canvasSlider.setPosition(Objects.requireNonNullElseGet(currentMousePosition, () -> new CanvasPoint(canvasObjectData.getPosition().getX() + pasteOffset, canvasObjectData.getPosition().getY() + pasteOffset)));
            canvasSlider.setUser(hmiApp.getUser());
            canvasSlider.setObjectId(generateIdForPasteOperation(canvasObjectData));
            this.addNewShape(canvasSlider);
            this.getChildren().add(canvasSlider);
            canvasSlider.getHmiApp().setWasModified(true);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private void addPastedButtonOnCanvasClicked(CanvasObjectData canvasObjectData) {
        CanvasButton canvasButton = new CanvasButton(canvasObjectData);
        canvasButton.setHmiApp(hmiApp);
        canvasButton.setSelectedPages(canvasObjectData.getSelectedPages());
        canvasButton.setCanvas(this);
        canvasButton.setPosition(Objects.requireNonNullElseGet(currentMousePosition, () -> new CanvasPoint(canvasObjectData.getPosition().getX() + pasteOffset, canvasObjectData.getPosition().getY() + pasteOffset)));
        canvasButton.setUser(hmiApp.getUser());
        canvasButton.setObjectId(generateIdForPasteOperation(canvasObjectData));
        this.addNewShape(canvasButton);
        this.getChildren().add(canvasButton);
        canvasButton.getHmiApp().setWasModified(true);
    }

    private void addPastedTextOnCanvasClicked(CanvasObjectData canvasObjectData) {
        CanvasText canvasText = new CanvasText(canvasObjectData);
        canvasText.setCanvas(this);
        canvasText.setHmiApp(hmiApp);
        canvasText.setPosition(Objects.requireNonNullElseGet(currentMousePosition, () -> new CanvasPoint(canvasObjectData.getPosition().getX() + pasteOffset, canvasObjectData.getPosition().getY() + pasteOffset)));
        canvasText.setObjectId(generateIdForPasteOperation(canvasObjectData));
        this.addNewShape(canvasText);
        this.getChildren().add(canvasText);
        canvasText.getHmiApp().setWasModified(true);
    }

    public void addPastedRectangle(CanvasObjectData canvasObjectData) {
        CanvasRectangle canvasRectangle = new CanvasRectangle(canvasObjectData);
        canvasRectangle.setCanvas(this);
        canvasRectangle.setHmiApp(hmiApp);
        canvasRectangle.setObjectId(generateIdForPasteOperation(canvasObjectData));
        canvasRectangle.setPosition(Objects.requireNonNullElseGet(currentMousePosition, () -> new CanvasPoint(canvasObjectData.getPosition().getX() + pasteOffset, canvasObjectData.getPosition().getY() + pasteOffset)));
        this.addNewShape(canvasRectangle);
        this.getChildren().add(canvasRectangle);
        canvasRectangle.setPercentFill(canvasObjectData.getExpression(), canvasObjectData.getPrimaryColor(), canvasObjectData.getBackgroundColor(), canvasObjectData.getOrientation());
        canvasRectangle.getHmiApp().setWasModified(true);
    }

    public void addPastedSystemDateTimeLabel(CanvasObjectData canvasObjectData) {
        CanvasSystemDateTime canvasSystemDateTime = new CanvasSystemDateTime(canvasObjectData);
        canvasSystemDateTime.setCanvas(this);
        canvasSystemDateTime.setHmiApp(hmiApp);
        canvasSystemDateTime.setPosition(Objects.requireNonNullElseGet(currentMousePosition, () -> new CanvasPoint(canvasObjectData.getPosition().getX() + pasteOffset, canvasObjectData.getPosition().getY() + pasteOffset)));
        canvasSystemDateTime.setObjectId(generateIdForPasteOperation(canvasObjectData));
        this.addNewShape(canvasSystemDateTime);
        this.getChildren().add(canvasSystemDateTime);
        canvasSystemDateTime.setTimeline();
        canvasSystemDateTime.getHmiApp().setWasModified(true);
    }

    public String generateIdForPasteOperation(CanvasObjectData canvasObjectData) {
        if (canvasObjectData.getOperation().equals("Copy")) {
            int copyNumber = 0;
            for (int i = 0; i < getShapeArrayList().size(); i++) {
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
        CanvasObject toDelete = new CanvasObject();
        for (CanvasObject temp : getShapeArrayList()) {
            if (temp.getId().equals(canvasObjectData.getId())) {
                toDelete = temp;
            }
        }
        this.shapeArrayList.remove(toDelete);
        this.getChildren().remove(toDelete);
        this.getHmiApp().setWasModified(true);
    }

    public void setType(String type) {
        this.type = type;
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

    public ArrayList<CanvasPoint> getCanvasObjectPoints() {
        return canvasObjectPoints;
    }

    public void setCanvasObjectPoints(ArrayList<CanvasPoint> canvasObjectPoints) {
        this.canvasObjectPoints = canvasObjectPoints;
    }

    public int getExpectedPoints() {
        return expectedPoints;
    }

    public void setExpectedPoints(int expectedPoints) {
        this.expectedPoints = expectedPoints;
    }
}
