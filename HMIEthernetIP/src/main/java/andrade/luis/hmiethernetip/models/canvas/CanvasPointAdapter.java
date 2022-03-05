package andrade.luis.hmiethernetip.models.canvas;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class CanvasPointAdapter extends TypeAdapter<CanvasPoint> {
    @Override
    public void write(JsonWriter jsonWriter, CanvasPoint canvasPoint) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("x");
        jsonWriter.value(canvasPoint.getX());
        jsonWriter.name("y");
        jsonWriter.value(canvasPoint.getY());
        jsonWriter.endObject();
    }

    @Override
    public CanvasPoint read(JsonReader jsonReader) throws IOException {
        CanvasPoint canvasPoint = new CanvasPoint();
        jsonReader.beginObject();
        String fieldname = null;
        while (jsonReader.hasNext()) {
            JsonToken token = jsonReader.peek();

            if (token.equals(JsonToken.NAME)) {
                fieldname = jsonReader.nextName();
            }

            if ("x".equals(fieldname)) {
                token = jsonReader.peek();
                canvasPoint.setX(jsonReader.nextDouble());
            }

            if ("y".equals(fieldname)) {
                token = jsonReader.peek();
                canvasPoint.setY(jsonReader.nextDouble());
            }
        }

        jsonReader.endObject();
        return canvasPoint;
    }
}
