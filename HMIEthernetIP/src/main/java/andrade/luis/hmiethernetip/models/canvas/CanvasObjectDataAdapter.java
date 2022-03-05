package andrade.luis.hmiethernetip.models.canvas;

import andrade.luis.hmiethernetip.models.Expression;
import andrade.luis.hmiethernetip.models.ExpressionAdapter;
import andrade.luis.hmiethernetip.models.Tag;
import andrade.luis.hmiethernetip.models.TagAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class CanvasObjectDataAdapter extends TypeAdapter<CanvasObjectData> {
    private final TagAdapter tagAdapter = new TagAdapter();
    private final CanvasColorAdapter colorAdapter = new CanvasColorAdapter();
    private final ExpressionAdapter expressionAdapter = new ExpressionAdapter();
    private final CanvasPointAdapter pointAdapter = new CanvasPointAdapter();
    @Override
    public void write(JsonWriter jsonWriter, CanvasObjectData canvasObjectData) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson;
        jsonWriter.beginObject();
        jsonWriter.name("selected");
        jsonWriter.value(canvasObjectData.isSelected());
        jsonWriter.name("mouseOver");
        jsonWriter.value(canvasObjectData.isMouseOver());
        jsonWriter.name("width");
        jsonWriter.value(canvasObjectData.getWidth());
        jsonWriter.name("height");
        jsonWriter.value(canvasObjectData.getHeight());
        jsonWriter.name("center");
        builder.registerTypeAdapter(CanvasPoint.class, new CanvasPointAdapter());
        gson = builder.create();
        jsonWriter.jsonValue(gson.toJson(canvasObjectData.getCenter()));
        jsonWriter.name("position");
        jsonWriter.jsonValue(gson.toJson(canvasObjectData.getPosition()));
        jsonWriter.name("id");
        jsonWriter.value(canvasObjectData.getId());
        jsonWriter.name("operation");
        jsonWriter.value(canvasObjectData.getOperation());
        jsonWriter.name("data");
        jsonWriter.value(canvasObjectData.getData());
        jsonWriter.name("name");
        jsonWriter.value(canvasObjectData.getName());
        jsonWriter.name("type");
        jsonWriter.value(canvasObjectData.getType());
        jsonWriter.name("tag");
        builder.registerTypeAdapter(Tag.class, new TagAdapter());
        gson = builder.create();
        jsonWriter.jsonValue(gson.toJson(canvasObjectData.getTag()));
        jsonWriter.name("expression");
        builder.registerTypeAdapter(Expression.class, new ExpressionAdapter());
        gson = builder.create();
        jsonWriter.jsonValue(gson.toJson(canvasObjectData.getExpression()));
        jsonWriter.name("visibilityExpression");
        jsonWriter.jsonValue(gson.toJson(canvasObjectData.getVisibilityExpression()));
        jsonWriter.name("primaryColor");
        builder.registerTypeAdapter(CanvasColor.class, new CanvasColorAdapter());
        gson = builder.create();
        jsonWriter.jsonValue(gson.toJson(canvasObjectData.getPrimaryColor()));
        jsonWriter.name("backgroundColor");
        jsonWriter.jsonValue(gson.toJson(canvasObjectData.getBackgroundColor()));
        jsonWriter.name("orientation");
        jsonWriter.jsonValue(gson.toJson(canvasObjectData.getOrientation()));
        jsonWriter.name("minValue");
        jsonWriter.value(canvasObjectData.getMinValue());
        jsonWriter.name("maxValue");
        jsonWriter.value(canvasObjectData.getMaxValue());
        jsonWriter.name("mirroringHorizontal");
        jsonWriter.value(canvasObjectData.isMirroringHorizontal());
        jsonWriter.name("mirroringVertical");
        jsonWriter.value(canvasObjectData.isMirroringVertical());
        jsonWriter.name("preservingRatio");
        jsonWriter.value(canvasObjectData.isPreservingRatio());
        jsonWriter.name("rotation");
        jsonWriter.value(canvasObjectData.getRotation());
        jsonWriter.name("imageSymbol");
        jsonWriter.value(canvasObjectData.isImageSymbol());
        jsonWriter.name("isModifyingImage");
        jsonWriter.value(canvasObjectData.isModifyingImage());
        jsonWriter.name("contrast");
        jsonWriter.value(canvasObjectData.getContrast());
        jsonWriter.name("brightness");
        jsonWriter.value(canvasObjectData.getBrightness());
        jsonWriter.name("saturation");
        jsonWriter.value(canvasObjectData.getSaturation());
        jsonWriter.name("hue");
        jsonWriter.value(canvasObjectData.getHue());
        jsonWriter.name("mode");
        jsonWriter.value(canvasObjectData.getMode());
        jsonWriter.name("status");
        jsonWriter.value(canvasObjectData.getStatus());
        jsonWriter.name("visible");
        jsonWriter.value(canvasObjectData.isVisible());
        jsonWriter.endObject();
    }

    @Override
    public CanvasObjectData read(JsonReader jsonReader) throws IOException {
        CanvasObjectData canvasObjectData = new CanvasObjectData();
        jsonReader.beginObject();
        String fieldname = null;
        while(jsonReader.hasNext()) {
            JsonToken token = jsonReader.peek();

            if(token.equals(JsonToken.NAME)){
                fieldname = jsonReader.nextName();
            }

            if("selected".equals(fieldname)){
                token = jsonReader.peek();
                canvasObjectData.setSelected(jsonReader.nextBoolean());
            }

            if("mouseOver".equals(fieldname)){
                token = jsonReader.peek();
                canvasObjectData.setMouseOver(jsonReader.nextBoolean());
            }

            if("width".equals(fieldname)){
                token = jsonReader.peek();
                canvasObjectData.setWidth(jsonReader.nextDouble());
            }

            if("height".equals(fieldname)){
                token = jsonReader.peek();
                canvasObjectData.setHeight(jsonReader.nextDouble());
            }

            if("center".equals(fieldname)){
                token = jsonReader.peek();
                //TODO: canvasObjectData.setCenter(jsonReader.next);
            }

            if("position".equals(fieldname)){
                token = jsonReader.peek();
                //TODO: canvasObjectData.set
            }

            if("operation".equals(fieldname)){
                token = jsonReader.peek();
                canvasObjectData.setOperation(jsonReader.nextString());
            }

            if("data".equals(fieldname)){
                token = jsonReader.peek();
                canvasObjectData.setData(jsonReader.nextString());
            }

            if("name".equals(fieldname)){
                token = jsonReader.peek();
                canvasObjectData.setName(jsonReader.nextString());
            }

            if("type".equals(fieldname)){
                token = jsonReader.peek();
                canvasObjectData.setType(jsonReader.nextString());
            }

            if("tag".equals(fieldname)){
                token = jsonReader.peek();
                //TODO: canvasObjectData.setType(jsonReader.nextString());
            }

            if("expression".equals(fieldname)){
                token = jsonReader.peek();
                //TODO: canvasObjectData.setType(jsonReader.nextString());
            }

            if("visibilityExpression".equals(fieldname)){
                token = jsonReader.peek();
                //TODO: canvasObjectData.set
            }

            if("primaryColor".equals(fieldname)){
                token = jsonReader.peek();
                //TODO: canvasObjectData.set
            }

            if("backgroundColor".equals(fieldname)){
                token = jsonReader.peek();
                //TODO: canvasObjectData.set
            }

            if("orientation".equals(fieldname)){
                token = jsonReader.peek();
                //TODO: canvasObjectData.set
            }

            if("minValue".equals(fieldname)){
                token = jsonReader.peek();
                canvasObjectData.setMinValue(jsonReader.nextDouble());
            }

            if("maxValue".equals(fieldname)){
                token = jsonReader.peek();
                canvasObjectData.setMinValue(jsonReader.nextDouble());
            }

            if("mirroringHorizontal".equals(fieldname)){
                token = jsonReader.peek();
                canvasObjectData.setMirroringHorizontal(jsonReader.nextBoolean());
            }

            if("mirroringVertical".equals(fieldname)){
                token = jsonReader.peek();
                canvasObjectData.setMirroringVertical(jsonReader.nextBoolean());
            }

            if("preservingRatio".equals(fieldname)){
                token = jsonReader.peek();
                canvasObjectData.setPreservingRatio(jsonReader.nextBoolean());
            }

            if("rotation".equals(fieldname)){
                token = jsonReader.peek();
                canvasObjectData.setRotation(jsonReader.nextDouble());
            }

            if("isImageSymbol".equals(fieldname)){
                token = jsonReader.peek();
                canvasObjectData.setImageSymbol(jsonReader.nextBoolean());
            }
            if("isModifyingImage".equals(fieldname)){
                token = jsonReader.peek();
                canvasObjectData.setModifyingImage(jsonReader.nextBoolean());
            }

            if("contrast".equals(fieldname)){
                token = jsonReader.peek();
                canvasObjectData.setContrast(jsonReader.nextDouble());
            }

            if("brightness".equals(fieldname)){
                token = jsonReader.peek();
                canvasObjectData.setBrightness(jsonReader.nextDouble());
            }

            if("saturation".equals(fieldname)){
                token = jsonReader.peek();
                canvasObjectData.setSaturation(jsonReader.nextDouble());
            }

            if("hue".equals(fieldname)){
                token = jsonReader.peek();
                canvasObjectData.setHue(jsonReader.nextDouble());
            }

            if("mode".equals(fieldname)){
                token = jsonReader.peek();
                canvasObjectData.setMode(jsonReader.nextString());
            }

            if("status".equals(fieldname)){
                token = jsonReader.peek();
                canvasObjectData.setStatus(jsonReader.nextString());
            }

            if("visible".equals(fieldname)){
                token = jsonReader.peek();
                canvasObjectData.setVisible(jsonReader.nextBoolean());
            }

            jsonReader.endObject();
            return canvasObjectData;
        }
        return null;
    }
}
