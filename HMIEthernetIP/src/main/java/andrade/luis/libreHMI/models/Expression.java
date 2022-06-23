package andrade.luis.libreHMI.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.ExpressionEvaluator;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class Expression implements Serializable {
    private static final ArrayList<String> arithmeticOperators = new ArrayList<>(Arrays.asList("+", "-", "*", "/", "%", "++", "--"));
    private static final ArrayList<String> comparisonOperators = new ArrayList<>(Arrays.asList("==", "!=", ">", "<", ">=", "<="));
    private static final ArrayList<String> logicalOperators = new ArrayList<>(Arrays.asList("&&", "||", "!"));
    private static final ArrayList<String> stringOperators = new ArrayList<>(Arrays.asList("\"", "+"));
    static final String BOOLEAN_STR = "Bool";
    static final String INT_STR = "Entero";
    static final String FLOAT_STR = "Flotante";
    static final String STRING_STR = "String";
    static final String NULL_STR = "<null>";
    @SerializedName("expressionToEvaluate")
    @Expose
    private String expressionToEvaluate;
    @SerializedName("resultType")
    @Expose
    private String resultType;
    @SerializedName("parameterNames")
    @Expose
    private String[] parameterNames;
    @SerializedName("parameterTypes")
    @Expose
    private String[] parameterTypes;
    @SerializedName("parameters")
    @Expose
    private ArrayList<Tag> parameters;
    @SerializedName("floatPrecision")
    @Expose
    private int floatPrecision=-1;

    public Expression() {

    }

    public ArrayList<Tag> getParameters() {
        return parameters;
    }

    public void setParameters(ArrayList<Tag> parameters) {
        this.parameters = parameters;
    }

    public String getExpressionToEvaluate() {
        return expressionToEvaluate;
    }

    public String getResultType() {
        return resultType;
    }

    public int getFloatPrecision() {
        return floatPrecision;
    }

    public Expression(String expressionToEvaluate, ArrayList<Tag> tags, int floatPrecision) {
        this.expressionToEvaluate = expressionToEvaluate;
        this.parameters = tags;
        this.parameterNames = new String[tags.size()];
        this.parameterTypes = new String[tags.size()];
        this.floatPrecision = floatPrecision;
        for (int i = 0; i < parameters.size(); i++) {
            this.parameterNames[i] = parameters.get(i).getName();
            this.parameterTypes[i] = parameters.get(i).getType();
        }
        this.resultType = determineResultType();
    }

    public String determineResultType() {
        StringBuilder sb = new StringBuilder(this.expressionToEvaluate);

        String localResultType = determineBoolean();
        if(localResultType.equals(BOOLEAN_STR)) return BOOLEAN_STR;

        for (String arithmeticOperator : arithmeticOperators) {
            for(String parameterType : parameterTypes){
                if(parameterType.equals(STRING_STR)) break;
            }
            if (expressionToEvaluate.contains(arithmeticOperator) && expressionToEvaluate.matches(".*\\d+.*")) {
                return FLOAT_STR;
            }
        }

        if(determineString(expressionToEvaluate).equals(STRING_STR)) return STRING_STR;

        localResultType = determineResultTypeIfOnlyOneParameter(sb);
        if(!localResultType.isEmpty()) return localResultType;

        return FLOAT_STR;

    }

    private String determineBoolean(){
        for (String comparisonOperator : comparisonOperators) {
            if (expressionToEvaluate.contains(comparisonOperator)) {
                return BOOLEAN_STR;
            }
        }

        for (String logicalOperator : logicalOperators) {
            if (expressionToEvaluate.contains(logicalOperator)) {
                return BOOLEAN_STR;
            }
        }
        return "";
    }

    private String determineResultTypeIfOnlyOneParameter(StringBuilder sb){
        if(parameters.size()==1){
            switch (parameters.get(0).getType()) {
                case BOOLEAN_STR:
                    sb.append("&& true");
                    this.expressionToEvaluate = sb.toString();
                    return BOOLEAN_STR;
                case FLOAT_STR:
                case INT_STR:
                    sb.append("*1");
                    this.expressionToEvaluate = sb.toString();
                    return FLOAT_STR;
                case STRING_STR:
                    this.expressionToEvaluate = sb.toString();
                    return STRING_STR;
                default:
                    return "Void";
            }
        }
        return "";
    }

    private String determineString(String expressionToEvaluate){
        for (String stringOperator : stringOperators) {
            if (expressionToEvaluate.contains(stringOperator)) {
                if(parameters.isEmpty() && !expressionToEvaluate.contains("+\"\"")){
                    this.expressionToEvaluate = expressionToEvaluate + "+\"\"";
                }else{
                    concatenateStringExpression();
                }
                return STRING_STR;
            }
        }

        return "";
    }

    private void concatenateStringExpression(){
        for(int i = 0; i < parameters.size(); i++) {
            if(!this.expressionToEvaluate.contains("String.valueOf("+parameterNames[i]+")")){
                this.expressionToEvaluate = this.expressionToEvaluate.replace(parameterNames[i]," String.valueOf("+parameterNames[i]+")");
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(CanvasObjectData.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("expressionToEvaluate");
        sb.append('=');
        sb.append(((this.expressionToEvaluate == null) ? NULL_STR : this.expressionToEvaluate));
        sb.append(',');
        sb.append("resultType");
        sb.append('=');
        sb.append(((this.resultType == null) ? NULL_STR : this.resultType));
        sb.append(',');
        sb.append("parametersNames");
        sb.append('=');
        sb.append(((this.parameterNames == null) ? NULL_STR : Arrays.toString(this.parameterNames)));
        sb.append(',');
        sb.append("parametersTypes");
        sb.append('=');
        sb.append(((this.parameterTypes == null) ? NULL_STR : Arrays.toString(this.parameterTypes)));
        sb.append(',');
        sb.append("parameters");
        sb.append('=');
        sb.append(((this.parameters == null) ? NULL_STR : arraysTagToString()));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    public Class[] getParameterTypesClasses() {
        Class[] parameterTypesClasses = new Class[this.parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            switch (parameterTypes[i]) {
                case INT_STR:
                case FLOAT_STR:
                    parameterTypesClasses[i] = double.class;
                    break;
                case BOOLEAN_STR:
                    parameterTypesClasses[i] = boolean.class;
                    break;
                default:
                    parameterTypesClasses[i] = void.class;
                    break;
            }
        }
        return parameterTypesClasses;
    }

    public ExpressionEvaluator prepareExpression() throws CompileException {
        ExpressionEvaluator ee = new ExpressionEvaluator();
        this.determineResultType();
        if (parameterNames.length == parameterTypes.length) {
            ee.setParameters(parameterNames, getParameterTypesClasses());
        }
        switch (this.resultType) {
            case INT_STR:
                ee.setExpressionType(int.class);
                break;
            case FLOAT_STR:
                ee.setExpressionType(double.class);
                break;
            case BOOLEAN_STR:
                ee.setExpressionType(boolean.class);
                break;
            case STRING_STR:
                ee.setExpressionType(String.class);
                break;
            default:
                ee.setExpressionType(void.class);
                break;
        }
        ee.cook(this.expressionToEvaluate);
        return ee;

    }

    public DecimalFormat generateDecimalFormat(){
        String precisionStr = "#.";
        for(int i=0;i<floatPrecision;i++){
            precisionStr = precisionStr.concat("#");
        }
        return new DecimalFormat(precisionStr);
    }

    public Object evaluate() throws CompileException, InvocationTargetException, SQLException, IOException, NullPointerException{
        ExpressionEvaluator ee = prepareExpression();
        Object[] valuesToEvaluate = new Object[parameterNames.length];
        for (int i = 0; i < parameters.size(); i++) {
            switch (parameters.get(i).getType()) {
                case INT_STR:
                case FLOAT_STR:
                    valuesToEvaluate[i] = Double.valueOf(parameters.get(i).read());
                    break;
                case BOOLEAN_STR:
                    valuesToEvaluate[i] = parameters.get(i).read().equals("1");
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        return ee.evaluate(valuesToEvaluate);
    }

    private String arraysTagToString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Tag parameter : parameters) {
            sb.append(parameter.toString());
            sb.append(",");
        }
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }
}
