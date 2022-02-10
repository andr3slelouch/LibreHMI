package andrade.luis.hmiethernetip.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Tag implements Serializable {
    public String getPlcName() {
        return plcName;
    }

    public void setPlcName(String plcName) {
        this.plcName = plcName;
    }

    public String getPlcAddress() {
        return plcAddress;
    }

    public void setPlcAddress(String plcAddress) {
        this.plcAddress = plcAddress;
    }

    public String getPlcDeviceGroup() {
        return plcDeviceGroup;
    }

    public void setPlcDeviceGroup(String plcDeviceGroup) {
        this.plcDeviceGroup = plcDeviceGroup;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }

    public String getTagAddress() {
        return tagAddress;
    }

    public void setTagAddress(String tagAddress) {
        this.tagAddress = tagAddress;
    }

    public String getTagAction() {
        return tagAction;
    }

    public void setTagAction(String tagAction) {
        this.tagAction = tagAction;
    }

    public String getTagValue() {
        return tagValue;
    }

    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }

    @SerializedName("plcName")
    @Expose
    private String plcName;
    @SerializedName("plcAddress")
    @Expose
    private String plcAddress;
    @SerializedName("plcDeviceGroup")
    @Expose
    private String plcDeviceGroup;
    @SerializedName("tagName")
    @Expose
    private String tagName;
    @SerializedName("tagType")
    @Expose
    private String tagType;
    @SerializedName("tagAddress")
    @Expose
    private String tagAddress;
    @SerializedName("tagAction")
    @Expose
    private String tagAction;
    @SerializedName("tagValue")
    @Expose
    private String tagValue;

    public Tag(String plcName, String plcAddress, String plcDeviceGroup, String tagName, String tagType, String tagAddress, String tagAction, String tagValue) {
        this.plcName = plcName;
        this.plcAddress = plcAddress;
        this.plcDeviceGroup = plcDeviceGroup;
        this.tagName = tagName;
        this.tagType = tagType;
        this.tagAddress = tagAddress;
        this.tagAction = tagAction;
        this.tagValue = tagValue;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(GraphicalRepresentationData.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("plcName");
        sb.append('=');
        sb.append(((this.plcName == null)?"<null>":this.plcName));
        sb.append(',');
        sb.append("plcAddress");
        sb.append('=');
        sb.append(((this.plcAddress == null)?"<null>":this.plcAddress));
        sb.append(',');
        sb.append("plcDeviceGroup");
        sb.append('=');
        sb.append(((this.plcDeviceGroup == null)?"<null>":this.plcDeviceGroup));
        sb.append(',');
        sb.append("tagName");
        sb.append('=');
        sb.append(((this.tagName == null)?"<null>":this.tagName));
        sb.append(',');
        sb.append("tagType");
        sb.append('=');
        sb.append(((this.tagType == null)?"<null>":this.tagType));
        sb.append(',');
        sb.append("tagAddress");
        sb.append('=');
        sb.append(((this.tagAddress == null)?"<null>":this.tagAddress));
        sb.append(',');
        sb.append("tagAction");
        sb.append('=');
        sb.append(((this.tagAction == null)?"<null>":this.tagAction));
        sb.append(',');
        sb.append("tagValue");
        sb.append('=');
        sb.append(((this.tagValue == null)?"<null>":this.tagValue));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }
}