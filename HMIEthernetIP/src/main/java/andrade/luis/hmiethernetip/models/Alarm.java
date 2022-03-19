package andrade.luis.hmiethernetip.models;

import org.codehaus.commons.compiler.CompileException;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Alarm implements Serializable {
    private static final String ACTIVATED_STR = "Activada";
    private static final String DEACTIVATED_STR = "Desactivada";
    private static final String UNACKNOWLEDGED_STATUS_STR = "No Reconocida";
    private Expression expression;
    private Double highLimit=null;
    private Double hiHiLimit=null;
    private Double lowLimit=null;
    private Double loloLimit=null;
    private boolean highAlarmEnabled=false;
    private boolean hiHiAlarmEnabled=false;
    private boolean lowAlarmEnabled=false;
    private boolean loloAlarmEnabled=false;
    private boolean conditionEnabled=false;
    private boolean condition;
    private String status=DEACTIVATED_STR;
    private String acknowledgement="";
    private String comment="";
    private String type="";
    private String name="";
    private String value="";
    private String alarmExecutionDateTime;

    public Alarm(
            Expression expression,
            double highLimit,
            double hiHiLimit,
            double lowLimit,
            double loloLimit,
            boolean highAlarmEnabled,
            boolean hiHiAlarmEnabled,
            boolean lowAlarmEnabled,
            boolean loloAlarmEnabled,
            String name,
            String comment) {
        this.expression = expression;
        this.highLimit = highLimit;
        this.hiHiLimit = hiHiLimit;
        this.lowLimit = lowLimit;
        this.loloLimit = loloLimit;
        this.loloAlarmEnabled = loloAlarmEnabled;
        this.lowAlarmEnabled = lowAlarmEnabled;
        this.hiHiAlarmEnabled = hiHiAlarmEnabled;
        this.highAlarmEnabled = highAlarmEnabled;
        this.name = name;
        this.comment = comment;
    }

    public Alarm(Expression expression, boolean condition, boolean conditionEnabled,String name, String comment) {
        this.expression = expression;
        this.condition = condition;
        this.conditionEnabled = conditionEnabled;
        this.name = name;
        this.comment = comment;
    }

    public boolean checkAlarm() throws SQLException, CompileException, IOException, InvocationTargetException {
        if(conditionEnabled){
            boolean localValue = (boolean) this.expression.evaluate();
            this.value = String.valueOf(localValue);
            if(localValue == condition){
                if(this.status.equals(DEACTIVATED_STR)){
                    this.acknowledgement = UNACKNOWLEDGED_STATUS_STR;
                }
                this.acknowledgement = this.acknowledgement==null ? UNACKNOWLEDGED_STATUS_STR :this.acknowledgement;
                this.type="Condición: "+ this.condition;
                this.status=ACTIVATED_STR;
                return true;
            }else{
                this.status=DEACTIVATED_STR;
                return false;
            }
        }else{
            if(hiHiAlarmEnabled){
                double localValue = (double) this.expression.evaluate();
                this.value = String.valueOf(localValue);
                if(localValue > hiHiLimit){
                    if(this.status.equals(DEACTIVATED_STR)){
                        this.acknowledgement = UNACKNOWLEDGED_STATUS_STR;
                    }
                    this.acknowledgement = this.acknowledgement==null ? UNACKNOWLEDGED_STATUS_STR :this.acknowledgement;
                    this.type="Condición: Alto Alto";
                    this.status=ACTIVATED_STR;
                    return true;
                }
            }
            if(highAlarmEnabled){
                double localValue = (double) this.expression.evaluate();
                this.value = String.valueOf(localValue);
                if(localValue> highLimit){
                    if(this.status.equals(DEACTIVATED_STR)){
                        this.acknowledgement = UNACKNOWLEDGED_STATUS_STR;
                    }
                    this.acknowledgement = this.acknowledgement==null ? UNACKNOWLEDGED_STATUS_STR :this.acknowledgement;
                    this.type="Condición: Alto";
                    this.status=ACTIVATED_STR;
                    return true;
                }
            }
            if(loloAlarmEnabled){
                double localValue = (double) this.expression.evaluate();
                this.value = String.valueOf(localValue);
                if(localValue < loloLimit){
                    if(this.status.equals(DEACTIVATED_STR)){
                        this.acknowledgement = UNACKNOWLEDGED_STATUS_STR;
                    }
                    this.acknowledgement = this.acknowledgement==null ? UNACKNOWLEDGED_STATUS_STR :this.acknowledgement;
                    this.type="Condición: Bajo Bajo";
                    this.status=ACTIVATED_STR;
                    return true;
                }
            }
            if(lowAlarmEnabled){
                double localValue = (double) this.expression.evaluate();
                this.value = String.valueOf(localValue);
                if(localValue < lowLimit){
                    if(this.status.equals(DEACTIVATED_STR)){
                        this.acknowledgement = UNACKNOWLEDGED_STATUS_STR;
                    }
                    this.acknowledgement = this.acknowledgement==null ? UNACKNOWLEDGED_STATUS_STR :this.acknowledgement;
                    this.type="Condición: Bajo";
                    this.status=ACTIVATED_STR;
                    return true;
                }
            }
            this.status=DEACTIVATED_STR;
            return false;
        }
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public Double getHighLimit() {
        return highLimit;
    }

    public void setHighLimit(Double highLimit) {
        this.highLimit = highLimit;
    }

    public Double getHiHiLimit() {
        return hiHiLimit;
    }

    public void setHiHiLimit(Double hiHiLimit) {
        this.hiHiLimit = hiHiLimit;
    }

    public Double getLowLimit() {
        return lowLimit;
    }

    public void setLowLimit(Double lowLimit) {
        this.lowLimit = lowLimit;
    }

    public Double getLoloLimit() {
        return loloLimit;
    }

    public void setLoloLimit(Double loloLimit) {
        this.loloLimit = loloLimit;
    }

    public boolean isCondition() {
        return condition;
    }

    public void setCondition(boolean condition) {
        this.condition = condition;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAcknowledgement() {
        return acknowledgement;
    }

    public void setAcknowledgement(String acknowledgement) {
        this.acknowledgement = acknowledgement;
    }

    public String getAlarmExecutionDateTime() {
        return alarmExecutionDateTime;
    }

    public void setAlarmExecutionDateTime(String alarmExecutionDateTime) {
        this.alarmExecutionDateTime = alarmExecutionDateTime;
    }

    public void setAlarmExecutionDateTime() {
        this.alarmExecutionDateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now());
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isHighAlarmEnabled() {
        return highAlarmEnabled;
    }

    public void setHighAlarmEnabled(boolean highAlarmEnabled) {
        this.highAlarmEnabled = highAlarmEnabled;
    }

    public boolean isHiHiAlarmEnabled() {
        return hiHiAlarmEnabled;
    }

    public void setHiHiAlarmEnabled(boolean hiHiAlarmEnabled) {
        this.hiHiAlarmEnabled = hiHiAlarmEnabled;
    }

    public boolean isLowAlarmEnabled() {
        return lowAlarmEnabled;
    }

    public void setLowAlarmEnabled(boolean lowAlarmEnabled) {
        this.lowAlarmEnabled = lowAlarmEnabled;
    }

    public boolean isLoloAlarmEnabled() {
        return loloAlarmEnabled;
    }

    public void setLoloAlarmEnabled(boolean loloAlarmEnabled) {
        this.loloAlarmEnabled = loloAlarmEnabled;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "expression=" + expression +
                ", highLimit=" + highLimit +
                ", hiHiLimit=" + hiHiLimit +
                ", lowLimit=" + lowLimit +
                ", loloLimit=" + loloLimit +
                ", highAlarmEnabled=" + highAlarmEnabled +
                ", hiHiAlarmEnabled=" + hiHiAlarmEnabled +
                ", lowAlarmEnabled=" + lowAlarmEnabled +
                ", loloAlarmEnabled=" + loloAlarmEnabled +
                ", conditionEnabled=" + conditionEnabled +
                ", condition=" + condition +
                ", status='" + status + '\'' +
                ", acknowledgement='" + acknowledgement + '\'' +
                ", comment='" + comment + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", alarmExecutionDateTime='" + alarmExecutionDateTime + '\'' +
                '}';
    }
}
