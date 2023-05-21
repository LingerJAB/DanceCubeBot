package com.mirai.tools;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class JsonUtil {

    @NotNull
    public static JsonObject mergeJson(JsonObject... jsonObjects) {
        JsonObject mergedObject = new JsonObject();
        for(JsonObject json : jsonObjects) {
            for(Map.Entry<String, JsonElement> entry : json.entrySet()) {
                mergedObject.add(entry.getKey(), entry.getValue());
            }
        }
        return mergedObject;
    }
}
