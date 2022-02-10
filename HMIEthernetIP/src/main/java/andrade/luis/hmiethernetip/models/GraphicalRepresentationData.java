package andrade.luis.hmiethernetip.models;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import andrade.luis.hmiethernetip.util.DBConnection;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import javafx.geometry.Orientation;

public class GraphicalRepresentationData implements Serializable, Transferable
{

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
    private String operation;
    @SerializedName("data")
    @Expose
    private String data;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("tag")
    @Expose
    private Tag tag;
    @SerializedName("refillExpression")
    @Expose
    private Expression refillExpression;
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


    private static final long serialVersionUID = 6976931227659398285L;

    private final Map<String, String> queries = Map.of("Entero", "select valor from ENTERO where nombreTag=", "Flotante", "select valor from FLOTANTE where nombreTag=", "Bool", "select valor from BOOLEAN where nombreTag=");

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
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public Expression getRefillExpression() {
        return refillExpression;
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

    public void setRefillExpression(Expression refillExpression) {
        this.refillExpression = refillExpression;
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
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(GraphicalRepresentationData.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
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
        sb.append(((this.center == null)?"<null>":this.center));
        sb.append(',');
        sb.append("position");
        sb.append('=');
        sb.append(((this.position == null)?"<null>":this.position));
        sb.append(',');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("operation");
        sb.append('=');
        sb.append(((this.operation == null)?"<null>":this.operation));
        sb.append(',');
        sb.append("data");
        sb.append('=');
        sb.append(((this.data == null)?"<null>":this.data));
        sb.append(',');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null)?"<null>":this.name));
        sb.append(',');
        sb.append("tag");
        sb.append('=');
        sb.append(((this.tag == null)?"<null>":this.tag.toString()));
        sb.append(',');
        sb.append("refillExpression");
        sb.append('=');
        sb.append(((this.refillExpression == null)?"<null>":this.refillExpression.toString()));
        sb.append(',');
        sb.append("primaryColor");
        sb.append('=');
        sb.append(((this.primaryColor == null)?"<null>":this.primaryColor.toString()));
        sb.append(',');
        sb.append("backgroundColor");
        sb.append('=');
        sb.append(((this.backgroundColor == null)?"<null>":this.backgroundColor.toString()));
        sb.append(',');
        sb.append("orientation");
        sb.append('=');
        sb.append(((this.orientation == null)?"<null>":this.orientation.toString()));
        sb.append(',');
        sb.append("minValue");
        sb.append('=');
        sb.append(minValue);
        sb.append(',');
        sb.append("maxValue");
        sb.append('=');
        sb.append(maxValue);
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
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
    public Object getTransferData(DataFlavor dataFlavor) throws UnsupportedFlavorException, IOException {
        if(!isDataFlavorSupported(dataFlavor)) throw  new UnsupportedFlavorException(dataFlavor);
        if(DataFlavor.stringFlavor.equals(dataFlavor)) return this.toString();
        return this;
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

    public String readTagFromDatabase(){
        return DBConnection.readTagValueFromDatabase(this.tag);
    }
}