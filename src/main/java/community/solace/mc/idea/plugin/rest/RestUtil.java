package community.solace.mc.idea.plugin.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.util.Map;

public class RestUtil {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static String getMessage(String responseBody) {
        Map<String, String> result = GSON.fromJson(responseBody, Map.class);
        return result.get("message");
    }

    public static String prettyPrint(String jsonString) {
        JsonElement je = JsonParser.parseString(jsonString);
        try {
            return GSON.toJson(je);
        } catch (JsonParseException e) {
            return jsonString;
        }
    }
}
