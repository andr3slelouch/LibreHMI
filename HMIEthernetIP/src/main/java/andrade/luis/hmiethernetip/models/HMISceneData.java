package andrade.luis.hmiethernetip.models;

import andrade.luis.hmiethernetip.models.canvas.CanvasColor;
import andrade.luis.hmiethernetip.models.canvas.CanvasObject;
import andrade.luis.hmiethernetip.models.canvas.CanvasObjectData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class HMISceneData implements Serializable, Transferable {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("sceneCommentary")
    @Expose
    private String sceneCommentary;
    @SerializedName("background")
    @Expose
    private CanvasColor background;
    @SerializedName("shapeArrayList")
    @Expose
    private ArrayList<CanvasObjectData> shapeArrayList;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSceneCommentary() {
        return sceneCommentary;
    }

    public void setSceneCommentary(String sceneCommentary) {
        this.sceneCommentary = sceneCommentary;
    }

    public CanvasColor getBackground() {
        return background;
    }

    public void setBackground(CanvasColor background) {
        this.background = background;
    }

    public ArrayList<CanvasObjectData> getShapeArrayList() {
        return shapeArrayList;
    }

    public void setShapeArrayList(ArrayList<CanvasObjectData> shapeArrayList) {
        this.shapeArrayList = shapeArrayList;
    }
}
