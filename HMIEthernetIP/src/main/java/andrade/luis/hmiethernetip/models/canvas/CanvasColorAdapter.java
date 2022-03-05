package andrade.luis.hmiethernetip.models.canvas;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class CanvasColorAdapter extends TypeAdapter<CanvasColor> {
    @Override
    public void write(JsonWriter jsonWriter, CanvasColor canvasColor) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("red");
        jsonWriter.value(canvasColor.getRed());
        jsonWriter.name("green");
        jsonWriter.value(canvasColor.getGreen());
        jsonWriter.name("blue");
        jsonWriter.value(canvasColor.getBlue());
        jsonWriter.name("opacity");
        jsonWriter.value(canvasColor.getOpacity());
        jsonWriter.endObject();
    }

    @Override
    public CanvasColor read(JsonReader jsonReader) throws IOException {
        CanvasColor canvasColor = new CanvasColor();
        jsonReader.beginObject();
        String fieldname = null;
        while (jsonReader.hasNext()) {
            JsonToken token = jsonReader.peek();

            if (token.equals(JsonToken.NAME)) {
                fieldname = jsonReader.nextName();
            }

            if ("red".equals(fieldname)) {
                token = jsonReader.peek();
                canvasColor.setRed(jsonReader.nextDouble());
            }

            if ("blue".equals(fieldname)) {
                token = jsonReader.peek();
                canvasColor.setBlue(jsonReader.nextDouble());
            }

            if ("green".equals(fieldname)) {
                token = jsonReader.peek();
                canvasColor.setGreen(jsonReader.nextDouble());
            }

            if ("opacity".equals(fieldname)) {
                token = jsonReader.peek();
                canvasColor.setOpacity(jsonReader.nextDouble());
            }
        }
        jsonReader.endObject();
        return canvasColor;
    }
}
