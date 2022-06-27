package andrade.luis.librehmi.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Clase que contendrá los datos de una celda de la lista de archivos recientes
 */
public class RecentUsedFilesData implements Serializable {
    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public ArrayList<String> getRecentlyUsedFilePaths() {
        return recentlyUsedFilePaths;
    }

    public void setRecentlyUsedFilePaths(ArrayList<String> recentlyUsedFilePaths) {
        this.recentlyUsedFilePaths = recentlyUsedFilePaths;
    }

    @SerializedName("capacity")
    @Expose
    int capacity;
    @SerializedName("recentlyUsedFilePaths")
    @Expose
    ArrayList<String> recentlyUsedFilePaths;

    @Override
    public String toString() {
        return "RecentUsedFilesData{" +
                "capacity=" + capacity +
                ", recentlyUsedFilePaths=" + recentlyUsedFilePaths +
                '}';
    }
}
