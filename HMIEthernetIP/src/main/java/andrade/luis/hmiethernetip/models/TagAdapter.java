package andrade.luis.hmiethernetip.models;

import andrade.luis.hmiethernetip.models.canvas.CanvasPoint;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class TagAdapter extends TypeAdapter<Tag> {
    @Override
    public void write(JsonWriter jsonWriter, Tag tag) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("plcName");
        jsonWriter.value(tag.getPlcName());
        jsonWriter.name("plcAddress");
        jsonWriter.value(tag.getPlcAddress());
        jsonWriter.name("plcDeviceGroup");
        jsonWriter.value(tag.getPlcDeviceGroup());
        jsonWriter.name("name");
        jsonWriter.value(tag.getName());
        jsonWriter.name("type");
        jsonWriter.value(tag.getType());
        jsonWriter.name("address");
        jsonWriter.value(tag.getAddress());
        jsonWriter.name("action");
        jsonWriter.value(tag.getAction());
        jsonWriter.name("value");
        jsonWriter.value(tag.getValue());
        jsonWriter.endObject();
    }

    @Override
    public Tag read(JsonReader jsonReader) throws IOException {
        Tag tag = new Tag();
        jsonReader.beginObject();
        String fieldname = null;
        while (jsonReader.hasNext()) {
            JsonToken token = jsonReader.peek();

            if (token.equals(JsonToken.NAME)) {
                fieldname = jsonReader.nextName();
            }

            if ("plcName".equals(fieldname)) {
                token = jsonReader.peek();
                tag.setPlcName(jsonReader.nextString());
            }

            if ("plcAddress".equals(fieldname)) {
                token = jsonReader.peek();
                tag.setPlcAddress(jsonReader.nextString());
            }

            if ("plcDeviceGroup".equals(fieldname)) {
                token = jsonReader.peek();
                tag.setPlcDeviceGroup(jsonReader.nextString());
            }

            if ("name".equals(fieldname)) {
                token = jsonReader.peek();
                tag.setName(jsonReader.nextString());
            }

            if ("type".equals(fieldname)) {
                token = jsonReader.peek();
                tag.setType(jsonReader.nextString());
            }
            if ("address".equals(fieldname)) {
                token = jsonReader.peek();
                tag.setAddress(jsonReader.nextString());
            }

            if ("action".equals(fieldname)) {
                token = jsonReader.peek();
                tag.setAction(jsonReader.nextString());
            }

            if ("value".equals(fieldname)) {
                token = jsonReader.peek();
                tag.setValue(jsonReader.nextString());
            }
        }

        jsonReader.endObject();
        return tag;
    }
}
