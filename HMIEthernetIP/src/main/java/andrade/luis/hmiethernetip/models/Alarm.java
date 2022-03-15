package andrade.luis.hmiethernetip.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Alarm implements Serializable {
    private Expression expression;
    private Double highLimit=0.0;
    private Double hiHiLimit=0.0;
    private Double lowLimit=0.0;
    private Double loloLimit=0.0;
    private boolean highAlarmEnabled=false;
    private boolean hiHiAlarmEnabled=false;
    private boolean lowAlarmEnabled=false;
    private boolean loloAlarmEnabled=false;
    private boolean condition;
    private String status;
    private String acknowledgement;
    private String comment;
    private LocalDateTime alarmExecutionDateTime;

    public Alarm(Expression expression, Double highLimit, Double hiHiLimit, Double lowLimit, Double loloLimit, String status, String acknowledgement) {
        this.expression = expression;
        this.highLimit = highLimit;
        this.hiHiLimit = hiHiLimit;
        this.lowLimit = lowLimit;
        this.loloLimit = loloLimit;
        this.status = status;
        this.acknowledgement = acknowledgement;
    }

    public Alarm(Expression expression, boolean condition, String status, String acknowledgement) {
        this.expression = expression;
        this.condition = condition;
        this.status = status;
        this.acknowledgement = acknowledgement;
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

    public LocalDateTime getAlarmExecutionDateTime() {
        return alarmExecutionDateTime;
    }

    public void setAlarmExecutionDateTime(LocalDateTime alarmExecutionDateTime) {
        this.alarmExecutionDateTime = alarmExecutionDateTime;
    }

    public void setAlarmExecutionDateTime() {
        this.alarmExecutionDateTime = LocalDateTime.now();
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
}
