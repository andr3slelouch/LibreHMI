package andrade.luis.librehmi.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Clase que contendrá los datos de un tag en cada fila de una tabla de selección de tags
 * o de administración de tag locales
 */
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


    public TagRow(String plcName, String plcAddress, String plcDeviceGroup, String tagName, String tagType, String tagAddress, String tagAction) {
        this.plcName = new SimpleStringProperty(plcName);
        this.plcAddress = new SimpleStringProperty(plcAddress);
        this.plcDeviceGroup = new SimpleStringProperty(plcDeviceGroup);
        this.tagName = new SimpleStringProperty(tagName);
        this.tagType = new SimpleStringProperty(tagType);
        this.tagAddress = new SimpleStringProperty(tagAddress);
        this.tagAction = new SimpleStringProperty(tagAction);
    }

    public Tag generateTag() throws SQLException, IOException {
        Tag tag = new Tag(getTagName(), getTagType(), getTagAddress(), getTagAction(), "", 0);
        tag.setPLCValues(getPlcName(), getPlcAddress(), getPlcDeviceGroup());
        if (tag.getPlcName().equals("LibreHMI") && tag.getPlcAddress().equals("localhost") && tag.getPlcDeviceGroup().equals("Local") && tag.getAddress().equals("Local")) {
            tag.setLocalTag(true);
        }else{
            tag.read();
        }
        return tag;
    }
}
