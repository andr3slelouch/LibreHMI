package andrade.luis.hmiethernetip.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public enum CanvasOrientation implements Serializable {
    @SerializedName("HORIZONTAL")
    @Expose
    HORIZONTAL,
    @SerializedName("HORIZONTAL_REVERSED")
    @Expose
    HORIZONTAL_REVERSED,
    @SerializedName("VERTICAL")
    @Expose
    VERTICAL,
    @SerializedName("VERTICAL_REVERSED")
    @Expose
    VERTICAL_REVERSED;

    CanvasOrientation(){

    }
}
