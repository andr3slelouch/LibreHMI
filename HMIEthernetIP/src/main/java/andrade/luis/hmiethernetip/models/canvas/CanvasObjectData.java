package andrade.luis.hmiethernetip.models.canvas;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;

import andrade.luis.hmiethernetip.models.Expression;
import andrade.luis.hmiethernetip.models.PercentFillOrientation;
import andrade.luis.hmiethernetip.models.Tag;
import andrade.luis.hmiethernetip.models.users.HMIUser;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CanvasObjectData implements Serializable, Transferable {
    private static final String NULL_STR = "<null>";
    @SerializedName("selected")
    @Expose
    private boolean selected;
    @SerializedName("mouseOver")
    @Expose
    private boolean mouseOver;
    @SerializedName("width")
    @Expose
    private double width;
    @SerializedName("height")
    @Expose
    private double height;
    @SerializedName("center")
    @Expose
    private CanvasPoint center;
    @SerializedName("position")
    @Expose
    private CanvasPoint position;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("operation")
    @Expose
    private String operation="";
    @SerializedName("data")
    @Expose
    private String data;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("dataType")
    @Expose
    private String dataType;
    @SerializedName("tag")
    @Expose
    private Tag tag;
    @SerializedName("expression")
    @Expose
    private Expression expression;
    @SerializedName("visibilityExpression")
    @Expose
    private Expression visibilityExpression;
    @SerializedName("primaryColor")
    @Expose
    private CanvasColor primaryColor;
    @SerializedName("backgroundColor")
    @Expose
    private CanvasColor backgroundColor;
    @SerializedName("orientation")
    @Expose
    private PercentFillOrientation orientation;
    @SerializedName("minValue")
    @Expose
    private double minValue;
    @SerializedName("maxValue")
    @Expose
    private double maxValue;
    @SerializedName("minorTickValue")
    @Expose
    private double minorTickValue;
    @SerializedName("majorTickValue")
    @Expose
    private double majorTickValue;
    @SerializedName("snapHandleToTick")
    @Expose
    private boolean snapHandleToTick;
    @SerializedName("showTicks")
    @Expose
    private boolean showingTicks;
    @SerializedName("showLabelsTicks")
    @Expose
    private boolean showingLabelsTicks;
    @SerializedName("mirroringHorizontal")
    @Expose
    private boolean mirroringHorizontal = false;
    @SerializedName("mirroringVertical")
    @Expose
    private boolean mirroringVertical = false;
    @SerializedName("preservingRatio")
    @Expose
    private boolean preservingRatio = false;
    @SerializedName("rotation")
    @Expose
    private double rotation = 0;
    @SerializedName("isImageSymbol")
    @Expose
    private boolean imageSymbol;
    @SerializedName("isModifyingImage")
    @Expose
    private boolean isModifyingImage = false;
    @SerializedName("contrast")
    @Expose
    private double contrast;
    @SerializedName("brightness")
    @Expose
    private double brightness;
    @SerializedName("saturation")
    @Expose
    private double saturation;
    @SerializedName("hue")
    @Expose
    private double hue;
    @SerializedName("mode")
    @Expose
    private String mode;
    @SerializedName("status")
    @Expose
    private String status="";
    @SerializedName("visible")
    @Expose
    private boolean visible;
    @SerializedName("selectedPages")
    @Expose
    private ArrayList<String> selectedPages;


    private static final long serialVersionUID = 6976931227659398285L;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isMouseOver() {
        return mouseOver;
    }

    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CanvasPoint getCenter() {
        return center;
    }

    public void setCenter(CanvasPoint center) {
        this.center = center;
    }

    public CanvasPoint getPosition() {
        return position;
    }

    public void setPosition(CanvasPoint position) {
        this.position = position;
        //this.center = determineCenterFromPosition(position);
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public Expression getExpression() {
        return expression;
    }

    public CanvasColor getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(CanvasColor primaryColor) {
        this.primaryColor = primaryColor;
    }

    public CanvasColor getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(CanvasColor backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public PercentFillOrientation getOrientation() {
        return orientation;
    }

    public void setOrientation(PercentFillOrientation orientation) {
        this.orientation = orientation;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public boolean isModifyingImage() {
        return isModifyingImage;
    }

    public void setModifyingImage(boolean modifyingImage) {
        isModifyingImage = modifyingImage;
    }

    public double getContrast() {
        return contrast;
    }

    public void setContrast(double contrast) {
        this.contrast = contrast;
    }

    public double getBrightness() {
        return brightness;
    }

    public void setBrightness(double brightness) {
        this.brightness = brightness;
    }

    public double getSaturation() {
        return saturation;
    }

    public void setSaturation(double saturation) {
        this.saturation = saturation;
    }

    public boolean isMirroringHorizontal() {
        return mirroringHorizontal;
    }

    public void setMirroringHorizontal(boolean mirroringHorizontal) {
        this.mirroringHorizontal = mirroringHorizontal;
    }

    public boolean isMirroringVertical() {
        return mirroringVertical;
    }

    public void setMirroringVertical(boolean mirroringVertical) {
        this.mirroringVertical = mirroringVertical;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<String> getSelectedPages() {
        return selectedPages;
    }

    public void setSelectedPages(ArrayList<String> selectedPages) {
        this.selectedPages = selectedPages;
    }

    public double getMinorTickValue() {
        return minorTickValue;
    }

    public void setMinorTickValue(double minorTickValue) {
        this.minorTickValue = minorTickValue;
    }

    public double getMajorTickValue() {
        return majorTickValue;
    }

    public void setMajorTickValue(double majorTickValue) {
        this.majorTickValue = majorTickValue;
    }

    public boolean isSnapHandleToTick() {
        return snapHandleToTick;
    }

    public void setSnapHandleToTick(boolean snapHandleToTick) {
        this.snapHandleToTick = snapHandleToTick;
    }

    public boolean isShowingTicks() {
        return showingTicks;
    }

    public void setShowingTicks(boolean showingTicks) {
        this.showingTicks = showingTicks;
    }

    public boolean isShowingLabelsTicks() {
        return showingLabelsTicks;
    }

    public void setShowingLabelsTicks(boolean showingLabelsTicks) {
        this.showingLabelsTicks = showingLabelsTicks;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public CanvasPoint determineCenterFromPosition(CanvasPoint position) {
        double tempX = position.getX() + getWidth()/2;
        double tempY = position.getY() + getHeight()/2;
        return new CanvasPoint(tempX, tempY);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(CanvasObjectData.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("selected");
        sb.append('=');
        sb.append(this.selected);
        sb.append(',');
        sb.append("mouseOver");
        sb.append('=');
        sb.append(this.mouseOver);
        sb.append(',');
        sb.append("width");
        sb.append('=');
        sb.append(this.width);
        sb.append(',');
        sb.append("height");
        sb.append('=');
        sb.append(this.height);
        sb.append(',');
        sb.append("center");
        sb.append('=');
        sb.append(((this.center == null) ? NULL_STR : this.center));
        sb.append(',');
        sb.append("position");
        sb.append('=');
        sb.append(((this.position == null) ? NULL_STR : this.position));
        sb.append(',');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null) ? NULL_STR : this.id));
        sb.append(',');
        sb.append("operation");
        sb.append('=');
        sb.append(((this.operation == null) ? NULL_STR : this.operation));
        sb.append(',');
        sb.append("data");
        sb.append('=');
        sb.append(((this.data == null) ? NULL_STR : this.data));
        sb.append(',');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null) ? NULL_STR : this.name));
        sb.append(',');
        sb.append("tag");
        sb.append('=');
        sb.append(((this.tag == null) ? NULL_STR : this.tag.toString()));
        sb.append(',');
        sb.append("refillExpression");
        sb.append('=');
        sb.append(((this.expression == null) ? NULL_STR : this.expression.toString()));
        sb.append(',');
        sb.append("primaryColor");
        sb.append('=');
        sb.append(((this.primaryColor == null) ? NULL_STR : this.primaryColor.toString()));
        sb.append(',');
        sb.append("backgroundColor");
        sb.append('=');
        sb.append(((this.backgroundColor == null) ? NULL_STR : this.backgroundColor.toString()));
        sb.append(',');
        sb.append("orientation");
        sb.append('=');
        sb.append(((this.orientation == null) ? NULL_STR : this.orientation.toString()));
        sb.append(',');
        sb.append("minValue");
        sb.append('=');
        sb.append(minValue);
        sb.append(',');
        sb.append("maxValue");
        sb.append('=');
        sb.append(maxValue);
        sb.append(',');
        sb.append("mirroringHorizontal");
        sb.append('=');
        sb.append(mirroringHorizontal);
        sb.append(',');
        sb.append("mirroringVertical");
        sb.append('=');
        sb.append(mirroringVertical);
        sb.append(',');
        sb.append("rotation");
        sb.append('=');
        sb.append(rotation);
        sb.append(',');
        sb.append("isImageSymbol");
        sb.append('=');
        sb.append(imageSymbol);
        sb.append(',');
        sb.append("isModifyingImage");
        sb.append('=');
        sb.append(isModifyingImage);
        sb.append(',');
        sb.append("contrast");
        sb.append('=');
        sb.append(contrast);
        sb.append(',');
        sb.append("brightness");
        sb.append('=');
        sb.append(brightness);
        sb.append(',');
        sb.append("saturation");
        sb.append('=');
        sb.append(saturation);
        sb.append(',');
        sb.append("hue");
        sb.append('=');
        sb.append(hue);
        sb.append(',');
        sb.append("mode");
        sb.append('=');
        sb.append(mode);
        sb.append(',');
        sb.append("status");
        sb.append('=');
        sb.append(status);
        sb.append(',');
        sb.append("isVisible");
        sb.append('=');
        sb.append(visible);
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        DataFlavor[] flavors = new DataFlavor[2];
        Class<?> type = this.getClass();
        String mimeType = "application/x-java-serialized-object;class=" + type.getName();
        try {
            flavors[0] = new DataFlavor(mimeType);
            flavors[1] = DataFlavor.stringFlavor;
            return flavors;
        } catch (ClassNotFoundException e) {
            return new DataFlavor[0];
        }
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return DataFlavor.stringFlavor.equals(flavor)
                || "application".equals(flavor.getPrimaryType())
                && "x-java-serialized-object".equals(flavor.getSubType())
                && flavor.getRepresentationClass().isAssignableFrom(this.getClass());
    }

    @Override
    public Object getTransferData(DataFlavor dataFlavor) throws UnsupportedFlavorException {
        if (!isDataFlavorSupported(dataFlavor)) throw new UnsupportedFlavorException(dataFlavor);
        if (DataFlavor.stringFlavor.equals(dataFlavor)) return this.toString();
        return this;
    }

    public String generateJSONString(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String readTagFromDatabase() throws SQLException, IOException {
        return this.tag.readFromDatabase();
    }

    public boolean isImageSymbol() {
        return imageSymbol;
    }

    public void setImageSymbol(boolean imageSymbol) {
        this.imageSymbol = imageSymbol;
    }

    public double getHue() {
        return hue;
    }

    public void setHue(double hue) {
        this.hue = hue;
    }

    public boolean isPreservingRatio() {
        return preservingRatio;
    }

    public void setPreservingRatio(boolean preservingRatio) {
        this.preservingRatio = preservingRatio;
    }


    public Expression getVisibilityExpression() {
        return visibilityExpression;
    }

    public void setVisibilityExpression(Expression visibilityExpression) {
        this.visibilityExpression = visibilityExpression;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }




}