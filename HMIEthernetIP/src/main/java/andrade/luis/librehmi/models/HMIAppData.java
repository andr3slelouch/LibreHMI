package andrade.luis.librehmi.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import java.io.Serializable;
import java.util.ArrayList;

public class HMIAppData implements Serializable, Transferable {

    public ArrayList<HMISceneData> getHmiAppPages() {
        return hmiAppPages;
    }

    public void setHmiAppPages(ArrayList<HMISceneData> hmiAppPages) {
        this.hmiAppPages = hmiAppPages;
    }

    public ArrayList<Alarm> getHmiAlarms() {
        return hmiAlarms;
    }

    public void setHmiAlarms(ArrayList<Alarm> hmiAlarms) {
        this.hmiAlarms = hmiAlarms;
    }

    @SerializedName("hmiAppPages")
    @Expose
    private ArrayList<HMISceneData> hmiAppPages;

    @SerializedName("hmiAlarms")
    @Expose
    private ArrayList<Alarm> hmiAlarms;
    @SerializedName("hmiLocalTags")
    @Expose
    private ArrayList<Tag> hmiLocalTags;

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("blockingTimeout")
    @Expose
    private int blockingTimeout;

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[0];
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor dataFlavor) {
        return false;
    }

    @Override
    public Object getTransferData(DataFlavor dataFlavor) {
        return null;
    }

    public int getBlockingTimeout() {
        return blockingTimeout;
    }

    public void setBlockingTimeout(int blockingTimeout) {
        this.blockingTimeout = blockingTimeout;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public ArrayList<Tag> getHmiLocalTags() {
        return hmiLocalTags;
    }

    public void setHmiLocalTags(ArrayList<Tag> hmiLocalTags) {
        this.hmiLocalTags = hmiLocalTags;
    }
}
