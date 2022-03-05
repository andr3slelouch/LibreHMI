package andrade.luis.hmiethernetip.models;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;


public class ExpressionAdapter extends TypeAdapter<Expression>{
    private final TagAdapter tagAdapter = new TagAdapter();
    @Override
    public void write(JsonWriter jsonWriter, Expression expression) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson;
        builder.setPrettyPrinting();

        jsonWriter.beginObject();
        jsonWriter.name("expressionToEvaluate");
        jsonWriter.value(expression.getExpressionToEvaluate());
        jsonWriter.name("resultType");
        jsonWriter.value(expression.getResultType());
        jsonWriter.name("parameterNames");
        jsonWriter.beginArray();
        for(String parameter : expression.getParameterNames()){
            jsonWriter.value(parameter);
        }
        jsonWriter.endArray();
        jsonWriter.name("parameterTypes");
        jsonWriter.beginArray();
        for(String parameter : expression.getParameterTypes()){
            jsonWriter.value(parameter);
        }
        jsonWriter.endArray();
        jsonWriter.name("parameters");
        jsonWriter.beginArray();

        builder.registerTypeAdapter(Tag.class, new TagAdapter());
        gson = builder.create();
        for(Tag tag : expression.getParameters()){
            jsonWriter.jsonValue(gson.toJson(tag));
        }
        jsonWriter.endArray();
        jsonWriter.endObject();
    }

    @Override
    public Expression read(JsonReader jsonReader) throws IOException {
        Expression expression = new Expression();
        jsonReader.beginObject();
        while(jsonReader.hasNext()){
            switch (jsonReader.nextName()) {
                case "expressionToEvaluate":
                    expression.setExpressionToEvaluate(jsonReader.nextString());
                    break;
                case "resultType":
                    expression.setResultType(jsonReader.nextString());
                    break;
                case "parameterNames":
                    jsonReader.beginArray();
                    ArrayList<String> parameterNamesArrayList = new ArrayList<>();
                    while (jsonReader.hasNext()) {
                        parameterNamesArrayList.add(jsonReader.nextString());
                    }
                    jsonReader.endArray();
                    String[] parameterNames = new String[parameterNamesArrayList.size()];
                    for (int i = 0; i < parameterNamesArrayList.size(); i++) {
                        parameterNames[i] = parameterNamesArrayList.get(i);
                    }
                    expression.setParameterNames(parameterNames);
                    break;
                case "parameterTypes":
                    jsonReader.beginArray();
                    ArrayList<String> parameterTypesArrayList = new ArrayList<>();
                    while (jsonReader.hasNext()) {
                        parameterTypesArrayList.add(jsonReader.nextString());
                    }
                    jsonReader.endArray();
                    String[] parameterTypes = new String[parameterTypesArrayList.size()];
                    for (int i = 0; i < parameterTypesArrayList.size(); i++) {
                        parameterTypes[i] = parameterTypesArrayList.get(i);
                    }
                    expression.setParameterTypes(parameterTypes);
                    break;
                case "parameters":
                    jsonReader.beginArray();
                    ArrayList<Tag> tagArrayList = new ArrayList<>();
                    while (jsonReader.hasNext()) {
                        tagArrayList.add(tagAdapter.read(jsonReader));
                    }
                    jsonReader.endArray();

                    expression.setParameters(tagArrayList);
                    break;
            }
        }
        jsonReader.endObject();
        return expression;
    }
}
