package andrade.luis.hmiethernetip.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;

public class AlarmRow implements Serializable {
    public AlarmRow(String rowNumber, String datetime, String type, String maxValue, String hiHiValue ,String minValue,String loLoValue ,String value, String status, String acknowledgement) {
        this.rowNumber = new SimpleStringProperty(rowNumber);
        this.datetime = new SimpleStringProperty(datetime);
        this.type = new SimpleStringProperty(type);
        this.maxValue = new SimpleStringProperty(maxValue);
        this.hiHiValue = new SimpleStringProperty(hiHiValue);
        this.minValue = new SimpleStringProperty(minValue);
        this.loLoValue = new SimpleStringProperty(loLoValue);
        this.value = new SimpleStringProperty(value);
        this.status = new SimpleStringProperty(status);
        this.acknowledgement = new SimpleStringProperty(acknowledgement);
    }

    @SerializedName("rowNumber")
    @Expose
    private final SimpleStringProperty rowNumber;
    @SerializedName("datetime")
    @Expose
    private final SimpleStringProperty datetime;
    @SerializedName("type")
    @Expose
    private final SimpleStringProperty type;
    @SerializedName("maxValue")
    @Expose
    private final SimpleStringProperty maxValue;
    @SerializedName("hiHiValue")
    @Expose
    private final SimpleStringProperty hiHiValue;
    @SerializedName("minValue")
    @Expose
    private final SimpleStringProperty minValue;
    @SerializedName("loLoValue")
    @Expose
    private final SimpleStringProperty loLoValue;
    @SerializedName("value")
    @Expose
    private final SimpleStringProperty value;
    @SerializedName("status")
    @Expose
    private final SimpleStringProperty status;
    @SerializedName("acknowledgement")
    @Expose
    private final SimpleStringProperty acknowledgement;
}
