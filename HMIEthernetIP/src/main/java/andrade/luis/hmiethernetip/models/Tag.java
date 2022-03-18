package andrade.luis.hmiethernetip.models;

import andrade.luis.hmiethernetip.models.canvas.CanvasObjectData;
import andrade.luis.hmiethernetip.util.DBConnection;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.Serializable;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Tag implements Serializable {
    private static final Map<String, String> selectQueries = Map.of("Entero", "select valor from entero where nombreTag=", "Flotante", "select valor from flotante where nombreTag=", "Bool", "select valor from boolean where nombreTag=");
    private static final Map<String, String> updateQueries = Map.of("Entero", "update entero SET valor=? where nombreTag=", "Flotante", "update flotante SET valor=? where nombreTag=", "Bool", "update boolean SET valor=? where nombreTag=");

    public Tag() {

    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getFloatPrecision() {
        return floatPrecision;
    }

    public void setFloatPrecision(int floatPrecision) {
        this.floatPrecision = floatPrecision;
    }

    public String readFromDatabase() throws SQLException, IOException {
        try (Connection con = DBConnection.createConnectionToBDDriverEIP()) {
            try(Statement statement = con.createStatement()){
                if (this.getType() != null && this.getName() != null) {
                    String query = selectQueries.get(this.getType()) + "'" + this.getName() + "'";
                    ResultSet resultSet = statement.executeQuery(query);
                    while (resultSet.next()) {
                        if (!resultSet.getString("valor").isEmpty()) {
                            String result = resultSet.getString("valor");
                            this.value = result;
                            return result;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        } catch (IOException e) {
            throw new IOException(e);
        }
        return null;
    }

    public boolean updateInDatabase() throws SQLException, IOException {
        try(Connection con = DBConnection.createConnectionToBDDriverEIP()){
            if (this.getType() != null && this.getName() != null) {
                String query = updateQueries.get(this.getType()) + "'" + this.getName() + "'";
                try(PreparedStatement preparedStatement = con.prepareStatement(query)){
                    String updateValue;
                    switch (this.getType()) {
                        case "Entero":
                        case "Bool":
                            updateValue = String.valueOf((int) Double.parseDouble(this.value.isEmpty() ? "0" : this.value));
                            break;
                        case "Flotante":
                            updateValue = String.valueOf(Double.parseDouble(this.value));
                            break;
                        default:
                            updateValue = this.value;
                    }
                    preparedStatement.setString(1, updateValue);
                    int insertRowResult = preparedStatement.executeUpdate();
                    return insertRowResult > 0;
                }

            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        } catch (IOException e) {
            throw new IOException(e);
        }

    }

    public DecimalFormat generateDecimalFormat() {
        String precisionStr = "#.";
        for (int i = 0; i < floatPrecision; i++) {
            precisionStr = precisionStr.concat("#");
        }
        return new DecimalFormat(precisionStr);
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
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("floatPrecision")
    @Expose
    private int floatPrecision = -1;

    public Tag(String plcName, String plcAddress, String plcDeviceGroup, String name, String type, String address, String action, String value, int floatPrecision) {
        this.plcName = plcName;
        this.plcAddress = plcAddress;
        this.plcDeviceGroup = plcDeviceGroup;
        this.name = name;
        this.type = type;
        this.address = address;
        this.action = action;
        this.value = value;
        this.floatPrecision = floatPrecision;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(CanvasObjectData.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("plcName");
        sb.append('=');
        sb.append(((this.plcName == null) ? "<null>" : this.plcName));
        sb.append(',');
        sb.append("plcAddress");
        sb.append('=');
        sb.append(((this.plcAddress == null) ? "<null>" : this.plcAddress));
        sb.append(',');
        sb.append("plcDeviceGroup");
        sb.append('=');
        sb.append(((this.plcDeviceGroup == null) ? "<null>" : this.plcDeviceGroup));
        sb.append(',');
        sb.append("tagName");
        sb.append('=');
        sb.append(((this.name == null) ? "<null>" : this.name));
        sb.append(',');
        sb.append("tagType");
        sb.append('=');
        sb.append(((this.type == null) ? "<null>" : this.type));
        sb.append(',');
        sb.append("tagAddress");
        sb.append('=');
        sb.append(((this.address == null) ? "<null>" : this.address));
        sb.append(',');
        sb.append("tagAction");
        sb.append('=');
        sb.append(((this.action == null) ? "<null>" : this.action));
        sb.append(',');
        sb.append("tagValue");
        sb.append('=');
        sb.append(((this.value == null) ? "<null>" : this.value));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }
}
