package andrade.luis.librehmi.models;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HMISymbolsTree {

    public static final String NULL_STR = "<null>";
    @SerializedName("ConveyorBelts")
    @Expose
    private ArrayList<String> conveyorBelts = null;
    @SerializedName("Tanks")
    @Expose
    private ArrayList<String> tanks = null;
    @SerializedName("Others")
    @Expose
    private ArrayList<String> others = null;
    @SerializedName("MotorsPumps")
    @Expose
    private ArrayList<String> motorsPumps = null;
    @SerializedName("BoilerFurnace")
    @Expose
    private ArrayList<String> boilerFurnace = null;
    @SerializedName("PipesValves")
    @Expose
    private ArrayList<String> pipesValves = null;

    public List<String> getConveyorBelts() {
        return conveyorBelts;
    }

    public void setConveyorBelts(ArrayList<String> conveyorBelts) {
        this.conveyorBelts = conveyorBelts;
    }

    public List<String> getTanks() {
        return tanks;
    }

    public void setTanks(ArrayList<String> tanks) {
        this.tanks = tanks;
    }

    public List<String> getOthers() {
        return others;
    }

    public void setOthers(ArrayList<String> others) {
        this.others = others;
    }

    public List<String> getMotorsPumps() {
        return motorsPumps;
    }

    public void setMotorsPumps(ArrayList<String> motorsPumps) {
        this.motorsPumps = motorsPumps;
    }

    public List<String> getBoilerFurnace() {
        return boilerFurnace;
    }

    public void setBoilerFurnace(ArrayList<String> boilerFurnace) {
        this.boilerFurnace = boilerFurnace;
    }

    public List<String> getPipesValves() {
        return pipesValves;
    }

    public void setPipesValves(ArrayList<String> pipesValves) {
        this.pipesValves = pipesValves;
    }

    public ArrayList<String> getCategoryFilesList(String category) {
        switch (category) {
            case "ConveyorBelts":
                return conveyorBelts;
            case "Tanks":
                return tanks;
            case "Others":
                return others;
            case "MotorsPumps":
                return motorsPumps;
            case "BoilerFurnace":
                return boilerFurnace;
            case "PipesValves":
                return pipesValves;
            default:
                return new ArrayList<>();
        }
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(HMISymbolsTree.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("conveyorBelts");
        sb.append('=');
        sb.append(((this.conveyorBelts == null)? NULL_STR :this.conveyorBelts));
        sb.append(',');
        sb.append("tanks");
        sb.append('=');
        sb.append(((this.tanks == null)? NULL_STR :this.tanks));
        sb.append(',');
        sb.append("others");
        sb.append('=');
        sb.append(((this.others == null)? NULL_STR :this.others));
        sb.append(',');
        sb.append("motorsPumps");
        sb.append('=');
        sb.append(((this.motorsPumps == null)? NULL_STR :this.motorsPumps));
        sb.append(',');
        sb.append("boilerFurnace");
        sb.append('=');
        sb.append(((this.boilerFurnace == null)? NULL_STR :this.boilerFurnace));
        sb.append(',');
        sb.append("pipesValves");
        sb.append('=');
        sb.append(((this.pipesValves == null)? NULL_STR :this.pipesValves));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }
}

