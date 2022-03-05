package andrade.luis.hmiethernetip.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class HMIAppData implements Serializable, Transferable {

    public ArrayList<HMISceneData> getHmiAppPages() {
        return hmiAppPages;
    }

    public void setHmiAppPages(ArrayList<HMISceneData> hmiAppPages) {
        this.hmiAppPages = hmiAppPages;
    }

    @SerializedName("hmiAppPages")
    @Expose
    private ArrayList<HMISceneData> hmiAppPages;

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[0];
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor dataFlavor) {
        return false;
    }

    @Override
    public Object getTransferData(DataFlavor dataFlavor) throws UnsupportedFlavorException, IOException {
        return null;
    }
}
