package andrade.luis.librehmi.models;

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

/**
 * Clase que se encarga de permitir el dinamismo de las los tags al permitir su manipulación en tiempo real
 */
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

    /**
     * Constructor de la clase permite definir los parámetros requeridos para utilizar una expresión de la librería
     * @param expressionToEvaluate Expresión a ser evaluada
     * @param tags Tags de la base de datos o locales que pertenecen a la expresión
     * @param floatPrecision Precisión de decimales de los tags
     */
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

    /**
     * Permite definir el tipo de resultado con base en la expresión definida y sus tags asociados
     * @return String del tipo de dato determinado puede ser:FLOTANTE,STRING,BOOL
     */
    public String determineResultType() {
        StringBuilder sb = new StringBuilder(this.expressionToEvaluate);

        String localResultType = determineBoolean();
        if(localResultType.equals(BOOLEAN_STR)) return BOOLEAN_STR;

        for (String arithmeticOperator : arithmeticOperators) {
            for(String parameterType : parameterTypes){
                if(parameterType.equals(STRING_STR)) break;
            }
            if (expressionToEvaluate.contains(arithmeticOperator) && expressionToEvaluate.matches("\\d")) {
                return FLOAT_STR;
            }
        }

        if(determineString(expressionToEvaluate).equals(STRING_STR)) return STRING_STR;

        localResultType = determineResultTypeIfOnlyOneParameter(sb);
        if(!localResultType.isEmpty()) return localResultType;

        return FLOAT_STR;

    }

    /**
     * Permite determinar si el tipo de resultado es bool
     * @return Bool si es de dicho tipo y un string vacío si no es así
     */
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

    /**
     * Permite determinar el tipo de resultado si la expresión contiene solamente un parámetro
     * @param sb String builder que permite verificar o corregir la expresión a una admitida por la librería
     * @return String del tipo de dato determinado puede ser:FLOTANTE,STRING,BOOL
     */
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

    /**
     * Permite determinar si el tipo de resultado de la expresión es String
     * @param expressionToEvaluate Expresión a ser evaluada
     * @return "String" si es de dicho tipo caso contrario un string vacío
     */
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

    /**
     * Permite actualizar una expresión para ser compatible con las propiedades de operaciones entre String
     */
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

    /**
     * Permite retornar la clase del parámetro correspondiente
     * @return Array de String del tipo de dato determinado puede ser:double.class, boolean.class,void.class
     */
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

    /**
     * Permite generar un objeto ExpressionEvaluator tomando de base los atributos de la clase
     * @return ExpressionEvaluator con sus parámetros definidos para su evaluación
     * @throws CompileException Si la librería tiene problemas para compilar la expresión
     */
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

    /**
     * Permite generar el objeto DecimalFormat correspondiente para la expresión basado en su atributo de floatPrecision
     * @return DecimalFormar basado en el número de decimales definido en floatPrecision
     */
    public DecimalFormat generateDecimalFormat(){
        String precisionStr = "#.";
        for(int i=0;i<floatPrecision;i++){
            precisionStr = precisionStr.concat("#");
        }
        return new DecimalFormat(precisionStr);
    }

    /**
     * Permite evaluar el ExpressionEvaluator generado de la clase y obtener su resultado
     * @return Objeto genérico conteniendo los datos calculados de la expresión
     * @throws CompileException
     * @throws InvocationTargetException
     * @throws SQLException
     * @throws IOException
     * @throws NullPointerException
     */
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
