package andrade.luis.librehmi.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Enum con los datos básicos para definir las orientaciones disponibles para una animación de relleno porcentual
 */
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
