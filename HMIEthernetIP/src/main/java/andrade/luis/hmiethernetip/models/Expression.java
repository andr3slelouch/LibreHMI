package andrade.luis.hmiethernetip.models;

import andrade.luis.hmiethernetip.util.DBConnection;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.ExpressionEvaluator;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

public class Expression implements Serializable {
    private static final ArrayList<String> arithmeticOperators = new ArrayList<>(Arrays.asList("+", "-", "*", "/", "%", "++", "--"));
    private static final ArrayList<String> comparisonOperators = new ArrayList<>(Arrays.asList("==", "!=", ">", "<", ">=", "<="));
    private static final ArrayList<String> logicalOperators = new ArrayList<>(Arrays.asList("&&", "||", "!"));
    private static final ArrayList<String> stringOperators = new ArrayList<>(Arrays.asList("\"", "+"));
    static final String BOOLEAN_STR = "Booleano";
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

    public ArrayList<Tag> getParameters() {
        return parameters;
    }

    public void setParameters(ArrayList<Tag> parameters) {
        this.parameters = parameters;
    }

    public String getExpressionToEvaluate() {
        return expressionToEvaluate;
    }

    public void setExpressionToEvaluate(String expressionToEvaluate) {
        this.expressionToEvaluate = expressionToEvaluate;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String[] getParameterNames() {
        return parameterNames;
    }

    public void setParameterNames(String[] parameterNames) {
        this.parameterNames = parameterNames;
    }

    public String[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(String[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Expression(String expressionToEvaluate, String resultType, String[] parameterNames, String[] parameterTypes) {
        this.expressionToEvaluate = expressionToEvaluate;
        this.resultType = resultType;
        this.parameterNames = parameterNames;
        this.parameterTypes = parameterTypes;
    }

    public Expression(String expressionToEvaluate, ArrayList<Tag> tags) {
        this.expressionToEvaluate = expressionToEvaluate;
        this.parameters = tags;
        this.parameterNames = new String[tags.size()];
        this.parameterTypes = new String[tags.size()];
        for (int i = 0; i < parameters.size(); i++) {
            this.parameterNames[i] = parameters.get(i).getTagName();
            this.parameterTypes[i] = parameters.get(i).getTagType();
        }
        this.resultType = determineResultType();
    }

    public String determineResultType() {
        StringBuilder sb = new StringBuilder(this.expressionToEvaluate);
        String res = null;

        for (String stringOperator : stringOperators) {
            if (expressionToEvaluate.contains(stringOperator)) {
                if(parameters.isEmpty()){
                    this.expressionToEvaluate = expressionToEvaluate + "+\"\"";
                }else{
                    for(int i = 0; i < parameters.size(); i++) {
                        if(!this.expressionToEvaluate.contains("String.valueOf("+parameterNames[i]+")")){
                            this.expressionToEvaluate = this.expressionToEvaluate.replace(parameterNames[i],"String.valueOf("+parameterNames[i]+")");
                        }
                    }
                }
                return STRING_STR;
            }
        }

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

        for (String arithmeticOperator : arithmeticOperators) {
            if (expressionToEvaluate.contains(arithmeticOperator)) {
                return FLOAT_STR;
            }
        }

        /*if(parameters.size()==1){
            if(parameters.get(0).getTagType().equals(BOOLEAN_STR)){
                sb.append("&& true");
            }else if(parameters.get(0).getTagType().equals(FLOAT_STR)){
                sb.append("*1");
            }else if(parameters.get(0).getTagType().equals(STRING_STR)){
                sb.append("+\"\"");
            }
            this.expressionToEvaluate = sb.toString();
        }*/

        return FLOAT_STR;

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(GraphicalRepresentationData.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
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

    public Object evaluate() throws CompileException, InvocationTargetException {
        ExpressionEvaluator ee = prepareExpression();
        Object[] valuesToEvaluate = new Object[parameterNames.length];
        for (int i = 0; i < parameters.size(); i++) {
            switch (parameters.get(i).getTagType()) {
                case INT_STR:
                case FLOAT_STR:
                    valuesToEvaluate[i] = Double.valueOf(DBConnection.readTagValueFromDatabase(parameters.get(i)));
                    break;
                case BOOLEAN_STR:
                    valuesToEvaluate[i] = Boolean.valueOf(DBConnection.readTagValueFromDatabase(parameters.get(i)));
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
