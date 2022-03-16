package andrade.luis.hmiethernetip.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;

public class AlarmRow implements Serializable {
    public AlarmRow(String rowNumber, String name ,String expression ,String datetime, String type, String maxValue, String hiHiValue , String minValue, String loLoValue , String value, String status, String acknowledgement) {
        this.rowNumber = new SimpleStringProperty(rowNumber);
        this.name = new SimpleStringProperty(name);
        this.expression = new SimpleStringProperty(expression);
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
    @SerializedName("name")
    @Expose
    private final SimpleStringProperty name;
    @SerializedName("expression")
    @Expose
    private final SimpleStringProperty expression;
    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getRowNumber() {
        return rowNumber.get();
    }

    public SimpleStringProperty rowNumberProperty() {
        return rowNumber;
    }

    public void setRowNumber(String rowNumber) {
        this.rowNumber.set(rowNumber);
    }

    public String getDatetime() {
        return datetime.get();
    }

    public SimpleStringProperty datetimeProperty() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime.set(datetime);
    }

    public String getType() {
        return type.get();
    }

    public SimpleStringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public String getMaxValue() {
        return maxValue.get();
    }

    public SimpleStringProperty maxValueProperty() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue.set(maxValue);
    }

    public String getHiHiValue() {
        return hiHiValue.get();
    }

    public SimpleStringProperty hiHiValueProperty() {
        return hiHiValue;
    }

    public void setHiHiValue(String hiHiValue) {
        this.hiHiValue.set(hiHiValue);
    }

    public String getMinValue() {
        return minValue.get();
    }

    public SimpleStringProperty minValueProperty() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue.set(minValue);
    }

    public String getLoLoValue() {
        return loLoValue.get();
    }

    public SimpleStringProperty loLoValueProperty() {
        return loLoValue;
    }

    public void setLoLoValue(String loLoValue) {
        this.loLoValue.set(loLoValue);
    }

    public String getValue() {
        return value.get();
    }

    public SimpleStringProperty valueProperty() {
        return value;
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public String getStatus() {
        return status.get();
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public String getAcknowledgement() {
        return acknowledgement.get();
    }

    public SimpleStringProperty acknowledgementProperty() {
        return acknowledgement;
    }

    public void setAcknowledgement(String acknowledgement) {
        this.acknowledgement.set(acknowledgement);
    }

    @SerializedName("acknowledgement")
    @Expose
    private final SimpleStringProperty acknowledgement;

    public String getExpression() {
        return expression.get();
    }

    public SimpleStringProperty expressionProperty() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression.set(expression);
    }
}
