package andrade.luis.librehmi.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;

public class TagRow {

    public static final String NULL_STR = "<null>";

    public String getPlcName() {
        return plcName.get();
    }

    public SimpleStringProperty plcNameProperty() {
        return plcName;
    }

    public void setPlcName(String plcName) {
        this.plcName.set(plcName);
    }

    public String getPlcAddress() {
        return plcAddress.get();
    }

    public SimpleStringProperty plcAddressProperty() {
        return plcAddress;
    }

    public void setPlcAddress(String plcAddress) {
        this.plcAddress.set(plcAddress);
    }

    public String getPlcDeviceGroup() {
        return plcDeviceGroup.get();
    }

    public SimpleStringProperty plcDeviceGroupProperty() {
        return plcDeviceGroup;
    }

    public void setPlcDeviceGroup(String plcDeviceGroup) {
        this.plcDeviceGroup.set(plcDeviceGroup);
    }

    public String getTagName() {
        return tagName.get();
    }

    public SimpleStringProperty tagNameProperty() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName.set(tagName);
    }

    public String getTagType() {
        return tagType.get();
    }

    public SimpleStringProperty tagTypeProperty() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType.set(tagType);
    }

    public String getTagAddress() {
        return tagAddress.get();
    }

    public SimpleStringProperty tagAddressProperty() {
        return tagAddress;
    }

    public void setTagAddress(String tagAddress) {
        this.tagAddress.set(tagAddress);
    }

    public String getTagAction() {
        return tagAction.get();
    }

    public SimpleStringProperty tagActionProperty() {
        return tagAction;
    }

    public void setTagAction(String tagAction) {
        this.tagAction.set(tagAction);
    }

    public String getTagValue() {
        return tagValue.get();
    }

    public SimpleStringProperty tagValueProperty() {
        return tagValue;
    }

    public void setTagValue(String tagValue) {
        this.tagValue.set(tagValue);
    }

    @SerializedName("plcName")
    @Expose
    private final SimpleStringProperty plcName;
    @SerializedName("plcAddress")
    @Expose
    private final SimpleStringProperty plcAddress;
    @SerializedName("plcDeviceGroup")
    @Expose
    private final SimpleStringProperty plcDeviceGroup;
    @SerializedName("tagName")
    @Expose
    private final SimpleStringProperty tagName;
    @SerializedName("tagType")
    @Expose
    private final SimpleStringProperty tagType;
    @SerializedName("tagAddress")
    @Expose
    private final SimpleStringProperty tagAddress;
    @SerializedName("tagAction")
    @Expose
    private final SimpleStringProperty tagAction;
    @SerializedName("tagValue")
    @Expose
    private SimpleStringProperty tagValue;

    public TagRow(String plcName, String plcAddress, String plcDeviceGroup, String tagName, String tagType, String tagAddress, String tagAction) {
        this.plcName = new SimpleStringProperty(plcName);
        this.plcAddress = new SimpleStringProperty(plcAddress);
        this.plcDeviceGroup = new SimpleStringProperty(plcDeviceGroup);
        this.tagName = new SimpleStringProperty(tagName);
        this.tagType = new SimpleStringProperty(tagType);
        this.tagAddress = new SimpleStringProperty(tagAddress);
        this.tagAction = new SimpleStringProperty(tagAction);
    }

    public Tag generateTag(){
        Tag tag = new Tag(getPlcName(),getPlcAddress(),getPlcDeviceGroup(),getTagName(),getTagType(),getTagAddress(),getTagAction(),getTagValue(),0);
        if(tag.getPlcName().equals("LibreHMI") && tag.getPlcAddress().equals("localhost")&& tag.getPlcDeviceGroup().equals("Local") && tag.getAddress().equals("Local")){
            tag.setLocalTag(true);
        }
        return tag;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(CanvasObjectData.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("plcName");
        sb.append('=');
        sb.append(((this.plcName == null)? NULL_STR :this.plcName));
        sb.append(',');
        sb.append("plcAddress");
        sb.append('=');
        sb.append(((this.plcAddress == null)? NULL_STR :this.plcAddress));
        sb.append(',');
        sb.append("plcDeviceGroup");
        sb.append('=');
        sb.append(((this.plcDeviceGroup == null)? NULL_STR :this.plcDeviceGroup));
        sb.append(',');
        sb.append("tagName");
        sb.append('=');
        sb.append(((this.tagName == null)? NULL_STR :this.tagName));
        sb.append(',');
        sb.append("tagType");
        sb.append('=');
        sb.append(((this.tagType == null)? NULL_STR :this.tagType));
        sb.append(',');
        sb.append("tagAddress");
        sb.append('=');
        sb.append(((this.tagAddress == null)? NULL_STR :this.tagAddress));
        sb.append(',');
        sb.append("tagAction");
        sb.append('=');
        sb.append(((this.tagAction == null)? NULL_STR :this.tagAction));
        sb.append(',');
        sb.append("tagValue");
        sb.append('=');
        sb.append(((this.tagValue == null)? NULL_STR :this.tagValue));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }
}
